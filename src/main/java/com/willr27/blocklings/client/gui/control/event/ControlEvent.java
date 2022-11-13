package com.willr27.blocklings.client.gui.control.event;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.util.event.CancelableEvent;
import com.willr27.blocklings.util.event.Event;

import javax.annotation.Nonnull;

/**
 * An event that is associated with a control. Any change of state applied to the control
 * should occur after the event has been handled so the current state of the control can still be
 * queried inside event handlers.
 */
public abstract class ControlEvent extends Event
{
    /**
     * The control the event is associated with.
     */
    @Nonnull
    public final Control control;

    /**
     * @param control the control the event is associated with.
     */
    protected ControlEvent(@Nonnull Control control)
    {
        this.control = control;
    }
}
