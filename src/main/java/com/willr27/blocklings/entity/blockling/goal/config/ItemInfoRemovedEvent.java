package com.willr27.blocklings.entity.blockling.goal.config;

import com.willr27.blocklings.entity.blockling.goal.config.ItemInfo;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An event used when an item info is removed.
 */
public class ItemInfoRemovedEvent implements IEvent
{
    /**
     * The item info that was removed.
     */
    @Nonnull
    public final ItemInfo itemInfo;

    /**
     * @param itemInfo the item info that was removed.
     */
    public ItemInfoRemovedEvent(@Nonnull ItemInfo itemInfo)
    {
        this.itemInfo = itemInfo;
    }
}
