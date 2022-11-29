package com.willr27.blocklings.client.gui.control.event.events.input;

import com.willr27.blocklings.client.gui.control.event.ControlEvent;
import com.willr27.blocklings.util.event.HandleableEvent;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A key event.
 */
@OnlyIn(Dist.CLIENT)
public class KeyEvent extends HandleableEvent
{
    /**
     * The key code.
     */
    public final int keyCode;

    /**
     * The scan code.
     */
    public final int scanCode;

    /**
     * The modifiers.
     */
    public final int modifiers;

    /**
     * @param keyCode the key code.
     * @param scanCode the scan code.
     * @param modifiers the modifiers.
     */
    public KeyEvent(int keyCode, int scanCode, int modifiers)
    {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
    }
}
