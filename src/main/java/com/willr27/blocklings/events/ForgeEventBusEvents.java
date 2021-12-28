package com.willr27.blocklings.events;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.EntitySize;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBusEvents
{
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        BlocklingType.init();
        ToolUtil.init();
        EntityUtil.init((World) event.getWorld());
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event)
    {
        if (event.getEntity() instanceof BlocklingEntity)
        {
            float scale = ((BlocklingEntity) event.getEntity()).getScale();
            event.setNewSize(new EntitySize(scale * 1.0f, scale * 1.0f, true), true);
        }
    }
}
