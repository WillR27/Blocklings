package com.willr27.blocklings.client.gui.properties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A margin.
 */
@OnlyIn(Dist.CLIENT)
public class Margin
{
    /**
     * The left margin.
     */
    public double left;

    /**
     * The right margin.
     */
    public double right;

    /**
     * The top margin.
     */
    public double top;

    /**
     * The bottom margin.
     */
    public double bottom;

    /**
     * @param left the left margin.
     * @param right the right margin.
     * @param top the top margin.
     * @param bottom the bottom margin.
     */
    public Margin(double left, double right, double top, double bottom)
    {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    /**
     * @param all the margin on all sides.
     */
    public Margin(double all)
    {
        this.left = all;
        this.right = all;
        this.top = all;
        this.bottom = all;
    }

    public Margin(@Nonnull Margin margin)
    {
        this(margin.left, margin.right, margin.top, margin.bottom);
    }
}
