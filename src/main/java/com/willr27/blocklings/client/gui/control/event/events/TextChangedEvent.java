package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An event used when a text control's text changes.
 */
@OnlyIn(Dist.CLIENT)
public class TextChangedEvent implements IEvent
{
    /**
     * The old text.
     */
    @Nonnull
    public final String oldText;

    /**
     * The new text.
     */
    @Nonnull
    public final String newText;

    /**
     * @param oldText the old text.
     * @param newText the new text.
     */
    public TextChangedEvent(@Nonnull String oldText, @Nonnull String newText)
    {
        this.oldText = oldText;
        this.newText = newText;
    }
}
