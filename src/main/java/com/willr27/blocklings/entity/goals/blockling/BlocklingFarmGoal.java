package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingFarmTargetGoal;
import com.willr27.blocklings.entity.goals.blockling.target.IHasTargetGoal;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.item.DropUtil;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.UUID;

public class BlocklingFarmGoal extends BlocklingGoal implements IHasTargetGoal
{
    private final BlocklingFarmTargetGoal targetGoal;

    private Path path = null;
    private int recalc = 0;
    private final int recalcInterval = 20;
    private float prevMoveDist = 0.0f;

    public BlocklingFarmGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        targetGoal = new BlocklingFarmTargetGoal(this);

        GoalWhitelist whitelist = new GoalWhitelist("25140edf-f60e-459e-b1f0-9ff82108ec0b", "crops", Whitelist.Type.BLOCK, this);
        BlockUtil.CROPS.forEach(crop -> whitelist.put(crop.getRegistryName(), true));
        whitelists.add(whitelist);

        GoalWhitelist whitelist2 = new GoalWhitelist("d77bf1c1-7718-4733-b763-298b03340eea", "seeds", Whitelist.Type.ITEM, this);
        BlockUtil.CROPS.forEach(crop ->
        {
            if (crop instanceof CropsBlock)
            {
                whitelist2.put(crop.getCloneItemStack(world, null, crop.defaultBlockState()).getItem().getRegistryName(), true);
            }
        });
        whitelists.add(whitelist2);

        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public BlocklingTargetGoal getTargetGoal()
    {
        return targetGoal;
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        if (!blockling.getEquipment().hasToolEquipped(ToolType.HOE))
        {
            return false;
        }

        if (!targetGoal.hasTarget())
        {
            return false;
        }

//        if (!blockling.getEquipment().canHarvestBlockWithEquippedTools(world.getBlockState(targetGoal.getTargetPos())))
//        {
//            return false;
//        }

        path = blockling.getNavigation().createPath(targetGoal.getTargetPos(), 0);

        if (path == null)
        {
            if (!isInRange(targetGoal.getTargetPos()))
            {
                targetGoal.markTargetBad();

                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        if (!blockling.getEquipment().hasToolEquipped(ToolType.HOE))
        {
            return false;
        }

        if (!targetGoal.hasTarget())
        {
            return false;
        }

//        if (!blockling.getEquipment().canHarvestBlockWithEquippedTools(world.getBlockState(targetGoal.getTargetPos())))
//        {
//            return false;
//        }

        if (path == null)
        {
            if (!isInRange(targetGoal.getTargetPos()))
            {
                targetGoal.markTargetBad();

                return false;
            }
        }

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        blockling.getNavigation().moveTo(path, 1.0);
    }

    @Override
    public void stop()
    {
        super.stop();

        if (targetGoal.hasTarget())
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getTargetPos(), -1);
        }

        if (targetGoal.hasPrevTarget())
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getPrevTargetPos(), -1);
        }

        blockling.getNavigation().stop();
        blockling.getActions().mine.stop();

        prevMoveDist = 0.0f;
    }

    @Override
    public void tick()
    {
        super.tick();

        // Tick to make sure isFinished is only true for a single tick
        blockling.getActions().mine.tick(0.0f);

        if (isInRange(targetGoal.getTargetPos()))
        {
            ItemStack mainStack = blockling.getMainHandItem();
            ItemStack offStack = blockling.getOffhandItem();

            BlockPos targetBlockPos = targetGoal.getTargetPos();
            BlockState targetBlockState = world.getBlockState(targetBlockPos);
            Block targetBlock = targetBlockState.getBlock();

            boolean mainCanHarvest = ToolUtil.isHoe(mainStack);
            boolean offCanHarvest = ToolUtil.isHoe(offStack);

            blockling.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(targetBlockPos.getX() + 0.5, targetBlockPos.getY() + 0.5, targetBlockPos.getZ() + 0.5));

            if (mainCanHarvest || offCanHarvest)
            {
                blockling.getActions().mine.tryStart();

                if (blockling.getActions().mine.isRunning())
                {
                    float blocklingDestroySpeed = blockling.getStats().farmingSpeed.getValue();
                    float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolFarmingSpeedWithEnchantments(mainStack) : 0.0f;
                    float offDestroySpeed = offCanHarvest ? ToolUtil.getToolFarmingSpeedWithEnchantments(offStack) : 0.0f;

                    float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                    float blockStrength = targetBlockState.getDestroySpeed(world, targetGoal.getTargetPos());

                    blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                    float progress = destroySpeed / (blockStrength + 5.0f) / 100.0f;
                    blockling.getActions().mine.tick(progress);

                    if (blockling.getActions().mine.isFinished())
                    {
                        blockling.getActions().mine.stop();
                        blockling.getStats().farmingXp.incValue((int) ((blockStrength + 1.0f) * 3.0f));

                        for (ItemStack stack : DropUtil.getDrops(blockling, targetBlockPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                        {
                            stack = blockling.getEquipment().addItem(stack);
                            blockling.dropItemStack(stack);
                        }

                        if (mainStack.hurt(mainCanHarvest ? 1 : 0, blockling.getRandom(), null))
                        {
                            mainStack.shrink(1);
                        }

                        if (offStack.hurt(offCanHarvest ? 1 : 0, blockling.getRandom(), null))
                        {
                            offStack.shrink(1);
                        }

                        ItemStack seedStack = ItemStack.EMPTY;

                        if (targetBlock instanceof CropsBlock)
                        {
                            CropsBlock cropsBlock = (CropsBlock) targetBlock;
                            seedStack = cropsBlock.getCloneItemStack(world, targetBlockPos, targetBlockState);
                        }

                        world.destroyBlock(targetBlockPos, false);
                        world.destroyBlockProgress(blockling.getId(), targetBlockPos, 0);

                        if (blockling.getEquipment().take(seedStack) && whitelists.get(1).isEntryWhitelisted(seedStack.getItem()))
                        {
                            world.setBlock(targetBlockPos, Block.byItem(seedStack.getItem()).defaultBlockState(), 3);
                        }

                        forceRecalc();
                    }
                    else if (targetBlockState.getMaterial().isSolid())
                    {
                        world.destroyBlockProgress(blockling.getId(), targetBlockPos, BlockUtil.calcBlockBreakProgress(blockling.getActions().mine.count()));
                    }
                }
            }
            else
            {
                world.destroyBlockProgress(blockling.getId(), targetBlockPos, -1);
                blockling.getActions().mine.stop();
            }
        }
        else
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getTargetPos(), -1);
            blockling.getActions().mine.stop();
        }

        if (tickRecalc())
        {
            if ((path == null || path.isDone() || !hasMovedSinceLastRecalc() || blockling.getNavigation().isStuck()) && !isInRange(targetGoal.getTargetPos()))
            {
                targetGoal.markTargetBad();
            }

            tryCalculatePath();

            prevMoveDist = blockling.moveDist;
        }
    }

    private void tryCalculatePath()
    {
        path = blockling.getNavigation().createPath(targetGoal.getTargetPos(), -1);
        blockling.getNavigation().moveTo(path, 1.0);
    }

    private boolean hasMovedSinceLastRecalc()
    {
        return blockling.moveDist - prevMoveDist > 0.01f;
    }

    private boolean isInRange(BlockPos blockPos)
    {
        float rangeSq = blockling.getStats().farmingRangeSq.getValue();
        float distanceSq = (float) blockling.distanceToSqr(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);

        return distanceSq < rangeSq;
    }

    private void forceRecalc()
    {
        targetGoal.forceRecalc();
        recalc = recalcInterval;
    }

    private boolean tickRecalc()
    {
        recalc++;

        if (recalc < recalcInterval)
        {
            return false;
        }
        else
        {
            recalc = 0;
        }

        return true;
    }
}
