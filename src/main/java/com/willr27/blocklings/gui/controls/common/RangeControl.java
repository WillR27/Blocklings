package com.willr27.blocklings.gui.controls.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.IControl;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

/**
 * A control used to display and edit a range.
 */
@OnlyIn(Dist.CLIENT)
public class RangeControl extends Control
{
    /**
     * The minimum value of the range.
     */
    public final int min;

    /**
     * The maximum value of the range.
     */
    public final int max;

    /**
     * The current value of the range.
     */
    private int value;

    /**
     * The percentage of the slider through the range.
     */
    private float percentage = 0.0f;

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
    private final GrabberControl grabberControl;

    /**
     * The text field used to set the value of the range.
     */
    private final TextFieldControl valueTextFieldControl;

    /**
     * @param parent the parent control.
     * @param min the minimum value.
     * @param max the maximum value.
     * @param startingValue the starting value.
     * @param textFieldWidth the width of the text field.
     */
    public RangeControl(@Nonnull IControl parent, int min, int max, int startingValue, int textFieldWidth)
    {
        super(parent, 0, 0, parent.getWidth() - parent.getPadding(Side.LEFT) - parent.getPadding(Side.RIGHT), 20);
        this.min = min;
        this.max = max;
        this.textFieldWidth = textFieldWidth;
        this.sliderWidth = width - textFieldWidth - 4;

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
                        RangeControl.this.setValue(Integer.parseInt(getValue()));
                    }
                    catch (NumberFormatException e)
                    {
                        RangeControl.this.setValue(RangeControl.this.getValue());
                    }
                }

                super.setFocus(focus);
            }
        };
        valueTextFieldControl.setMaxLength(10);
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
    public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
//        screen.renderTooltip(matrixStack, new StringTextComponent(String.valueOf(value)), mouseX, mouseY);
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
     * Recalculates the grabber position.
     */
    private void recalcGrabberPosition()
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
    private int calcValueFromPercentage(float percentage)
    {
        return Math.round((percentage * (max - min)) + min);
    }

    /**
     * @return the current value of the range.
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Sets the current value of the range.
     */
    public void setValue(int value)
    {
        this.value = Math.max(min, Math.min(max, value));

        valueTextFieldControl.setValue(String.valueOf(this.value));

        percentage = (float) (this.value - min) / (float) (max - min);

        recalcGrabberPosition();
    }

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
    private static class GrabberControl extends Control
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
        public void preRender(int mouseX, int mouseY, float partialTicks)
        {
            if (isDragging())
            {
                int value = rangeControl.calcValueFromPercentage(rangeControl.calcPercentageFromMouse(mouseX));

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
            rangeControl.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }
}
