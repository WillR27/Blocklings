package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.util.event.HandleableEvent;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An event that is fired when a control's size is changed.
 */
@OnlyIn(Dist.CLIENT)
public class SizeChangedEvent implements IEvent
{
    /**
     * The previous width of the control.
     */
    public final double oldWidth;

    /**
     * The previous height of the control.
     */
    public final double oldHeight;

    /**
     * @param oldWidth the previous width of the control.
     * @param oldHeight the previous height of the control.
     */
    public SizeChangedEvent(double oldWidth, double oldHeight)
    {
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
    }
}
