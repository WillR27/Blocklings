package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Occurs when a child is added to a control.
 */
public class ChildAddedEvent extends ControlEvent
{
    /**
     * The child that has been added.
     */
    @Nullable
    public final Control childAdded;

    /**
     * @param control the control the child was added to.
     * @param childAdded the child that has been added.
     */
    public ChildAddedEvent(@Nonnull Control control, @Nullable Control childAdded)
    {
        super(control);
        this.childAdded = childAdded;
    }
}
