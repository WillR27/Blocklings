package com.willr27.blocklings.client.gui.control;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents the side of a {@link Control}.
 */
@OnlyIn(Dist.CLIENT)
public enum Side
{
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
}
