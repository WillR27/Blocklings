package com.willr27.blocklings.skills.info;

import java.util.Arrays;
import java.util.List;

public class SkillRelationshipsInfo
{
    public final List<SkillInfo> parents;
    public final List<SkillInfo> conflicts;

    public SkillRelationshipsInfo(SkillInfo[] parents, SkillInfo[] conflicts)
    {
        this.parents = Arrays.asList(parents);
        this.conflicts = Arrays.asList(conflicts);
    }
}
