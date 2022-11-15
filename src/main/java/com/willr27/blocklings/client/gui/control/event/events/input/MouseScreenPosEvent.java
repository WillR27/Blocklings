package com.willr27.blocklings.client.gui.control.event.events.input;

import com.willr27.blocklings.util.event.Event;

/**
 * A mouse screen position event.
 */
public class MouseScreenPosEvent extends Event
{
    /**
     * The mouse screen x position.
     */
    public final int mouseScreenX;

    /**
     * The mouse screen y position.
     */
    public final int mouseScreenY;

    /**
     * @param mouseScreenX the mouse screen x position.
     * @param mouseScreenY the mouse screen y position.
     */
    public MouseScreenPosEvent(int mouseScreenX, int mouseScreenY)
    {
        this.mouseScreenX = mouseScreenX;
        this.mouseScreenY = mouseScreenY;
    }
}
