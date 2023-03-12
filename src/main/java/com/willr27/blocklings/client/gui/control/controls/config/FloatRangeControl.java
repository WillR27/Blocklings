package com.willr27.blocklings.client.gui.control.controls.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Displays and edits a float range.
 */
@OnlyIn(Dist.CLIENT)
public class FloatRangeControl extends RangeControl<Float>
{
    /**
     * @param min           the minimum value.
     * @param max           the maximum value.
     * @param startingValue the starting value.
     */
    public FloatRangeControl(@Nonnull Float min, @Nonnull Float max, @Nonnull Float startingValue)
    {
        super(min, max, startingValue);
    }

    @Nonnull
    @Override
    protected Float parseValue(@Nonnull String text)
    {
        return Float.parseFloat(text);
    }

    @Nonnull
    @Override
    protected Float calculateValue(double percentage)
    {
        return min + (max - min) * (float) percentage;
    }
}
