package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.util.event.IEvent;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An event used when an item is moved.
 */
@OnlyIn(Dist.CLIENT)
public class ItemMovedEvent implements IEvent
{
    /**
     * The item that was moved.
     */
    @Nonnull
    public final Item movedItem;

    /**
     * The closest item that the moved item was move to.
     */
    @Nonnull
    public final Item closestItem;

    /**
     * Whether to insert the moved item before or after the closest item.
     */
    public final boolean insertBefore;

    /**
     * @param movedItem the item that was moved.
     * @param closestItem the closest item that the moved item was move to.
     * @param insertBefore whether to insert the moved item before or after the closest item.
     */
    public ItemMovedEvent(@Nonnull Item movedItem, @Nonnull Item closestItem, boolean insertBefore)
    {
        this.movedItem = movedItem;
        this.closestItem = closestItem;
        this.insertBefore = insertBefore;
    }
}
