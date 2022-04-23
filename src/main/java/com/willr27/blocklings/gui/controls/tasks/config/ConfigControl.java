package com.willr27.blocklings.gui.controls.tasks.config;

import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.IControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Used as a base class for displaying config controls.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ConfigControl extends Control
{
    /**
     * @param parent the parent control.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     * @param height the height.
     */
    public ConfigControl(@Nonnull IControl parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
    }
}
