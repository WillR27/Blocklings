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
     * The new scaled local x position.
     */
    public final int newX;

    /**
     * The new scaled local y position.
     */
    public final int newY;

    /**
     * The change in the x position.
     */
    public final int dX;

    /**
     * The change in the y position.
     */
    public final int dY;

    /**
     * @param control the control to update the position of.
     * @param newX the new scaled local x position.
     * @param newY the new scaled local y position.
     */
    public PositionChangedEvent(@Nonnull Control control, int newX, int newY)
    {
        super(control);
        this.newX = newX;
        this.newY = newY;
        this.dX = newX - control.getX();
        this.dY = newY - control.getY();
    }
}
