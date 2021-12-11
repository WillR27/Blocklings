package com.willr27.blocklings.item.items;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.EntityTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Items
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Blocklings.MODID);

    public static final RegistryObject<Item> BLOCKLING_SPAWN_EGG = ITEMS.register("blockling_spawn_egg",
            () -> new BlocklingSpawnEgg(EntityTypes.BLOCKLING_ENTITY, 0x464F56, 0x000FFF,
                    new Item.Properties().tab(ItemGroup.TAB_MISC)));

    public static final RegistryObject<Item> BLOCKLING = ITEMS.register("blockling", () -> new BlocklingItem(
            new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)));

    public static void register(IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }
}
