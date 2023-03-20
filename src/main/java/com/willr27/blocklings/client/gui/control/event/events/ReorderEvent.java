package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.util.event.HandleableEvent;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An event used items are reordered within a panel.
 */
@OnlyIn(Dist.CLIENT)
public class ReorderEvent implements IEvent
{
    /**
     * The dragged control.
     */
    public final BaseControl draggedControl;

    /**
     * The closest control.
     */
    public final BaseControl closestControl;

    /**
     * Whether to insert the dragged control before the closest control.
     */
    public final boolean insertBefore;

    /**
     * @param draggedControl the dragged control.
     * @param closestControl the closest control.
     * @param insertBefore whether to insert the dragged control before or after the closest control.
     */
    public ReorderEvent(BaseControl draggedControl, BaseControl closestControl, boolean insertBefore)
    {
        this.draggedControl = draggedControl;
        this.closestControl = closestControl;
        this.insertBefore = insertBefore;
    }
}
