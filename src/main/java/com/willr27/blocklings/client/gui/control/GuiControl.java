package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
        RenderSystem.enableBlend();
    }

    /**
     * Renders a rectangle with the given width and height centered on the given position.
     *
     * @param matrixStack the matrix stack.
     * @param x the x position to render at.
     * @param y the y position to render at.
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     * @param colour the colour of the rectangle.
     */
    public void renderCenteredRectangle(@Nonnull MatrixStack matrixStack, double x, double y, double width, double height, int colour)
    {
        Matrix4f matrix = matrixStack.last().pose();

        float x1 = (float) (x - width / 2.0);
        float y1 = (float) (y - height / 2.0);
        float x2 = (float) (x + width / 2.0);
        float y2 = (float) (y + height / 2.0);

        float f3 = (float)(colour >> 24 & 255) / 255.0F;
        float f = (float)(colour >> 16 & 255) / 255.0F;
        float f1 = (float)(colour >> 8 & 255) / 255.0F;
        float f2 = (float)(colour & 255) / 255.0F;

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.vertex(matrix, x1, y2, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, x2, y1, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, x1, y1, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.end();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Renders a texture using the given matrix stack.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     */
    protected void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull Texture texture)
    {
        GuiUtil.get().bindTexture(texture.resourceLocation);
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
    protected void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull Texture texture, double x, double y, double scaleX, double scaleY)
    {
        matrixStack.pushPose();
        matrixStack.translate((int) Math.round(x), (int) Math.round(y), 0.0);
        matrixStack.scale((float) scaleX, (float) scaleY, 1.0f);
        renderTexture(matrixStack, texture);
        matrixStack.popPose();
    }

    /**
     * Renders shadowed text using the given matrix stack.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param colour the colour of the text.
     */
    protected void renderShadowedText(@Nonnull MatrixStack matrixStack, @Nonnull IReorderingProcessor text, int colour)
    {
        GuiUtil.get().renderShadowedText(matrixStack, text, 0, 0, colour);
        RenderSystem.enableAlphaTest();
    }

    /**
     * Renders shadowed text using the given matrix stack and coordinates.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param colour the colour of the text.
     */
    protected void renderShadowedText(@Nonnull MatrixStack matrixStack, @Nonnull IReorderingProcessor text, int x, int y, int colour)
    {
        GuiUtil.get().renderShadowedText(matrixStack, text, x, y, colour);
        RenderSystem.enableAlphaTest();
    }

    /**
     * Renders text using the given matrix stack.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param colour the colour of the text.
     */
    protected void renderText(@Nonnull MatrixStack matrixStack, @Nonnull IReorderingProcessor text, int colour)
    {
        GuiUtil.get().renderText(matrixStack, text, 0, 0, colour);
        RenderSystem.enableAlphaTest();
    }

    /**
     * Renders a tooltip at the mouse position.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param pixelScaleX the x pixel scale.
     * @param pixelScaleY the y pixel scale.
     * @param tooltip the tooltip to render.
     */
    public void renderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, double pixelScaleX, double pixelScaleY, @Nonnull ITextComponent tooltip)
    {
        List<IReorderingProcessor> tooltip2 = new ArrayList<>();
        tooltip2.add(tooltip.getVisualOrderText());
        renderTooltip(matrixStack, mouseX, mouseY, pixelScaleX, pixelScaleY, tooltip2);
    }

    /**
     * Renders a tooltip at the mouse position.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param pixelScaleX the x pixel scale.
     * @param pixelScaleY the y pixel scale.
     * @param tooltip the tooltip to render.
     */
    public void renderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, double pixelScaleX, double pixelScaleY, @Nonnull List<IReorderingProcessor> tooltip)
    {
        Minecraft.getInstance().screen.renderTooltip(matrixStack, tooltip, (int) (mouseX / pixelScaleX), (int) (mouseY / pixelScaleY));
    }
}
