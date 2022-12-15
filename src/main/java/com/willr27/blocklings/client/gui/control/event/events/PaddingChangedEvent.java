package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.event.HandleableControlEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Occurs when a control's padding change.
 */
public class PaddingChangedEvent extends HandleableControlEvent
{
    /**
     * The old padding for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> oldPadding = new HashMap<>();

    /**
     * The change in the padding for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> dPadding = new HashMap<>();

    /**
     * @param control the control the padding are changing for.
     * @param oldLeft the oldLeft padding.
     * @param oldTop the oldTop padding.
     * @param oldRight the oldRight padding.
     * @param oldBottom the oldBottom padding.
     */
    public PaddingChangedEvent(@Nonnull Control control, int oldLeft, int oldTop, int oldRight, int oldBottom)
    {
        super(control);
        oldPadding.put(Side.LEFT, oldLeft);
        oldPadding.put(Side.TOP, oldTop);
        oldPadding.put(Side.RIGHT, oldRight);
        oldPadding.put(Side.BOTTOM, oldBottom);
        dPadding.put(Side.LEFT, control.getPadding().get(Side.LEFT) - oldLeft);
        dPadding.put(Side.TOP, control.getPadding().get(Side.TOP) - oldTop);
        dPadding.put(Side.RIGHT, control.getPadding().get(Side.RIGHT) - oldRight);
        dPadding.put(Side.BOTTOM, control.getPadding().get(Side.BOTTOM) - oldBottom);
    }

    /**
     * @return the old padding for the control.
     */
    @Nonnull
    public Map<Side, Integer> getOldPadding()
    {
        return new HashMap<>(oldPadding);
    }

    /**
     * @return the change in the padding.
     */
    @Nonnull
    public Map<Side, Integer> getDeltaPadding()
    {
        return new HashMap<>(dPadding);
    }
}
