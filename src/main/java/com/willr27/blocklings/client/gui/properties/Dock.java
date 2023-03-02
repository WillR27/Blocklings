package com.willr27.blocklings.client.gui.properties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Possible docking options.
 */
@OnlyIn(Dist.CLIENT)
public enum Dock
{
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
    FILL,
}
