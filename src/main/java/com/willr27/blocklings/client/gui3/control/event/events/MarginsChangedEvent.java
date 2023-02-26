package com.willr27.blocklings.client.gui3.control.event.events;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.Side;
import com.willr27.blocklings.client.gui3.control.event.ControlEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Occurs when a control's margins change.
 */
public class MarginsChangedEvent extends ControlEvent
{
    /**
     * The previous margins for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> prevMargins = new HashMap<>();

    /**
     * The change in the margins for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> dMargins = new HashMap<>();

    /**
     * @param control the control the margins are changing for.
     * @param prevLeft the previous left margin.
     * @param prevTop the previous utop margin.
     * @param prevRight the previous right margin.
     * @param prevBottom the previous bottom margin.
     */
    public MarginsChangedEvent(@Nonnull Control control, int prevLeft, int prevTop, int prevRight, int prevBottom)
    {
        super(control);
        prevMargins.put(Side.LEFT, prevLeft);
        prevMargins.put(Side.TOP, prevTop);
        prevMargins.put(Side.RIGHT, prevRight);
        prevMargins.put(Side.BOTTOM, prevBottom);
        dMargins.put(Side.LEFT, control.getMargins().get(Side.LEFT) - prevLeft);
        dMargins.put(Side.TOP, control.getMargins().get(Side.TOP) - prevTop);
        dMargins.put(Side.RIGHT, control.getMargins().get(Side.RIGHT) - prevRight);
        dMargins.put(Side.BOTTOM, control.getMargins().get(Side.BOTTOM) - prevBottom);
    }
}
