package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlockChunk;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.IHasTargetGoal;
import com.willr27.blocklings.goal.goals.target.BlocklingGatherTargetGoal;
import com.willr27.blocklings.task.BlocklingTasks;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Contains common behaviour shared between gathering goals.
 */
public abstract class BlocklingGatherGoal<T extends BlocklingGatherTargetGoal<?>> extends BlocklingGoal implements IHasTargetGoal<T>
{
    /**
     * The number of ticks between each recalc.
     */
    private static final int RECALC_INTERVAL = 10;

    /**
     * The number of ticks a bad path target needs elapse before it is removed.
     */
    private static final int BAD_PATH_TARGET_COOLDOWN_INTERVAL = 10 * 20;

    /**
     * Counts the number of ticks since the last recalc.
     */
    private int recalcCounter = 0;

    /**
     * The current pos to path to.
     */
    @Nullable
    private BlockPos pathTargetPos = blockling.blockPosition();

    /**
     * The current path to the target.
     */
    @Nullable
    protected Path path = null;

    /**
     * The map of block positions to ignore as they led to the blockling getting stuck and their cooldowns.
     */
    @Nonnull
    protected final Map<BlockChunk, Integer> badPathTargetChunks = new HashMap<>();

    /**
     * The distance the blockling had moved last check.
     */
    private float prevMoveDist = 0.0f;

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

        if (!getTargetGoal().hasTarget())
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        if (!getTargetGoal().hasTarget())
        {
            return false;
        }

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        setPathTargetPos(getPathTargetPos(), path);
    }

    @Override
    public void stop()
    {
        super.stop();

        setPathTargetPos(null, null);

        blockling.getActions().gather.stop();
    }

    @Override
    public void tick()
    {
        super.tick();

        // Tick to make sure isFinished() is only true for a single tick
        blockling.getActions().gather.tick(0.0f);

        boolean recalc = tickRecalc();

        // First try a normal recalc
        if (recalc)
        {
            recalcPath(false);
        }

        // If we are still stuck force a recalc
        if (isStuck() || (isInRangeOfPathTarget() && !isValidPathTargetPos(getPathTargetPos())))
        {
             recalcPath(true);
        }

        // If we are still stuck, give up and mark it as bad
        if (isStuck())
        {
            blockling.getActions().gather.stop();

            markPathTargetPosBad();
            getTargetGoal().markTargetBad();
        }
        else if (isInRangeOfPathTarget())
        {
            tickGather();
        }

        if (recalc)
        {
            recalcCounter = 0;
            prevMoveDist = blockling.moveDist;
        }
    }

    /**
     * Called every tick when in range of the path target pos.
     */
    protected void tickGather()
    {
        if (!hasMovedSinceLastRecalc())
        {
            blockling.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(getTargetGoal().getTargetPos().getX() + 0.5, getTargetGoal().getTargetPos().getY() + 0.5, getTargetGoal().getTargetPos().getZ() + 0.5));
        }
    }

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
     * Recalculates the path and path target pos.
     *
     * @param force forces the recalculation to take place.
     */
    protected abstract void recalcPath(boolean force);

    /**
     * Updates the cooldowns for the bad path target positions.
     * Removes them from the map if their cooldown has expired or they have changed.
     */
    public void updateBadPathTargetPositions()
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
     * @return true if the blockling has moved since the last recalc (within 0.01 of a block).
     */
    protected boolean hasMovedSinceLastRecalc()
    {
        return !blockling.isOnGround() || blockling.moveDist - prevMoveDist > 0.01f;
    }

    /**
     * @return true if the blockling is within range of the center of the path pos;
     */
    public boolean isInRangeOfPathTarget()
    {
        return isInRange(pathTargetPos);
    }

    /**
     * @return true if the blockling is within range of the center of the given block pos.
     */
    public boolean isInRange(@Nonnull BlockPos blockPos)
    {
        return isInRange(blockPos, getRangeSq());
    }

    /**
     * @return true if the blockling is within range of the center of the given block pos.
     */
    public boolean isInRange(@Nonnull BlockPos blockPos, float rangeSq)
    {
        return BlockUtil.distanceSq(blockling.blockPosition(), blockPos) <= rangeSq;
    }

    /**
     * @return the gathering range squared.
     */
    abstract float getRangeSq();

    /**
     * @return true if the blockling is stuck (i.e. not mining, not moving, not in range etc.).
     */
    public boolean isStuck()
    {
        return (!hasPath() || path.isDone() || !hasMovedSinceLastRecalc() || blockling.getNavigation().isStuck()) && (!hasPathTargetPos() || !isInRange(pathTargetPos));
    }

    /**
     * @param blockPos the block position to test.
     * @return true if the block position is a valid path target.
     */
    protected abstract boolean isValidPathTargetPos(@Nonnull BlockPos blockPos);

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
     * @param updateBlocklingPath whether to also set the blockling's path.
     */
    public void setPathTargetPos(@Nullable BlockPos blockPos, @Nullable Path pathToPos, boolean updateBlocklingPath)
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

        if (updateBlocklingPath)
        {
            blockling.getNavigation().moveTo(path, 1.0);
        }
    }

    /**
     * @return true if we have a path.
     */
    public boolean hasPath()
    {
        return path != null;
    }

    /**
     * Creates a path to the given block or a surrounding block.
     *
     * @param blockPos the pos to create a path to.
     * @return the path.
     */
    @Nullable
    protected Path createPath(@Nonnull BlockPos blockPos)
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
}
