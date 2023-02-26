package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Occurs when a child is removed from a control.
 */
public class ChildRemovedEvent extends ControlEvent
{
    /**
     * The child that was removed.
     */
    @Nullable
    public final Control childRemoved;

    /**
     * @param control the control the child was removed from.
     * @param childRemoved the child removed.
     */
    public ChildRemovedEvent(@Nonnull Control control, @Nullable Control childRemoved)
    {
        super(control);
        this.childRemoved = childRemoved;
    }
}
