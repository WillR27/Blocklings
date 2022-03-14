package com.willr27.blocklings.goal;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.messages.GoalStateMessage;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BlocklingGoal extends Goal
{
    /**
     * The id associated with the goal's task.
     */
    @Nonnull
    public final UUID id;

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
     * The whitelists associated with the goal.
     */
    @Nonnull
    public final List<GoalWhitelist> whitelists = new ArrayList<>();

    /**
     * The current state of the goal.
     */
    @Nonnull
    private State state = State.IDLE;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        this.id = id;
        this.blockling = blockling;
        this.world = blockling.level;
        this.tasks = tasks;
    }

    @Override
    public boolean canUse()
    {
        if (state == State.DISABLED)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (state == State.DISABLED)
        {
            return false;
        }

        return true;
    }

    @Override
    public void start()
    {
        if (state != State.DISABLED)
        {
            setState(State.ACTIVE);
        }
    }

    @Override
    public void stop()
    {
        if (state != State.DISABLED)
        {
            setState(State.IDLE);
        }
    }

    /**
     * @return the current state of the goal.
     */
    @Nonnull
    public State getState()
    {
        return state;
    }

    /**
     * Sets the current state and syncs to the client/server.
     *
     * @param state the new state.
     */
    public void setState(@Nonnull State state)
    {
       setState(state, true);
    }

    /**
     * Sets the current state and might sync to the client/server.
     *
     * @param state the new state.
     * @param sync whether to sync to the client/server.
     */
    public void setState(@Nonnull State state, boolean sync)
    {
        this.state = state;

        if (sync)
        {
            new GoalStateMessage(blockling, id, state).sync();
        }
    }

    /**
     * @return true if the blockling is within range of the center of the given block pos.
     */
    public boolean isInRange(@Nonnull BlockPos blockPos, float rangeSq)
    {
        return BlockUtil.distanceSq(blockling.blockPosition(), blockPos) <= rangeSq;
    }

    /**
     * The state of a goal.
     */
    public enum State
    {
        /**
         * The goal is currently disabled and won't be run.
         */
        DISABLED,

        /**
         * The goal is currently enabled but is not running.
         */
        IDLE,

        /**
         * The goal is currently enabled and is running.
         */
        ACTIVE
    }
}
