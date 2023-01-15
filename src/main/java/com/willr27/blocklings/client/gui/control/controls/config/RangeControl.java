package com.willr27.blocklings.client.gui.control.controls.config;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Alignment;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Dock;
import com.willr27.blocklings.client.gui.control.Fill;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A control used to display and edit a range.
 */
@OnlyIn(Dist.CLIENT)
public class RangeControl<T extends Number> extends Control
{
    /**
     * The minimum value of the range.
     */
    public final T min;

    /**
     * The maximum value of the range.
     */
    public final T max;

    /**
     * The current value of the range.
     */
    protected T value;

    /**
     * The initial value of the range.
     */
    protected final T startingValue;

    /**
     * The percentage of the slider through the range.
     */
    protected float percentage = 0.0f;

    /**
     * @param min the minimum value.
     * @param max the maximum value.
     * @param startingValue the starting value.
     */
    public RangeControl(T min, T max, T startingValue)
    {
        super();
        this.min = min;
        this.max = max;
        this.startingValue = startingValue;

        setWidth(new Fill(1.0f));
        setFitToContentsY(true);

        TextFieldControl valueFieldControl = new TextFieldControl()
        {
            @Override
            public void onTick()
            {
                super.onTick();
            }

            @Override
            public void setWidth(float width)
            {
                super.setWidth(width);
            }
        };
        valueFieldControl.setParent(this);
        valueFieldControl.setDock(Dock.RIGHT);
        valueFieldControl.setWidth(30.0f);
        valueFieldControl.setText("!asd");
        valueFieldControl.setShouldScissor(false);

        Control sliderContainerControl = new Control();
        sliderContainerControl.setParent(this);
        sliderContainerControl.setDock(Dock.FILL);
        sliderContainerControl.setHeight(new Fill(1.0f));

        Control sliderControl = new TexturedControl(GuiTextures.Common.SLIDER_BAR)
        {
            @Override
            public void onRenderBackground(@Nonnull RenderArgs renderArgs)
            {
                renderTexture(renderArgs.matrixStack, getTexture().width((int) (getWidth() - 1)));
                renderTexture(renderArgs.matrixStack, getWidth() - 1, 0, getTexture().width(1).shift(255, 0));
            }
        };
        sliderControl.setParent(sliderContainerControl);
        sliderControl.setWidth(new Fill(1.0f));
        sliderControl.setAlignmentY(new Alignment(0.5f));

        Control grabberControl = new TexturedControl(GuiTextures.Common.NODE_UNPRESSED, GuiTextures.Common.NODE_PRESSED)
        {

        };
        grabberControl.setParent(sliderContainerControl);
        grabberControl.setAlignmentY(new Alignment(0.5f));
    }
}
