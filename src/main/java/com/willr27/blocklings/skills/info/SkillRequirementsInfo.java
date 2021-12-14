package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.entity.entities.blockling.BlocklingStats;

import java.util.Map;

public class SkillRequirementsInfo
{
    public final int skillPoints;
    public final Map<BlocklingStats.Level, Integer> levels;

    public SkillRequirementsInfo(int skillPoints, Map<BlocklingStats.Level, Integer> levels)
    {
        this.skillPoints = skillPoints;
        this.levels = levels;
    }
}
