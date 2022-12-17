package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's position is changed.
 */
public class PositionChangedEvent extends ControlEvent
{
    /**
     * The previous scaled local x position.
     */
    public final float previousX;

    /**
     * The previous scaled local y position.
     */
    public final float previousY;

    /**
     * The change in the x position.
     */
    public final float dX;

    /**
     * The change in the y position.
     */
    public final float dY;

    /**
     * @param control the control to update the position of.
     * @param previousX the previous scaled local x position.
     * @param previousY the previous scaled local y position.
     */
    public PositionChangedEvent(@Nonnull Control control, float previousX, float previousY)
    {
        super(control);
        this.previousX = previousX;
        this.previousY = previousY;
        this.dX = control.getX() - previousX;
        this.dY = control.getY() - previousY;
    }
}
