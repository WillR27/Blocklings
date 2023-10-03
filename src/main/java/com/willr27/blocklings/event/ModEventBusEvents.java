package com.willr27.blocklings.event;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.config.BlocklingsConfig;
import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingType;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;

/**
 * Handles any mod event.s
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents
{
    /**
     * Adds the additional attributes a blockling needs to function.
     */
    @SubscribeEvent
    public static void addEntityAttributes(@Nonnull EntityAttributeCreationEvent event)
    {
        event.put(BlocklingsEntityTypes.BLOCKLING.get(), BlocklingEntity.createAttributes().build());
    }

    /**
     * Used to prevent the merged blockling textures being created multiple times.
     */
    private static boolean blocklingTextureStitched = false;

    public static class SimpleTexture extends AbstractTexture
    {
        static final Logger LOGGER = LogUtils.getLogger();
        protected final ResourceLocation location;

        public SimpleTexture(ResourceLocation p_118133_) {
            this.location = p_118133_;
        }

        public void load(ResourceManager p_118135_) throws IOException {
            SimpleTexture.TextureImage simpletexture$textureimage = this.getTextureImage(p_118135_);
            simpletexture$textureimage.throwIfError();
            TextureMetadataSection texturemetadatasection = simpletexture$textureimage.getTextureMetadata();
            boolean flag;
            boolean flag1;
            if (texturemetadatasection != null) {
                flag = texturemetadatasection.isBlur();
                flag1 = texturemetadatasection.isClamp();
            } else {
                flag = false;
                flag1 = false;
            }

            NativeImage nativeimage = simpletexture$textureimage.getImage();
            if (!RenderSystem.isOnRenderThreadOrInit()) {
                RenderSystem.recordRenderCall(() -> {
                    this.doLoad(nativeimage, flag, flag1);
                });
            } else {
                this.doLoad(nativeimage, flag, flag1);
            }

        }

        private void doLoad(NativeImage p_118137_, boolean p_118138_, boolean p_118139_) {
            TextureUtil.prepareImage(this.getId(), 0, p_118137_.getWidth(), p_118137_.getHeight());
            p_118137_.upload(0, 0, 0, 0, 0, p_118137_.getWidth(), p_118137_.getHeight(), p_118138_, p_118139_, false, true);
        }

        protected SimpleTexture.TextureImage getTextureImage(ResourceManager p_118140_) {
            return SimpleTexture.TextureImage.load(p_118140_, this.location);
        }

        @OnlyIn(Dist.CLIENT)
        public static class TextureImage implements Closeable
        {
            @Nullable
            private final TextureMetadataSection metadata;
            @Nullable
            private final NativeImage image;
            @Nullable
            private final IOException exception;

            public TextureImage(IOException p_118153_) {
                this.exception = p_118153_;
                this.metadata = null;
                this.image = null;
            }

            public TextureImage(@Nullable TextureMetadataSection p_118150_, NativeImage p_118151_) {
                this.exception = null;
                this.metadata = p_118150_;
                this.image = p_118151_;
            }

            public static SimpleTexture.TextureImage load(ResourceManager p_118156_, ResourceLocation p_118157_) {
                try {
                    Resource resource = p_118156_.getResource(p_118157_);

                    SimpleTexture.TextureImage $$5;
                    try {
                        NativeImage nativeimage = NativeImage.read(resource.getInputStream());
                        TextureMetadataSection texturemetadatasection = null;

                        try {
                            texturemetadatasection = resource.getMetadata(TextureMetadataSection.SERIALIZER);
                        } catch (RuntimeException runtimeexception) {
                            Blocklings.LOGGER.warn("Failed reading metadata of: {}", p_118157_, runtimeexception);
                        }

                        $$5 = new SimpleTexture.TextureImage(texturemetadatasection, nativeimage);
                    } catch (Throwable throwable1) {
                        if (resource != null) {
                            try {
                                resource.close();
                            } catch (Throwable throwable) {
                                throwable1.addSuppressed(throwable);
                            }
                        }

                        throw throwable1;
                    }

                    if (resource != null) {
                        resource.close();
                    }

                    return $$5;
                } catch (IOException ioexception) {
                    return new SimpleTexture.TextureImage(ioexception);
                }
            }

            @Nullable
            public TextureMetadataSection getTextureMetadata() {
                return this.metadata;
            }

            public NativeImage getImage() throws IOException {
                if (this.exception != null) {
                    throw this.exception;
                } else {
                    return this.image;
                }
            }

            public void close() {
                if (this.image != null) {
                    this.image.close();
                }

            }

            public void throwIfError() throws IOException {
                if (this.exception != null) {
                    throw this.exception;
                }
            }
        }
    }

    /**
     * Creates merged blockling type textures for each blockling type and each variant.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTextureStitch(@Nonnull TextureStitchEvent.Post event)
    {
        if (blocklingTextureStitched)
        {
            return;
        }

        blocklingTextureStitched = true;

        if (BlocklingsConfig.CLIENT.disableDirtyBlocklings.get())
        {
            Blocklings.LOGGER.info("Skipping texture creation for merged blockling textures.");

            return;
        }

        ResourceLocation texture = null;

        for (int mask = 0; mask < 3; mask++)
        {
            final NativeImage maskNativeImage;

            try
            {
                texture = new BlocklingsResourceLocation("textures/entity/blockling/blockling_mask_" + mask + ".png");
                maskNativeImage = SimpleTexture.TextureImage.load(Minecraft.getInstance().getResourceManager(), texture).getImage();
            }
            catch (IOException e)
            {
                Blocklings.LOGGER.warn("Couldn't find texture: " + texture);

                continue;
            }

            for (BlocklingType baseBlocklingType : BlocklingType.TYPES)
            {
                final NativeImage baseNativeImage;

                try
                {
                    baseNativeImage = SimpleTexture.TextureImage.load(Minecraft.getInstance().getResourceManager(), baseBlocklingType.entityTexture).getImage();
                }
                catch (IOException e)
                {
                    Blocklings.LOGGER.warn("Couldn't find texture: " + baseBlocklingType.entityTexture);

                    continue;
                }

                for (BlocklingType outerBlocklingType : BlocklingType.TYPES)
                {
                    final NativeImage outerNativeImage;

                    try
                    {
                        outerNativeImage = SimpleTexture.TextureImage.load(Minecraft.getInstance().getResourceManager(), outerBlocklingType.entityTexture).getImage();
                    }
                    catch (IOException e)
                    {
                        Blocklings.LOGGER.warn("Couldn't find texture: " + outerBlocklingType.entityTexture);

                        continue;
                    }

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

                            outerNativeImage.setPixelRGBA(i, j, colour);
                        }
                    }

                    Minecraft.getInstance().textureManager.register(baseBlocklingType.getCombinedTexture(outerBlocklingType, mask), new DynamicTexture(outerNativeImage));
                }
            }
        }
    }
}
