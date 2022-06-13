package com.willr27.blocklings.entity.blockling.skill;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.info.SkillGroupInfo;
import com.willr27.blocklings.entity.blockling.skill.info.SkillInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Contains a list of skills.
 */
public class SkillGroup
{
    /**
     * The blockling.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The skill group's info.
     */
    @Nonnull
    public final SkillGroupInfo info;

    /**
     * The list og skills in the group.
     */
    @Nonnull
    private List<Skill> skills = new ArrayList<>();

    /**
     * @param blockling the blockling.
     * @param info the group's info.
     */
    public SkillGroup(@Nonnull BlocklingEntity blockling, @Nonnull SkillGroupInfo info)
    {
        this.blockling = blockling;
        this.info = info;
    }

    /**
     * @return true if the given ability is part of the group.
     */
    public boolean contains(@Nullable Skill ability)
    {
        return skills.contains(ability);
    }

    /**
     * @return the list of skills in the group.
     */
    @Nonnull
    public List<Skill> getSkills()
    {
        return skills;
    }

    /**
     * @return the skill for the given skill info if it exists in the group, else null.
     */
    @Nullable
    public Skill getSkill(@Nonnull SkillInfo skillInfo)
    {
        return getSkill(skillInfo.id);
    }

    /**
     * @return the skill for the given skill id if it exists in the group, else null.
     */
    @Nullable
    public Skill getSkill(@Nonnull UUID skillId)
    {
        for (Skill skill : skills)
        {
            if (skill.info.id.equals(skillId))
            {
                return skill;
            }
        }

        return null;
    }

    /**
     * Adds the given skill to the group.
     *
     * @param skill the skill to add.
     */
    public void addSkill(@Nonnull Skill skill)
    {
        skills.add(skill);
    }

    /**
     * Adds a list of skills to the group.
     *
     * @param skills the list of skills to add.
     */
    public void addSkills(List<Skill> skills)
    {
        for (Skill skill : skills)
        {
            addSkill(skill);
        }
    }
}
