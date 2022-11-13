package com.willr27.blocklings.client.gui2.controls.common.range;

import com.willr27.blocklings.client.gui2.IControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Displays and edits an int range.
 */
@OnlyIn(Dist.CLIENT)
public class IntRangeControl extends RangeControl<Integer>
{
    /**
     * @param parent         the parent control.
     * @param min            the minimum value.
     * @param max            the maximum value.
     * @param startingValue  the starting value.
     */
    public IntRangeControl(@Nonnull IControl parent, int min, int max, int startingValue)
    {
        super(parent, min, max, startingValue);

        init();
    }

    @Override
    Integer parse(@Nonnull String value) throws NumberFormatException
    {
        return Integer.parseInt(value);
    }

    @Override
    protected Integer calcValueFromPercentage(float percentage)
    {
        return Math.round((percentage * (max - min)) + min);
    }

    @Override
    int calcMaxNumberOfChars()
    {
        return Math.max(String.valueOf(Math.abs(min)).length(), String.valueOf(Math.abs(max)).length()) + (min < 0 ? 1 : 0);
    }

    @Override
    int calcTextFieldWidth()
    {
        String widestValue = min < 0 ? "-" : "";

        for (int i = 0; i < Math.max(String.valueOf(Math.abs(min)).length(), String.valueOf(Math.abs(max)).length()); i++)
        {
            widestValue += "0";
        }

        return Math.min(50, font.width(widestValue) + 8);
    }

    @Override
    public void setValue(Integer value)
    {
        this.value = Math.max(min, Math.min(max, value));

        valueTextFieldControl.setValue(String.valueOf(this.value));

        percentage = (float) (this.value - min) / (float) (max - min);

        recalcGrabberPosition();
    }
}
