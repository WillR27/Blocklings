package com.willr27.blocklings.skills;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.skills.info.SkillGroupInfo;
import com.willr27.blocklings.skills.info.SkillInfo;

import java.util.*;

public class SkillGroup
{
    public final BlocklingEntity blockling;
    public final SkillGroupInfo info;

    private List<Skill> skills = new ArrayList<>();

    public SkillGroup(BlocklingEntity blockling, SkillGroupInfo info)
    {
        this.blockling = blockling;
        this.info = info;
    }

    public boolean contains(Skill ability)
    {
        return skills.contains(ability);
    }

    public List<Skill> getSkills()
    {
        return skills;
    }

    public Skill getSkill(SkillInfo skillInfo)
    {
        return getSkill(skillInfo.id);
    }

    public Skill getSkill(UUID skillId)
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

    public void addSkill(Skill skill)
    {
        skills.add(skill);
    }

    public void addSkills(List<Skill> abilities)
    {
        for (Skill ability : abilities)
        {
            addSkill(ability);
        }
    }
}
