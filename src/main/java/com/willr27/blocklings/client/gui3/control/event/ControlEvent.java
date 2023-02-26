package com.willr27.blocklings.client.gui3.control.event;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An event that is associated with a control that cannot be handled. Is useful for notifying
 * listeners when actions have taken place.
 */
@OnlyIn(Dist.CLIENT)
public class ControlEvent implements IEvent
{
    /**
     * The control the event is associated with.
     */
    @Nonnull
    public final Control control;

    /**
     * @param control the control the event is associated with.
     */
    protected ControlEvent(@Nonnull Control control)
    {
        this.control = control;
    }
}
