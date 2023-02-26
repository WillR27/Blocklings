package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Occurs when a control's parent is changed.
 */
public class ParentChangedEvent extends ControlEvent
{
    /**
     * The old parent control.
     */
    @Nullable
    public final Control oldParent;

    /**
     * @param control the control to change the parent of.
     * @param oldParent the old parent control.
     */
    public ParentChangedEvent(@Nonnull Control control, @Nullable Control oldParent)
    {
        super(control);
        this.oldParent = oldParent;
    }
}
