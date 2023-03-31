package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.FocusChangedEvent;
import com.willr27.blocklings.util.event.ValueChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jline.utils.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A control used to display and edit a range.
 */
@OnlyIn(Dist.CLIENT)
public abstract class RangeControl<T extends Number> extends Control
{
    /**
     * The minimum value of the range.
     */
    @Nonnull
    public final T min;

    /**
     * The maximum value of the range.
     */
    @Nonnull
    public final T max;

    /**
     * The current value of the range.
     */
    @Nonnull
    protected T value;

    /**
     * The initial value of the range.
     */
    @Nonnull
    protected final T startingValue;

    /**
     * The control used to display the value.
     */
    @Nonnull
    public final TextFieldControl valueFieldControl;

    /**
     * The control used to grab the slider.
     */
    @Nonnull
    protected final Control grabberControl;

    /**
     * @param min the minimum value.
     * @param max the maximum value.
     * @param startingValue the starting value.
     */
    public RangeControl(@Nonnull T min, @Nonnull T max, @Nonnull T startingValue)
    {
        super();
        this.min = min;
        this.max = max;
        this.startingValue = startingValue;

        setWidthPercentage(1.0);
        setFitHeightToContent(true);

        GridPanel gridPanel = new GridPanel();
        gridPanel.setParent(this);
        gridPanel.setWidthPercentage(1.0);
        gridPanel.setFitHeightToContent(true);
        gridPanel.addRowDefinition(GridDefinition.AUTO, 1.0);
        gridPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);
        gridPanel.addColumnDefinition(GridDefinition.AUTO, 1.0);
        gridPanel.setHoverable(false);

        valueFieldControl = new TextFieldControl();
        gridPanel.addChild(valueFieldControl, 0, 1);
        valueFieldControl.setWidth(24.0);
        valueFieldControl.setHeight(16.0);
        valueFieldControl.setHorizontalContentAlignment(0.5);
        valueFieldControl.eventBus.subscribe((BaseControl c, FocusChangedEvent e) ->
        {
            try
            {
                setValue(parseValue(valueFieldControl.getText()), true, true);
            }
            catch (NumberFormatException ex)
            {
                valueFieldControl.setText(value.toString());
            }
        });

        Control sliderContainerControl = new Control()
        {
            @Override
            protected void arrange()
            {
                super.arrange();
            }
        };
        gridPanel.addChild(sliderContainerControl, 0, 0);
        sliderContainerControl.setWidthPercentage(1.0);
        sliderContainerControl.setHeightPercentage(1.0);
        sliderContainerControl.setHoverable(false);
        sliderContainerControl.setMarginRight(3.0);

        Control sliderControl = new TexturedControl(Textures.Common.SLIDER_BAR)
        {
            @Override
            public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTextureAsBackground(matrixStack, getBackgroundTexture().width((int) (getWidth() - 1)));
                renderTextureAsBackground(matrixStack, getBackgroundTexture().width(1).dx(255), getWidth() - 1, 0);
            }

            @Override
            protected void onMouseClicked(@Nonnull MouseClickedEvent e)
            {
                grabberControl.setIsDragging(true);
                grabberControl.setPressed(true);

                e.setIsHandled(true);
            }
        };
        sliderControl.setParent(sliderContainerControl);
        sliderControl.setWidthPercentage(1.0);
        sliderControl.setVerticalAlignment(0.5);
        sliderControl.setHoverable(false);
        sliderControl.setPressable(false);

        grabberControl = new TexturedControl(Textures.Common.NODE_UNPRESSED, Textures.Common.NODE_PRESSED)
        {
            @Override
            protected void onRenderUpdate(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (isDragging())
                {
                    double minPixelX = getParent().toPixelX(0.0);
                    double maxPixelX = getParent().toPixelX(getParent().getWidth() - getWidth());
                    double percentage = (mouseX - (getPixelWidth() / 2.0) - minPixelX) / (maxPixelX - minPixelX);

                    setValue(calculateValue(percentage), false, false);
                }
            }

            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

                if (isPressed() && getPressedBackgroundTexture() != null)
                {
                    renderTextureAsBackground(matrixStack, getPressedBackgroundTexture());
                }
                else
                {
                    renderTextureAsBackground(matrixStack, getBackgroundTexture());
                }
            }

            @Override
            public void onDragEnd()
            {
                setValue(getValue(), true, true);
            }

            @Override
            public void setHorizontalAlignment(@Nullable Double horizontalAlignment)
            {
                super.setHorizontalAlignment(horizontalAlignment);
            }
        };
        grabberControl.setParent(sliderContainerControl);
        grabberControl.setVerticalAlignment(0.5);
        grabberControl.setDraggableX(true);
        grabberControl.setHoverable(false);

        setValue(startingValue, true, false);
    }

    /**
     * Updates the position of the grabber.
     */
    private void updateGrabberPosition()
    {
        double percentage = Math.max(0.0, Math.min(1.0, calculatePercentage()));
        grabberControl.setHorizontalAlignment(percentage);
    }

    /**
     * @return parses the value from the given text.
     */
    @Nonnull
    protected abstract T parseValue(@Nonnull String text);

    /**
     * @return calculates the current value of the range.
     */
    @Nonnull
    protected abstract T calculateValue(double percentage);

    /**
     * @return the current value of the range.
     */
    @Nonnull
    public T getValue()
    {
        return value;
    }

    /**
     * Sets the current value of the range.
     *
     * @param value the current value of the range.
     * @param updateGrabberPosition whether to update the position of the grabber.
     * @param postEvent whether to post a value changed event.
     */
    public void setValue(@Nonnull T value, boolean updateGrabberPosition, boolean postEvent)
    {
        if (value.doubleValue() < min.doubleValue())
        {
            value = min;
        }
        else if (value.doubleValue() > max.doubleValue())
        {
            value = max;
        }

        boolean hasValueChanged = this.value == value;

        T oldValue = this.value;

        this.value = value;

        valueFieldControl.setText(value.toString());

        if (updateGrabberPosition)
        {
            updateGrabberPosition();
        }

        if (postEvent && hasValueChanged)
        {
            eventBus.post(this, new ValueChangedEvent(oldValue, value));
        }
    }

    /**
     * @return the percentage of the slider through the range.
     */
    private double calculatePercentage()
    {
        return (value.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
    }
}
