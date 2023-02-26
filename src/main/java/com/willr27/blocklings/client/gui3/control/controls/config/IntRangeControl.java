package com.willr27.blocklings.client.gui3.control.controls.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Displays and edits an int range.
 */
@OnlyIn(Dist.CLIENT)
public class IntRangeControl extends RangeControl<Integer>
{
    /**
     * @param min           the minimum value.
     * @param max           the maximum value.
     * @param startingValue the starting value.
     */
    public IntRangeControl(Integer min, Integer max, Integer startingValue)
    {
        super(min, max, startingValue);
    }
}
