package com.willr27.blocklings.skills;

import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.SkillTryBuyMessage;
import com.willr27.blocklings.network.messages.SkillStateMessage;
import com.willr27.blocklings.skills.info.SkillInfo;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public class Skill
{
    public final SkillInfo info;
    public final SkillGroup group;

    private State state;

    public Skill(SkillInfo info, SkillGroup group)
    {
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

        if (info.requirements.skillPoints > group.blockling.getStats().skillPoints.getInt())
        {
            return false;
        }

        if (info.requirements.levels.keySet().stream().filter(level -> group.blockling.getStats().getLevel(level).getInt() < info.requirements.levels.get(level)).findAny().isPresent())
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
            NetworkHandler.sync(group.blockling.level, new SkillTryBuyMessage(this, group.blockling.getId()));
        }

        return true;
    }

    private void buy()
    {
        group.blockling.getStats().skillPoints.incBaseValue(-info.requirements.skillPoints);

        setState(State.BOUGHT, false);
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
            NetworkHandler.sync(group.blockling.level, new SkillStateMessage(this, group.blockling.getId()));
        }

        if (state == State.BOUGHT)
        {
            for (Skill child : children())
            {
                if (child.state == State.BOUGHT || child.state == State.UNLOCKED)
                {
                    continue;
                }

                if (!child.parents().stream().filter(skill -> skill.state == State.LOCKED).findAny().isPresent())
                {
                    child.setState(State.UNLOCKED, sync);
                }
            }
        }
    }

    public List<Skill> children()
    {
        return group.getSkills().stream().filter(skill -> skill.info.relationships.parents.contains(info)).collect(Collectors.toList());
    }

    public List<Skill> parents()
    {
        return group.getSkills().stream().filter(skill -> info.relationships.parents.contains(skill.info)).collect(Collectors.toList());
    }

    public boolean areParentsBought()
    {
        return !parents().stream().filter(skill -> skill.getState() != State.BOUGHT).findAny().isPresent();
    }

    public List<Skill> conflicts()
    {
        return group.getSkills().stream().filter(skill -> skill.info.relationships.conflicts.contains(skill)).collect(Collectors.toList());
    }

    public boolean hasConflict()
    {
        return conflicts().stream().filter(skill -> skill.getState() == State.BOUGHT).findAny().isPresent();
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
