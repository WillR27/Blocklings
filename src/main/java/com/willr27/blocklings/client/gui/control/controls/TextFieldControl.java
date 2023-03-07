package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.SizeChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.*;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    protected final TextFieldWidget textFieldWidget = new TextFieldWidget(font, 0, 0, 100, font.lineHeight - 2, new StringTextComponent("message?"));

    /**
     */
    public TextFieldControl()
    {
        super();

        textFieldWidget.setBordered(false);

        eventBus.subscribe((BaseControl control, SizeChangedEvent e) ->
        {
            textFieldWidget.moveCursorToStart();
        });

        setHorizontalContentAlignment(0.0);
        setVerticalContentAlignment(0.5);
        setPadding(6, 3, 6, 3);
    }

    /**
     * Recalculates the position of the {@link TextFieldControl#textFieldWidget} based on the position of this control.
     */
    private void recalcTextFieldWidget()
    {
        textFieldWidget.setWidth((int) Math.min(getPixelWidthWithoutPadding() / getGuiScale(), (getPixelWidthWithoutPadding() / getGuiScale())));

        textFieldWidget.x = (int) (((getPixelX() / getGuiScale()) + Math.max(0.0, getWidthWithoutPadding() - font.width(getText())) * getHorizontalContentAlignment()) + getPadding().left);
        textFieldWidget.y = (int) (((getPixelY() / getGuiScale()) + (getHeightWithoutPadding() - textFieldWidget.getHeight()) * getVerticalContentAlignment()) + getPadding().top);
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
            // I have no idea why this is needed. Without it, the text field's text is rendered behind the background.
            RenderSystem.disableDepthTest();

            renderRectangleAsBackground(matrixStack, isFocused() ? 0xffdddddd : 0xffaaaaaa);
            renderRectangleAsBackground(matrixStack, 0xff111111, 1, 1, (int) (getWidth() - 2), (int) (getHeight() - 2));

            RenderSystem.enableDepthTest();
        }

        textFieldWidget.renderButton(new MatrixStack(), (int) Math.round(mouseX), (int) Math.round(mouseY), partialTicks);
    }

    @Override
    public void onMouseClicked(@Nonnull MouseClickedEvent e)
    {
        double textFieldMouseX = e.mouseX / getGuiScale();
        double textFieldMouseY = e.mouseY / getGuiScale();
        boolean flag = textFieldMouseX < (double)textFieldWidget.x;

        textFieldWidget.mouseClicked(textFieldMouseX, textFieldMouseY, e.button);

        if (flag)
        {
            textFieldWidget.moveCursorToStart();
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
        if (e.keyCode == GLFW.GLFW_KEY_ENTER || e.keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (getScreen() != null)
            {
                getScreen().setFocusedControl(null);
            }

            e.setIsHandled(true);
        }
        else
        {
            e.setIsHandled(textFieldWidget.keyPressed(e.keyCode, e.scanCode, e.modifiers));
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

    @Nonnull
    public String getText()
    {
        return textFieldWidget.getValue();
    }

    public void setText(@Nonnull String text)
    {
        textFieldWidget.setValue(text);
    }

    public void setText(@Nonnull ITextComponent text)
    {
        textFieldWidget.setValue(text.getString());
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
}

