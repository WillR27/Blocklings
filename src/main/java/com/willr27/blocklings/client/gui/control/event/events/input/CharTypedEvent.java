package com.willr27.blocklings.client.gui.control.event.events.input;

import com.willr27.blocklings.util.event.HandleableEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A char typed event.
 */
@OnlyIn(Dist.CLIENT)
public class CharTypedEvent extends HandleableEvent
{
    /**
     * The char typed.
     */
    public final char character;

    /**
     * The key code.
     */
    public final int keyCode;

    /**
     * @param charTyped the char typed.
     * @param keyCode the key code.
     */
    public CharTypedEvent(char charTyped, int keyCode)
    {
        this.character = charTyped;
        this.keyCode = keyCode;
    }
}
