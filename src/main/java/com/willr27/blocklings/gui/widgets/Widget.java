package com.willr27.blocklings.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

public class Widget extends AbstractGui
{
    public final Screen screen;

    public FontRenderer font;
    public int x, y;
    public int width, height;
    public boolean isPressed = false;

    public Widget(FontRenderer font, int x, int y, int width, int height)
    {
        this.screen = Minecraft.getInstance().screen;
        this.font = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {

    }

    public void renderText(MatrixStack matrixStack, String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -font.width(text) - dx : width + dx;
        drawString(matrixStack, font, text, x + bonusX, y + dy, colour);
        RenderSystem.enableDepthTest(); // Apparently depth test gets turned off so turn it back on
    }

    public void renderCenteredText(MatrixStack matrixStack, String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -dx : width + dx;
        drawCenteredString(matrixStack, font, text, x + bonusX, y + dy, colour);
        RenderSystem.enableDepthTest(); // Apparently depth test gets turned off so turn it back on
    }

    public void renderTexture(MatrixStack matrixStack, GuiTexture texture)
    {
        renderTexture(matrixStack, 0, 0, texture);
    }

    public void renderTexture(MatrixStack matrixStack, int dx, int dy, GuiTexture texture)
    {
        GuiUtil.bindTexture(texture.texture);
        renderTexture(matrixStack, texture, x + dx, y + dy);
    }

    public void renderTexture(MatrixStack matrixStack, GuiTexture texture, int x, int y)
    {
        GuiUtil.bindTexture(texture.texture);
        blit(matrixStack, x, y, texture.x, texture.y, texture.width, texture.height);
    }

    public void enableScissor()
    {
        GuiUtil.scissor(x, y, width, height);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int state)
    {
        if (isMouseOver(mouseX, mouseY))
        {
            isPressed = true;
        }

        return false;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        isPressed = false;

        return false;
    }

    public boolean mouseScrolled(int mouseX, int mouseY, double scroll)
    {
        return false;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return GuiUtil.isMouseOver(mouseX, mouseY, x, y, width, height);
    }

    public boolean isMouseOver(int mouseX, int mouseY, float scale)
    {
        return GuiUtil.isMouseOver((int) (mouseX / scale), (int) (mouseY / scale), x, y, width, height);
    }

    public int toLocalX(int x)
    {
        return x - this.x;
    }

    public int toLocalY(int y)
    {
        return y - this.y;
    }
}
