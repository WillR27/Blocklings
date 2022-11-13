package com.willr27.blocklings.client.gui2.controls.common.range;

import com.willr27.blocklings.client.gui2.IControl;
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
     * The maximum number of decimal places the value can have.
     */
    public final int maxDecimalPlaces;

    /**
     * @param parent         the parent control.
     * @param min            the minimum value.
     * @param max            the maximum value.
     * @param startingValue  the starting value.
     */
    public FloatRangeControl(@Nonnull IControl parent, float min, float max, float startingValue, int maxDecimalPlaces)
    {
        super(parent, min, max, startingValue);
        this.maxDecimalPlaces = maxDecimalPlaces;

        init();
    }

    @Override
    Float parse(@Nonnull String value) throws NumberFormatException
    {
        return Float.parseFloat(value);
    }

    @Override
    protected Float calcValueFromPercentage(float percentage)
    {
        return (percentage * (max - min)) + min;
    }

    @Override
    int calcMaxNumberOfChars()
    {
        return maxDecimalPlaces + (min < 0 ? 3 : 2);
    }

    @Override
    int calcTextFieldWidth()
    {
        String widestValue = min < 0 ? "-0." : "0.";

        for (int i = 0; i < maxDecimalPlaces; i++)
        {
            widestValue += "0";
        }

        return Math.min(50, font.width(widestValue) + 8);
    }

    @Override
    public void setValue(Float value)
    {
        this.value = Math.max(min, Math.min(max, value));

        valueTextFieldControl.setValue(String.valueOf(this.value));

        percentage = (this.value - min) / (max - min);

        recalcGrabberPosition();
    }
}
