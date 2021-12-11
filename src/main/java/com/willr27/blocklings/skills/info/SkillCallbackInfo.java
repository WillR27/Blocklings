package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.skills.Skill;

import java.util.function.Function;

public class SkillCallbackInfo
{
    public final Function<Skill, Boolean> onBuy;

    public SkillCallbackInfo(Function<Skill, Boolean> onBuy)
    {
        this.onBuy = onBuy;
    }
}
