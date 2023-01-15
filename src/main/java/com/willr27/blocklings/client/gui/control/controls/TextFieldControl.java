package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.VerticalAlignment;
import com.willr27.blocklings.client.gui.control.event.events.input.CharEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A wrapper around a {@link TextFieldWidget}
 */
@OnlyIn(Dist.CLIENT)
public class TextFieldControl extends TextControl
{
    /**
     * Whether to render the text field's background.
     */
    private boolean shouldRenderBackground = true;

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
        textFieldWidget.setHeight(getLineHeight());

        textFieldWidget.setResponder((text) ->
        {
            recalcTextPosition();
        });

        onSizeChanged.subscribe((e) ->
        {
            textFieldWidget.setWidth((int) (getScreenWidth() - ((getPadding(Side.LEFT) + getPadding(Side.RIGHT)) * getCumulativeScale())));
            textFieldWidget.moveCursorToStart();
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
                textFieldWidget.y = (int) (getScreenY() + (getScreenHeight() / 2) - (getLineHeight() / 2));
                break;
            case BOTTOM:
                textFieldWidget.y = (int) (getScreenY() + (getScreenHeight() - getLineHeight()) - (getPadding(Side.BOTTOM) * getCumulativeScale()));
                break;
        }
    }

    @Override
    public void onTick()
    {
        textFieldWidget.tick();
    }

    @Override
    public void onRenderBackground(@Nonnull RenderArgs renderArgs)
    {
        super.onRenderBackground(renderArgs);

        if (shouldRenderBackground())
        {
            renderRectangle(renderArgs.matrixStack, isFocused() ? 0xffdddddd : 0xffaaaaaa);
            renderRectangle(renderArgs.matrixStack, 1, 1, (int) (getWidth() - 2), (int) (getHeight() - 2), 0xff111111);
        }
    }

    @Override
    public void onRender(@Nonnull RenderArgs renderArgs)
    {
        float z = isDraggingOrAncestorIsDragging() ? 100.0f : -1.0f;

        try
        {
            // For some reason we can't just access the values in the matrix.
            // So we have to get the z translation via reflection. Nice.
            z = ObfuscationReflectionHelper.getPrivateValue(Matrix4f.class, renderArgs.matrixStack.last().pose(), "m23");
        }
        catch (Exception ex)
        {
//            Blocklings.LOGGER.warn(ex.toString());
        }

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0, 0, z);
        matrixStack.scale(1.0f, 1.0f, 1.0f);
        textFieldWidget.renderButton(matrixStack, renderArgs.pixelMouseX, renderArgs.pixelMouseY, renderArgs.partialTicks);
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
    public void onUnfocused(@Nullable Control focusedControl)
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

    @Override
    public void setLineHeight(int lineHeight)
    {
        super.setLineHeight(lineHeight);

        textFieldWidget.setHeight(getLineHeight());
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
