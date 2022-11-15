package com.willr27.blocklings.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Contains test implementations for methods in {@link GuiUtil}.
 */
@OnlyIn(Dist.CLIENT)
public class TestGuiUtil extends GuiUtil
{
    @Override
    public float getGuiScale()
    {
        return 1.0f;
    }

    @Override
    public int getPixelMouseX()
    {
        return 0;
    }

    @Override
    public int getPixelMouseY()
    {
        return 0;
    }
}
