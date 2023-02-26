package com.willr27.blocklings.client.gui3.control.event.events.input;

import com.willr27.blocklings.util.event.HandleableEvent;

/**
 * A mouse pixel position event.
 */
public class MousePixelPosEvent extends HandleableEvent
{
    /**
     * The mouse x pixel position.
     */
    public final int mousePixelX;

    /**
     * The mouse y pixel position.
     */
    public final int mousePixelY;

    /**
     * @param mousePixelX the mouse x pixel position.
     * @param mousePixelY the mouse y pixel position.
     */
    public MousePixelPosEvent(int mousePixelX, int mousePixelY)
    {
        this.mousePixelX = mousePixelX;
        this.mousePixelY = mousePixelY;
    }
}
