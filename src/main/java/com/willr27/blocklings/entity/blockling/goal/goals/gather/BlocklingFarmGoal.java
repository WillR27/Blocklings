package com.willr27.blocklings.entity.blockling.goal.goals.gather;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import com.willr27.blocklings.entity.blockling.skill.skills.FarmingSkills;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.GoalWhitelist;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.Whitelist;
import com.willr27.blocklings.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Harvests the targeted crop.
 */
public class BlocklingFarmGoal extends BlocklingGatherGoal
{
    /**
     * The x and z search radius.
     */
    private static final int SEARCH_RADIUS_X = 8;

    /**
     * The y search radius.
     */
    private static final int SEARCH_RADIUS_Y = 8;

    /**
     * The crop whitelist.
     */
    public final GoalWhitelist cropWhitelist;

    /**
     * The seed whitelist.
     */
    public final GoalWhitelist seedWhitelist;

    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingFarmGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        cropWhitelist = new GoalWhitelist("25140edf-f60e-459e-b1f0-9ff82108ec0b", "crops", Whitelist.Type.BLOCK, this);
        cropWhitelist.setIsUnlocked(blockling.getSkills().getSkill(FarmingSkills.CROP_WHITELIST).isBought(), false);
        BlockUtil.CROPS.get().forEach(crop -> cropWhitelist.put(crop.getRegistryName(), true));
        whitelists.add(cropWhitelist);

        seedWhitelist = new GoalWhitelist("d77bf1c1-7718-4733-b763-298b03340eea", "seeds", Whitelist.Type.ITEM, this);
        seedWhitelist.setIsUnlocked(blockling.getSkills().getSkill(FarmingSkills.SEED_WHITELIST).isBought(), false);
        BlockUtil.CROPS.get().forEach(crop ->
        {
            if (crop instanceof CropsBlock)
            {
                seedWhitelist.put(crop.getCloneItemStack(world, null, crop.defaultBlockState()).getItem().getRegistryName(), true);
            }
        });
        whitelists.add(seedWhitelist);

        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse()
    {
        return super.canContinueToUse();
    }

