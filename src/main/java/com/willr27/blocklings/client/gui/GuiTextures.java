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

        public static final GuiTexture STATS = new GuiTexture(TABS, 0, 28, 22, 22);
        public static final GuiTexture TASKS = new GuiTexture(TABS, 22, 28, 22, 22);
        public static final GuiTexture EQUIPMENT = new GuiTexture(TABS, 44, 28, 22, 22);
        public static final GuiTexture GENERAL = new GuiTexture(TABS, 0, 50, 22, 22);
        public static final GuiTexture COMBAT = new GuiTexture(TABS, 22, 50, 22, 22);
        public static final GuiTexture MINING = new GuiTexture(TABS, 44, 50, 22, 22);
        public static final GuiTexture WOODCUTTING = new GuiTexture(TABS, 66, 50, 22, 22);
        public static final GuiTexture FARMING = new GuiTexture(TABS, 88, 50, 22, 22);
    }

    public static class Stats
    {
        public static final ResourceLocation STATS = new BlocklingsResourceLocation("textures/gui/stats.png");
        public static final GuiTexture BACKGROUND = new GuiTexture(STATS, 0, 0, 176, 166);
    }

    public static class Tasks
    {
        public static final ResourceLocation TASKS = new BlocklingsResourceLocation("textures/gui/tasks.png");
        public static final GuiTexture BACKGROUND = new GuiTexture(TASKS, 0, 0, 176, 166);
    }

    public static class Equipment
    {
        public static final ResourceLocation EQUIPMENT = new BlocklingsResourceLocation("textures/gui/equipment.png");
        public static final GuiTexture BACKGROUND = new GuiTexture(EQUIPMENT, 0, 0, 176, 166);
    }

    public static class Skills
    {
        public static final ResourceLocation SKILLS = new BlocklingsResourceLocation("textures/gui/skills.png");
        public static final GuiTexture BACKGROUND = new GuiTexture(SKILLS, 0, 0, 176, 166);
    }
}
