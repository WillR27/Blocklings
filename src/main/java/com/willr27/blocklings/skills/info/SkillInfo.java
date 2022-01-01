package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.skills.Skill;

import java.util.UUID;

public class SkillInfo
{
    public final UUID id;
    public final SkillGeneralInfo general;
    public final SkillDefaultsInfo defaults;
    public final SkillRelationshipsInfo relationships;
    public final SkillRequirementsInfo requirements;
    public final SkillCallbackInfo callbacks;
    public final SkillGuiInfo gui;

    public SkillInfo(String id, SkillGeneralInfo generalInfo, SkillDefaultsInfo defaultsInfo, SkillRelationshipsInfo relationshipsInfo, SkillRequirementsInfo requirements, SkillCallbackInfo callbacks, SkillGuiInfo guiInfo)
    {
        this.id = UUID.fromString(id);
        this.general = generalInfo;
        this.defaults = defaultsInfo;
        this.relationships = relationshipsInfo;
        this.requirements = requirements;
        this.callbacks = callbacks;
        this.gui = guiInfo;
    }

    /**
     * Called once every tick for a bought skill.
     */
    public void tick(Skill skill)
    {

    }
}
