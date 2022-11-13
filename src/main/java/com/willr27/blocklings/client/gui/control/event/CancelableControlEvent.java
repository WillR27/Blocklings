package com.willr27.blocklings.client.gui.control.event;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.util.event.CancelableEvent;

import javax.annotation.Nonnull;

/**
 * A cancelable event that is associated with a control. Any change of state applied to the control
 * should occur after the event has been handled so the current state of the control can still be
 * queried inside event handlers.
 */
public abstract class CancelableControlEvent extends CancelableEvent
{
    /**
     * The control the event is associated with.
     */
    @Nonnull
    public final Control control;

    /**
     * @param control the control the event is associated with.
     */
    protected CancelableControlEvent(@Nonnull Control control)
    {
        this.control = control;
    }
}
