package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.VerticalAlignment;
import com.willr27.blocklings.client.gui.control.event.events.input.CharEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
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
public class TextFieldControl extends TextControl
{
    /**
     * The underlying {@link TextFieldWidget};
     */
    @Nonnull
    protected final TextFieldWidget textFieldWidget = new TextFieldWidget(font, 0, 0, 100, 20, new StringTextComponent("message?"));

    /**
     */
    public TextFieldControl()
    {
        super();

        textFieldWidget.setBordered(false);
        textFieldWidget.setHeight(font.lineHeight);

        textFieldWidget.setResponder((text) ->
        {
            recalcTextPosition();
        });

        onSizeChanged.subscribe((e) ->
        {
            textFieldWidget.setWidth((int) (getScreenWidth() - ((getPadding(Side.LEFT) + getPadding(Side.RIGHT)) * getCumulativeScale())));
        });

        setX(getX());
        setY(getY());
        setWidth(getWidth());
        setHeight(20);
        setPadding(6, 3, 6, 3);
        setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    @Override
    protected void recalcTextPosition()
    {
        switch (getHorizontalAlignment())
        {
            case LEFT:
                textFieldWidget.x = (int) (getScreenX() + (getPadding(Side.LEFT) * getCumulativeScale()));
                break;
            case MIDDLE:
                textFieldWidget.x = (int) (getScreenX() + (getScreenWidth() / 2) - (font.width(getText()) / 2));
                break;
            case RIGHT:
                textFieldWidget.x = (int) (getScreenX() + getScreenWidth() - font.width(getText()) - (getPadding(Side.RIGHT) * getCumulativeScale()));
                break;
        }

        switch (getVerticalAlignment())
        {
            case TOP:
                textFieldWidget.y = (int) (getScreenY() + (getPadding(Side.TOP) * getCumulativeScale()));
                break;
            case MIDDLE:
                textFieldWidget.y = (int) (getScreenY() + (getScreenHeight() / 2) - (font.lineHeight / 2));
                break;
            case BOTTOM:
                textFieldWidget.y = (int) (getScreenY() + (getScreenHeight() - font.lineHeight) - (getPadding(Side.BOTTOM) * getCumulativeScale()));
                break;
        }
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
        if (keyEvent.keyCode == GLFW.GLFW_KEY_ENTER || keyEvent.keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (getScreen() != null)
            {
                getScreen().setFocusedControl(null);
            }

            keyEvent.setIsHandled(true);
        }
        else
        {
            keyEvent.setIsHandled(textFieldWidget.keyPressed(keyEvent.keyCode, keyEvent.scanCode, keyEvent.modifiers));
        }
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
    public void onFocused()
    {
        textFieldWidget.setFocus(true);
    }

    @Override
    public void onUnfocused()
    {
        textFieldWidget.setFocus(false);
    }

    @Override
    @Nonnull
    public String getText()
    {
        return textFieldWidget.getValue();
    }

    @Override
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
}
