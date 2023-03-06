package com.willr27.blocklings.client.gui.properties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Represents an offset.
 */
@OnlyIn(Dist.CLIENT)
public class Offset
{
    /**
     * The x offset.
     */
    public double x;

    /**
     * The y offset.
     */
    public double y;

    /**
     * @param x the offset.
     * @param y the offset.
     */
    public Offset(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     *
     * @param offset the offset.
     */
    public Offset(@Nonnull Offset offset)
    {
        this(offset.x, offset.y);
    }
}
