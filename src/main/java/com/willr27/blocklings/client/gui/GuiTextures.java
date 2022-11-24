package com.willr27.blocklings.client.gui;

import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A collection of textures.
 */
@OnlyIn(Dist.CLIENT)
public class GuiTextures
{
    public static class Tabs
    {
        public static final ResourceLocation TABS = new BlocklingsResourceLocation("textures/gui/tabs.png");
        public static final GuiTexture UNSELECTED_BACKGROUND_TEXTURE_LEFT = new GuiTexture(TABS, 0, 0, 25, 28);
        public static final GuiTexture UNSELECTED_BACKGROUND_TEXTURE_RIGHT = new GuiTexture(TABS, 26, 0, 25, 28);
        public static final GuiTexture SELECTED_BACKGROUND_TEXTURE_LEFT = new GuiTexture(TABS, 52, 0, 32, 28);
        public static final GuiTexture SELECTED_BACKGROUND_TEXTURE_RIGHT = new GuiTexture(TABS, 85, 0, 32, 28);
    }

    public static class Stats
    {
        public static final ResourceLocation STATS = new BlocklingsResourceLocation("textures/gui/stats.png");
        public static final GuiTexture STATS_BACKGROUND = new GuiTexture(STATS, 0, 0, 176, 166);
    }
}
