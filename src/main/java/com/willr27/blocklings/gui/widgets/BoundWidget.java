package com.willr27.blocklings.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

public class BoundWidget extends TexturedWidget
{
    public BoundWidget(FontRenderer font, int x, int y, int width, int height, int textureX, int textureY)
    {
        super(font, x, y, width, height, textureX, textureY);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        blit(matrixStack, x, y, textureX, textureY, width, height);
    }
}
