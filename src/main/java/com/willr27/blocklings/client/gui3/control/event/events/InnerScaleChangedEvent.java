package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.HandleableControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's inner scale is changed.
 */
public class InnerScaleChangedEvent extends HandleableControlEvent
{
    /**
     * The new scale of the control.
     */
    public final float newScale;

    /**
     * The factor the scale has changed by.
     */
    public final float dScale;

    /**
     * @param control the control to change the scale of.
     * @param newScale the new scale of the control.
     */
    public InnerScaleChangedEvent(@Nonnull Control control, float newScale)
    {
        super(control);
        this.newScale = newScale;
        this.dScale = newScale / control.getInnerScale();
    }
}
