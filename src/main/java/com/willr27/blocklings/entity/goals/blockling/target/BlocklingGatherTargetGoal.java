package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.entity.goals.blockling.BlocklingGatherGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains common behaviour between gathering target goals.
 *
 * @param <T> the type of the associated gather goal.
 */
public abstract class BlocklingGatherTargetGoal<T extends BlocklingGatherGoal<?>> extends BlocklingTargetGoal<T>
{
    /**
     * How many recalcs are called before a block is no longer marked bad.
     */
    private static final int RECALC_BAD_INTERVAL = 20;

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
     * A map of block positions to counts.
     * Used to determine which blocks to ignore as they have been deemed ungatherable.
     */
    @Nonnull
    private final Map<BlockPos, Integer> badBlockPositions = new HashMap<>();

    /**
     * @param goal The associated goal instance.
     */
    public BlocklingGatherTargetGoal(@Nonnull T goal)
    {
        super(goal);
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        if (!hasTarget())
        {
            return false;
        }

        return true;
    }

    @Override
    public void start()
    {
        recalcTarget();
    }

    @Override
    public void stop()
    {
        setTargetPos(null);
        setPreviousTargetPos(null);
    }

    @Override
    protected void recalc()
    {
        updateBadPositions();
    }

    @Override
    public void recalcTarget()
    {
        setTargetPos(findNextTargetPos());
    }

    /**
     * Increments the counters for each bad block pos.
     * Removes a position if the counter exceeds the interval or the number of bad block positions.
     */
    private void updateBadPositions()
    {
        Set<BlockPos> freshBlockPositions = new HashSet<>();

        badBlockPositions.forEach((blockPos, time) ->
        {
            if (time >= Math.min(RECALC_BAD_INTERVAL, Math.max(badBlockPositions.size(), 5)))
            {
                freshBlockPositions.add(blockPos);
            }

            badBlockPositions.put(blockPos, time + 1);
        });

        freshBlockPositions.forEach(badBlockPositions::remove);
    }

    /**
     * Finds the next target pos.
     *
     * @return a new target pos or null if none is found.
     */
    @Nullable
    abstract BlockPos findNextTargetPos();

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
        return blockPos != null && !badBlockPositions.containsKey(blockPos);
    }

    /**
     * @return true if the given block is a valid block.
     */
    abstract boolean isValidTargetBlock(@Nullable Block block);

    /**
     * Marks the all block positions bad.
     */
    abstract void markBad();

    /**
     * Marks the current target as bad, so it will be ignored temporarily when searching for a new target.
     */
    public void markTargetBad()
    {
        markPosBad(targetPos);
    }

    /**
     * Marks the given pos as bad, so it will be ignored temporarily when searching for a new target.
     */
    public void markPosBad(@Nonnull BlockPos blockPos)
    {
        badBlockPositions.put(blockPos, 0);
    }

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
     * Resets any break progress at the old previous target pos if not null.
     *
     * @param targetPos the new target pos.
     */
    private void setPreviousTargetPos(@Nullable BlockPos targetPos)
    {
        if (prevTargetPos != null)
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
