package com.willr27.blocklings.item.items;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlocklingsItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Blocklings.MODID);

    public static final RegistryObject<Item> BLOCKLING_SPAWN_EGG = ITEMS.register("blockling_spawn_egg",
            () -> new BlocklingSpawnEgg(BlocklingsEntityTypes.BLOCKLING_ENTITY, 0x785439, 0x466f33,
                    new Item.Properties().tab(ItemGroup.TAB_MISC)));

    public static final RegistryObject<Item> BLOCKLING = ITEMS.register("blockling", BlocklingItem::new);

    public static final RegistryObject<Item> BLOCKLING_WHISTLE = ITEMS.register("blockling_whistle", BlocklingWhistleItem::new);

    public static void register(IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }
}
