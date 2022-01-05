package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingGatherTargetGoal;
import com.willr27.blocklings.entity.goals.blockling.target.IHasTargetGoal;
import com.willr27.blocklings.goal.BlocklingGoal;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public abstract class BlocklingGatherGoal<T extends BlocklingGatherTargetGoal<?>> extends BlocklingGoal implements IHasTargetGoal<T>
{
    /**
     * The number of ticks between each recalc.
     */
    private static final int RECALC_INTERVAL = 20;

    /**
     * Counts the number of ticks since the last recalc.
     */
    private int recalcCounter = 0;

    /**
     * The current pos to path to.
     */
    @Nonnull
    private BlockPos pathTargetPos = blockling.blockPosition();

    /**
     * The current path to the target.
     */
    @Nullable
    protected Path path;

    /**
     * The distance the blockling had moved last check.
     */
    private float prevMoveDist = 0.0f;

    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingGatherGoal(UUID id, BlocklingEntity blockling, BlocklingTasks tasks)
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

        if (!canHarvestTargetPos())
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

        if (!canHarvestTargetPos())
        {
            return false;
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

        blockling.getNavigation().stop();
        blockling.getActions().gather.stop();
    }

    @Override
    public void tick()
    {
        super.tick();

        // Tick to make sure isFinished() is only true for a single tick
        blockling.getActions().gather.tick(0.0f);

        if (isInRangeOfPathTarget())
        {
            tickGather();
        }

        if (tickRecalc())
        {
            recalc();

            recalcCounter = RECALC_INTERVAL;
            prevMoveDist = blockling.moveDist;
        }
    }

    /**
     * Called every tick when in range of the target pos.
     */
    protected void tickGather()
    {
        blockling.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(getPathTargetPos().getX() + 0.5, getPathTargetPos().getY() + 0.5, getPathTargetPos().getZ() + 0.5));
    }

    /**
     * Recalculates the current state of the goal.
     */
    protected void recalc()
    {

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
     * @return true if the blockling has moved since the last recalc (within 0.01 of a block).
     */
    protected boolean hasMovedSinceLastRecalc()
    {
        return !blockling.isOnGround() || blockling.moveDist - prevMoveDist > 0.01f;
    }

    /**
     * @return true if the blockling can harvest the block at the target pos.
     */
    public boolean canHarvestTargetPos()
    {
        return blockling.getEquipment().canHarvestBlockWithEquippedTools(world.getBlockState(getTargetGoal().getTargetPos()));
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
    public  boolean isInRange(@Nonnull BlockPos blockPos, float rangeSq)
    {
        return EntityUtil.isInRange(blockling, blockPos, rangeSq);
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
        return (path == null || path.isDone() || !hasMovedSinceLastRecalc() || blockling.getNavigation().isStuck()) && !isInRange(pathTargetPos);
    }

    /**
     * @return the current pos to path to.
     */
    @Nonnull
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
    protected void setPathTargetPos(@Nonnull BlockPos blockPos, Path pathToPos)
    {
        pathTargetPos = blockPos;

        if (pathToPos != null)
        {
            path = pathToPos;
        }
        else
        {
            Path newPath = blockling.getNavigation().createPath(pathTargetPos, -1);

            if (newPath != null)
            {
                path = newPath;
            }
        }

        blockling.getNavigation().moveTo(path, 1.0);
    }
}
