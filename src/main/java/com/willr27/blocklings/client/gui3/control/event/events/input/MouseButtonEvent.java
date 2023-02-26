package com.willr27.blocklings.client.gui3.control.event.events.input;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A mouse button event.
 */
@OnlyIn(Dist.CLIENT)
public class MouseButtonEvent extends MousePosEvent
{
    /**
     * The mouse button clicked.
     */
    public final int mouseButton;

    /**
     * @param mouseX      the mouse x position.
     * @param mouseY      the mouse y position.
     * @param mousePixelX the mouse x pixel position.
     * @param mousePixelY the mouse y pixel position.
     * @param mouseButton the mouse button.
     */
    public MouseButtonEvent(int mouseX, int mouseY, int mousePixelX, int mousePixelY, int mouseButton)
    {
        super(mouseX, mouseY, mousePixelX, mousePixelY);
        this.mouseButton = mouseButton;
    }
}
