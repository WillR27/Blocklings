package com.willr27.blocklings.client.gui3.control.event.events.input;

import com.willr27.blocklings.util.event.HandleableEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A char event.
 */
@OnlyIn(Dist.CLIENT)
public class CharEvent extends HandleableEvent
{
    /**
     * The character.
     */
    public final char character;

    /**
     * The key code.
     */
    public final int keyCode;

    /**
     * @param character the character.
     * @param keyCode the key code.
     */
    public CharEvent(char character, int keyCode)
    {
        this.character = character;
        this.keyCode = keyCode;
    }
}
