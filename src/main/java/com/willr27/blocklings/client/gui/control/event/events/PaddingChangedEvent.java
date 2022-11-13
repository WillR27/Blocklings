package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.event.CancelableControlEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Occurs when a control's padding change.
 */
public class PaddingChangedEvent extends CancelableControlEvent
{
    /**
     * The new padding for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> newPadding = new HashMap<>();

    /**
     * The change in the padding for each side of the control.
     */
    @Nonnull
    private final Map<Side, Integer> dPadding = new HashMap<>();

    /**
     * @param control the control the padding are changing for.
     * @param left the left padding.
     * @param top the top padding.
     * @param right the right padding.
     * @param bottom the bottom padding.
     */
    public PaddingChangedEvent(@Nonnull Control control, int left, int top, int right, int bottom)
    {
        super(control);
        newPadding.put(Side.LEFT, left);
        newPadding.put(Side.TOP, top);
        newPadding.put(Side.RIGHT, right);
        newPadding.put(Side.BOTTOM, bottom);
        dPadding.put(Side.LEFT, left - control.getPadding().get(Side.LEFT));
        dPadding.put(Side.TOP, top - control.getPadding().get(Side.TOP));
        dPadding.put(Side.RIGHT, right - control.getPadding().get(Side.RIGHT));
        dPadding.put(Side.BOTTOM, bottom - control.getPadding().get(Side.BOTTOM));
    }

    /**
     * @return the new padding for the control.
     */
    @Nonnull
    public Map<Side, Integer> getNewPadding()
    {
        return new HashMap<>(newPadding);
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
