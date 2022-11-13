package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.event.ControlEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Occurs when a control's margins change.
 */
public class MarginsChangedEvent extends ControlEvent
{
    /**
     * The new margins for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> newMargins = new HashMap<>();

    /**
     * The change in the margins for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> dMargins = new HashMap<>();

    /**
     * @param control the control the margins are changing for.
     * @param left the left margin.
     * @param top the top margin.
     * @param right the right margin.
     * @param bottom the bottom margin.
     */
    public MarginsChangedEvent(@Nonnull Control control, int left, int top, int right, int bottom)
    {
        super(control);
        newMargins.put(Side.LEFT, left);
        newMargins.put(Side.TOP, top);
        newMargins.put(Side.RIGHT, right);
        newMargins.put(Side.BOTTOM, bottom);
        dMargins.put(Side.LEFT, left - control.getMargins().get(Side.LEFT));
        dMargins.put(Side.TOP, top - control.getMargins().get(Side.TOP));
        dMargins.put(Side.RIGHT, right - control.getMargins().get(Side.RIGHT));
        dMargins.put(Side.BOTTOM, bottom - control.getMargins().get(Side.BOTTOM));
    }

    /**
     * @return the new margins.
     */
    @Nonnull
    public Map<Side, Integer> getNewMargins()
    {
        return new HashMap<>(newMargins);
    }

    /**
     * @return the change in the margins.
     */
    @Nonnull
    public Map<Side, Integer> getDeltaMargins()
    {
        return new HashMap<>(dMargins);
    }
}
