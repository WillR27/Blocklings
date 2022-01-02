package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.entity.entities.blockling.BlocklingStats;

import java.util.Map;

public class SkillRequirementsInfo
{
    public final Map<BlocklingStats.Level, Integer> levels;

    public SkillRequirementsInfo(Map<BlocklingStats.Level, Integer> levels)
    {
        this.levels = levels;
    }
}
