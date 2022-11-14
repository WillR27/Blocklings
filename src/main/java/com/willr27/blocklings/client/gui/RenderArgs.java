package com.willr27.blocklings.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import javax.annotation.Nonnull;
import java.util.Stack;

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
     * The mouse x position on the screen. This includes the scaling done by the gui scale option.
     */
    public final int screenMouseX;

    /**
     * The mouse y position on the screen. This includes the scaling done by the gui scale option.
     */
    public final int screenMouseY;

    /**
     * The percentage through the current tick.
     */
    public final float partialTicks;

    /**
     * @param matrixStack the current matrix stack used for rendering.
     * @param screenMouseX the mouse x position on the window.
     * @param screenMouseY the mouse y position on the window.
     * @param partialTicks the percentage through the current tick.
     */
    public RenderArgs(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        this.matrixStack = matrixStack;
        this.scissorStack = new ScissorStack();
        this.screenMouseX = screenMouseX;
        this.screenMouseY = screenMouseY;
        this.partialTicks = partialTicks;
    }
}
