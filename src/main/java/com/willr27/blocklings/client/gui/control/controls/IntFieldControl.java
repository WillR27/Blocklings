package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.event.events.TextChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.ValueChangedEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A text field that only accepts integers.
 */
@OnlyIn(Dist.CLIENT)
public class IntFieldControl extends TextFieldControl
{
    /**
     * The minimum value that can be entered.
     */
    private int minVal = Integer.MIN_VALUE;

    /**
     * The maximum value that can be entered.
     */
    private int maxVal = Integer.MAX_VALUE;

    /**
     */
    public IntFieldControl()
    {
        super();

        eventBus.subscribe((BaseControl c, TextChangedEvent e) ->
        {
            String oldText = e.oldText.isEmpty() || e.oldText.equals("-") ? "0" : e.oldText;
            String newText = e.newText.isEmpty() || e.newText.endsWith("-") ? "0" : e.newText;

            eventBus.post(this, new ValueChangedEvent<>(Integer.parseInt(oldText), Integer.parseInt(newText)));
        });
    }

    @Override
    protected boolean isValidText(@Nonnull String text)
    {
        try
        {
            Integer.parseInt(text);
        }
        catch (NumberFormatException e)
        {
            return text.isEmpty() || text.equals("-");
        }

        return true;
    }

    @Override
    @Nonnull
    protected String processText(@Nonnull String text)
    {
        try
        {
            int value = Integer.parseInt(text);

            if (value < minVal) return String.valueOf(minVal);
            if (value > maxVal) return String.valueOf(maxVal);

            return text;
        }
        catch (NumberFormatException e)
        {
            return text.isEmpty() ? text : getText();
        }
    }

    /**
     * @return the value of the int field.
     */
    public int getValue()
    {
        return Integer.parseInt(getText());
    }

    /**
     * Sets the value of the int field.
     *
     * @param value the value to set.
     */
    public void setValue(int value)
    {
        setText(String.valueOf(value));
    }

    /**
     * @return the minimum value that can be entered.
     */
    public int getMinVal()
    {
        return minVal;
    }

    /**
     * Sets the minimum value that can be entered.
     *
     * @param minVal the minimum value that can be entered.
     */
    public void setMinVal(int minVal)
    {
        this.minVal = minVal;
    }

    /**
     * @return the maximum value that can be entered.
     */
    public int getMaxVal()
    {
        return maxVal;
    }

    /**
     * Sets the maximum value that can be entered.
     *
     * @param maxVal the maximum value that can be entered.
     */
    public void setMaxVal(int maxVal)
    {
        this.maxVal = maxVal;
    }
}
