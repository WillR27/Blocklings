package com.willr27.blocklings.client.gui.control;

import com.willr27.blocklings.client.gui.IScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The root control for each screen.
 */
@OnlyIn(Dist.CLIENT)
public class RootControl extends Control
{
    /**
     * @param screen the screen the control belongs to.
     */
    public RootControl(@Nonnull IScreen screen)
    {
        this.screen = screen;
    }
}
