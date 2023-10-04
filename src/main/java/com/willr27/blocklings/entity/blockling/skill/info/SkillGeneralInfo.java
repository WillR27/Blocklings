package com.willr27.blocklings.entity.blockling.skill.info;

import com.willr27.blocklings.entity.blockling.skill.Skill;
import com.willr27.blocklings.util.BlocklingsTranslatableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;

/**
 * Info regarding the general properties of a skill.
 */
public class SkillGeneralInfo
{
    /**
     * The skill's type.
     */
    @Nonnull
    public final Skill.Type type;

    /**
     * The skill's name's translation text component.
     */
    @Nonnull
    public final TranslatableComponent name;

    /**
     * The skill's description's translation text component.
     */
    @Nonnull
    public final TranslatableComponent desc;

    /**
     * @param type the skill's type.
     * @param key the skill's key.
     */
    public SkillGeneralInfo(@Nonnull Skill.Type type, @Nonnull String key)
    {
        this.type = type;
        this.name = new SkillTranslatableComponent(key + ".name");
        this.desc = new SkillTranslatableComponent(key + ".desc");
    }

    /**
     * A blocklings translation text component for skills.
     */
    public static class SkillTranslatableComponent extends BlocklingsTranslatableComponent
    {
        /**
         * @param key the skill's key.
         */
        public SkillTranslatableComponent(String key)
        {
            super("skill." + key);
        }
    }
}
