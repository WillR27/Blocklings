package com.willr27.blocklings.entity.blockling.goal.goals.gather;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import com.willr27.blocklings.entity.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.blockling.skill.skills.GeneralSkills;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.util.ToolContext;
import com.willr27.blocklings.util.ToolType;
import com.willr27.blocklings.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Contains common behaviour shared between gathering goals.
 */
public abstract class BlocklingGatherGoal extends BlocklingTargetGoal<BlockPos>
{
    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingGatherGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
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

        return true;
    }

    @Override
    public void start()
    {
        super.start();
    }

    @Override
    public void stop()
    {
        super.stop();

        trySetPathTarget(null, null);

        blockling.getActions().gather.stop();
    }

    @Override
    public void tickGoal()
    {
        // Tick to make sure isFinished() is only true for a single tick
        blockling.getActions().gather.tick(0.0f);

        if (isStuck(false))
        {
            blockling.getActions().gather.stop();

            markEntireTargetBad();
        }
        else if (isInRangeOfPathTargetPos())
        {
            tickGather();
        }
    }

    @Override
    public void recalcTarget()
    {
        if (isTargetValid())
        {
            return;
        }
        else
        {
            markTargetBad();
            setTarget(null);
        }
    }

    /**
     * Called every tick when in range of the path target pos.
     */
    protected void tickGather()
    {
        if (!hasMovedSinceLastPathRecalc())
        {
            blockling.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(getTarget().getX() + 0.5, getTarget().getY() + 0.5, getTarget().getZ() + 0.5));
        }

        if (blockling.getSkills().getSkill(GeneralSkills.AUTOSWITCH).isBought())
        {
            blockling.getEquipment().trySwitchToBestTool(BlocklingHand.BOTH, new ToolContext(getToolType(), getTargetBlockState()));
        }
    }

    /**
     * @return true if the blockling can harvest the block at the target pos.
     */
    public boolean canHarvestTargetPos()
    {
        return canHarvestPos(getTarget());
    }

    /**
     * @param blockPos the block pos to test.
     * @return true if the blockling can harvest the block at the given pos.
     */
    public boolean canHarvestPos(@Nullable BlockPos blockPos)
    {
        if (blockPos == null)
        {
            return false;
        }

        BlockState blockState = world.getBlockState(blockPos);

        if (blockling.getEquipment().canHarvestBlockWithEquippedTools(blockState))
        {
            return true;
        }
        else if (blockling.getSkills().getSkill(GeneralSkills.AUTOSWITCH).isBought())
        {
            Pair<ItemStack, ItemStack> bestTools = blockling.getEquipment().findBestToolsToSwitchTo(BlocklingHand.BOTH, new ToolContext(getToolType(), blockState));

            if (ToolUtil.canToolHarvest(bestTools.getKey(), blockState) || ToolUtil.canToolHarvest(bestTools.getValue(), blockState))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the tool type used to harvest the targets.
     */
    @Nonnull
    protected abstract ToolType getToolType();

    @Override
    public boolean isValidTarget(@Nullable BlockPos target)
    {
        return isValidTargetPos(target) && isValidTargetBlock(world.getBlockState(target).getBlock()) && canHarvestPos(target);
    }

    /**
     * @param blockPos the pos to test.
     * @return true if the given pos is a valid target position.
     */
    protected boolean isValidTargetPos(@Nullable BlockPos blockPos)
    {
        return blockPos != null && !badTargets.contains(blockPos);
    }

    /**
     * @param block the block to test.
     * @return true if the given block is a valid block.
     */
    protected abstract boolean isValidTargetBlock(@Nonnull Block block);

    @Override
    public void markBad(@Nonnull BlockPos target)
    {
        super.markBad(target);

        // Any position we have deemed to be bad is one we are no longer gathering
        // So make sure to reset any block break progress
        world.destroyBlockProgress(blockling.getId(), target, -1);
    }

    @Override
    protected void setPreviousTarget(@Nullable BlockPos target)
    {
        if (target != null && (getTarget() == null || !getTarget().equals(target)))
        {
            world.destroyBlockProgress(blockling.getId(), target, -1);
        }

        super.setPreviousTarget(target);
    }

    /**
     * @return the current target block.
     */
    @Nullable
    public Block getTargetBlock()
    {
        BlockState blockState = getTargetBlockState();

        return blockState != null ? blockState.getBlock() : null;
    }

    /**
     * @return the current target block state.
     */
    @Nullable
    public BlockState getTargetBlockState()
    {
        return getTarget() != null ? world.getBlockState(getTarget()) : null;
    }
}
