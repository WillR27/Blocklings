package com.willr27.blocklings.goal;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class BlocklingTargetGoal<T extends BlocklingGoal> extends Goal
{
    /**
     * The number of ticks between each recalc.
     */
    private static final int RECALC_INTERVAL = 20;

    /**
     * The associated goal instance.
     */
    @Nonnull
    public final T goal;

    /**
     * The blockling.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The world.
     */
    @Nonnull
    public final World world;

    /**
     * The blockling tasks.
     */
    @Nonnull
    public final BlocklingTasks tasks;

    /**
     * Counts the number of ticks since the last recalc.
     */
    private int recalcCounter = 0;

    /**
     * @param goal the associate goal instance.
     */
    public BlocklingTargetGoal(@Nonnull T goal)
    {
        this.goal = goal;
        this.tasks = goal.tasks;
        this.blockling = goal.blockling;
        this.world = blockling.level;
    }

    @Override
    public boolean canUse()
    {
        if (!tickRecalc())
        {
            return false;
        }

        recalc();

        if (goal.getState() == BlocklingGoal.State.DISABLED)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (goal.getState() == BlocklingGoal.State.DISABLED)
        {
            return false;
        }

        return true;
    }

    @Override
    public void tick()
    {
        if (tickRecalc())
        {
            recalc();

            if (!isTargetValid())
            {
                recalcTarget();
            }
        }
    }

    /**
     * Recalculates the state of the target goal.
     */
    protected void recalc()
    {

    }

    /**
     * Recalculates the current target.
     */
    public void recalcTarget()
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
     * Returns whether the current target is valid or not.
     */
    protected boolean isTargetValid()
    {
        return true;
    }
}
