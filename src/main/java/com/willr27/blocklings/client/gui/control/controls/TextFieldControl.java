package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.SizeChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.TextChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.*;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

/**
 * A wrapper around a {@link TextFieldWidget}
 */
@OnlyIn(Dist.CLIENT)
public class TextFieldControl extends Control
{
    /**
     * Whether to render the text field's background.
     */
    private boolean shouldRenderBackground = true;

    /**
     * The underlying {@link TextFieldWidget};
     */
    @Nonnull
    protected final TextFieldWidget textFieldWidget = new TextFieldWidget(Minecraft.getInstance().font, 0, 0, 100, GuiUtil.get().getLineHeight() - 1, new StringTextComponent("message?"));

    /**
     * The amount to offset the text field's x position by.
     */
    private double textFieldXRemainder = 0.0;

    /**
     * The amount to offset the text field's y position by.
     */
    private double textFieldYRemainder = 0.0;

    /**
     * The colour of the text field's border.
     */
    private int borderColour = 0xffcccccc;

    /**
     * The colour of the text field's border when focused.
     */
    private int borderFocusedColour = 0xffffffff;

    /**
     */
    public TextFieldControl()
    {
        super();

        textFieldWidget.setBordered(false);
        textFieldWidget.setFilter(this::isValidText);
        textFieldWidget.setResponder((text) ->
        {
            setText(text);
        });

        eventBus.subscribe((BaseControl control, SizeChangedEvent e) ->
        {
            textFieldWidget.moveCursorToStart();
        });

        setHorizontalContentAlignment(0.0);
        setVerticalContentAlignment(0.5);
        setPadding(6, 3, 6, 3);
        setHeight(20.0);
        setBackgroundColour(0xff111111);
    }

    /**
     * Recalculates the position of the {@link TextFieldControl#textFieldWidget} based on the position of this control.
     */
    private void recalcTextFieldWidget()
    {
        textFieldWidget.setWidth((int) Math.min(getPixelWidthWithoutPadding() / getGuiScale(), (getPixelWidthWithoutPadding() / getGuiScale())));

        double screenX = ((getPixelX() / getGuiScale()) + Math.max(0.0, getWidthWithoutPadding() - GuiUtil.get().getTextWidth(getText())) * getHorizontalContentAlignment()) + getPadding().left;
        double screenY = ((getPixelY() / getGuiScale()) + (getHeightWithoutPadding() - textFieldWidget.getHeight()) * getVerticalContentAlignment()) + getPadding().top;
        textFieldWidget.x = (int) screenX;
        textFieldWidget.y = (int) screenY;
        textFieldXRemainder = screenX - textFieldWidget.x;
        textFieldYRemainder = screenY - textFieldWidget.y;
    }

    @Override
    public void onTick()
    {
        textFieldWidget.tick();
    }

    @Override
    public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        // This would be better done using events, but there aren't currently events for when pixel sizes and positions change.
        recalcTextFieldWidget();

        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        if (shouldRenderBackground())
        {
            renderRectangleAsBackground(matrixStack, getBackgroundColourInt(), 1, 1, (int) (getWidth() - 2), (int) (getHeight() - 2));
        }

        float z = isDraggingOrAncestor() ? (float) getDraggedControl().getDragZ() : (float) getRenderZ();

        try
        {
            // For some reason we can't just access the values in the matrix.
            // So we have to get the z translation via reflection. Nice.
            z = ObfuscationReflectionHelper.getPrivateValue(Matrix4f.class, matrixStack.last().pose(), "m23");
        }
        catch (Exception ex)
        {
//            Blocklings.LOGGER.warn(ex.toString());
        }

        MatrixStack textFieldMatrixStack = new MatrixStack();
        textFieldMatrixStack.translate(textFieldXRemainder, textFieldYRemainder, z);
        textFieldWidget.renderButton(textFieldMatrixStack, (int) Math.round(mouseX), (int) Math.round(mouseY), partialTicks);
        RenderSystem.enableDepthTest();

