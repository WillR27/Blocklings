package com.willr27.blocklings.gui.controls;

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
}
