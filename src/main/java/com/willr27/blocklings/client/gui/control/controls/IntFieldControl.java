package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.event.events.TextChangedEvent;
import com.willr27.blocklings.util.event.ValueChangedEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A text field that only accepts integers.
 */
@OnlyIn(Dist.CLIENT)
public class IntFieldControl extends NullableIntFieldControl
{
    /**
     */
    public IntFieldControl()
    {
        super();
    }

    @Override
    protected void onTextChanged(@Nonnull BaseControl c, @Nonnull TextChangedEvent e)
    {
        int oldValue = 0;
        int newValue = 0;

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
    public void onUnfocused()
    {
        super.onUnfocused();

        if (getText().isEmpty() || getText().equals("-")) setText("0");
    }

    @Nonnull
    @Override
    public Integer getValue()
    {
        return Integer.parseInt(getText());
    }

    @Override
    public void setValue(@Nullable Integer value)
    {
        super.setValue(value == null ? 0 : value);
    }
}
