package com.willr27.blocklings.client.gui.properties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents a way in which a row/column in a grid can be defined.
 */
@OnlyIn(Dist.CLIENT)
public enum GridDefinition
{
    /**
     * The row/column will be defined by a fixed size.
     */
    FIXED,

    /**
     * The row/column will be defined by the size of the largest child.
     */
    AUTO,

    /**
     * The row/column will be defined by a ratio of the remaining size.
     */
    RATIO,
}
