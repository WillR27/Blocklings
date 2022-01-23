package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.skills.Skill;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Info regarding the callbacks for each skill.
 */
public class SkillCallbackInfo
{
    /**
     * The callback called when a skill is bought.
     */
    @Nonnull
    public final Function<Skill, Boolean> onBuy;

    /**
     * @param onBuy the callback called when a skill is bought.
     */
    public SkillCallbackInfo(@Nonnull Function<Skill, Boolean> onBuy)
    {
        this.onBuy = onBuy;
    }
}
