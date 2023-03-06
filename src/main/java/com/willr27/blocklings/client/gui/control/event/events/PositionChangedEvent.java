package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An event that is fired when a control's position is changed.
 */
@OnlyIn(Dist.CLIENT)
public class PositionChangedEvent implements IEvent
{
    /**
     * The previous x position of the control.
     */
    public final double oldX;

    /**
     * The previous y position of the control.
     */
    public final double oldY;

    /**
     * @param oldX the previous x position of the control.
     * @param oldY the previous y position of the control.
     */
    public PositionChangedEvent(double oldX, double oldY)
    {
        this.oldX = oldX;
        this.oldY = oldY;
    }
}
