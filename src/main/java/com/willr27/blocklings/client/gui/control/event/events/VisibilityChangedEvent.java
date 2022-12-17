package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's size is changed.
 */
@OnlyIn(Dist.CLIENT)
public class VisibilityChangedEvent extends ControlEvent
{
    /**
     * The previous visibility.
     */
    public final boolean wasVisible;

    /**
     * @param control the control the event is associated with.
     * @param wasVisible the previous visibility.
     */
    public VisibilityChangedEvent(@Nonnull Control control, boolean wasVisible)
    {
        super(control);
        this.wasVisible = wasVisible;
    }
}
