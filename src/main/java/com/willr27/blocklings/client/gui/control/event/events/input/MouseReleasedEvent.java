package com.willr27.blocklings.client.gui.control.event.events.input;

import com.willr27.blocklings.util.event.HandleableEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A mouse released event.
 */
@OnlyIn(Dist.CLIENT)
public class MouseReleasedEvent extends HandleableEvent
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
    public MouseReleasedEvent(double mouseX, double mouseY, int button)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }
}
