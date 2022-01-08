package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.attribute.BlocklingAttributes;

import java.util.Map;

public class SkillRequirementsInfo
{
    public final Map<BlocklingAttributes.Level, Integer> levels;

    public SkillRequirementsInfo(Map<BlocklingAttributes.Level, Integer> levels)
    {
        this.levels = levels;
    }
}
