package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.Side;
import com.willr27.blocklings.client.gui3.control.event.HandleableControlEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Occurs when a control's anchor is changed.
 */
public class AnchorChangedEvent extends HandleableControlEvent
{
    /**
     * The new anchor.
     */
    @Nullable
    public final EnumSet<Side> newAnchor;

    /**
     * @param control the control to change the anchor of.
     * @param newAnchor the new anchor.
     */
    public AnchorChangedEvent(@Nonnull Control control, @Nullable EnumSet<Side> newAnchor)
    {
        super(control);
        this.newAnchor = newAnchor;
    }
}
