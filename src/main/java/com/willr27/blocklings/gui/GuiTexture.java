package com.willr27.blocklings.gui;

import net.minecraft.util.ResourceLocation;

public class GuiTexture
{
    public final ResourceLocation texture;
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public GuiTexture(ResourceLocation texture, int x, int y, int width, int height)
    {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GuiTexture width(int newWidth)
    {
        return new GuiTexture(texture, x, y, newWidth, height);
    }

    public GuiTexture height(int newHeight)
    {
        return new GuiTexture(texture, x, y, width, newHeight);
    }

    public GuiTexture shift(int dx, int dy)
    {
        return new GuiTexture(texture, x + dx, y + dy, width, height);
    }

    public GuiTexture resize(int dw, int dh)
    {
        return new GuiTexture(texture, x, y, width + dw, height + dh);
    }

    public static class GoalGuiTexture extends GuiTexture
    {
        public static final int ICON_SIZE = 20;
        public static final int ICON_TEXTURE_Y = 186;

        public GoalGuiTexture(int x, int y)
        {
            super(GuiTextures.TASKS, x * ICON_SIZE, y * ICON_SIZE + ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE);
        }
    }
}
