package com.willr27.blocklings.entity.blockling.skill.info;

import com.willr27.blocklings.entity.blockling.skill.Skill;

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
     * The gui info.
     */
    @Nonnull
    public final SkillGuiInfo gui;

    /**
     * @param id the skill type's id in string form.
     * @param generalInfo the general info.
     * @param defaultsInfo the defaults info.
     * @param requirements the requirements info.
     * @param guiInfo the gui info.
     */
    public SkillInfo(@Nonnull String id, @Nonnull SkillGeneralInfo generalInfo, @Nonnull SkillDefaultsInfo defaultsInfo, @Nonnull SkillRequirementsInfo requirements, @Nonnull SkillGuiInfo guiInfo)
    {
        this.id = UUID.fromString(id);
        this.general = generalInfo;
        this.defaults = defaultsInfo;
        this.requirements = requirements;
        this.gui = guiInfo;
    }

    /**
     * Called when the given skill is first initialised.
     */
    public void init(@Nonnull Skill skill)
    {

    }

    /**
     * Called when the given skill is bought.
     */
    public void onBuy(@Nonnull Skill skill)
    {

    }

    /**
     * Called once every tick for a bought skill.
     */
    public void tick(@Nonnull Skill skill)
    {

    }

    /**
     * Returns an array of all parent skill infos.
     */
    @Nonnull
    public List<SkillInfo> parents()
    {
        return new ArrayList<>();
    }

    /**
     * Returns an array of all conflict skill infos.
     */
    @Nonnull
    public List<SkillInfo> conflicts()
    {
        return new ArrayList<>();
    }
}
