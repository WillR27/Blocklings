package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Occurs when a control gains or loses focus.
 */
@OnlyIn(Dist.CLIENT)
public class FocusChangedEvent extends ControlEvent
{
    /**
     * The previous state of focus for the control.
     */
    public final boolean previousFocus;

    /**
     * @param control the control the event is associated with.
     * @param previousFocus the previous state of focus for the control.
     */
    public FocusChangedEvent(@Nonnull Control control, boolean previousFocus)
    {
        super(control);
        this.previousFocus = previousFocus;
    }
}
