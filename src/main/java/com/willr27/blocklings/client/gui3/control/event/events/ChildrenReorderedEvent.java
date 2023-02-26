package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's children are reordered.
 */
public class ChildrenReorderedEvent extends ControlEvent
{
    /**
     * @param control the control the child was removed from.
     */
    public ChildrenReorderedEvent(@Nonnull Control control)
    {
        super(control);
    }
}
