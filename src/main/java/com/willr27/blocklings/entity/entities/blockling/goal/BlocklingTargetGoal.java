package com.willr27.blocklings.entity.entities.blockling.goal;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;

public abstract class BlocklingTargetGoal extends Goal
{
    public final BlocklingGoal goal;
    public final BlocklingTasks goals;
    public final BlocklingEntity blockling;
    public final World world;

    private int recalc = 0;

    /**
     * The number of ticks between each attempted recalculation of the target.
     */
    private final int recalcInterval = 20;

    public BlocklingTargetGoal(BlocklingGoal goal)
    {
        this.goal = goal;
        this.goals = goal.tasks;
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
        if (tickRecalc() && !isTargetValid())
        {
            recalcTarget();
        }
    }

    /**
     * Returns whether the current target is valid or not
     */
    protected boolean isTargetValid()
    {
        return true;
    }

    /**
     * Called when recalc >= recalcInterval
     */
    protected void recalc()
    {

    }

    /**
     * Called when recalc >= recalcInterval and the target is invalid
     */
    protected void recalcTarget()
    {

    }

    public void forceRecalc()
    {
        recalc = recalcInterval;
    }

    private boolean tickRecalc()
    {
        recalc++;

        if (recalc < recalcInterval)
        {
            return false;
        }
        else
        {
            recalc();

            recalc = 0;
        }

        return true;
    }
}
