package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.event.events.TextChangedEvent;
import com.willr27.blocklings.util.event.ValueChangedEvent;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A text field that only accepts integers.
 */
@OnlyIn(Dist.CLIENT)
public class NullableIntFieldControl extends TextFieldControl
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
    public NullableIntFieldControl()
    {
        super();

        eventBus.subscribe(this::onTextChanged);
    }

    @Override
    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {
        if (isTextTrimmed())
        {
            renderTooltip(matrixStack, mouseX, mouseY, new StringTextComponent(getText()));
        }
    }

    /**
     * Called when the text is changed.
     *
     * @param c the control.
     * @param e the event.
     */
    protected void onTextChanged(@Nonnull BaseControl c, @Nonnull TextChangedEvent e)
    {
        Integer oldValue = null;
        Integer newValue = null;

        if (!e.oldText.isEmpty() && !e.oldText.equals("-"))
        {
            try
            {
                oldValue = Integer.parseInt(e.oldText);
            }
            catch (NumberFormatException ex)
            {
                oldValue = e.oldText.contains("-") ? getMinVal() : getMaxVal();
            }
        }

        if (!e.newText.isEmpty() && !e.newText.equals("-"))
        {
            try
            {
                newValue = Integer.parseInt(e.newText);
            }
            catch (NumberFormatException ex)
            {
                newValue = e.newText.contains("-") ? getMinVal() : getMaxVal();
            }
        }

        eventBus.post(this, new ValueChangedEvent<>(oldValue, newValue));
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
        if (text.isEmpty() || text.equals("-"))
        {
            return text;
        }

        try
        {
            int value = Integer.parseInt(text);

            if (value < getMinVal()) return String.valueOf(getMinVal());
            if (value > getMaxVal()) return String.valueOf(getMaxVal());

            return text;
        }
        catch (NumberFormatException e)
        {
            return text.contains("-") ? String.valueOf(getMinVal()) : String.valueOf(getMaxVal());
        }
    }

    /**
     * @return the value of the int field.
     */
    @Nullable
    public Integer getValue()
    {
        return getText().isEmpty() || getText().equals("-") ? null : Integer.parseInt(getText());
    }

    /**
     * Sets the value of the int field.
     *
     * @param value the value to set.
     */
    public void setValue(@Nullable Integer value)
    {
        setText(value != null ? String.valueOf(value) : "");
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

    /**
     * @return whether the text is trimmed.
     */
    public boolean isTextTrimmed()
    {
        return !GuiUtil.get().trim(new StringTextComponent(getText()), (int) getWidthWithoutPadding()).getString().equals(getText());
    }
}
