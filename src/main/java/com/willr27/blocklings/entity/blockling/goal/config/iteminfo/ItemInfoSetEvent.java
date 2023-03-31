package com.willr27.blocklings.entity.blockling.goal.config.iteminfo;

import com.willr27.blocklings.util.event.IEvent;

import javax.annotation.Nonnull;

/**
 * An event used when an item info is set.
 */
public class ItemInfoSetEvent implements IEvent
{
    /**
     * The old item info.
     */
    @Nonnull
    public final ItemInfo oldItemInfo;

    /**
     * The new item info.
     */
    @Nonnull
    public final ItemInfo newItemInfo;

    /**
     * The index of the item info.
     */
    public final int index;

    /**
     * @param oldItemInfo the old item info.
     * @param newItemInfo the new item info.
     * @param index the index of the item info.
     */
    public ItemInfoSetEvent(@Nonnull ItemInfo oldItemInfo, @Nonnull ItemInfo newItemInfo, int index)
    {
        this.oldItemInfo = oldItemInfo;
        this.newItemInfo = newItemInfo;
        this.index = index;
    }
}
