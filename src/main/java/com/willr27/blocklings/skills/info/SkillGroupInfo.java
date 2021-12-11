package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class SkillGroupInfo
{
    public final UUID id;
    public final String key;
    public final ResourceLocation backgroundTexture;
    public final TranslationTextComponent guiTitle;

    public SkillGroupInfo(String id, String key, ResourceLocation backgroundTexture)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.backgroundTexture = backgroundTexture;
        this.guiTitle = new BlocklingsTranslationTextComponent("skill_group." + key + ".gui_title");
    }
}
