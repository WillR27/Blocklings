package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control stops being be dragged.
 */
public class DragEndEvent extends ControlEvent
{
    /**
     * @param control the control being dragged.
     */
    public DragEndEvent(@Nonnull Control control)
    {
        super(control);
    }
}
