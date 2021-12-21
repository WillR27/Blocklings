package com.willr27.blocklings.action;

import com.willr27.blocklings.attribute.attributes.numbers.ModifiableFloatAttribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class Action
{
    @Nonnull
    public final BlocklingEntity blockling;

    @Nonnull
    protected final ModifiableFloatAttribute count;

    public Action(@Nonnull BlocklingEntity blockling, @Nonnull String key)
    {
        this.blockling = blockling;

        count = blockling.getStats().createModifiableFloatAttribute(UUID.randomUUID().toString(), key + "_action", -1.0f, null, null);
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

    /**
     * Starts the action whether it's running or not.
     */
    public void start()
    {
        count.setBaseValue(0.0f);
    }

    /**
     * Increments the count by 1.0f.
     */
    public void tick()
    {
        tick(1.0f);
    }

    /**
     * Increments the count by the given amount.
     */
    public void tick(float increment)
    {
        if (isRunning())
        {
            count.incBaseValue(increment);
        }
    }

    /**
     * Stops the action if it is running.
     */
    public void stop()
    {
        if (isRunning())
        {
            count.setBaseValue(-1.0f);
        }
    }

    /**
     * Returns true if the action is currently running.
     */
    public boolean isRunning()
    {
        return count.getValue() != -1;
    }

    /**
     * Returns the current value of the count attribute.
     */
    public float count()
    {
        return count.getValue();
    }
}
