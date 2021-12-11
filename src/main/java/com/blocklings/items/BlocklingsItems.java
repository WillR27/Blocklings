package com.blocklings.items;

import com.blocklings.main.Blocklings;
import com.blocklings.util.BlocklingType;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Blocklings.MODID)
public class BlocklingsItems
{
    public static Item itemBlockling;

    public static void init()
    {
        itemBlockling = new ItemBlockling("blockling");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(itemBlockling);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerRenders(ModelRegistryEvent event)
    {
        ModelBakery.registerItemVariants(itemBlockling, new ModelResourceLocation(itemBlockling.getRegistryName(), "inventory"));

        for (BlocklingType blocklingType : BlocklingType.blocklingTypes)
        {
            ModelBakery.registerItemVariants(itemBlockling, blocklingType.itemModel);
        }

        ModelLoader.setCustomMeshDefinition(itemBlockling, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(Blocklings.MODID + ":" + stack.getTagCompound().getString("BlocklingType"), "inventory");
            }
        });
    }
}
