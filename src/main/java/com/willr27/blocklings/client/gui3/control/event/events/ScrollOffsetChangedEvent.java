package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's scroll offset is changed.
 */
@OnlyIn(Dist.CLIENT)
public class ScrollOffsetChangedEvent extends ControlEvent
{
    /**
     * The previous scroll offset.
     */
    public final float prevScrollOffset;

    /**
     * @param control the associated control.
     * @param prevScrollOffset the previous scroll offset.
     */
    public ScrollOffsetChangedEvent(@Nonnull Control control, float prevScrollOffset)
    {
        super(control);
        this.prevScrollOffset = prevScrollOffset;
    }
}
