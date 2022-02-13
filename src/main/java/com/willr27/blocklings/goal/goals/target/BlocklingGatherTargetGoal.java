package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.goal.goals.BlocklingGatherGoal;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.skill.skills.GeneralSkills;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains common behaviour between gathering target goals.
 *
 * @param <T> the type of the associated gather goal.
 */
public abstract class BlocklingGatherTargetGoal<T extends BlocklingGatherGoal<?>> extends BlocklingTargetGoal<T>
{
    /**
     * The current position to try to gather.
     */
    @Nullable
    private BlockPos targetPos = null;

    /**
     * The previous position to try to gather.
     */
    @Nullable
    private BlockPos prevTargetPos = null;

    /**
     * The set of block positions to ignore as they were recently deemed invalid.
     */
    @Nonnull
    public final Set<BlockPos> badTargetPositions = new HashSet<>();

    /**
     * @param goal the associated goal instance.
     */
    public BlocklingGatherTargetGoal(@Nonnull T goal)
    {
        super(goal);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        goal.updateBadPathTargetPositions();

        if (!tryRecalcTargetPos() || !canHarvestTargetPos())
        {
            badTargetPositions.clear();

            markTargetBad();
            setTargetPos(null);

            goal.setPathTargetPos(null, null, false);

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

        goal.updateBadPathTargetPositions();

        checkForAndRemoveInvalidTargets();

        if (!tryRecalcTargetPos() || !canHarvestTargetPos())
        {
            badTargetPositions.clear();

            markTargetBad();
            setTargetPos(null);

            goal.setPathTargetPos(null, null, false);

            return false;
        }

        return true;
    }

    @Override
    public void stop()
    {
        super.stop();

        setTargetPos(null);

        badTargetPositions.clear();
    }

    /**
     * Checks for and removes any invalid targets.
     */
    public abstract void checkForAndRemoveInvalidTargets();

    /**
     * Recalculates the current target pos.
     *
     * @return whether a target was found.
     */
    public abstract boolean tryRecalcTargetPos();

    /**
     * Marks the entire target bad.
     * This could be an entire vein in the case of mining, or tree for woodcutting.
     */
    public abstract void markTargetBad();

    /**
     * Marks the current target pos as bad.
     * It will then be ignored until the target goal starts again.
     */
    public void markTargetPosBad()
    {
        if (hasTarget())
        {
            markPosBad(getTargetPos());
            setTargetPos(null);
        }
    }

    /**
     * Marks the given block pos as bad.
     * It will then be ignored until the target goal starts again.
     *
     * @param blockPos the block pos to mark as bad.
     */
    public void markPosBad(@Nonnull BlockPos blockPos)
    {
        badTargetPositions.add(blockPos);

        // Any position we have deemed to be bad is one we are no longer gathering
        // So make sure to reset any block break progress
        world.destroyBlockProgress(blockling.getId(), blockPos, -1);
    }

    @Override
    public boolean isTargetValid()
    {
        return isValidTarget(getTargetPos());
    }

    /**
     * @param blockPos the pos to test.
     * @return true if the given pos is a valid target.
     */
    public boolean isValidTarget(@Nullable BlockPos blockPos)
    {
        return isValidTargetPos(blockPos) && isValidTargetBlock(world.getBlockState(blockPos).getBlock());
    }

    /**
     * @param blockPos the pos to test.
     * @return true if the given pos is a valid target position.
     */
    protected boolean isValidTargetPos(@Nullable BlockPos blockPos)
    {
        return blockPos != null && !badTargetPositions.contains(blockPos);
    }

    /**
     * @param block the block to test.
     * @return true if the given block is a valid block.
     */
    protected abstract boolean isValidTargetBlock(@Nonnull Block block);

    /**
     * @return true if the blockling can harvest the block at the target pos.
     */
    public boolean canHarvestTargetPos()
    {
        if (blockling.getEquipment().canHarvestBlockWithEquippedTools(getTargetBlockState()))
        {
            return true;
        }
        else if (blockling.getSkills().getSkill(GeneralSkills.AUTOSWITCH).isBought())
        {
            Pair<ItemStack, ItemStack> bestTools = blockling.getEquipment().findBestToolsToSwitchTo(BlocklingHand.BOTH, getToolType());

            if (ToolUtil.canToolHarvestBlock(bestTools.getKey(), getTargetBlockState()) || ToolUtil.canToolHarvestBlock(bestTools.getValue(), getTargetBlockState()))
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

    /**
     * @return true if the current target position is not null.
     */
    public boolean hasTarget()
    {
        return targetPos != null;
    }

    /**
     * @return the current target position.
     */
    @Nullable
    public BlockPos getTargetPos()
    {
        return targetPos;
    }

    /**
     * Sets the current target pos to the given target pos.
     * Sets the previous target pos to the old target pos.
     *
     * @param targetPos the new target pos.
     */
    protected void setTargetPos(@Nullable BlockPos targetPos)
    {
        setPreviousTargetPos(this.targetPos);

        this.targetPos = targetPos;
    }

    /**
     * @return true if the current target position is not null.
     */
    public boolean hasPrevTarget()
    {
        return prevTargetPos != null;
    }

    /**
     * @return the previous target position.
     */
    @Nullable
    public BlockPos getPrevTargetPos()
    {
        return prevTargetPos;
    }

    /**
     * Sets the previous target pos to the given target pos.
     * Resets any break progress at the old previous target pos if not null or the same.
     *
     * @param targetPos the new target pos.
     */
    private void setPreviousTargetPos(@Nullable BlockPos targetPos)
    {
        if (prevTargetPos != null && (targetPos == null || !targetPos.equals(prevTargetPos)))
        {
            world.destroyBlockProgress(blockling.getId(), prevTargetPos, -1);
        }

        prevTargetPos = targetPos;
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
        return targetPos != null ? world.getBlockState(targetPos) : null;
    }
}
