package com.willr27.blocklings.client.gui.control.event.events.input;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A mouse position event.
 */
@OnlyIn(Dist.CLIENT)
public class MousePosEvent extends MousePixelPosEvent
{
    /**
     * The local mouse x position.
     */
    public int mouseX;

    /**
     * The local mouse y position.
     */
    public int mouseY;

    /**
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param mousePixelX the mouse x pixel position.
     * @param mousePixelY the mouse y pixel position.
     */
    public MousePosEvent(int mouseX, int mouseY, int mousePixelX, int mousePixelY)
    {
        super(mousePixelX, mousePixelY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