    @Override
    protected void tickGather()
    {
        super.tickGather();

        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        BlockPos targetPos = getTarget();
        BlockState targetBlockState = world.getBlockState(targetPos);
        Block targetBlock = targetBlockState.getBlock();

        boolean mainCanHarvest = ToolUtil.canToolHarvest(mainStack, targetBlockState);
        boolean offCanHarvest = ToolUtil.canToolHarvest(offStack, targetBlockState);

        if (mainCanHarvest || offCanHarvest)
        {
            blockling.getActions().gather.tryStart();

            if (blockling.getActions().gather.isRunning())
            {
                float blocklingDestroySpeed = blockling.getStats().farmingSpeed.getValue();
                float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolHarvestSpeedWithEnchantments(mainStack, targetBlockState) : 0.0f;
                float offDestroySpeed = offCanHarvest ? ToolUtil.getToolHarvestSpeedWithEnchantments(offStack, targetBlockState) : 0.0f;

                float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                float blockStrength = targetBlockState.getDestroySpeed(world, targetPos);

                blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                float progress = destroySpeed / (blockStrength + 2.5f) / 100.0f;
                blockling.getActions().gather.tick(progress);

                if (blockling.getActions().gather.isFinished())
                {
                    blockling.getActions().gather.stop();
                    blockling.getStats().farmingXp.incrementValue((int) ((blockStrength + 1.0f) * 3.0f));

                    for (ItemStack stack : DropUtil.getDrops(DropUtil.Context.FARMING, blockling, targetPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                    {
                        stack = blockling.getEquipment().addItem(stack);
                        blockling.dropItemStack(stack);
                    }

                    if (ToolUtil.damageTool(mainStack, blockling, mainCanHarvest ? blockling.getSkills().getSkill(FarmingSkills.HASTY).isBought() ? 2 : 1 : 0))
                    {
                        mainStack.shrink(1);
                    }

                    if (ToolUtil.damageTool(offStack, blockling, offCanHarvest ? blockling.getSkills().getSkill(FarmingSkills.HASTY).isBought() ? 2 : 1 : 0))
                    {
                        offStack.shrink(1);
                    }

                    blockling.incCropsHarvestedRecently();

                    ItemStack seedStack = ItemStack.EMPTY;

                    if (blockling.getSkills().getSkill(FarmingSkills.REPLANTER).isBought() && targetBlock instanceof CropsBlock)
                    {
                        CropsBlock cropsBlock = (CropsBlock) targetBlock;
                        seedStack = cropsBlock.getCloneItemStack(world, targetPos, targetBlockState);
                    }

                    world.destroyBlock(targetPos, false);
                    world.destroyBlockProgress(blockling.getId(), targetPos, -1);

                    if (blockling.getSkills().getSkill(FarmingSkills.SCYTHE).isBought())
                    {
                        for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(targetPos))
                        {
                            BlockState surroundingBlockState = world.getBlockState(surroundingPos);
                            Block surroundingBlock = surroundingBlockState.getBlock();

                            if (isValidTarget(surroundingPos))
                            {
                                for (ItemStack stack : DropUtil.getDrops(DropUtil.Context.FARMING, blockling, surroundingPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                                {
                                    stack = blockling.getEquipment().addItem(stack);
                                    blockling.dropItemStack(stack);
                                }

                                world.destroyBlock(surroundingPos, false);

                                ItemStack seedStack2 = ItemStack.EMPTY;

                                if (blockling.getSkills().getSkill(FarmingSkills.REPLANTER).isBought() && surroundingBlock instanceof CropsBlock)
                                {
                                    CropsBlock cropsBlock = (CropsBlock) surroundingBlock;
                                    seedStack2 = cropsBlock.getCloneItemStack(world, surroundingPos, surroundingBlockState);
                                }

                                if (!seedStack2.isEmpty() && blockling.getEquipment().take(seedStack2) && seedWhitelist.isEntryWhitelisted(seedStack2.getItem()))
                                {
                                    world.setBlock(surroundingPos, Block.byItem(seedStack2.getItem()).defaultBlockState(), 3);
                                }
                            }
                        }
                    }

                    if (!seedStack.isEmpty() && blockling.getEquipment().take(seedStack) && seedWhitelist.isEntryWhitelisted(seedStack.getItem()))
                    {
                        world.setBlock(targetPos, Block.byItem(seedStack.getItem()).defaultBlockState(), 3);
                    }
                }
                else if (targetBlockState.getMaterial().isSolid())
                {
                    world.destroyBlockProgress(blockling.getId(), targetPos, BlockUtil.calcBlockBreakProgress(blockling.getActions().gather.getCount()));
                }
            }
        }
        else
        {
            world.destroyBlockProgress(blockling.getId(), targetPos, -1);
            blockling.getActions().gather.stop();
        }
    }

    @Override
    public void recalcTarget()
    {
        super.recalcTarget();

        if (getTarget() != null)
        {
            return;
        }

        if (!tryFindCrop())
        {
            return;
        }

        Pair<BlockPos, Path> pathToCrop = findPathToCrop();

        if (pathToCrop != null)
        {
            trySetPathTarget(pathToCrop.getKey(), pathToCrop.getValue());
        }
    }

    @Override
    protected void recalcPathTargetPosAndPath(boolean force)
    {
        Pair<BlockPos, Path> result = findPathToCrop();

        if (result != null)
        {
            trySetPathTarget(result.getKey(), result.getValue());
        }
        else
        {
            trySetPathTarget(null, null);

            return;
        }
    }

    /**
     * Tries to find the nearest tree.
     *
     * @return true if a tree was found.
     */
    private boolean tryFindCrop()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        BlockPos closestPos = null;
        double closestCropDistSq = Float.MAX_VALUE;

        for (int i = -SEARCH_RADIUS_X; i <= SEARCH_RADIUS_X; i++)
        {
            for (int j = -SEARCH_RADIUS_Y; j <= SEARCH_RADIUS_Y; j++)
            {
                for (int k = -SEARCH_RADIUS_X; k <= SEARCH_RADIUS_X; k++)
                {
                    BlockPos testBlockPos = blocklingBlockPos.offset(i, j, k);

                    if (isValidTarget(testBlockPos))
                    {
                        float distanceSq = (float) blockling.distanceToSqr(testBlockPos.getX() + 0.5f, testBlockPos.getY() + 0.5f, testBlockPos.getZ() + 0.5f);

                        if (distanceSq < closestCropDistSq)
                        {
                            closestPos = testBlockPos;
                            closestCropDistSq = distanceSq;

                            break;
                        }
                    }
                }
            }
        }

        if (closestPos != null)
        {
            setTarget(closestPos);

            return true;
        }

        return false;
    }

    /**
     * Finds the first valid path to the crop, not necessarily the most optimal.
     *
     * @return the path target position and the path to the crop, or null if no path could be found.
     */
    @Nullable
    public Pair<BlockPos, Path> findPathToCrop()
    {
        if (BlockUtil.areAllAdjacentBlocksSolid(world, getTarget()))
        {
            return null;
        }

        if (isBadPathTargetPos(getTarget()))
        {
            return null;
        }

        Path path = PathUtil.createPathTo(blockling, getTarget(), getPathTargetRangeSq(), false);

        if (path != null)
        {
            return new MutablePair<>(getTarget(), path);
        }

        return null;
    }

    @Override
    public void checkForAndHandleInvalidTargets()
    {
        if (!isTargetValid())
        {
            markEntireTargetBad();
        }
    }

    @Override
    public void markEntireTargetBad()
    {
        if (getTarget() != null)
        {
            markBad(getTarget());
        }
    }

    @Override
    protected boolean isValidTargetBlock(@Nonnull Block block)
    {
        return cropWhitelist.isEntryWhitelisted(block);
    }

    @Override
    protected boolean isValidPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return getTarget() != null && getTarget().equals(blockPos);
    }

    @Override
    public boolean isValidTarget(@Nullable BlockPos target)
    {
        if (!super.isValidTarget(target))
        {
            return false;
        }

        BlockState blockState = world.getBlockState(target);
        Block block = blockState.getBlock();

        if (block instanceof CropsBlock)
        {
            CropsBlock cropsBlock = (CropsBlock) block;

            if (!cropsBlock.isMaxAge(blockState))
            {
                return false;
            }
        }

        return true;
    }

    @Nonnull
    @Override
    protected ToolType getToolType()
    {
        return ToolType.HOE;
    }

    @Override
    public float getPathTargetRangeSq()
    {
        return blockling.getStats().farmingRangeSq.getValue();
    }
}