        // Reset the color to white so that the rest of the GUI renders properly.
        // The text field renders a blue highlight which can cascade into other controls.
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        if (shouldRenderBackground)
        {
            int borderColour = isFocused() ? getBorderFocusedColour() : getBorderColour();
            renderRectangleAsBackground(matrixStack, borderColour, 0, 0, 1, getHeight());
            renderRectangleAsBackground(matrixStack, borderColour, getWidth() - 1, 0, 1, getHeight());
            renderRectangleAsBackground(matrixStack, borderColour, 0, 0, getWidth(), 1);
            renderRectangleAsBackground(matrixStack, borderColour, 0, getHeight() - 1, getWidth(), 1);
        }
    }

    @Override
    protected void onMouseClicked(@Nonnull MouseClickedEvent e)
    {
        double textFieldMouseX = e.mouseX / getGuiScale();
        double textFieldMouseY = e.mouseY / getGuiScale();
        boolean beforeStartOfText = textFieldMouseX < (double)textFieldWidget.x;
        boolean afterEndOfText = textFieldMouseX > (double)(textFieldWidget.x + textFieldWidget.getWidth());

        textFieldWidget.mouseClicked(textFieldMouseX, textFieldMouseY, e.button);

        if (beforeStartOfText)
        {
            textFieldWidget.moveCursorToStart();
            textFieldWidget.setFocus(true);
        }
        else if (afterEndOfText)
        {
            textFieldWidget.moveCursorToEnd();
            textFieldWidget.setFocus(true);
        }

        e.setIsHandled(true);
    }

    @Override
    public void onMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        textFieldWidget.mouseReleased(e.mouseX / getGuiScale(), e.mouseY / getGuiScale(), e.button);

        e.setIsHandled(true);
    }

    @Override
    public void onKeyPressed(@Nonnull KeyPressedEvent e)
    {
        if (GuiUtil.get().isUnfocusTextFieldKey(e.keyCode))
        {
            if (getScreen() != null)
            {
                getScreen().setFocusedControl(null);
            }

            e.setIsHandled(true);
        }
        else
        {
            e.setIsHandled(textFieldWidget.keyPressed(e.keyCode, e.scanCode, e.modifiers) || textFieldWidget.isFocused());
        }
    }

    @Override
    public void onKeyReleased(@Nonnull KeyReleasedEvent e)
    {
        e.setIsHandled(textFieldWidget.keyReleased(e.keyCode, e.scanCode, e.modifiers));
    }

    @Override
    public void onCharTyped(@Nonnull CharTypedEvent e)
    {
        e.setIsHandled(textFieldWidget.charTyped(e.character, e.keyCode));
    }

    @Override
    public void onFocused()
    {
        textFieldWidget.setFocus(true);
    }

    @Override
    public void onUnfocused()
    {
        // Send a fake key input to clear the shiftPressed flag.
        onKeyPressed(new KeyPressedEvent(GLFW.GLFW_KEY_LEFT_CONTROL, 0, 0));

        textFieldWidget.setFocus(false);
        textFieldWidget.setCursorPosition(0);
        textFieldWidget.setHighlightPos(0);
    }

    @Nonnull
    @Override
    public Double getHorizontalContentAlignment()
    {
        return super.getHorizontalContentAlignment() != null ? super.getHorizontalContentAlignment() : 0.5;
    }

    @Nonnull
    @Override
    public Double getVerticalContentAlignment()
    {
        return super.getVerticalContentAlignment() != null ? super.getVerticalContentAlignment() : 0.5;
    }

    /**
     * @param text the text to validate.
     * @return whether the text is valid.
     */
    protected boolean isValidText(@Nonnull String text)
    {
        return true;
    }

    /**
     * Processes the text before it is set.
     */
    @Nonnull
    protected String processText(@Nonnull String text)
    {
        return text;
    }

    @Nonnull
    public String getText()
    {
        return textFieldWidget.getValue();
    }

    public void setText(@Nonnull String text)
    {
        text = processText(text);

        String oldText = textFieldWidget.getValue();

        if (!oldText.equals(text))
        {
            textFieldWidget.setValue(text);
            textFieldWidget.moveCursorToStart();
        }

        eventBus.post(this, new TextChangedEvent(oldText, text));
    }

    public void setText(@Nonnull ITextComponent text)
    {
        setText(text.getString());
    }

    /**
     * Sets the max text length for the text field.
     */
    public void setMaxTextLength(int maxTextLength)
    {
        textFieldWidget.setMaxLength(maxTextLength);
    }

    /**
     * @return whether to render the text field's background.
     */
    public boolean shouldRenderBackground()
    {
        return shouldRenderBackground;
    }

    /**
     * Sets whether to render the text field's background.
     */
    public void setShouldRenderBackground(boolean shouldRenderBackground)
    {
        this.shouldRenderBackground = shouldRenderBackground;
    }

    /**
     * @return the text field's border colour.
     */
    public int getBorderColour()
    {
        return borderColour;
    }

    /**
     * Sets the text field's border colour.
     */
    public void setBorderColour(int borderColour)
    {
        this.borderColour = borderColour;
    }

    /**
     * @return the text field's border colour when focused.
     */
    public int getBorderFocusedColour()
    {
        return borderFocusedColour;
    }

    /**
     * Sets the text field's border colour when focused.
     */
    public void setBorderFocusedColour(int borderFocusedColour)
    {
        this.borderFocusedColour = borderFocusedColour;
    }
}

