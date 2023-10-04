package com.willr27.blocklings.entity.blockling.skill.info;

import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.util.BlocklingsTranslatableComponent;
import net.minecraft.network.chat.TranslatableComponent;

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
    public final Textures.Skills.Tiles backgroundTexture;

    /**
     * The skill group's gui title.
     */
    @Nonnull
    public final TranslatableComponent guiTitle;

    /**
     * @param id the skill group's id in string form.
     * @param key the skill group's key.
     * @param backgroundTexture the skill group's background texture.
     */
    public SkillGroupInfo(@Nonnull String id, @Nonnull String key, @Nonnull Textures.Skills.Tiles backgroundTexture)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.backgroundTexture = backgroundTexture;
        this.guiTitle = new BlocklingsTranslatableComponent("skill_group." + key + ".gui_title");
    }
}
