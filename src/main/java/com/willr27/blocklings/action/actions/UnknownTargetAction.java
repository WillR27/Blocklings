package com.willr27.blocklings.action.actions;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.action.Action;

public class UnknownTargetAction extends Action
{
    public UnknownTargetAction(BlocklingEntity blockling, String key)
    {
        super(blockling, key);
    }

    public float percentThroughAction(int targetTicks)
    {
        return (float) count() / (float) targetTicks;
    }

    public float percentThroughActionSq(int targetTicks)
    {
        return (float) (count() * count()) / (float) (targetTicks * targetTicks);
    }
}
