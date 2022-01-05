package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingWoodcutTargetGoal;
import com.willr27.blocklings.entity.goals.blockling.target.IHasTargetGoal;
import com.willr27.blocklings.item.DropUtil;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.skills.BlocklingSkills;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BlocklingWoodcutGoal extends BlocklingGatherGoal<BlocklingWoodcutTargetGoal> implements IHasTargetGoal<BlocklingWoodcutTargetGoal>
{
    /**
     * The log whitelist.
     */
    public final GoalWhitelist logWhitelist;

    /**
     * The associated target goal.
     */
    private final BlocklingWoodcutTargetGoal targetGoal;

    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingWoodcutGoal(UUID id, BlocklingEntity blockling, BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingWoodcutTargetGoal(this);

        logWhitelist = new GoalWhitelist("fbfbfd44-c1b0-4420-824a-270b34c866f7", "logs", Whitelist.Type.BLOCK, this);
        logWhitelist.setIsUnlocked(blockling.getSkills().getSkill(BlocklingSkills.Woodcutting.WHITELIST).isBought(), false);
        BlockUtil.LOGS.forEach(log -> logWhitelist.put(log.getRegistryName(), true));
        whitelists.add(logWhitelist);

        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    @Nonnull
    public BlocklingWoodcutTargetGoal getTargetGoal()
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

        calculatePathToTree();

        if (isStuck())
        {
            getTargetGoal().markBad();

            return false;
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

        if (isStuck())
        {
            getTargetGoal().markBad();

            return false;
        }

        return true;
    }

    @Override
    protected void tickGather()
    {
        super.tickGather();

        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        BlockPos targetBlockPos = targetGoal.getTargetPos();
        BlockState targetBlockState = world.getBlockState(targetBlockPos);

        boolean mainCanHarvest = ToolUtil.canToolHarvestBlock(mainStack, targetBlockState);
        boolean offCanHarvest = ToolUtil.canToolHarvestBlock(offStack, targetBlockState);

        if (mainCanHarvest || offCanHarvest)
        {
            blockling.getActions().gather.tryStart();

            if (blockling.getActions().gather.isRunning())
            {
                float blocklingDestroySpeed = blockling.getStats().woodcuttingSpeed.getValue();
                float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolWoodcuttingSpeedWithEnchantments(mainStack) : 0.0f;
                float offDestroySpeed = offCanHarvest ? ToolUtil.getToolWoodcuttingSpeedWithEnchantments(offStack) : 0.0f;

                float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                float blockStrength = targetBlockState.getDestroySpeed(world, targetGoal.getTargetPos()) + 1.5f;

                blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                float progress = destroySpeed / blockStrength / 100.0f;
                blockling.getActions().gather.tick(progress);

                if (blockling.getActions().gather.isFinished())
                {
                    blockling.getActions().gather.stop();
                    blockling.getStats().woodcuttingXp.incValue((int) (blockStrength * 2.0f));

                    for (ItemStack stack : DropUtil.getDrops(blockling, targetBlockPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                    {
                        stack = blockling.getEquipment().addItem(stack);
                        blockling.dropItemStack(stack);
                    }

                    if (mainStack.hurt(mainCanHarvest ? blockling.getSkills().getSkill(BlocklingSkills.Woodcutting.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        mainStack.shrink(1);
                    }

                    if (offStack.hurt(offCanHarvest ? blockling.getSkills().getSkill(BlocklingSkills.Woodcutting.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        offStack.shrink(1);
                    }

                    blockling.incLogsChoppedRecently();

                    world.destroyBlock(targetBlockPos, false);
                    world.destroyBlockProgress(blockling.getId(), targetBlockPos, 0);

                    recalc();
                }
                else
                {
                    world.destroyBlockProgress(blockling.getId(), targetBlockPos, BlockUtil.calcBlockBreakProgress(blockling.getActions().gather.count()));
                }
            }
        }
        else
        {
            world.destroyBlockProgress(blockling.getId(), targetBlockPos, -1);
            blockling.getActions().gather.stop();
        }
    }

    @Override
    protected void recalc()
    {
        if (isStuck())
        {
            targetGoal.markBad();
        }

        if (!getTargetGoal().isTargetValid())
        {
            getTargetGoal().recalcTarget();
        }

        if (targetGoal.hasTarget())
        {
            calculatePathToTree();
        }
    }

    /**
     * Finds a path towards the tree.
     */
    private void calculatePathToTree()
    {
        List<BlockPos> sortedLogBlockPositions = targetGoal.getTree().logs.stream().sorted(Comparator.comparingInt(Vector3i::getY)).collect(Collectors.toList());

        BlockPos closestPos = null;
        Path closestPath = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (BlockPos testPathTargetPos : sortedLogBlockPositions)
        {
            Path path = createPath(testPathTargetPos);

            if (path != null)
            {
                if (isInRange(testPathTargetPos))
                {
                    setPathTargetPos(testPathTargetPos, path);

                    return;
                }

                double distanceSq = testPathTargetPos.distSqr(path.getTarget());

                if (distanceSq < closestDistanceSq)
                {
                    closestPos = testPathTargetPos;
                    closestPath = path;
                    closestDistanceSq = distanceSq;
                }
            }
        }

        if (closestPath != null)
        {
            setPathTargetPos(closestPos, closestPath);
        }
    }

    /**
     * Creates a path to the given block or a surrounding block.
     *
     * @param blockPos the pos to create a path to.
     * @return the path.
     */
    @Nullable
    private Path createPath(@Nonnull BlockPos blockPos)
    {
        Path closestPath = null;
        double closestDistanceSq = Double.MAX_VALUE;

        Path path = blockling.getNavigation().createPath(blockPos, 0);

        if (path != null)
        {
            closestPath = path;
            closestDistanceSq = blockPos.distSqr(path.getTarget());
        }

        for (BlockPos adjacentPos : BlockUtil.getSurroundingBlockPositions(blockPos))
        {
            path = blockling.getNavigation().createPath(adjacentPos, 0);

            if (path != null)
            {
                double distanceSq = adjacentPos.distSqr(path.getTarget());

                if (distanceSq < closestDistanceSq)
                {
                    closestPath = path;
                    closestDistanceSq = distanceSq;
                }
            }
        }

        return closestPath;
    }

    @Override
    float getRangeSq()
    {
        return blockling.getStats().woodcuttingRangeSq.getValue();
    }
}
