package com.willr27.blocklings.entity.blockling.goal;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.util.BlockUtil;
import com.willr27.blocklings.util.PathUtil;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A goal that has less than trivial pathing needs. Even though this class does contain a target {@link BlockPos}
 * and a {@link Path} object, it doesn't mean they always need to be set for the goal to start/stop/tick etc. A goal
 * might already be where they need to be and not need to path to anything.
 */
public abstract class BlocklingPathGoal extends BlocklingGoal
{
    /**
     * The number of elapsed ticks a bad path target needs before it is no longer regarded as a bad path target.
     */
    private static final int BAD_PATH_TARGET_COOLDOWN_INTERVAL = 10 * 20;

    /**
     * The block positions that caused the blockling to get stuck mapped to their current remaining cooldowns.
     */
    @Nonnull
    protected final Map<BlockChunk, Integer> badPathTargetChunksToCooldowns = new HashMap<>();

    /**
     * The current {@link BlockPos} we are trying to path to and the associated {@link Path} to get there. This is not necessarily
     * the same as the {@link PathTarget#path} object's target position. E.g. when chopping a tree, the path target position is the
     * bottom block in the trunk, but the {@link PathTarget#path} object's target position is the ground block adjacent to the trunk.
     * It is up to the derived class to enforce whichever behaviour is desired by overriding {@link #shouldEnforcePathTargetPosEqualsPathsTargetPos()}.
     */
    @Nullable
    private PathTarget pathTarget = null;

    /**
     * The {@link net.minecraft.entity.Entity#moveDist} the blockling had when the path was last recalculated.
     */
    private float moveDistanceAtLastPathRecalc = 0.0f;

    /**
     * The last time in ticks that the path was recalculated, successfully or not.
     */
    private int lastPathRecalcAttemptTime = 0;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingPathGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        getFlags().add(Flag.MOVE);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        updateCooldownsForBadPathTargetPositions();

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        updateCooldownsForBadPathTargetPositions();

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
        updateBlocklingsPath();

