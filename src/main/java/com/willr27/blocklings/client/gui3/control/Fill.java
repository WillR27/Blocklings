package com.willr27.blocklings.client.gui3.control;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Specifies how a control is sized relative to its parent.
 */
@OnlyIn(Dist.CLIENT)
public class Fill
{
    /**
     * The current fill percentage.
     */
    public final float percent;

    /**
     * @param percent the fill percentage.
     */
    public Fill(float percent)
    {
        this.percent = percent;
    }
}
