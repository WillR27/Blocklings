package com.willr27.blocklings.entity.blockling.skill;

import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.info.SkillGroupInfo;
import com.willr27.blocklings.entity.blockling.skill.info.SkillInfo;
import com.willr27.blocklings.entity.blockling.skill.skills.*;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The blockling's skills.
 */
public class BlocklingSkills implements IReadWriteNBT
{
    public static class Groups
    {
        @Nonnull public static final SkillGroupInfo GENERAL = new SkillGroupInfo("cf5f4d12-03c1-475c-a4a6-fee8484e8ec4", "general", Textures.Skills.General.TILES);
        @Nonnull public static final SkillGroupInfo COMBAT = new SkillGroupInfo("adfab53d-03e7-47e1-8dbe-cf40ee597045", "combat", Textures.Skills.Combat.TILES);
        @Nonnull public static final SkillGroupInfo MINING = new SkillGroupInfo("c28f70f5-e775-489f-ba08-5d53d1e4200f", "mining", Textures.Skills.Mining.TILES);
        @Nonnull public static final SkillGroupInfo WOODCUTTING = new SkillGroupInfo("2297bd04-0ea9-401f-a690-9774a9785f75", "woodcutting", Textures.Skills.Woodcutting.TILES);
        @Nonnull public static final SkillGroupInfo FARMING = new SkillGroupInfo("e71f5788-1a88-41df-8311-c397d5174d51", "farming", Textures.Skills.Farming.TILES);
    }

    /**
     * Helper method to unlock any existing whitelist with the given id.
     *
     * @param skill the skill that has been unlocked.
     * @param whitelistId the whitelist id to unlock.
     */
    public static void unlockExistingWhitelists(@Nonnull Skill skill, @Nonnull String whitelistId)
    {
        skill.blockling.getTasks().getPrioritisedTasks().forEach(task ->
        {
            if (task.isConfigured())
            {
                task.getGoal().whitelists.forEach(goalWhitelist ->
                {
                    if (goalWhitelist.id.toString().equals(whitelistId))
                    {
                        goalWhitelist.setIsUnlocked(true, false);
                    }
                });
            }
        });
    }

    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * The list of skill groups.
     */
    @Nonnull
    private final List<SkillGroup> skillGroups = new ArrayList<>();

    /**
     * @param blockling the blockling.
     */
    public BlocklingSkills(@Nonnull BlocklingEntity blockling)
    {
        this.blockling = blockling;

        reset();
    }

    /**
     * Resets all the skills and skill groups back to default.
     */
    public void reset()
    {
        skillGroups.clear();

        SkillGroup general = new SkillGroup(blockling, Groups.GENERAL);
        general.addSkills(GeneralSkills.SKILLS.stream().map(createSkill -> createSkill.apply(general)).collect(Collectors.toList()));
        skillGroups.add(general);

        SkillGroup combat = new SkillGroup(blockling, Groups.COMBAT);
        combat.addSkills(CombatSkills.SKILLS.stream().map(createSkill -> createSkill.apply(combat)).collect(Collectors.toList()));
        skillGroups.add(combat);

        SkillGroup mining = new SkillGroup(blockling, Groups.MINING);
        mining.addSkills(MiningSkills.SKILLS.stream().map(createSkill -> createSkill.apply(mining)).collect(Collectors.toList()));
        skillGroups.add(mining);

        SkillGroup woodcutting = new SkillGroup(blockling, Groups.WOODCUTTING);
        woodcutting.addSkills(WoodcuttingSkills.SKILLS.stream().map(createSkill -> createSkill.apply(woodcutting)).collect(Collectors.toList()));
        skillGroups.add(woodcutting);

        SkillGroup farming = new SkillGroup(blockling, Groups.FARMING);
        farming.addSkills(FarmingSkills.SKILLS.stream().map(createSkill -> createSkill.apply(farming)).collect(Collectors.toList()));
        skillGroups.add(farming);
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT skillsTag)
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            CompoundNBT groupTag = new CompoundNBT();

            for (Skill skill : skillGroup.getSkills())
            {
                CompoundNBT skillTag = new CompoundNBT();

                skillTag.putInt("state", skill.getState().ordinal());

                groupTag.put(skill.info.id.toString(), skillTag);
            }

            skillsTag.put(skillGroup.info.id.toString(), groupTag);
        }

        return skillsTag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT skillsTag, @Nonnull Version tagVersion)
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            CompoundNBT groupTag = (CompoundNBT) skillsTag.get(skillGroup.info.id.toString());

            if (groupTag == null)
            {
                continue;
            }

            for (Skill skill : skillGroup.getSkills())
            {
                CompoundNBT skillTag = (CompoundNBT) groupTag.get(skill.info.id.toString());

                if (skillTag == null)
                {
                    continue;
                }

                skill.setState(Skill.State.values()[skillTag.getInt("state")], false);
            }
        }
    }

    /**
     * Writes the skills to the given buffer.
     *
     * @param buf the buffer to write to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            for (Skill skill : skillGroup.getSkills())
            {
                buf.writeEnum(skill.getState());
            }
        }
    }

    /**
     * Reads the skills from the given buffer.
     *
     * @param buf the buffer to read from.
     */
    public void decode(@Nonnull PacketBuffer buf)
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            for (Skill skill : skillGroup.getSkills())
            {
                skill.setState(buf.readEnum(Skill.State.class), false);
            }
        }
    }

    /**
     * Ticks skills.
     */
    public void tick()
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            skillGroup.getSkills().stream().filter(Skill::isBought).forEach(skill -> skill.info.tick(skill));
        }
    }

    /**
     * @return the instance of the skill group containing the given skill info.
     */
    @Nonnull
    public SkillGroup findGroup(@Nonnull SkillInfo skillInfo)
    {
        return skillGroups.stream().filter(group -> group.getSkills().stream().filter(skill -> skill.info == skillInfo).findFirst().orElse(null) != null).findFirst().get();
    }

    /**
     *
     * @return the instance of the group for the given group info.
     */
    @Nonnull
    public SkillGroup getGroup(@Nonnull SkillGroupInfo groupInfo)
    {
        return getGroup(groupInfo.id);
    }

    /**
     * @return the instance of the group for the given group info id.
     */
    @Nonnull
    public SkillGroup getGroup(@Nonnull UUID groupId)
    {
        return skillGroups.stream().filter(group -> group.info.id.equals(groupId)).findFirst().get();
    }

    /**
     * @return the instance of the skill for the given skill info.
     */
    @Nonnull
    public Skill getSkill(@Nonnull SkillInfo skillInfo)
    {
        return findGroup(skillInfo).getSkill(skillInfo);
    }
}
