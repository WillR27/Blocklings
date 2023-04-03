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

        boolean recalculatedTarget = tryRecalcTarget();
        lastRecalcTime = blockling.getAge();

        if (!recalculatedTarget || !recalcPath(false) || isStuck())
        {
            if (!recalculatedTarget)
            {
                badTargets.clear();
            }

            markEntireTargetBad();
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

        if (isRecalcIntervalExceeded() || !hasTarget() || !isTargetValid())
        {
            recalculatedTarget = true;
            lastRecalcTime = blockling.tickCount;

            if (!tryRecalcTarget())
            {
                return false;
            }
        }

        if (hasTarget() && isStuck() && !recalculatedTarget)
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
    }

    @Override
    public void stop()
    {
        super.stop();

        setTarget(null);
    }

    /**
     * Recalculates the current target.
     *
     * @return true if a target was found.
     */
    public abstract boolean tryRecalcTarget();

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
        if (hasTarget())
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
     * @return true if the goal currently has a target.
     */
    public final boolean hasTarget()
    {
        return target != null;
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
     * @return true if the goal has a previous target.
     */
    public final boolean hasPrevTarget()
    {
        return prevTarget != null;
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
