package com.willr27.blocklings.item;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

/**
 * Handles the registration of items.
 */
public class BlocklingsItems
{
    /**
     * The deferred item registry.
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Blocklings.MODID);

    public static final RegistryObject<Item> BLOCKLING_SPAWN_EGG = ITEMS.register("blockling_spawn_egg", () -> new ForgeSpawnEggItem(BlocklingsEntityTypes.BLOCKLING, 0x785439, 0x466f33, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> BLOCKLING = ITEMS.register("blockling", BlocklingItem::new);
    public static final RegistryObject<Item> BLOCKLING_WHISTLE = ITEMS.register("blockling_whistle", BlocklingWhistleItem::new);

    /**
     * Registers the items.
     */
    public static void register(@Nonnull IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }
}
