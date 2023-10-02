package com.willr27.blocklings.util.event;

import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An event used when a value is changed.
 */
@OnlyIn(Dist.CLIENT)
public class ValueChangedEvent<T> implements IEvent
{
    /**
     * @param oldValue the old value.
     */
    @Nullable
    public final T oldValue;

    /**
     * @param newValue the new value.
     */
    @Nullable
    public final T newValue;

    /**
     * @param oldValue the old value.
     * @param newValue the new value.
     */
    public ValueChangedEvent(@Nullable T oldValue, @Nullable T newValue)
    {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
