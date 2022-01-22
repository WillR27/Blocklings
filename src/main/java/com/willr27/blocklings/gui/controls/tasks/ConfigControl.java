package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.IControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Used as a base class for displaying config control.s
 */
@OnlyIn(Dist.CLIENT)
public abstract class ConfigControl extends Control
{
    /**
     * @param parent the parent control.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     * @param height the height.
     */
    public ConfigControl(@Nonnull IControl parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
    }

    /**
     * Renders the config control.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param partialTicks the partial ticks.
     */
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {

    }

    /**
     * Renders the config control's tooltips.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     */
    public void renderTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {

    }
}
