package com.willr27.blocklings.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Contains fully implemented methods for {@link GuiUtil}.
 */
@OnlyIn(Dist.CLIENT)
public class FullGuiUtil extends GuiUtil
{
    @Override
    public float getGuiScale()
    {
        return (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    @Override
    public int getPixelMouseX()
    {
        return (int) Minecraft.getInstance().mouseHandler.xpos();
    }

    @Override
    public int getPixelMouseY()
    {
        return (int) Minecraft.getInstance().mouseHandler.ypos();
    }
}
