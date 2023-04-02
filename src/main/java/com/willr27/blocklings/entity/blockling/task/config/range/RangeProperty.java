package com.willr27.blocklings.entity.blockling.task.config.range;

import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.config.Property;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

/**
 * Used to configure a range property.
 */
public abstract class RangeProperty<T extends Number> extends Property
{
    /**
     * The minimum value of the range.
     */
    @Nonnull
    protected T min;

    /**
     * The maximum value of the range.
     */
    @Nonnull
    protected T max;

    /**
     * The current value.
     */
    @Nonnull
    protected T value;

    /**
     * @param id            the id of the property (used for serialising\deserialising).
     * @param goal          the associated task's goal.
     * @param name          the name of the property.
     * @param desc          the description of the property.
     * @param min           the minimum value of the range.
     * @param max           the maximum value of the range.
     * @param startingValue the range starting value.
     */
    public RangeProperty(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull ITextComponent name, @Nonnull ITextComponent desc, @Nonnull T min, @Nonnull T max, @Nonnull T startingValue)
    {
        super(id, goal, name, desc);
        this.min = min;
        this.max = max;
        this.value = startingValue;
    }

    /**
     * @return the current value of the range.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * @param value the new value.
     * @param sync whether to sync to the client/server.
     */
    @Nonnull
    public void setValue(T value, boolean sync)
    {
        this.value = value;

        if (sync)
        {
            new TaskPropertyMessage(this).sync();
        }
    }

    /**
     * @return the minimum value of the range.
     */
    public T getMin()
    {
        return min;
    }

    /**
     * @return the maximum value of the range.
     */
    @Nonnull
    public T getMax()
    {
        return max;
    }
}
