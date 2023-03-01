package com.willr27.blocklings.client.gui.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents an area to scissor.
 */
@OnlyIn(Dist.CLIENT)
public class ScissorBounds
{
    /**
     * The left pixel coordinate of the bounds.
     */
    public final int pixelX;

    /**
     * The top pixel coordinate of the bounds.
     */
    public final int pixelY;

    /**
     * The pixel width of the bounds.
     */
    public final int pixelWidth;

    /**
     * The pixel height of the bounds.
     */
    public final int pixelHeight;

    /**
     * @param pixelX      the left pixel coordinate of the bounds.
     * @param pixelY      the top pixel coordinate of the
     * @param pixelWidth  the pixel width of the bounds.
     * @param pixelHeight the pixel height of the bounds.
     */
    public ScissorBounds(int pixelX, int pixelY, int pixelWidth, int pixelHeight)
    {
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.pixelWidth = Math.max(0, pixelWidth);
        this.pixelHeight = Math.max(0, pixelHeight);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ScissorBounds)
        {
            ScissorBounds scissorBounds = (ScissorBounds) obj;

            return pixelX == scissorBounds.pixelX && pixelY == scissorBounds.pixelY && pixelWidth == scissorBounds.pixelWidth && pixelHeight == scissorBounds.pixelHeight;
        }

        return super.equals(obj);
    }
}

