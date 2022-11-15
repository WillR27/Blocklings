package com.willr27.blocklings.client.gui.util;

import com.willr27.blocklings.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Contains useful methods related to guis.
 */
@OnlyIn(Dist.CLIENT)
public abstract class GuiUtil
{
    /**
     * The instance of {@link GuiUtil}.
     */
    @Nullable
    private static GuiUtil instance;

    /**
     * @return the instance of {@link GuiUtil}.
     */
    @Nonnull
    public static GuiUtil getInstance()
    {
        if (instance == null)
        {
            if (Minecraft.getInstance() != null)
            {
                instance = new FullGuiUtil();
            }
            else
            {
                instance = new TestGuiUtil();
            }
        }

        return instance;
    }

    /**
     * @return the current gui scale.
     */
    public abstract float getGuiScale();

    /**
     * @return the current mouse pixel x coordinate.
     */
    public abstract int getPixelMouseX();

    /**
     * @return the current mouse pixel y coordinate.
     */
    public abstract int getPixelMouseY();
}
