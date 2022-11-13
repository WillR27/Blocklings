package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Occurs when a control's parent is changed.
 */
public class ChildRemovedEvent extends ControlEvent
{
    /**
     * The new parent control.
     */
    @Nullable
    public final Control childToRemove;

    /**
     * @param control the control to remove the child from.
     * @param childToRemove the control to remove as a child.
     */
    public ChildRemovedEvent(@Nonnull Control control, @Nullable Control childToRemove)
    {
        super(control);
        this.childToRemove = childToRemove;
    }
}
