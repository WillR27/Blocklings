package com.willr27.blocklings.client.gui2.controls.common.range;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui2.Control;
import com.willr27.blocklings.client.gui2.GuiTextures;
import com.willr27.blocklings.client.gui2.IControl;
import com.willr27.blocklings.client.gui2.controls.common.TextFieldControl;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

/**
 * A control used to display and edit a range.
 */
@OnlyIn(Dist.CLIENT)
public abstract class RangeControl<T extends Number> extends Control
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
     * The width of the slider portion of the control.
     */
    private int sliderWidth = 0;

    /**
     * The width of the text portion of the control.
     */
    private int textFieldWidth = 0;

    /**
     * The grabber control used to change the value of the range.
     */
    protected GrabberControl grabberControl;

    /**
     * The text field used to set the value of the range.
     */
    protected TextFieldControl valueTextFieldControl;

    /**
     * @param parent the parent control.
     * @param min the minimum value.
     * @param max the maximum value.
     * @param startingValue the starting value.
     */
    public RangeControl(@Nonnull IControl parent, T min, T max, T startingValue)
    {
        super(parent, 0, 0, parent.getWidth() - parent.getPadding(Side.LEFT) - parent.getPadding(Side.RIGHT), 20);
        this.min = min;
        this.max = max;
        this.startingValue = startingValue;
    }

    protected void init()
    {
        textFieldWidth = calcTextFieldWidth();
        sliderWidth = width - textFieldWidth - 4;

        grabberControl = new GrabberControl(this);

        IControl screen = (IControl) getScreen();
        float scale = screen.getScale();
        valueTextFieldControl = new TextFieldControl(font, (int) ((getScreenX() / scale) + getWidth() - textFieldWidth), (int) (getScreenY() / scale), textFieldWidth, 20, new StringTextComponent(""))
        {
            @Override
            public void setFocus(boolean focus)
            {
                if (!focus)
                {
                    try
                    {
                        RangeControl.this.setValue(parse(getValue()));
                    }
                    catch (NumberFormatException ex)
                    {
                        RangeControl.this.setValue(RangeControl.this.getValue());
                    }
                }

                moveCursorToStart();

                super.setFocus(focus);
            }
        };
        valueTextFieldControl.setMaxLength(calcMaxNumberOfChars());
        valueTextFieldControl.setVisible(true);
        valueTextFieldControl.setTextColor(16777215);

        setValue(startingValue);
    }

    @Override
    public void tick()
    {
        valueTextFieldControl.tick();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderTexture(matrixStack, 0, 8, GuiTextures.SLIDER_BAR.width(sliderWidth - GuiTextures.SLIDER_BAR_END.width));
        renderTexture(matrixStack, sliderWidth - GuiTextures.SLIDER_BAR_END.width, 8, GuiTextures.SLIDER_BAR_END);

        IControl screen = (IControl) getScreen();
        float scale = screen.getScale();
        valueTextFieldControl.x = ((int) ((getScreenX() / scale) + getWidth() - textFieldWidth));
        valueTextFieldControl.y = ((int) (getScreenY() / scale));
        valueTextFieldControl.render(new MatrixStack(), mouseX, mouseY, partialTicks);
    }

    @Override
    public void controlMouseClicked(@Nonnull MouseButtonEvent e)
    {
        valueTextFieldControl.mouseClicked(e.mouseX / getEffectiveScale(), e.mouseY / getEffectiveScale(), e.button);

        if (new Control(null, getScreenX(), (int) (getScreenY() + 4 * getEffectiveScale()), (int) (sliderWidth * getEffectiveScale()), (int) (GuiTextures.SLIDER_GRABBER_RAISED.height * getEffectiveScale())).isMouseOver(e.mouseX, e.mouseY))
        {
            setPercentage(calcPercentageFromMouse(e.mouseX));

            grabberControl.setIsPressed(true, e.mouseX, e.mouseY);
            grabberControl.setIsDragging(true);
        }

        e.setIsHandled(true);
    }

    @Override
    public void controlMouseReleased(@Nonnull MouseButtonEvent e)
    {
        if (valueTextFieldControl.isFocused())
        {
            valueTextFieldControl.mouseReleased(e.mouseX / getEffectiveScale(), e.mouseY / getEffectiveScale(), e.button);
        }

        e.setIsHandled(true);
    }

    @Override
    public void globalMouseClicked(@Nonnull MouseButtonEvent e)
    {
        if (valueTextFieldControl.isFocused())
        {
            if (!valueTextFieldControl.isMouseOver(e.mouseX / getEffectiveScale(), e.mouseY / getEffectiveScale()))
            {
                valueTextFieldControl.setFocus(false);
            }
        }
    }

    @Override
    public void controlKeyPressed(@Nonnull KeyEvent e)
    {
        if (e.keyCode == GLFW.GLFW_KEY_ENTER || e.keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (valueTextFieldControl.isFocused())
            {
                valueTextFieldControl.setFocus(false);

                e.setIsHandled(true);
            }
        }
        else
        {
            e.setIsHandled(valueTextFieldControl.keyPressed(e.keyCode, e.scanCode, e.modifiers));
        }
    }

    @Override
    public void controlCharTyped(@Nonnull CharEvent e)
    {
        e.setIsHandled(valueTextFieldControl.charTyped(e.character, e.keyCode));
    }

    /**
     * @return the string value parsed as T.
     */
    abstract T parse(@Nonnull String value) throws NumberFormatException;

    /**
     * Recalculates the grabber position.
     */
    protected void recalcGrabberPosition()
    {
        grabberControl.setX((int) ((percentage * (sliderWidth - grabberControl.getWidth()))));
    }

    /**
     * @return the percentage of the slider through the range based on the mouse position.
     */
    private float calcPercentageFromMouse(int mouseX)
    {
        int adjustedSliderWidth = sliderWidth - grabberControl.getWidth();
        int localMouseX = Math.max(grabberControl.getWidth() / 2, Math.min(adjustedSliderWidth + grabberControl.getWidth() / 2, (int) (toLocalX(mouseX) / getEffectiveScale())));

        return Math.max(0.0f, Math.min(1.0f, (float) (localMouseX - grabberControl.getWidth() / 2) / adjustedSliderWidth));
    }

    /**
     * @return the value from the given percentage.
     */
    abstract T calcValueFromPercentage(float percentage);

    /**
     * @return the maximum number of characters the value could have.
     */
    abstract int calcMaxNumberOfChars();

    /**
     * @return the width of the text field in pixels.
     */
    abstract int calcTextFieldWidth();

    /**
     * @return the current value of the range.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * Sets the current value of the range.
     */
    abstract void setValue(T value);

    /**
     * @return the percentage of the slider through the range.
     */
    public float getPercentage()
    {
        return percentage;
    }

    /**
     * Sets the percentage of the slider through the range.
     */
    public void setPercentage(float percentage)
    {
        setValue(calcValueFromPercentage(percentage));
    }

    /**
     * A grabber used by range controls.
     */
    protected class GrabberControl extends Control
    {
        /**
         * The parent range control.
         */
        public final RangeControl rangeControl;

        /**
         * @param rangeControl the parent range control.
         */
        public GrabberControl(@Nonnull RangeControl rangeControl)
        {
            super(rangeControl, 0, 4, GuiTextures.SLIDER_GRABBER_RAISED.width, GuiTextures.SLIDER_GRABBER_RAISED.height);
            this.rangeControl = rangeControl;
        }

        @Override
        public void TODOrenamePreRender(int mouseX, int mouseY, float partialTicks)
        {
            if (isDragging())
            {
                T value = (T) rangeControl.calcValueFromPercentage(rangeControl.calcPercentageFromMouse(mouseX));

                if (value != rangeControl.value)
                {
                    rangeControl.setValue(value);
                }
            }
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            if (isPressed())
            {
                renderTexture(matrixStack, GuiTextures.SLIDER_GRABBER_PRESSED);
            }
            else
            {
                renderTexture(matrixStack, GuiTextures.SLIDER_GRABBER_RAISED);
            }
        }

        @Override
        public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            parent.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }
}
