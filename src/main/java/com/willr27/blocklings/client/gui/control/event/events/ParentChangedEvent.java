package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An event fired when a control's parent changes.
 */
@OnlyIn(Dist.CLIENT)
public class ParentChangedEvent implements IEvent
{
    /**
     * The old parent.
     */
    @Nullable
    public final BaseControl oldParent;

    /**
     * The new parent.
     */
    @Nullable
    public final BaseControl newParent;

    /**
     * @param oldParent the old parent.
     * @param newParent the new parent.
     */
    public ParentChangedEvent(@Nullable BaseControl oldParent, @Nullable BaseControl newParent)
    {
        this.oldParent = oldParent;
        this.newParent = newParent;
    }
}
