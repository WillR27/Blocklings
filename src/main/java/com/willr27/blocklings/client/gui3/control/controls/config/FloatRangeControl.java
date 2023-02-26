package com.willr27.blocklings.client.gui3.control.controls.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public FloatRangeControl(Float min, Float max, Float startingValue)
    {
        super(min, max, startingValue);
    }
}
