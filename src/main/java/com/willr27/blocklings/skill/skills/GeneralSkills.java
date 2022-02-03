package com.willr27.blocklings.skill.skills;

import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.controls.skills.SkillControl;
import com.willr27.blocklings.skill.Skill;
import com.willr27.blocklings.skill.SkillGroup;
import com.willr27.blocklings.skill.info.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * The general skills.
 */
public class GeneralSkills
{
    public static final SkillInfo HEAL = new SkillInfo("e6361ca8-a0c5-4a64-8be9-6928a98a4594",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.heal"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.TOTAL, 10); }}),
            new SkillGuiInfo(0, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xa8f4a1, new SkillGuiInfo.SkillIconTexture(GuiTextures.GENERAL_ICONS, 0, 0)));

    public static final SkillInfo PACKLING = new SkillInfo("5cd54257-954f-4962-b248-99f58fb11d5d",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.packling"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.TOTAL, 25); }}),
            new SkillGuiInfo(-50, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xcca58a, new SkillGuiInfo.SkillIconTexture(GuiTextures.GENERAL_ICONS, 1, 0)));

    public static final SkillInfo ARMADILLO = new SkillInfo("28ae60b1-1e8a-4c73-b1a1-5519be35d0ea",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.armadillo"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.TOTAL, 50); }}),
            new SkillGuiInfo(50, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xa8924f, new SkillGuiInfo.SkillIconTexture(GuiTextures.GENERAL_ICONS, 2, 0)))
    {
        @Override
        @Nonnull
        public List<SkillInfo> parents()
        {
            return Collections.singletonList(PACKLING);
        }
    };

    public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
    {{
        add(group -> new Skill(HEAL, group));
        add(group -> new Skill(PACKLING, group));
        add(group -> new Skill(ARMADILLO, group));
    }};
}
