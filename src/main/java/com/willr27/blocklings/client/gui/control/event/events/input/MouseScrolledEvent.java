package com.willr27.blocklings.client.gui.control.event.events.input;

import com.willr27.blocklings.util.event.HandleableEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A mouse scrolled event.
 */
@OnlyIn(Dist.CLIENT)
public class MouseScrolledEvent extends HandleableEvent
{
    /**
     * The pixel x position of the mouse.
     */
    public final double mouseX;

    /**
     * The pixel y position of the mouse.
     */
    public final double mouseY;

    /**
     * The amount scrolled.
     */
    public double amount;

    /**
     * @param mouseX the pixel x position of the mouse.
     * @param mouseY the pixel y position of the mouse.
     * @param amount the amount scrolled.
     */
    public MouseScrolledEvent(double mouseX, double mouseY, double amount)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.amount = amount;
    }
}
