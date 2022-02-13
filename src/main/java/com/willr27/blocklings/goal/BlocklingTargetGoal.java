package com.willr27.blocklings.goal;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public abstract class BlocklingTargetGoal<T extends BlocklingGoal> extends Goal
{
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
     * @param goal the associated goal instance.
     */
    public BlocklingTargetGoal(@Nonnull T goal)
    {
        this.goal = goal;
        this.tasks = goal.tasks;
        this.blockling = goal.blockling;
        this.world = blockling.level;

        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse()
    {
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

    }

    /**
     * Returns whether the current target is valid or not.
     */
    protected abstract boolean isTargetValid();

    /**
     * @return true if the target goal currently has a target.
     */
    protected abstract boolean hasTarget();
}