        moveDistanceAtLastPathRecalc = 0.0f;
    }

    @Override
    public final void tick()
    {
        super.tick();

        // See if enough time has passed since the last path recalculation attempt.
        boolean wasRecalcIntervalExceeded = isPathRecalcIntervalExceeded();
        boolean didRecalcOccur = false;

        if (wasRecalcIntervalExceeded)
        {
            // We don't want to force a recalculation as the derived class might not need to. E.g. if a blockling is pathing to ores,
            // it might actually want to use the recalculation to try to improve the current path, but not necessarily replace the current
            // one if it is still valid.
            recalcPathTargetPosAndPath(false);

            didRecalcOccur = true;
        }

        // If the blockling is stuck, or the path target is no longer valid, mark the target pos as bad and force the path to be recalculated.
        if (isStuck(false) || (getPathTarget() != null && !isValidPathTargetPos(getPathTarget().pos)))
        {
            markPathTargetPosBad();
            recalcPathTargetPosAndPath(true);

            didRecalcOccur = true;
        }

        updateBlocklingsPath();
        tickGoal();

        // Only update the move distance when the interval has been exceeded. Otherwise, we end up in an infinite loop of stuckness where the
        // every tick the blockling appears not to move.
        if (wasRecalcIntervalExceeded)
        {
            moveDistanceAtLastPathRecalc = blockling.moveDist;
        }

        if (didRecalcOccur)
        {
            lastPathRecalcAttemptTime = blockling.tickCount;
        }
    }

    /**
     * Used to perform the regular {@link #tick()} of a {@link net.minecraft.entity.ai.goal.Goal}. Is always called after
     * any potential path recalculations are done, but does not guarantee {@link #pathTarget} is not null.
     */
    protected abstract void tickGoal();

    /**
     * Recalculates the {@link #pathTarget}.
     *
     * @param force forces the recalculation to take place.
     */
    protected abstract void recalcPathTargetPosAndPath(boolean force);

    /**
     * Updates the cooldowns for the bad path target positions.
     * Removes them from the map if their cooldown has expired, or they have changed.
     */
    private void updateCooldownsForBadPathTargetPositions()
    {
        Set<BlockChunk> blockChunksToRemove = new HashSet<>();

        badPathTargetChunksToCooldowns.forEach((blockChunk, cooldown) ->
        {
            cooldown--;

            if (cooldown <= 0 || blockChunk.hasChanged())
            {
                blockChunksToRemove.add(blockChunk);
            }

            badPathTargetChunksToCooldowns.put(blockChunk, cooldown);
        });

        blockChunksToRemove.forEach(badPathTargetChunksToCooldowns::remove);
    }

    /**
     * @return true if the blockling has moved since the last path recalculation.
     */
    protected boolean hasMovedSinceLastPathRecalc()
    {
        return !blockling.isOnGround() || blockling.moveDist - moveDistanceAtLastPathRecalc > (0.001f * getPathRecalcInterval());
    }

    /**
     * @return true if the blockling is stuck (e.g. has a path target but is not moving, not in range, can't move etc.).
     */
    public boolean isStuck(boolean ignoreHasMovedSinceLastPathRecalc)
    {
        return getPathTarget() != null && !isInRangeOfPathTargetPos() && (getPathTarget().path.isDone() || (!ignoreHasMovedSinceLastPathRecalc && !hasMovedSinceLastPathRecalc()) || blockling.getNavigation().isStuck());
    }

    /**
     * @return true if the given {@link BlockPos} is a bad {@link #pathTarget} (i.e. shouldn't be used).
     */
    public boolean isBadPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return badPathTargetChunksToCooldowns.get(new BlockChunk(blockPos, world)) != null;
    }

    /**
     * Marks the current {@link PathTarget#pos} as a bad path target pos.
     */
    public void markPathTargetPosBad()
    {
        if (getPathTarget() != null)
        {
            markPathTargetPosBad(getPathTarget().pos);
        }
    }

    /**
     * Marks the given {@link BlockPos} as a bad {@link PathTarget#pos}.
     *
     * @param blockPos the {@link BlockPos} to mark as bad.
     */
    public void markPathTargetPosBad(@Nonnull BlockPos blockPos)
    {
        badPathTargetChunksToCooldowns.put(new BlockChunk(blockPos, world), BAD_PATH_TARGET_COOLDOWN_INTERVAL);
    }

    /**
     * Checks whether the given {@link BlockPos} is a valid path target. E.g. when mining, the path target {@link BlockPos}
     * is only valid if it is part of the vein of ore.
     *
     * @param blockPos the {@link BlockPos} to test.
     * @return true if the {@link BlockPos} is a valid {@link #pathTarget}.
     */
    protected abstract boolean isValidPathTargetPos(@Nonnull BlockPos blockPos);

    /**
     * @return true if the blockling is within range of the center of the {@link PathTarget#pos} or if {@link #pathTarget} is null.
     */
    public boolean isInRangeOfPathTargetPos()
    {
        if (getPathTarget() == null)
        {
            return true;
        }

        return BlockUtil.distanceSq(blockling.blockPosition(), getPathTarget().pos) <= getPathTargetRangeSq();
    }

    /**
     * @return the current {@link #pathTarget}.
     */
    @Nullable
    public PathTarget getPathTarget()
    {
        return pathTarget;
    }

    /**
     * Sets the {@link #pathTarget}. If no {@code pathToPathTargetPos} is given, a new {@link Path} will attempt to be created.
     * Note: The given {@code pathToPathTargetPos} might be overwritten if {@link #shouldEnforcePathTargetPosEqualsPathsTargetPos()}
     * returns true and the {@link Path#getTarget()} does not match the given {@code pathTargetPos}.
     *
     * @param pathTargetPos the new {@link BlockPos} to path to.
     * @param pathToPathTargetPos an optional {@link Path} to {@code pathTargetPos}.
     * @return true if the {@link #pathTarget} was set to something other than null.
     */
    public boolean trySetPathTarget(@Nullable BlockPos pathTargetPos, @Nullable Path pathToPathTargetPos)
    {
        // If the path target pos is null or invalid, clear the path target and path.
        if (pathTargetPos == null || !isValidPathTargetPos(pathTargetPos))
        {
            pathTarget = null;

            return false;
        }

        // If the given path is not null, and we don't need to enforce the path target pos equals the paths target pos,
        // or we do, and they are equal, then set the path target to the given arguments.
        if (pathToPathTargetPos != null && (!shouldEnforcePathTargetPosEqualsPathsTargetPos() || pathToPathTargetPos.getTarget().equals(pathTargetPos)))
        {
            pathTarget = new PathTarget(pathTargetPos, pathToPathTargetPos);

            return true;
        }

        // If the given path is not valid, but the path target pos is, try to create a new path.
        pathToPathTargetPos = PathUtil.createPathTo(blockling, pathTargetPos, getPathTargetRangeSq(), false);

        // If the path is still null, clear the path target and path.
        if (pathToPathTargetPos == null)
        {
            pathTarget = null;

            return false;
        }

        // Again, if we don't need to enforce the path target pos equals the paths target pos, or we do, and they are equal,
        // then set the path target to the given target pos and newly created path.
        if ((!shouldEnforcePathTargetPosEqualsPathsTargetPos() || pathToPathTargetPos.getTarget().equals(pathTargetPos)))
        {
            pathTarget = new PathTarget(pathTargetPos, pathToPathTargetPos);

            return true;
        }

        return false;
    }

    /**
     * Sets the blockling's navigation to move to the {@link #pathTarget}.
     */
    public void updateBlocklingsPath()
    {
        Path path = getPathTarget() != null ? getPathTarget().path : null;

        if (blockling.getNavigation().getPath() != path)
        {
            blockling.getNavigation().moveTo(path, 1.0);
        }
    }

    /**
     * @return the number of ticks to delay between an attempt at recalculating the path to the target.
     */
    public int getPathRecalcInterval()
    {
        return 10;
    }

    /**
     * @return true if enough time has passed for a path recalculation to occur.
     */
    public boolean isPathRecalcIntervalExceeded()
    {
        return blockling.tickCount - lastPathRecalcAttemptTime > getPathRecalcInterval();
    }

    /**
     * @return whether the {@link PathTarget#pos} must be equal to the {@link PathTarget#path} object's target position.
     */
    public boolean shouldEnforcePathTargetPosEqualsPathsTargetPos()
    {
        return false;
    }

    /**
     * @return the max distance squared a blockling can be to be in range of their {@link #pathTarget}.
     */
    public abstract float getPathTargetRangeSq();

    /**
     * Wraps a target {@link BlockPos} and a {@link Path} to that target.
     */
    public static class PathTarget
    {
        @Nonnull
        public final BlockPos pos;

        @Nonnull
        public final Path path;

        /**
         * @param pos the target {@link BlockPos}.
         * @param path the {@link Path} to the target {@link BlockPos}.
         */
        public PathTarget(@Nonnull BlockPos pos, @Nonnull Path path)
        {
            this.pos = pos;
            this.path = path;
        }
    }
}
