package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.skills.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A container for all the info for a skill.
 */
public class SkillInfo
{
    /**
     * The skill type's id.
     */
    @Nonnull
    public final UUID id;

    /**
     * The general info.
     */
    @Nonnull
    public final SkillGeneralInfo general;

    /**
     * The defaults info.
     */
    @Nonnull
    public final SkillDefaultsInfo defaults;

    /**
     * The requirements info.
     */
    @Nonnull
    public final SkillRequirementsInfo requirements;

    /**
     * The callbacks info.*
     */
    @Nonnull
    public final SkillCallbackInfo callbacks;

    /**
     * The gui info.
     */
    @Nonnull
    public final SkillGuiInfo gui;

    /**
     * @param id the skill type's id in string form.
     * @param generalInfo the general info.
     * @param defaultsInfo the defaults info.
     * @param requirements the requirements info.
     * @param callbacks the callbacks info.
     * @param guiInfo the gui info.
     */
    public SkillInfo(@Nonnull String id, @Nonnull SkillGeneralInfo generalInfo, @Nonnull SkillDefaultsInfo defaultsInfo, @Nonnull SkillRequirementsInfo requirements, @Nonnull SkillCallbackInfo callbacks, @Nonnull SkillGuiInfo guiInfo)
    {
        this.id = UUID.fromString(id);
        this.general = generalInfo;
        this.defaults = defaultsInfo;
        this.requirements = requirements;
        this.callbacks = callbacks;
        this.gui = guiInfo;
    }

    /**
     * Returns an array of all parent skill infos.
     */
    public List<SkillInfo> parents()
    {
        return new ArrayList<>();
    }

    /**
     * Returns an array of all conflict skill infos.
     */
    public List<SkillInfo> conflicts()
    {
        return new ArrayList<>();
    }

    /**
     * Called once every tick for a bought skill.
     */
    public void tick(Skill skill)
    {

    }
}
