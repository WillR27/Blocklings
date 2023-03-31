package com.willr27.blocklings.entity.blockling.goal.config;

import com.willr27.blocklings.entity.blockling.goal.config.ItemInfo;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An event used when an item info is added.
 */
public class ItemInfoAddedEvent implements IEvent
{
    /**
     * The item info that was added.
     */
    @Nonnull
    public final ItemInfo itemInfo;

    /**
     * Whether the item info should be added to the beginning of the list.
     */
    public boolean addFirst = false;

    /**
     * @param itemInfo the item info that was added.
     * @param addFirst whether the item info should be added to the beginning of the list.
     */
    public ItemInfoAddedEvent(@Nonnull ItemInfo itemInfo, boolean addFirst)
    {
        this.itemInfo = itemInfo;
        this.addFirst = addFirst;
    }
}
