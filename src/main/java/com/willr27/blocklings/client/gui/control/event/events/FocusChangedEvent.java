package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An event used when a control loses or gains focus.
 */
@OnlyIn(Dist.CLIENT)
public class FocusChangedEvent implements IEvent
{
    /**
     * The old focus state of the control.
     */
    public final boolean oldFocus;

    /**
     * @param oldFocus the old focus state of the control.
     */
    public FocusChangedEvent(boolean oldFocus)
    {
        this.oldFocus = oldFocus;
    }
}
