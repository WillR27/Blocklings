package com.willr27.blocklings.client.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.Control;
import com.willr27.blocklings.client.gui.GuiTexture;
import com.willr27.blocklings.client.gui.IControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A simple control to display a constant texture.
 */
@OnlyIn(Dist.CLIENT)
public class TexturedControl extends Control
{
    /**
     * The texture to render.
     */
    @Nonnull
    public GuiTexture texture;

    /**
     * @param parent the parent control.
     * @param x the x position.
     * @param y the y position.
     * @param texture the texture to render.
     */
    public TexturedControl(@Nonnull IControl parent, int x, int y, GuiTexture texture)
    {
        super(parent, x, y, texture.width, texture.height);
        this.texture = texture;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderTexture(matrixStack, texture);
    }
}
