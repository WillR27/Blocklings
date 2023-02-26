package com.willr27.blocklings.client.gui.properties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A padding.
 */
@OnlyIn(Dist.CLIENT)
public class Padding
{
    /**
     * The left padding.
     */
    public double left;

    /**
     * The right padding.
     */
    public double right;

    /**
     * The top padding.
     */
    public double top;

    /**
     * The bottom padding.
     */
    public double bottom;

    /**
     * @param left the left padding.
     * @param top the top padding.
     * @param right the right padding.
     * @param bottom the bottom padding.
     */
    public Padding(double left, double top, double right, double bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * @param all the padding on all sides.
     */
    public Padding(double all)
    {
        this.left = all;
        this.right = all;
        this.top = all;
        this.bottom = all;
    }

    public Padding(@Nonnull Padding padding)
    {
        this(padding.left, padding.right, padding.top, padding.bottom);
    }
}
