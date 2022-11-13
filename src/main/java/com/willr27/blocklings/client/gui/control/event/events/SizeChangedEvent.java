package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.CancelableControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's size is changed.
 */
public class SizeChangedEvent extends CancelableControlEvent
{
    /**
     * The new scaled width.
     */
    public final int newWidth;

    /**
     * The new scaled height.
     */
    public final int newHeight;

    /**
     * The change in the width.
     */
    public final int dWidth;

    /**
     * The change in the height.
     */
    public final int dHeight;

    /**
     * @param control the control to change the size of.
     * @param newWidth the new scaled width.
     * @param newHeight the new scaled height.
     */
    public SizeChangedEvent(@Nonnull Control control, int newWidth, int newHeight)
    {
        super(control);
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.dWidth = newWidth - control.getWidth();
        this.dHeight = newHeight - control.getHeight();
    }
}
