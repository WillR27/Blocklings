package com.willr27.blocklings.client.gui3.control.controls;

import com.willr27.blocklings.client.gui3.RenderArgs;
import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui2.GuiTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A control with a texture for the background that is initially sized to fit that texture.
 */
@OnlyIn(Dist.CLIENT)
public class TexturedControl extends Control
{
    /**
     * The background texture.
     */
    @Nonnull
    private GuiTexture texture;

    /**
     * The pressed background texture.
     */
    @Nullable
    private GuiTexture pressedTexture;

    /**
     * @param texture the texture to use for the background.
     */
    public TexturedControl(@Nonnull GuiTexture texture)
    {
        this(texture, null);
    }

    /**
     * @param texture the texture to use for the background.
     * @param pressedTexture the texture to use for the background when pressed.
     */
    public TexturedControl(@Nonnull GuiTexture texture, @Nullable GuiTexture pressedTexture)
    {
        super();
        this.texture = texture;
        this.pressedTexture = pressedTexture;

        setWidth(texture.width);
        setHeight(texture.height);
    }

    @Override
    public void onRenderBackground(@Nonnull RenderArgs renderArgs)
    {
        super.onRenderBackground(renderArgs);

        renderTexture(renderArgs.matrixStack, pressedTexture != null && isPressed() && !(isDraggingOrAncestorIsDragging() && !isDragging()) ? pressedTexture : texture);
    }

    /**
     * @return the texture.
     */
    @Nonnull
    public GuiTexture getTexture()
    {
        return texture;
    }

    /**
     * Sets the texture.
     */
    public void setTexture(@Nonnull GuiTexture texture)
    {
        this.texture = texture;
    }

    /**
     * @return the pressed texture.
     */
    @Nullable
    public GuiTexture getPressedTexture()
    {
        return pressedTexture;
    }

    /**
     * Sets the pressed texture.
     */
    public void setPressedTexture(@Nullable GuiTexture pressedTexture)
    {
        this.pressedTexture = pressedTexture;
    }
}
