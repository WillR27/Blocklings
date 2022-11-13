package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Occurs when a control's parent is changed.
 */
public class ChildAddedEvent extends ControlEvent
{
    /**
     * The new parent control.
     */
    @Nullable
    public final Control childToAdd;

    /**
     * @param control the control to add the child to.
     * @param childToAdd the control to add as a child.
     */
    public ChildAddedEvent(@Nonnull Control control, @Nullable Control childToAdd)
    {
        super(control);
        this.childToAdd = childToAdd;
    }
}
