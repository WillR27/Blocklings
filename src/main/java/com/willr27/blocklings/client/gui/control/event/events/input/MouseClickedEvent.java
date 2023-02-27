package com.willr27.blocklings.client.gui.control.event.events.input;

import com.willr27.blocklings.util.event.HandleableEvent;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A mouse clicked event.
 */
@OnlyIn(Dist.CLIENT)
public class MouseClickedEvent extends HandleableEvent
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
     * The mouse button.
     */
    public final int button;

    /**
     * @param mouseX the pixel x position of the mouse.
     * @param mouseY the pixel y position of the mouse.
     * @param button the mouse button.
     */
    public MouseClickedEvent(double mouseX, double mouseY, int button)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }
}
