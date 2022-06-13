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

        boolean recalulatedTarget = tryRecalcTarget();

        if (!recalulatedTarget || !recalcPath(false) || isStuck())
        {
            if (!recalulatedTarget)
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

        checkForAndRemoveInvalidTargets();

        if (!tryRecalcTarget())
        {
            return false;
        }
        else if (isStuck())
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
     * Checks for and removes any invalid targets.
     */
    protected abstract void checkForAndRemoveInvalidTargets();

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
