package com.willr27.blocklings.entity.blockling.goal;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Contains common behaviour between target goals.
 *
 * @param <T> the type of the target.
 */
public abstract class BlocklingTargetGoal<T> extends BlocklingPathGoal
{
    /**
     * The current target (block position, entity etc.).
     */
    @Nullable
    private T target;

    /**
     * The previous target (block position, entity etc.).
     */
    @Nullable
    private T prevTarget;

    /**
     * The set of targets to ignore as they were recently deemed invalid.
     */
    @Nonnull
    public final Set<T> badTargets = new HashSet<>();

    /**
     * The last time in ticks that a recalculation occurred.
     */
    private int lastRecalcTime = 0;

    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingTargetGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        getFlags().add(Flag.TARGET);
        getFlags().add(Flag.LOOK);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        if (!isRecalcIntervalExceeded())
        {
            return false;
        }

        recalcTarget();

        boolean hasTarget = getTarget() != null;
        lastRecalcTime = blockling.getAge();

        // If we have successfully recalculated the target, recalculate the target position and path.
        if (hasTarget)
        {
            recalcPathTargetPosAndPath(false);
        }

        // If we have no target or cannot path to the target, then mark them as bad and return false.
        if (!hasTarget || (getPathTarget() != null && isStuck(true)))
        {
            // If we simply failed to find a target, then we can clear the bad targets and try again.
            if (!hasTarget)
            {
                badTargets.clear();
            }

            markPathTargetPosBad();
            setTarget(null);

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

        checkForAndHandleInvalidTargets();

        boolean recalculatedTarget = false;

        // If we have no target, or the target is invalid, or the recalculation interval has been exceeded, then recalculate the target.
        if (isRecalcIntervalExceeded() || getTarget() == null || !isTargetValid())
        {
            recalcTarget();

            recalculatedTarget = true;
            lastRecalcTime = blockling.tickCount;

            // If we still have no target, then return false.
            if (getTarget() == null || !isTargetValid())
            {
                return false;
            }
        }

        // For some reason, path finding will occasionally fail to find a (good) path to a valid target (I've seen it only do 2/3 of a path and set the target to the
        // target block anyway). So attempt to recalculate the path as the blockling might have changed position and this might allow the path to be found.
        if (getTarget() != null && getPathTarget() != null && isStuck(false) && !recalculatedTarget)
        {
            trySetPathTarget(getPathTarget().pos, null);
        }

        // If we have a target, but are stuck and haven't just recalculated the target, then mark the target as bad and return false.
        if (getTarget() != null && isStuck(false) && !recalculatedTarget)
        {
            markEntireTargetBad();
            markPathTargetPosBad();

            return false;
        }

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        updateBlocklingsPath();
    }

    @Override
    public void stop()
    {
        super.stop();

        setTarget(null);
    }

    /**
     * Recalculates and sets the current {@link #target}.
     */
    public abstract void recalcTarget();

    /**
     * Checks for and handles any invalid targets. This might involve marking the targets as bad
     * and removing them from a target list (e.g. removing ores from a vein).
     */
    protected abstract void checkForAndHandleInvalidTargets();

    /**
     * Marks the entire target bad.
     * This could be an entire vein in the case of mining, or tree for woodcutting.
     */
    public abstract void markEntireTargetBad();

    /**
     * Marks the current target as bad.
     * It will then be ignored until the goal starts again.
     */
    public void markTargetBad()
    {
        if (getTarget() != null)
        {
            markBad(getTarget());
            setTarget(null);
        }
    }

    /**
     * Marks the given target as bad.
     * It will then be ignored until the target goal starts again.
     *
     * @param target the target to mark as bad.
     */
    public void markBad(@Nonnull T target)
    {
        badTargets.add(target);
    }

    /**
     * @return whether the current target is valid or not.
     */
    public boolean isTargetValid()
    {
        return isValidTarget(getTarget());
    }

    /**
     * @param target the target to test.
     * @return true if the given target is a valid target.
     */
    public abstract boolean isValidTarget(@Nullable T target);

    /**
     * @return the number of ticks to delay between an attempt at recalculating the target.
     */
    public int getRecalcInterval()
    {
        return 5;
    }

    /**
     * @return true if enough time has passed for a recalc to occur.
     */
    public boolean isRecalcIntervalExceeded()
    {
        return blockling.tickCount - lastRecalcTime > getRecalcInterval();
    }

    /**
     * @return the current target.
     */
    @Nullable
    protected final T getTarget()
    {
        return target;
    }

    /**
     * Sets the current target to the given target.
     * Sets the previous target to the old target.
     *
     * @param target the new target.
     */
    protected void setTarget(@Nullable T target)
    {
        setPreviousTarget(this.target);

        this.target = target;
    }

    /**
     * @return the previous target.
     */
    @Nullable
    public final T getPrevTarget()
    {
        return prevTarget;
    }

    /**
     * Sets the previous target to the given target.
     *
     * @param target the new target.
     */
    protected void setPreviousTarget(@Nullable T target)
    {
        prevTarget = target;
    }
}
