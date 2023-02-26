package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's size is changed.
 */
public class SizeChangedEvent extends ControlEvent
{
    /**
     * The previous scaled width.
     */
    public final float prevWidth;

    /**
     * The previous scaled height.
     */
    public final float prevHeight;

    /**
     * The change in the width.
     */
    public final float dWidth;

    /**
     * The change in the height.
     */
    public final float dHeight;

    /**
     * @param control the control to change the size of.
     * @param prevWidth the previous scaled width.
     * @param prevHeight the previous scaled height.
     */
    public SizeChangedEvent(@Nonnull Control control, float prevWidth, float prevHeight)
    {
        super(control);
        this.prevWidth = prevWidth;
        this.prevHeight = prevHeight;
        this.dWidth = control.getWidth() - prevWidth;
        this.dHeight = control.getHeight() - prevHeight;
    }
}
