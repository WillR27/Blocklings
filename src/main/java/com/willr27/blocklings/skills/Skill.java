package com.willr27.blocklings.skills;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.SkillTryBuyMessage;
import com.willr27.blocklings.network.messages.SkillStateMessage;
import com.willr27.blocklings.skills.info.SkillInfo;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public class Skill
{
    public final BlocklingEntity blockling;
    public final SkillInfo info;
    public final SkillGroup group;

    private State state;

    public Skill(SkillInfo info, SkillGroup group)
    {
        this.blockling = group.blockling;
        this.info = info;
        this.group = group;
        this.state = info.defaults.defaultState;
    }

    public boolean canBuy()
    {
        if (state != State.UNLOCKED)
        {
            return false;
        }

        if (info.requirements.levels.keySet().stream().anyMatch(level -> group.blockling.getStats().getLevelAttribute(level).getValue() < info.requirements.levels.get(level)))
        {
            return false;
        }

        if (!areParentsBought())
        {
            return false;
        }

        if (hasConflict())
        {
            return false;
        }

        return true;
    }

    public boolean tryBuy()
    {
        return tryBuy(true);
    }

    public boolean tryBuy(boolean sync)
    {
        if (!canBuy())
        {
            return false;
        }

        if (!info.callbacks.onBuy.apply(this))
        {
            return false;
        }

        buy();

        if (sync)
        {
            new SkillTryBuyMessage(blockling, this).sync();
        }

        return true;
    }

    private void buy()
    {
        setState(State.BOUGHT, false);
    }

    public boolean isBought()
    {
        return state == State.BOUGHT;
    }

    public State getState()
    {
        return this.state;
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
            new SkillStateMessage(blockling, this).sync();
        }

        if (state == State.BOUGHT)
        {
            for (Skill child : children())
            {
                if (child.state == State.BOUGHT || child.state == State.UNLOCKED)
                {
                    continue;
                }

                if (child.parents().stream().noneMatch(skill -> skill.state == State.LOCKED))
                {
                    child.setState(State.UNLOCKED, sync);
                }
            }
        }
    }

    public List<Skill> children()
    {
        return group.getSkills().stream().filter(skill -> skill.info.parents().contains(info)).collect(Collectors.toList());
    }

    public List<Skill> parents()
    {
        return group.getSkills().stream().filter(skill -> info.parents().contains(skill.info)).collect(Collectors.toList());
    }

    public boolean areParentsBought()
    {
        return parents().stream().noneMatch(skill -> skill.getState() != State.BOUGHT);
    }

    public List<Skill> conflicts()
    {
        return group.getSkills().stream().filter(skill -> info.conflicts().contains(skill.info)).collect(Collectors.toList());
    }

    public boolean hasConflict()
    {
        return conflicts().stream().anyMatch(skill -> skill.getState() == State.BOUGHT);
    }

    public enum Type
    {
        STAT(0),
        AI(1),
        UTILITY(2),
        OTHER(3);

        public final int textureX;

        Type(int textureX)
        {
            this.textureX = textureX;
        }
    }

    public enum State
    {
        LOCKED(0x343434),
        UNLOCKED(0xf4f4f4),
        BOUGHT(0xffc409);

        public final Color colour;

        State(int colour)
        {
            this.colour = new Color(colour);
        }
    }
}
