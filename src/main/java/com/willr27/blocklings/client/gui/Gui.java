package com.willr27.blocklings.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A facade for the underlying {@link AbstractGui}. This makes using {@link Control} easier.
 */
@OnlyIn(Dist.CLIENT)
public class Gui extends AbstractGui
{
    /**
     * Renders a texture at the given position.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     * @param x the x position to render at.
     * @param y the y position to render at.
     */
    protected void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture, int x, int y)
    {
        GuiUtil.bindTexture(texture.texture);
        blit(matrixStack, x, y, texture.x, texture.y, texture.width, texture.height);
    }
}
