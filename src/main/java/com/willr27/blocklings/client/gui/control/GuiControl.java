package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A facade for the underlying {@link AbstractGui}. This makes using {@link Control} easier.
 */
@OnlyIn(Dist.CLIENT)
public abstract class GuiControl extends AbstractGui
{
    /**
     * Renders a rectangle with a solid colour at the given position with the size.
     *
     * @param matrixStack the matrix stack.
     * @param x the x position to render at.
     * @param y the y position to render at.
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     * @param colour the colour of the rectangle.
     */
    public void renderRectangle(@Nonnull MatrixStack matrixStack, double x, double y, int width, int height, int colour)
    {
        fill(matrixStack, (int) Math.round(x), (int) Math.round(y), (int) Math.round(x + width), (int) Math.round(y + height), colour);
    }

    /**
     * Renders a texture using the given matrix stack.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     */
    protected void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture)
    {
        GuiUtil.bindTexture(texture.texture);
        blit(matrixStack, 0, 0, texture.x, texture.y, texture.width, texture.height);
    }

    /**
     * Renders a texture at the given pixel position at the given scale.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     * @param x the x position to render at.
     * @param y the y position to render at.
     * @param scaleX the x scale to render at.
     * @param scaleY the y scale to render at.
     */
    protected void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture, double x, double y, double scaleX, double scaleY)
    {
        matrixStack.pushPose();
        matrixStack.translate((int) Math.round(x), (int) Math.round(y), 0.0);
        matrixStack.scale((float) scaleX, (float) scaleY, 1.0f);
        renderTexture(matrixStack, texture);
        matrixStack.popPose();
    }
}
