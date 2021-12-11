package com.willr27.blocklings.entity.entities.blockling.action.actions;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.action.Action;

public class UnknownTargetAction extends Action
{
    public UnknownTargetAction(BlocklingEntity blockling, String key)
    {
        super(blockling, key);
    }

    public float percentThroughAction(int targetTicks)
    {
        return (float) elapsedTicks() / (float) targetTicks;
    }

    public float percentThroughActionSq(int targetTicks)
    {
        return (float) (elapsedTicks() * elapsedTicks()) / (float) (targetTicks * targetTicks);
    }
}
