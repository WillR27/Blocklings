package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui2.GuiTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

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
    protected final GuiTexture texture;

    /**
     * @param texture the texture to use for the background.
     */
    public TexturedControl(@Nonnull GuiTexture texture)
    {
        super();
        this.texture = texture;

        setWidth(texture.width);
        setHeight(texture.height);
    }

    @Override
    protected void onRenderBackground(@Nonnull RenderArgs renderArgs)
    {
        super.onRenderBackground(renderArgs);

        renderTexture(renderArgs.matrixStack, texture);
    }
}
