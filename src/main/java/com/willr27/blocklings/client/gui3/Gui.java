package com.willr27.blocklings.client.gui3;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A facade for the underlying {@link AbstractGui}. This makes using {@link Control} easier.
 */
@OnlyIn(Dist.CLIENT)
public class Gui extends AbstractGui
{
    /**
     * The font rendered.
     */
    @Nonnull
    public final FontRenderer font = Minecraft.getInstance().font;

    /**
     * Renders a texture at the given position.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     * @param x the x position to render at.
     * @param y the y position to render at.
     */
    protected void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture, float x, float y)
    {
        GuiUtil.bindTexture(texture.texture);

        blit(matrixStack, (int) x, (int) y, texture.x, texture.y, texture.width, texture.height);
    }

    /**
     * Renders shadowed text using the given matrix stack.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param colour the colour of the text.
     */
    protected void renderShadowedText(@Nonnull MatrixStack matrixStack, @Nonnull ITextComponent text, int colour)
    {
        Screen.drawString(matrixStack, Minecraft.getInstance().font, text, 0, 0, colour);
        RenderSystem.enableAlphaTest();
    }

    /**
     * Renders text using the given matrix stack.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param colour the colour of the text.
     */
    protected void renderText(@Nonnull MatrixStack matrixStack, @Nonnull ITextComponent text, int colour)
    {
        font.draw(matrixStack, text, 0, 0, colour);
        RenderSystem.enableAlphaTest();
    }
}
