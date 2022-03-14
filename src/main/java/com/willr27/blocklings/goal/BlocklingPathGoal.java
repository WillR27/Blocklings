package com.willr27.blocklings.goal;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A goal that has less than trivial pathing needs.
 */
public abstract class BlocklingPathGoal extends BlocklingGoal
{
    /**
     * The number of ticks between each recalc.
     * Used to determine how often we want to recalculate the path.
     */
    private static final int RECALC_INTERVAL = 10;

    /**
     * The number of ticks a bad path target needs elapse before it is removed.
     */
    private static final int BAD_PATH_TARGET_COOLDOWN_INTERVAL = 10 * 20;

    /**
     * The map of block positions to ignore as they led to the blockling getting stuck to their cooldowns.
     */
    @Nonnull
    protected final Map<BlockChunk, Integer> badPathTargetChunks = new HashMap<>();

    /**
     * The current pos to path to.
     * This is not necessarily the same as the path object's target position.
     */
    @Nullable
    private BlockPos pathTargetPos = null;

    /**
     * The current path to the target.
     */
    @Nullable
    protected Path path = null;

    /**
     * The distance the blockling had moved last check.
     * This is used to determine if the blockling is moving.
     */
    private float prevMoveDist = 0.0f;

    /**
     * Counts the number of ticks since the last recalc.
     * Used to determine when we want to recalculate the current path.
     */
    private int recalcCounter = 0;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingPathGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        getFlags().add(Flag.MOVE);
        getFlags().add(Flag.JUMP);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        updateBadPathTargetPositions();

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        updateBadPathTargetPositions();

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        moveBlocklingToPath();
    }

    @Override
    public void stop()
    {
        super.stop();

        prevMoveDist = 0.0f;
    }

    @Override
    public final void tick()
    {
        super.tick();

        boolean shouldRecalc = tickRecalc();

        if (shouldRecalc)
        {
            recalcPath(false);
        }

        if (isStuck() || (isInRangeOfPathTarget() && !isValidPathTargetPos(getPathTargetPos())))
        {
            recalcPath(true);
        }

        if (isStuck())
        {
            markPathTargetPosBad();
        }

        tickGoal();

        if (shouldRecalc)
        {
            prevMoveDist = blockling.moveDist;
        }
    }

    /**
     * Called after trying to recalculate the current path.
     * Used to perform the regular tick() of a goal.
     */
    protected abstract void tickGoal();

    /**
     * Increments the recalc counter and checks if it has reached the recalc interval.
     *
     * @return true if recalc counter has reached the recalc interval.
     */
    private boolean tickRecalc()
    {
        recalcCounter++;

        if (recalcCounter < RECALC_INTERVAL)
        {
            return false;
        }
        else
        {
            recalcCounter = 0;
        }

        return true;
    }

    /**
     * Updates the cooldowns for the bad path target positions.
     * Removes them from the map if their cooldown has expired, or they have changed.
     */
    private void updateBadPathTargetPositions()
    {
        Set<BlockChunk> blockChunksToRemove = new HashSet<>();

        badPathTargetChunks.forEach((blockChunk, cooldown) ->
        {
            cooldown--;

            if (cooldown <= 0 || blockChunk.hasChanged())
            {
                blockChunksToRemove.add(blockChunk);
            }

            badPathTargetChunks.put(blockChunk, cooldown);
        });

        blockChunksToRemove.forEach(badPathTargetChunks::remove);
    }

    /**
     * Recalculates the path and path target pos.
     *
     * @param force forces the recalculation to take place.
     */
    protected abstract void recalcPath(boolean force);

    /**
     * @return true if the blockling has moved since the last recalc (within 0.01 of a block).
     */
    protected boolean hasMovedSinceLastRecalc()
    {
        return !blockling.isOnGround() || blockling.moveDist - prevMoveDist > 0.01f;
    }

    /**
     * @return true if the blockling is stuck (e.g. not moving, not in range, can't move etc.).
     */
    public boolean isStuck()
    {
        return (!hasPathTargetPos() || !isInRangeOfPathTarget()) && (!hasPath() || path.isDone() || !hasMovedSinceLastRecalc() || blockling.getNavigation().isStuck());
    }

    /**
     * @return true if the given block pos is a bad target path pos.
     */
    public boolean isBadPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return badPathTargetChunks.get(new BlockChunk(blockPos, world)) != null;
    }

    /**
     * Marks the path target pos as a bad path target pos.
     */
    public void markPathTargetPosBad()
    {
        if (hasPathTargetPos())
        {
            markPathTargetPosBad(pathTargetPos);
        }
    }

    /**
     * Marks the given block pos as a bad path target pos.
     *
     * @param blockPos the block pos to mark as bad.
     */
    public void markPathTargetPosBad(@Nonnull BlockPos blockPos)
    {
        badPathTargetChunks.put(new BlockChunk(blockPos, world), BAD_PATH_TARGET_COOLDOWN_INTERVAL);
    }

    /**
     * @param blockPos the block position to test.
     * @return true if the block position is a valid path target.
     */
    protected abstract boolean isValidPathTargetPos(@Nonnull BlockPos blockPos);

    /**
     * @return true if the blockling is within range of the center of the path pos;
     */
    public boolean isInRangeOfPathTarget()
    {
        return BlockUtil.distanceSq(blockling.blockPosition(), pathTargetPos) <= getRangeSq();
    }

    /**
     * @return true if we have a path target position.
     */
    public boolean hasPathTargetPos()
    {
        return pathTargetPos != null;
    }

    /**
     * @return the current pos to path to.
     */
    @Nullable
    public BlockPos getPathTargetPos()
    {
        return pathTargetPos;
    }

    /**
     * @return true if we have a path.
     */
    public boolean hasPath()
    {
        return path != null;
    }

    /**
     * @return the current path.
     */
    @Nullable
    public Path getPath()
    {
        return path;
    }

    /**
     * Sets the current pos to path to and recalculates the path if none is given.
     *
     * @param blockPos the new pos to path to.
     * @param pathToPos an optional path to the given pos.
     */
    public void setPathTargetPos(@Nullable BlockPos blockPos, @Nullable Path pathToPos)
    {
        setPathTargetPos(blockPos, pathToPos, true);
    }

    /**
     * Sets the current pos to path to and recalculates the path if none is given.
     *
     * @param blockPos the new pos to path to.
     * @param pathToPos an optional path to the given pos.
     * @param moveBlocklingToPath whether to also make the blockling move to the path.
     */
    public void setPathTargetPos(@Nullable BlockPos blockPos, @Nullable Path pathToPos, boolean moveBlocklingToPath)
    {
        pathTargetPos = blockPos;
        path = pathToPos;

        if (hasPathTargetPos())
        {
            Path newPath = EntityUtil.createPathTo(blockling, pathTargetPos, getRangeSq());

            if (newPath != null)
            {
                path = newPath;
            }
        }

        if (moveBlocklingToPath)
        {
            moveBlocklingToPath();
        }
    }

    /**
     * Sets the blockling's navigation to move to the current path.
     */
    public void moveBlocklingToPath()
    {
        blockling.getNavigation().moveTo(path, 1.0);
    }

    /**
     * @return the range a blockling can reach the path target pos squared.
     */
    public abstract float getRangeSq();
}
