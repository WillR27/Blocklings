package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.HandleableControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's position is changed.
 */
public class PositionChangedEvent extends HandleableControlEvent
{
    /**
     * The new scaled local x position.
     */
    public final float newX;

    /**
     * The new scaled local y position.
     */
    public final float newY;

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
     * @param newX the new scaled local x position.
     * @param newY the new scaled local y position.
     */
    public PositionChangedEvent(@Nonnull Control control, float newX, float newY)
    {
        super(control);
        this.newX = newX;
        this.newY = newY;
        this.dX = newX - control.getX();
        this.dY = newY - control.getY();
    }
}
