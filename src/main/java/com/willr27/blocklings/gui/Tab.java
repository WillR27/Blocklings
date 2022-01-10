package com.willr27.blocklings.gui;

import com.willr27.blocklings.skills.BlocklingSkillGroups;
import com.willr27.blocklings.skills.info.SkillGroupInfo;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public enum Tab
{
    STATS("stats", BlocklingGuiHandler.STATS_ID, null, 0, 0, true),
    TASKS("tasks", BlocklingGuiHandler.TASKS_ID, null, 1, 0, true),
    EQUIPMENT("equipment", BlocklingGuiHandler.EQUIPMENT_ID, null, 2, 0, true),
//    UTILITY_1("utility_1", GuiHandler.UTILITY_ID, "", 3, 0, true),
//    UTILITY_2("utility_2", GuiHandler.UTILITY_ID, "", 3, 0, true);
    GENERAL("general", BlocklingGuiHandler.GENERAL_ID, BlocklingSkillGroups.GENERAL, 0, 1, false),
    COMBAT("combat", BlocklingGuiHandler.COMBAT_ID, BlocklingSkillGroups.COMBAT, 1, 1, false),
    MINING("mining", BlocklingGuiHandler.MINING_ID, BlocklingSkillGroups.MINING, 2, 1, false),
    WOODCUTTING("woodcutting", BlocklingGuiHandler.WOODCUTTING_ID, BlocklingSkillGroups.WOODCUTTING, 3, 1, false),
    FARMING("farming", BlocklingGuiHandler.FARMING_ID, BlocklingSkillGroups.FARMING, 4, 1, false);

    public final TranslationTextComponent name;
    public final int guiId;
    public final SkillGroupInfo skillGroup;
    public final int textureX, textureY;
    public final boolean left;

    Tab(String key, int guiId, SkillGroupInfo skillGroup, int textureX, int textureY, boolean left)
    {
        this.name = new TabTranslationTextComponent(key);
        this.guiId = guiId;
        this.skillGroup = skillGroup;
        this.textureX = textureX;
        this.textureY = textureY;
        this.left = left;
    }

    public static List<Tab> leftTabs = new ArrayList<>();
    public static List<Tab> rightTabs = new ArrayList<>();
    static
    {
        for (Tab tab : values())
        {
            if (tab.left) leftTabs.add(tab);
            else rightTabs.add(tab);
        }
    }

    /**
     * @return the index of the tab for the side it's on where 0 is at the top and 1 is below etc.
     */
    public int getIndex()
    {
        return left ? leftTabs.indexOf(this) : rightTabs.indexOf(this);
    }

    public static boolean hasTab(int guiId)
    {
        return getTab(guiId) != null;
    }

    public static Tab getTab(int guiId)
    {
        for (Tab tab: values())
        {
            if (tab.guiId == guiId)
            {
                return tab;
            }
        }

        return null;
    }

    public class TabTranslationTextComponent extends BlocklingsTranslationTextComponent
    {
        public TabTranslationTextComponent(String key)
        {
            super("tab." + key);
        }
    }
}
