package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Occurs when a control's parent is changed.
 */
public class ParentChangedEvent extends ControlEvent
{
    /**
     * The new parent control.
     */
    @Nullable
    public final Control newParent;

    /**
     * @param control the control to change the parent of.
     * @param newParent the new parent control.
     */
    public ParentChangedEvent(@Nonnull Control control, @Nullable Control newParent)
    {
        super(control);
        this.newParent = newParent;
    }
}
