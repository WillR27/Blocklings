package com.willr27.blocklings.client.gui.texture;

import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A collection of all gui textures used.
 */
@OnlyIn(Dist.CLIENT)
public class Textures
{
    public static class Common
    {
        public static final ResourceLocation COMMON = new BlocklingsResourceLocation("textures/gui/common_widgets.png");

        public static final Texture NODE_UNPRESSED = new Texture(COMMON, 46, 0, 12, 12);
        public static final Texture NODE_PRESSED = new Texture(COMMON, 58, 0, 12, 12);

        public static final Texture ROUND_GRABBER_UNPRESSED = new Texture(COMMON, 46, 0, 12, 12);
        public static final Texture ROUND_GRABBER_PRESSED = new Texture(COMMON, 58, 0, 12, 12);

        public static final Texture SLIDER_BAR = new Texture(COMMON, 0, 88, 256, 4);

        public static class Scrollbar
        {
            public static final Texture GRABBER_UNPRESSED = new Texture(COMMON, 0, 0, 12, 15);
            public static final Texture GRABBER_PRESSED = new Texture(COMMON, 12, 0, 12, 15);
        }

        public static class DropDown
        {
            public static final int BORDER_SIZE = 2;

            public static final Texture DOWN_ARROW = new Texture(COMMON, 24, 0, 11, 7);
            public static final Texture UP_ARROW = new Texture(COMMON, 35, 0, 11, 7);

            public static final Texture SELECTED_BACKGROUND = new Texture(COMMON, 0, 48, 256, 20);
            public static final Texture UNSELECTED_BACKGROUND = new Texture(COMMON, 0, 69, 256, 19);
        }

        public static class Tab
        {
            public static final int EDGE_WIDTH = 4;
            public static final int FULLY_OPAQUE_HEIGHT = 13;

            public static final Texture TAB_SELECTED_BACKGROUND = new Texture(COMMON, 0, 15, 256, 18);
            public static final Texture TAB_UNSELECTED_BACKGROUND = new Texture(COMMON, 0, 33, 256, 15);
        }
    }

    public static class Tabs
    {
        public static final ResourceLocation TABS = new BlocklingsResourceLocation("textures/gui/tabs.png");

        public static final Texture UNSELECTED_BACKGROUND_TEXTURE_LEFT = new Texture(TABS, 0, 0, 25, 28);
        public static final Texture UNSELECTED_BACKGROUND_TEXTURE_RIGHT = new Texture(TABS, 26, 0, 25, 28);
        public static final Texture SELECTED_BACKGROUND_TEXTURE_LEFT = new Texture(TABS, 52, 0, 32, 28);
        public static final Texture SELECTED_BACKGROUND_TEXTURE_RIGHT = new Texture(TABS, 85, 0, 32, 28);

        public static final Texture STATS = new Texture(TABS, 0, 28, 22, 22);
        public static final Texture TASKS = new Texture(TABS, 22, 28, 22, 22);
        public static final Texture EQUIPMENT = new Texture(TABS, 44, 28, 22, 22);
        public static final Texture GENERAL = new Texture(TABS, 0, 50, 22, 22);
        public static final Texture COMBAT = new Texture(TABS, 22, 50, 22, 22);
        public static final Texture MINING = new Texture(TABS, 44, 50, 22, 22);
        public static final Texture WOODCUTTING = new Texture(TABS, 66, 50, 22, 22);
        public static final Texture FARMING = new Texture(TABS, 88, 50, 22, 22);
    }

    public static class Stats
    {
        public static final ResourceLocation STATS = new BlocklingsResourceLocation("textures/gui/stats.png");
        public static final Texture BACKGROUND = new Texture(STATS, 0, 0, 176, 166);

        public static final Texture HEALTH_BAR = new Texture(STATS, 0, 228, 134, 5);

