package com.willr27.blocklings.client.gui3;

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
    public static class Common
    {
        public static final ResourceLocation COMMON = new BlocklingsResourceLocation("textures/gui/common_widgets.png");

        public static final GuiTexture NODE_UNPRESSED = new GuiTexture(COMMON, 46, 0, 12, 12);
        public static final GuiTexture NODE_PRESSED = new GuiTexture(COMMON, 58, 0, 12, 12);

        public static final GuiTexture ROUND_GRABBER_UNPRESSED = new GuiTexture(COMMON, 46, 0, 12, 12);
        public static final GuiTexture ROUND_GRABBER_PRESSED = new GuiTexture(COMMON, 58, 0, 12, 12);

        public static final GuiTexture SLIDER_BAR = new GuiTexture(COMMON, 0, 88, 256, 4);

        public static class Scrollbar
        {
            public static final GuiTexture GRABBER_UNPRESSED = new GuiTexture(COMMON, 0, 0, 12, 15);
            public static final GuiTexture GRABBER_PRESSED = new GuiTexture(COMMON, 12, 0, 12, 15);
        }

        public static class DropDown
        {
            public static final int BORDER_SIZE = 2;

            public static final GuiTexture DOWN_ARROW = new GuiTexture(COMMON, 24, 0, 11, 7);
            public static final GuiTexture UP_ARROW = new GuiTexture(COMMON, 35, 0, 11, 7);

            public static final GuiTexture SELECTED_BACKGROUND = new GuiTexture(COMMON, 0, 48, 256, 20);
            public static final GuiTexture UNSELECTED_BACKGROUND = new GuiTexture(COMMON, 0, 69, 256, 19);
        }

        public static class Tab
        {
            public static final int EDGE_WIDTH = 4;
            public static final int FULLY_OPAQUE_HEIGHT = 13;

            public static final GuiTexture TAB_SELECTED_BACKGROUND = new GuiTexture(COMMON, 0, 15, 256, 18);
            public static final GuiTexture TAB_UNSELECTED_BACKGROUND = new GuiTexture(COMMON, 0, 33, 256, 15);
        }
    }

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

        public static final GuiTexture HEALTH_BAR = new GuiTexture(STATS, 0, 228, 134, 5);

        public static final GuiTexture COMBAT_BAR_BACKGROUND = new XpBarTexture(1);
        public static final GuiTexture COMBAT_BAR_FOREGROUND = new XpBarTexture(0);
        public static final GuiTexture MINING_BAR_BACKGROUND = new XpBarTexture(3);
        public static final GuiTexture MINING_BAR_FOREGROUND = new XpBarTexture(2);
        public static final GuiTexture WOODCUTTING_BAR_BACKGROUND = new XpBarTexture(5);
        public static final GuiTexture WOODCUTTING_BAR_FOREGROUND = new XpBarTexture(4);
        public static final GuiTexture FARMING_BAR_BACKGROUND = new XpBarTexture(7);
        public static final GuiTexture FARMING_BAR_FOREGROUND = new XpBarTexture(6);

        public static final LevelIconsTexture COMBAT_LEVEL_ICONS = new LevelIconsTexture(0);
        public static final LevelIconsTexture MINING_LEVEL_ICONS = new LevelIconsTexture(1);
        public static final LevelIconsTexture WOODCUTTING_LEVEL_ICONS = new LevelIconsTexture(2);
        public static final LevelIconsTexture FARMING_LEVEL_ICONS = new LevelIconsTexture(3);

        public static final GuiTexture ATTACK_DAMAGE_MAIN = new StatIconTexture(12, 0);
        public static final GuiTexture ATTACK_DAMAGE_OFF = new StatIconTexture(10, 0);
        public static final GuiTexture ATTACK_SPEED = new StatIconTexture(11, 0);

        public static final GuiTexture ARMOUR = new StatIconTexture(5, 0);
        public static final GuiTexture ARMOUR_TOUGHNESS = new StatIconTexture(6, 0);
        public static final GuiTexture KNOCKBACK_RESISTANCE = new StatIconTexture(7, 0);

        public static final GuiTexture MINING_SPEED = new StatIconTexture(1, 0);
        public static final GuiTexture WOODCUTTING_SPEED = new StatIconTexture(2, 0);
        public static final GuiTexture FARMING_SPEED = new StatIconTexture(3, 0);

        public static final GuiTexture MOVE_SPEED = new StatIconTexture(8, 0);

        public static class StatIconTexture extends GuiTexture
        {
            public StatIconTexture(int x, int y)
            {
                super(STATS, x * 11, 166 + y * 11, 11, 11);
            }
        }

        private static class XpBarTexture extends GuiTexture
        {
            public XpBarTexture(int y)
            {
                super(STATS, 0, 188 + y * 5, 111, 5);
            }
        }

        public static class LevelIconsTexture extends GuiTexture
        {
            public static final int ICON_SIZE = 11;
            public static final int NUMBER_OF_ICONS = 6;

            public LevelIconsTexture(int y)
            {
                super(STATS, 176, y * ICON_SIZE, ICON_SIZE * NUMBER_OF_ICONS, ICON_SIZE);
            }
        }
    }

    public static class Tasks
    {
        public static final ResourceLocation TASKS = new BlocklingsResourceLocation("textures/gui/tasks.png");
        public static final ResourceLocation TASKS_CONFIG = new BlocklingsResourceLocation("textures/gui/task_configure.png");
        public static final GuiTexture BACKGROUND = new GuiTexture(TASKS, 0, 0, 176, 166);
        public static final GuiTexture CONFIG_BACKGROUND = new GuiTexture(TASKS_CONFIG, 0, 0, 176, 166);

        public static final GuiTexture TASK_ICON_BACKGROUND_RAISED = new GuiTexture(TASKS, 0, 166, 20, 20);
        public static final GuiTexture TASK_ICON_BACKGROUND_PRESSED = new GuiTexture(TASKS, 20, 166, 20, 20);
        public static final GuiTexture TASK_NAME_BACKGROUND = new GuiTexture(TASKS, 41, 166, 96, 20);
        public static final GuiTexture TASK_ADD_ICON_BACKGROUND = new GuiTexture(TASKS, 176, 0, 20, 20);
        public static final GuiTexture TASK_ADD_ICON = new GuiTexture(TASKS, 136, 166, 17, 20);
        public static final GuiTexture TASK_REMOVE_ICON = new GuiTexture(TASKS, 156, 166, 17, 20);
        public static final GuiTexture TASK_CONFIG_ICON = new GuiTexture(TASKS, 176, 166, 20, 20);
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
