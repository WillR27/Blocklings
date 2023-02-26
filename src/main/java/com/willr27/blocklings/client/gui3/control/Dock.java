package com.willr27.blocklings.client.gui3.control;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The available docking options for a control.
 */
@OnlyIn(Dist.CLIENT)
public enum Dock
{
    NONE,
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
    FILL,
}
