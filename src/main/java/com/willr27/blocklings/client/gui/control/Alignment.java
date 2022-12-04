package com.willr27.blocklings.client.gui.control;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Specifies how a control is position within its parent.
 */
@OnlyIn(Dist.CLIENT)
public class Alignment
{
    /**
     * The current alignment percentage.
     */
    public final float percent;

    /**
     * @param percent the alignment percentage.
     */
    public Alignment(float percent)
    {
        this.percent = percent;
    }
}
