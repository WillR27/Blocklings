package com.willr27.blocklings.client.gui.texture;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A {@link ResourceLocation} based gui texture.
 */
@OnlyIn(Dist.CLIENT)
public class Texture
{
    /**
     * The resource location of the texture.
     */
    @Nonnull
    public final ResourceLocation resourceLocation;

    /**
     * The pixel x position of the texture inside the resource location.
     */
    public final int x;

    /**
     * The pixel y position of the texture inside the resource location.
     */
    public final int y;

    /**
     * The pixel width of the texture.
     */
    public final int width;

    /**
     * The pixel height of the texture.
     */
    public final int height;

    /**
     * @param resourceLocation the resource location of the texture.
     * @param x                the pixel x position of the texture inside the resource location.
     * @param y                the pixel y position of the texture inside the resource location.
     * @param width            the pixel width of the texture.
     * @param height           the pixel height of the texture.
     */
    public Texture(@Nonnull ResourceLocation resourceLocation, int x, int y, int width, int height)
    {
        this.resourceLocation = resourceLocation;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Nonnull
    public Texture x(int x)
    {
        return new Texture(resourceLocation, x, y, width, height);
    }

    @Nonnull
    public Texture y(int y)
    {
        return new Texture(resourceLocation, x, y, width, height);
    }

    @Nonnull
    public Texture width(int width)
    {
        return new Texture(resourceLocation, x, y, width, height);
    }

    @Nonnull
    public Texture height(int height)
    {
        return new Texture(resourceLocation, x, y, width, height);
    }

    @Nonnull
    public Texture dx(int dx)
    {
        return new Texture(resourceLocation, x + dx, y, width, height);
    }

    @Nonnull
    public Texture dy(int dy)
    {
        return new Texture(resourceLocation, x, y + dy, width, height);
    }

    @Nonnull
    public Texture dWidth(int dWidth)
    {
        return new Texture(resourceLocation, x, y, width + dWidth, height);
    }

    @Nonnull
    public Texture dHeight(int dHeight)
    {
        return new Texture(resourceLocation, x, y, width, height + dHeight);
    }
}
