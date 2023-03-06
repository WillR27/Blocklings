package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.util.event.HandleableEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An event used when trying to hover over a control.
 */
@OnlyIn(Dist.CLIENT)
public class TryHoverEvent extends HandleableEvent
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
     * @param mouseX the pixel x position of the mouse.
     * @param mouseY the pixel y position of the mouse.
     */
    public TryHoverEvent(double mouseX, double mouseY)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
