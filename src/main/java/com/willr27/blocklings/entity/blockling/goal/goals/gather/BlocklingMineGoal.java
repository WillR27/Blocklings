package com.willr27.blocklings.entity.blockling.goal.goals.gather;

import com.mojang.datafixers.util.Pair;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import com.willr27.blocklings.entity.blockling.skill.skills.MiningSkills;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.GoalWhitelist;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.Whitelist;
import com.willr27.blocklings.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Mines the targeted ore/vein.
 */
public class BlocklingMineGoal extends BlocklingGatherGoal
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
     * The max number of blocks that can be part of a vein.
     */
    private static final int MAX_VEIN_SIZE = 40;

    /**
     * The list of block positions in the current vein.
     */
    @Nonnull
    public final List<BlockPos> veinBlockPositions = new ArrayList<>();

    /**
     * The ore whitelist.
     */
    @Nonnull
    public final GoalWhitelist oreWhitelist;

    /**
     * The set of positions we have attempted to use as path targets so far.
     */
    @Nonnull
    private final Set<BlockPos> pathTargetPositionsTested = new HashSet<>();

    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingMineGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        oreWhitelist = new GoalWhitelist("24d7135e-607b-413b-a2a7-00d19119b9de", "ores", Whitelist.Type.BLOCK, this);
        oreWhitelist.setIsUnlocked(blockling.getSkills().getSkill(MiningSkills.WHITELIST).isBought(), false);
        BlockUtil.ORES.get().forEach(ore -> oreWhitelist.put(ore.getRegistryName(), true));
        whitelists.add(oreWhitelist);

        setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
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
    public void stop()
    {
        super.stop();

        veinBlockPositions.clear();
    }

    @Override
    protected void tickGather()
    {
        super.tickGather();

        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        BlockPos targetPos = getTarget();
        BlockState targetBlockState = getTargetBlockState();

        boolean mainCanHarvest = ToolUtil.canToolHarvest(mainStack, targetBlockState);
        boolean offCanHarvest = ToolUtil.canToolHarvest(offStack, targetBlockState);

        if (mainCanHarvest || offCanHarvest)
        {
            blockling.getActions().gather.tryStart();

            if (blockling.getActions().gather.isRunning())
            {
                float blocklingDestroySpeed = blockling.getStats().miningSpeed.getValue();
                float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolHarvestSpeedWithEnchantments(mainStack, targetBlockState) : 0.0f;
                float offDestroySpeed = offCanHarvest ? ToolUtil.getToolHarvestSpeedWithEnchantments(offStack, targetBlockState) : 0.0f;

                float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                float blockStrength = targetBlockState.getDestroySpeed(world, targetPos);

                blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                float progress = destroySpeed / blockStrength / 100.0f;
                blockling.getActions().gather.tick(progress);

                if (blockling.getActions().gather.isFinished())
                {
                    blockling.getActions().gather.stop();
                    blockling.getStats().miningXp.incrementValue((int) (blockStrength * 2.0f));

                    for (ItemStack stack : DropUtil.getDrops(DropUtil.Context.MINING, blockling, targetPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                    {
                        stack = blockling.getEquipment().addItem(stack);
                        blockling.dropItemStack(stack);
                    }

                    if (ToolUtil.damageTool(mainStack, blockling, mainCanHarvest ? blockling.getSkills().getSkill(MiningSkills.HASTY).isBought() ? 2 : 1 : 0))
                    {
                        mainStack.shrink(1);
                    }

                    if (ToolUtil.damageTool(offStack, blockling, offCanHarvest ? blockling.getSkills().getSkill(MiningSkills.HASTY).isBought() ? 2 : 1 : 0))
                    {
                        offStack.shrink(1);
                    }

                    blockling.incOresMinedRecently();

                    world.destroyBlock(targetPos, false);
                    world.destroyBlockProgress(blockling.getId(), targetPos, -1);

                    if (blockling.getSkills().getSkill(MiningSkills.HAMMER).isBought())
                    {
                        for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(targetPos))
                        {
                            if (isValidTarget(surroundingPos))
                            {
                                for (ItemStack stack : DropUtil.getDrops(DropUtil.Context.MINING, blockling, surroundingPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                                {
                                    stack = blockling.getEquipment().addItem(stack);
                                    blockling.dropItemStack(stack);
                                }

                                world.destroyBlock(surroundingPos, false);
                            }
                        }
                    }
                }
                else
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
    public void checkForAndHandleInvalidTargets()
    {
        for (BlockPos blockPos : new ArrayList<>(veinBlockPositions))
        {
            if (!isValidTarget(blockPos))
            {
                markBad(blockPos);
            }
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

        if (veinBlockPositions.isEmpty())
        {
            if (!tryFindVein())
            {
                setTarget(null);

                return;
            }

            Pair<BlockPos, Path> pathToVein = findPathToVein();

            if (pathToVein == null)
            {
                setTarget(null);

                return;
            }

            trySetPathTarget(pathToVein.getFirst(), pathToVein.getSecond());
        }

        setTarget((BlockPos) veinBlockPositions.toArray()[veinBlockPositions.size() - 1]);
    }

    @Override
    public void markEntireTargetBad()
    {
        while (!veinBlockPositions.isEmpty())
        {
            markBad(veinBlockPositions.get(0));
        }
    }

    @Override
    public void markBad(@Nonnull BlockPos blockPos)
    {
        super.markBad(blockPos);

        veinBlockPositions.remove(blockPos);
    }

    @Override
    protected boolean isValidTargetBlock(@Nonnull Block block)
    {
        return oreWhitelist.isEntryWhitelisted(block);
    }

    @Nonnull
    @Override
    protected ToolType getToolType()
    {
        return ToolType.PICKAXE;
    }

    /**
     * Tries to find the nearest vein.
     *
     * @return true if a vein was found.
     */
    private boolean tryFindVein()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        List<BlockPos> veinBlockPositions = new ArrayList<>();
        List<BlockPos> testedBlockPositions = new ArrayList<>();

        double closestVeinDistSq = Float.MAX_VALUE;

        for (int i = -SEARCH_RADIUS_X; i <= SEARCH_RADIUS_X; i++)
        {
            for (int j = -SEARCH_RADIUS_Y; j <= SEARCH_RADIUS_Y; j++)
            {
                for (int k = -SEARCH_RADIUS_X; k <= SEARCH_RADIUS_X; k++)
                {
                    BlockPos testBlockPos = blocklingBlockPos.offset(i, j, k);

                    if (testedBlockPositions.contains(testBlockPos))
                    {
                        continue;
                    }

                    if (isValidTarget(testBlockPos))
                    {
                        List<BlockPos> veinBlockPositionsToTest = findVeinFrom(testBlockPos);

                        boolean canSeeVein = false;

                        for (BlockPos veinBlockPos : veinBlockPositionsToTest)
                        {
                            if (!testedBlockPositions.contains(veinBlockPos))
                            {
                                testedBlockPositions.add(veinBlockPos);
                            }

                            if (!canSeeVein && EntityUtil.canSee(blockling, veinBlockPos))
                            {
                                canSeeVein = true;
                            }
                        }

                        if (!canSeeVein)
                        {
                            continue;
                        }

                        for (BlockPos veinBlockPos : veinBlockPositionsToTest)
                        {
                            float distanceSq = (float) blockling.distanceToSqr(veinBlockPos.getX() + 0.5f, veinBlockPos.getY() + 0.5f, veinBlockPos.getZ() + 0.5f);

                            if (distanceSq < closestVeinDistSq)
                            {
                                closestVeinDistSq = distanceSq;
                                veinBlockPositions = veinBlockPositionsToTest;

                                break;
                            }
                        }
                    }
                }
            }
        }

        if (!veinBlockPositions.isEmpty())
        {
            this.veinBlockPositions.clear();
            this.veinBlockPositions.addAll(veinBlockPositions);

            return true;
        }

        return false;
    }

    /**
     * Returns a vein from the given starting block pos.
     *
     * @param startingBlockPos the starting block pos.
     * @return the list of block positions in the vein.
     */
    @Nonnull
    private List<BlockPos> findVeinFrom(@Nonnull BlockPos startingBlockPos)
    {
        List<BlockPos> veinBlockPositionsToTest = new ArrayList<>();
        List<BlockPos> veinBlockPositions = new ArrayList<>();

        veinBlockPositionsToTest.add(startingBlockPos);
        veinBlockPositions.add(startingBlockPos);

        while (!veinBlockPositionsToTest.isEmpty() && veinBlockPositions.size() < MAX_VEIN_SIZE)
        {
            BlockPos testBlockPos = veinBlockPositionsToTest.stream().findFirst().get();

            BlockPos[] surroundingBlockPositions = new BlockPos[]
                    {
                            testBlockPos.offset(-1, 0, 0),
                            testBlockPos.offset(1, 0, 0),
                            testBlockPos.offset(0, -1, 0),
                            testBlockPos.offset(0, 1, 0),
                            testBlockPos.offset(0, 0, -1),
                            testBlockPos.offset(0, 0, 1),
                    };

            for (BlockPos surroundingPos : surroundingBlockPositions)
            {
                if (isValidTarget(surroundingPos))
                {
                    if (!veinBlockPositions.contains(surroundingPos))
                    {
                        veinBlockPositions.add(surroundingPos);
                        veinBlockPositionsToTest.add(surroundingPos);
                    }
                }
            }

            veinBlockPositionsToTest.remove(testBlockPos);
        }

        return veinBlockPositions;
    }

    /**
     * Finds the first valid path to the vein, not necessarily the most optimal.
     *
     * @return the path target position and the path to the vein, or null if no path could be found.
     */
    @Nullable
    public Pair<BlockPos, Path> findPathToVein()
    {
        for (BlockPos veinBlockPos : veinBlockPositions)
        {
            if (BlockUtil.areAllAdjacentBlocksSolid(world, veinBlockPos))
            {
                continue;
            }

            if (isBadPathTargetPos(veinBlockPos))
            {
                continue;
            }

            Path path = PathUtil.createPathTo(blockling, veinBlockPos, getPathTargetRangeSq(), false);

            if (path != null)
            {
                return new Pair<>(veinBlockPos, path);
            }
        }

        return null;
    }

    /**
     * Sets the root vein position to the given block pos.
     * Will then recalculate the vein.
     *
     * @param blockPos the block pos to use as the vein root.
     */
    public void changeVeinRootTo(@Nonnull BlockPos blockPos)
    {
        veinBlockPositions.clear();
        veinBlockPositions.addAll(findVeinFrom(blockPos));
    }

    @Override
    protected void recalcPathTargetPosAndPath(boolean force)
    {
        if (force)
        {
            Pair<BlockPos, Path> result = findPathToVein();

            if (result != null)
            {
                trySetPathTarget(result.getFirst(), result.getSecond());
            }
            else
            {
                trySetPathTarget(null, null);
            }

            return;
        }

        // Try to improve our path each recalc by testing different blocks in the vein
        for (BlockPos veinBlockPos : veinBlockPositions)
        {
            if (pathTargetPositionsTested.contains(veinBlockPos))
            {
                continue;
            }

            pathTargetPositionsTested.add(veinBlockPos);

            if (BlockUtil.areAllAdjacentBlocksSolid(world, veinBlockPos))
            {
                continue;
            }

            Path path = PathUtil.createPathTo(blockling, veinBlockPos, getPathTargetRangeSq(), false);

            if (path != null)
            {
                if (getPathTarget() == null || path.getDistToTarget() < getPathTarget().path.getDistToTarget())
                {
                    trySetPathTarget(veinBlockPos, path);

                    return;
                }
            }

            return;
        }

        pathTargetPositionsTested.clear();
    }

    @Override
    protected boolean isValidPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return veinBlockPositions.contains(blockPos);
    }

    @Override
    public boolean trySetPathTarget(@Nullable BlockPos pathTargetPos, @Nullable Path pathToPathTargetPos)
    {
        boolean result = super.trySetPathTarget(pathTargetPos, pathToPathTargetPos);

        if (getPathTarget() != null)
        {
            changeVeinRootTo(getPathTarget().pos);
        }

        return result;
    }

    @Override
    public float getPathTargetRangeSq()
    {
        return blockling.getStats().miningRangeSq.getValue();
    }
}
