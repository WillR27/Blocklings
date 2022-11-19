package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Hitbox;
import com.willr27.blocklings.client.gui.control.event.HandleableControlEvent;

import javax.annotation.Nonnull;

/**
 * Occurs when a control's hitbox is changed.
 */
public class HitboxChangedEvent extends HandleableControlEvent
{
    /**
     * The new hitbox.
     */
    @Nonnull
    public final Hitbox newHitbox;

    /**
     * @param control the control to change the hitbox of.
     * @param newHitbox the new hitbox.
     */
    public HitboxChangedEvent(@Nonnull Control control, @Nonnull Hitbox newHitbox)
    {
        super(control);
        this.newHitbox = newHitbox;
    }
}
