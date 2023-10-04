package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.util.event.IEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An event used when an item is remove from a control.
 */
@OnlyIn(Dist.CLIENT)
public class ItemRemovedEvent implements IEvent
{
    /**
     * The item that was removed.
     */
    @Nonnull
    public final Item item;

    /**
     * @param item the item that was removed.
     */
    public ItemRemovedEvent(@Nonnull Item item)
    {
        this.item = item;
    }
}
