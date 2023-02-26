package com.willr27.blocklings.client.gui3;

import com.mojang.blaze3d.matrix.MatrixStack;

import javax.annotation.Nonnull;

/**
 * The set of arguments that are passed around when rendering.
 */
public class RenderArgs
{
    /**
     * The current matrix stack used for rendering.
     */
    @Nonnull
    public final MatrixStack matrixStack;

    /**
     * The current scissor stack.
     */
    @Nonnull
    public ScissorStack scissorStack;

    /**
     * The mouse x pixel position on the screen.
     */
    public final int pixelMouseX;

    /**
     * The mouse y pixel position on the screen.
     */
    public final int pixelMouseY;

    /**
     * The percentage through the current tick.
     */
    public final float partialTicks;

    /**
     * @param matrixStack the current matrix stack used for rendering.
     * @param pixelMouseX the mouse x pixel position on the window.
     * @param pixelMouseY the mouse y pixel position on the window.
     * @param partialTicks the percentage through the current tick.
     */
    public RenderArgs(@Nonnull MatrixStack matrixStack, int pixelMouseX, int pixelMouseY, float partialTicks)
    {
        this.matrixStack = matrixStack;
        this.scissorStack = new ScissorStack();
        this.pixelMouseX = pixelMouseX;
        this.pixelMouseY = pixelMouseY;
        this.partialTicks = partialTicks;
    }
}
