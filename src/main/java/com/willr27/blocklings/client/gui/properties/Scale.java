package com.willr27.blocklings.client.gui.properties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Represents a scale.
 */
@OnlyIn(Dist.CLIENT)
public class Scale
{
    /**
     * The scale in the x direction.
     */
    public double x;

    /**
     * The scale in the y direction.
     */
    public double y;

    /**
     * @param x the scale in the x direction.
     * @param y the scale in the y direction.
     */
    public Scale(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     *
     * @param scale the scale.
     */
    public Scale(@Nonnull Scale scale)
    {
        this(scale.x, scale.y);
    }
}
