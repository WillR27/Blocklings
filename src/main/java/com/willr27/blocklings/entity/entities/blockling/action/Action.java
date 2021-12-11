package com.willr27.blocklings.entity.entities.blockling.action;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.attribute.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class Action
{
    public final BlocklingEntity blockling;

    protected final Attribute elapsedTicks;

    public Action(BlocklingEntity blockling, String key)
    {
        this.blockling = blockling;

        elapsedTicks = blockling.getStats().createAttribute(key + "_action", -1.0f);

    }

    /**
     * Starts the action if it is not already being performed.
     * @return Returns true if the action was started.
     */
    public boolean tryStart()
    {
        if (isRunning())
        {
            return false;
        }
        else
        {
            start();

            return true;
        }
    }

    public void start()
    {
        elapsedTicks.setBaseValue(0.0f);
    }

    public void tick()
    {
        if (isRunning())
        {
            elapsedTicks.incBaseValue(1.0f);
        }
    }

    public void stop()
    {
        if (isRunning())
        {
            elapsedTicks.setBaseValue(-2.0f);
        }
    }

    public boolean isRunning()
    {
        return elapsedTicks.getInt() != -1;
    }

    public int elapsedTicks()
    {
        return elapsedTicks.getInt();
    }
}
