package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.skills.Skill;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SkillGeneralInfo
{
    public final Skill.Type type;
    public final TranslationTextComponent name;
    public final TranslationTextComponent desc;

    public SkillGeneralInfo(Skill.Type type, String key)
    {
        this.type = type;
        this.name = new SkillTranslationTextComponent(key + ".name");
        this.desc = new SkillTranslationTextComponent(key + ".desc");
    }

    public class SkillTranslationTextComponent extends BlocklingsTranslationTextComponent
    {
        public SkillTranslationTextComponent(String key)
        {
            super("skill." + key);
        }
    }
}
