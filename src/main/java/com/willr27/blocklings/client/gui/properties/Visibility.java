package com.willr27.blocklings.client.gui.properties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents the types of visibility for a control.
 */
@OnlyIn(Dist.CLIENT)
public enum Visibility
{
    /**
     * The control is visible.
     */
    VISIBLE,

    /**
     * The control is invisible but will still take up space.
     */
    INVISIBLE,

    /**
     * The control is invisible and will not take up space.
     */
    COLLAPSED,
}
