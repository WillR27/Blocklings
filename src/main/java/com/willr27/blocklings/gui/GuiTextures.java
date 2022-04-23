package com.willr27.blocklings.gui;

import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.util.ResourceLocation;

public class GuiTextures
{
    public static final ResourceLocation COMMON_WIDGETS = new BlocklingsResourceLocation("textures/gui/common_widgets.png");

    public static final ResourceLocation TABS = new BlocklingsResourceLocation("textures/gui/tabs.png");
    public static final ResourceLocation STATS = new BlocklingsResourceLocation("textures/gui/stats.png");
    public static final ResourceLocation TASKS = new BlocklingsResourceLocation("textures/gui/tasks.png");
    public static final ResourceLocation EQUIPMENT = new BlocklingsResourceLocation("textures/gui/equipment.png");

    public static final ResourceLocation SKILLS = new BlocklingsResourceLocation("textures/gui/skills.png");
    public static final ResourceLocation SKILLS_WIDGETS = new BlocklingsResourceLocation("textures/gui/skills_widgets.png");

    public static final ResourceLocation GENERAL_ICONS = new BlocklingsResourceLocation("textures/gui/skills_icons/general.png");
    public static final ResourceLocation COMBAT_ICONS = new BlocklingsResourceLocation("textures/gui/skills_icons/combat.png");
    public static final ResourceLocation MINING_ICONS = new BlocklingsResourceLocation("textures/gui/skills_icons/mining.png");
    public static final ResourceLocation WOODCUTTING_ICONS = new BlocklingsResourceLocation("textures/gui/skills_icons/woodcutting.png");
    public static final ResourceLocation FARMING_ICONS = new BlocklingsResourceLocation("textures/gui/skills_icons/farming.png");

    public static final ResourceLocation GENERAL_BACKGROUND = new BlocklingsResourceLocation("textures/gui/skills_backgrounds/general.png");
    public static final ResourceLocation COMBAT_BACKGROUND = new BlocklingsResourceLocation("textures/gui/skills_backgrounds/combat.png");
    public static final ResourceLocation MINING_BACKGROUND = new BlocklingsResourceLocation("textures/gui/skills_backgrounds/mining.png");
    public static final ResourceLocation WOODCUTTING_BACKGROUND = new BlocklingsResourceLocation("textures/gui/skills_backgrounds/woodcutting.png");
    public static final ResourceLocation FARMING_BACKGROUND = new BlocklingsResourceLocation("textures/gui/skills_backgrounds/farming.png");

    public static final ResourceLocation TASK_CONFIGURE = new BlocklingsResourceLocation("textures/gui/task_configure.png");
    public static final ResourceLocation WHITELIST = new BlocklingsResourceLocation("textures/gui/whitelist.png");

    public static final GuiTexture RAISED_BAR = new GuiTexture(COMMON_WIDGETS, 0, 48, 256, 20);
    public static final GuiTexture RAISED_BAR_END = new GuiTexture(COMMON_WIDGETS, 254, 48, 2, 20);
    public static final GuiTexture FLAT_BAR = new GuiTexture(COMMON_WIDGETS, 0, 68, 256, 20);
    public static final GuiTexture FLAT_BAR_END = new GuiTexture(COMMON_WIDGETS, 254, 68, 2, 20);

    public static final GuiTexture DROPDOWN_DOWN_ARROW = new GuiTexture(GuiTextures.COMMON_WIDGETS, 24, 0, 11, 7);
    public static final GuiTexture DROPDOWN_UP_ARROW = new GuiTexture(GuiTextures.COMMON_WIDGETS, 35, 0, 11, 7);
}
