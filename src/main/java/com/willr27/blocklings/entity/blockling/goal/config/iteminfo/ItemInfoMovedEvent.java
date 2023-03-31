package com.willr27.blocklings.entity.blockling.goal.config.iteminfo;

import com.willr27.blocklings.util.event.IEvent;

import javax.annotation.Nonnull;

/**
 * An event used when an item info is moved.
 */
public class ItemInfoMovedEvent implements IEvent
{
    /**
     * The item info that was moved.
     */
    @Nonnull
    public final ItemInfo movedItemInfo;

    /**
     * The closest item info that the moved item info was moved to.
     */
    @Nonnull
    public final ItemInfo closestItemInfo;

    /**
     * Whether to insert the moved item info before or after the closest item info.
     */
    public final boolean insertBefore;

    /**
     * @param movedItemInfo the item info that was moved.
     * @param closestItemInfo the closest item info that the moved item info was move to.
     * @param insertBefore whether to insert the moved item info before or after the closest item info.
     */
    public ItemInfoMovedEvent(@Nonnull ItemInfo movedItemInfo, @Nonnull ItemInfo closestItemInfo, boolean insertBefore)
    {
        this.movedItemInfo = movedItemInfo;
        this.closestItemInfo = closestItemInfo;
        this.insertBefore = insertBefore;
    }
}
