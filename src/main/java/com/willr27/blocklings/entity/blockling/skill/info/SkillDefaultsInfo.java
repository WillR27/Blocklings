package com.willr27.blocklings.entity.blockling.skill.info;

import com.willr27.blocklings.entity.blockling.skill.Skill;

import javax.annotation.Nonnull;

/**
 * Info regarding the default values for a skill.
 */
public class SkillDefaultsInfo
{
    /**
     * The default state of a skill.
     */
    @Nonnull
    public final Skill.State defaultState;

    /**
     * @param defaultState the default state of a skill.
     */
    public SkillDefaultsInfo(@Nonnull Skill.State defaultState)
    {
        this.defaultState = defaultState;
    }
}
