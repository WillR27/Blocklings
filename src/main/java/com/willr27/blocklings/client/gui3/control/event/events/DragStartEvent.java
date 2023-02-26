package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control starts to be dragged.
 */
public class DragStartEvent extends ControlEvent
{
    /**
     * @param control the control being dragged.
     */
    public DragStartEvent(@Nonnull Control control)
    {
        super(control);
    }
}
