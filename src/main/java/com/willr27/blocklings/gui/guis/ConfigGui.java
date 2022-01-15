package com.willr27.blocklings.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

public abstract class ConfigGui extends AbstractGui
{
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {

    }

    public void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {

    }

    public  boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        return false;
    }

    public  boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        return false;
    }
}
