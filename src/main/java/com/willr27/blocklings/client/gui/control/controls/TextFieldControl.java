package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.input.CharEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A wrapper around a {@link TextFieldWidget}
 */
@OnlyIn(Dist.CLIENT)
public class TextFieldControl extends Control
{
    /**
     * The underlying {@link TextFieldWidget};
     */
    @Nonnull
    private final TextFieldWidget textFieldWidget = new TextFieldWidget(Minecraft.getInstance().font, 0, 0, 100, 20, new StringTextComponent("message?"));

    /**
     */
    public TextFieldControl()
    {
        onPositionChanged.subscribe((e) ->
        {
            textFieldWidget.x = getScreenX();
            textFieldWidget.y = getScreenY();
        });

        onSizeChanged.subscribe((e) ->
        {
            textFieldWidget.setWidth(getScreenWidth());
            textFieldWidget.setHeight(getScreenHeight());
        });

        setX(getX());
        setY(getY());
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    protected void onTick()
    {
        textFieldWidget.tick();
    }

    @Override
    protected void onRender(@Nonnull RenderArgs renderArgs)
    {
        textFieldWidget.renderButton(new MatrixStack(), renderArgs.pixelMouseX, renderArgs.pixelMouseY, renderArgs.partialTicks);
    }

    @Override
    protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        textFieldWidget.mouseClicked(toScreenX(mouseButtonEvent.mousePixelX), toScreenY(mouseButtonEvent.mousePixelY), mouseButtonEvent.mouseButton);

        mouseButtonEvent.setIsHandled(true);
    }

    @Override
    protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        textFieldWidget.mouseReleased(toScreenX(mouseButtonEvent.mousePixelX), toScreenY(mouseButtonEvent.mousePixelY), mouseButtonEvent.mouseButton);

        mouseButtonEvent.setIsHandled(true);
    }

    @Override
    public void onKeyPressed(@Nonnull KeyEvent keyEvent)
    {
        keyEvent.setIsHandled(textFieldWidget.keyPressed(keyEvent.keyCode, keyEvent.scanCode, keyEvent.modifiers));
    }

    @Override
    public void onKeyReleased(@Nonnull KeyEvent keyEvent)
    {
        keyEvent.setIsHandled(textFieldWidget.keyReleased(keyEvent.keyCode, keyEvent.scanCode, keyEvent.modifiers));
    }

    @Override
    public void onCharTyped(@Nonnull CharEvent charEvent)
    {
        charEvent.setIsHandled(textFieldWidget.charTyped(charEvent.character, charEvent.keyCode));
    }

    @Override
    public void onFocused(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        textFieldWidget.setFocus(true);
    }

    @Override
    public void onUnfocused(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        textFieldWidget.setFocus(false);
    }

    /**
     * Sets the text inside the text field.
     */
    public void setText(@Nonnull String text)
    {
        textFieldWidget.setValue(text);
    }
}
