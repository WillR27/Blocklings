package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
