package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.event.CancelableControlEvent;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Occurs when a control's anchor is changed.
 */
public class AnchorChangedEvent extends CancelableControlEvent
{
    /**
     * The new anchor.
     */
    @Nonnull
    public final Optional<EnumSet<Side>> newAnchor;

    /**
     * @param control the control to change the anchor of.
     * @param newAnchor the new anchor.
     */
    public AnchorChangedEvent(@Nonnull Control control, @Nonnull Optional<EnumSet<Side>> newAnchor)
    {
        super(control);
        this.newAnchor = newAnchor;
    }
}
