package com.willr27.blocklings.skill.info;

import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Info regarding a skill group.
 */
public class SkillGroupInfo
{
    /**
     * The skill group's id.
     */
    @Nonnull
    public final UUID id;

    /**
     * The skill group's key.
     */
    @Nonnull
    public final String key;

    /**
     * The skill group's background texture.
     */
    @Nonnull
    public final ResourceLocation backgroundTexture;

    /**
     * The skill group's gui title.
     */
    @Nonnull
    public final TranslationTextComponent guiTitle;

    /**
     * @param id the skill group's id in string form.
     * @param key the skill group's key.
     * @param backgroundTexture the skill group's background texture.
     */
    public SkillGroupInfo(@Nonnull String id, @Nonnull String key, @Nonnull ResourceLocation backgroundTexture)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.backgroundTexture = backgroundTexture;
        this.guiTitle = new BlocklingsTranslationTextComponent("skill_group." + key + ".gui_title");
    }
}
