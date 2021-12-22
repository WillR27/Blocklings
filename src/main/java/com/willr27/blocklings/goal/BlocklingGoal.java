package com.willr27.blocklings.goal;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.GoalSetStateMessage;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BlocklingGoal extends Goal
{
    public final UUID id;
    public final BlocklingEntity blockling;
    public final World world;
    public final BlocklingTasks tasks;

    public final List<GoalWhitelist> whitelists = new ArrayList<>();

    private State state = State.IDLE;

    public BlocklingGoal(UUID id, BlocklingEntity blockling, BlocklingTasks tasks)
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

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
       setState(state, true);
    }

    public void setState(State state, boolean sync)
    {
        this.state = state;

        if (sync)
        {
            NetworkHandler.sync(blockling.level, new GoalSetStateMessage(id, state, blockling.getId()));
        }
    }

    public enum State
    {
        DISABLED,
        IDLE,
        ACTIVE
    }
}
