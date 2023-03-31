package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.controls.ComboBoxControl;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An event used when a selection changes.
 */
@OnlyIn(Dist.CLIENT)
public class SelectionChangedEvent<T> implements IEvent
{
    /**
     * The previosly selected item.
     */
    @Nullable
    public final T previousItem;

    /**
     * The newly selected item.
     */
    @Nullable
    public final T newItem;

    /**
     * @param previousItem the previously selected item.
     * @param newItem the newly selected item.
     */
    public SelectionChangedEvent(@Nullable T previousItem, @Nullable T newItem)
    {
        this.previousItem = previousItem;
        this.newItem = newItem;
    }
}
