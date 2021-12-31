package com.willr27.blocklings.events;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.EntityTypes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.item.items.BlocklingSpawnEgg;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents
{
    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(EntityTypes.BLOCKLING_ENTITY.get(), BlocklingEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event)
    {
        BlocklingSpawnEgg.initSpawnEggs();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Post event) throws IOException
    {
        for (int mask = 0; mask < 3; mask++)
        {
            final NativeImage maskNativeImage = SimpleTexture.TextureData.load(Minecraft.getInstance().getResourceManager(), new BlocklingsResourceLocation("textures/entity/blockling/blockling_mask_" + mask + ".png")).getImage();

            for (BlocklingType baseBlocklingType : BlocklingType.TYPES)
            {
                for (BlocklingType outerBlocklingType : BlocklingType.TYPES)
                {
                    NativeImage baseNativeImage = SimpleTexture.TextureData.load(Minecraft.getInstance().getResourceManager(), baseBlocklingType.entityTexture).getImage();
                    NativeImage outerNativeImage = SimpleTexture.TextureData.load(Minecraft.getInstance().getResourceManager(), outerBlocklingType.entityTexture).getImage();

                    for (int i = 0; i < baseNativeImage.getWidth(); i++)
                    {
                        for (int j = 0; j < baseNativeImage.getHeight(); j++)
                        {
                            float maskAlpha = ((maskNativeImage.getPixelRGBA(i, j) >> 24) & 0xff) / 255.0f;

                            int baseColour = baseNativeImage.getPixelRGBA(i, j);
                            int baseBlue = (baseColour >> 16) & 0xff;
                            int baseGreen = (baseColour >> 8) & 0xff;
                            int baseRed = (baseColour & 0xff);

                            int outerColour = outerNativeImage.getPixelRGBA(i, j);
                            int outerBlue = (outerColour >> 16) & 0xff;
                            int outerGreen = (outerColour >> 8) & 0xff;
                            int outerRed = (outerColour & 0xff);

                            int a = 255;
                            int b = (int) ((baseBlue * maskAlpha) + (outerBlue * (1.0f - maskAlpha)));
                            int g = (int) ((baseGreen * maskAlpha) + (outerGreen * (1.0f - maskAlpha)));
                            int r = (int) ((baseRed * maskAlpha) + (outerRed * (1.0f - maskAlpha)));
                            int colour = (a << 24) + (b << 16) + (g << 8) + r;

                            baseNativeImage.setPixelRGBA(i, j, colour);
                        }
                    }

                    Minecraft.getInstance().textureManager.register(baseBlocklingType.getCombinedTexture(outerBlocklingType, mask), new DynamicTexture(baseNativeImage));
                }
            }
        }
    }
}
