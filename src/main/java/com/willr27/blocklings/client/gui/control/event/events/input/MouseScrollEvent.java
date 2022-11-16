package com.willr27.blocklings.client.gui.control.event.events.input;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A mouse scroll event.
 */
@OnlyIn(Dist.CLIENT)
public class MouseScrollEvent extends MousePosEvent
{
    /**
     * The mouse scroll amount.
     */
    public final double scrollAmount;

    /**
     * @param mouseX      the mouse x position.
     * @param mouseY      the mouse y position.
     * @param mousePixelX the mouse x pixel position.
     * @param mousePixelY the mouse y pixel position.
     * @param scrollAmount the scroll amount.
     */
    public MouseScrollEvent(int mouseX, int mouseY, int mousePixelX, int mousePixelY, double scrollAmount)
    {
        super(mouseX, mouseY, mousePixelX, mousePixelY);
        this.scrollAmount = scrollAmount;
    }
}
