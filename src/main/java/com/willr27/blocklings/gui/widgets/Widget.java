package com.willr27.blocklings.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.Control;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents a gui that has a location and dimensions.
 *
 */
@OnlyIn(Dist.CLIENT)
@Deprecated
public class Widget extends Control
{
    @Deprecated
    public Widget(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    @Deprecated
    public Widget(FontRenderer font, int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public void renderText(MatrixStack matrixStack, String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -font.width(text) - dx : width + dx;
        drawString(matrixStack, font, text, screenX + bonusX, screenY + dy, colour);
        RenderSystem.enableDepthTest(); // Apparently depth test gets turned off so turn it back on
    }

    public void renderCenteredText(MatrixStack matrixStack, String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -dx : width + dx;
        drawCenteredString(matrixStack, font, text, screenX + bonusX, screenY + dy, colour);
        RenderSystem.enableDepthTest(); // Apparently depth test gets turned off so turn it back on
    }
}