        public static final Texture COMBAT_BAR_BACKGROUND = new XpBarTexture(1);
        public static final Texture COMBAT_BAR_FOREGROUND = new XpBarTexture(0);
        public static final Texture MINING_BAR_BACKGROUND = new XpBarTexture(3);
        public static final Texture MINING_BAR_FOREGROUND = new XpBarTexture(2);
        public static final Texture WOODCUTTING_BAR_BACKGROUND = new XpBarTexture(5);
        public static final Texture WOODCUTTING_BAR_FOREGROUND = new XpBarTexture(4);
        public static final Texture FARMING_BAR_BACKGROUND = new XpBarTexture(7);
        public static final Texture FARMING_BAR_FOREGROUND = new XpBarTexture(6);

        public static final LevelIconsTexture COMBAT_LEVEL_ICONS = new LevelIconsTexture(0);
        public static final LevelIconsTexture MINING_LEVEL_ICONS = new LevelIconsTexture(1);
        public static final LevelIconsTexture WOODCUTTING_LEVEL_ICONS = new LevelIconsTexture(2);
        public static final LevelIconsTexture FARMING_LEVEL_ICONS = new LevelIconsTexture(3);

        public static final Texture ATTACK_DAMAGE_MAIN = new StatIconTexture(12, 0);
        public static final Texture ATTACK_DAMAGE_OFF = new StatIconTexture(10, 0);
        public static final Texture ATTACK_SPEED = new StatIconTexture(11, 0);

        public static final Texture ARMOUR = new StatIconTexture(5, 0);
        public static final Texture ARMOUR_TOUGHNESS = new StatIconTexture(6, 0);
        public static final Texture KNOCKBACK_RESISTANCE = new StatIconTexture(7, 0);

        public static final Texture MINING_SPEED = new StatIconTexture(1, 0);
        public static final Texture WOODCUTTING_SPEED = new StatIconTexture(2, 0);
        public static final Texture FARMING_SPEED = new StatIconTexture(3, 0);

        public static final Texture MOVE_SPEED = new StatIconTexture(8, 0);

        public static class StatIconTexture extends Texture
        {
            public StatIconTexture(int x, int y)
            {
                super(STATS, x * 11, 166 + y * 11, 11, 11);
            }
        }

        private static class XpBarTexture extends Texture
        {
            public XpBarTexture(int y)
            {
                super(STATS, 0, 188 + y * 5, 111, 5);
            }
        }

        public static class LevelIconsTexture extends Texture
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
        public static final ResourceLocation WHITELIST = new BlocklingsResourceLocation("textures/gui/whitelist.png");

        public static final Texture BACKGROUND = new Texture(TASKS, 0, 0, 176, 166);
        public static final Texture CONFIG_BACKGROUND = new Texture(TASKS_CONFIG, 0, 0, 176, 166);

        public static final Texture TASK_ICON_BACKGROUND_RAISED = new Texture(TASKS, 0, 166, 20, 20);
        public static final Texture TASK_ICON_BACKGROUND_PRESSED = new Texture(TASKS, 20, 166, 20, 20);
        public static final Texture TASK_NAME_BACKGROUND = new Texture(TASKS, 41, 166, 96, 20);
        public static final Texture TASK_ADD_ICON_BACKGROUND = new Texture(TASKS, 176, 0, 20, 20);
        public static final Texture TASK_ADD_ICON = new Texture(TASKS, 136, 166, 17, 20);
        public static final Texture TASK_REMOVE_ICON = new Texture(TASKS, 156, 166, 17, 20);
        public static final Texture TASK_CONFIG_ICON = new Texture(TASKS, 176, 166, 20, 20);

        public static final Texture ENTRY_UNSELECTED = new Texture(WHITELIST, 0, 166, 30, 30);
        public static final Texture ENTRY_SELECTED = ENTRY_UNSELECTED.dx(ENTRY_UNSELECTED.width);
    }

    public static class Equipment
    {
        public static final ResourceLocation EQUIPMENT = new BlocklingsResourceLocation("textures/gui/equipment.png");
        public static final Texture BACKGROUND = new Texture(EQUIPMENT, 0, 0, 176, 166);
    }

    public static class Skills
    {
        public static final ResourceLocation SKILLS = new BlocklingsResourceLocation("textures/gui/skills.png");
        public static final Texture BACKGROUND = new Texture(SKILLS, 0, 0, 176, 166);
    }
}
