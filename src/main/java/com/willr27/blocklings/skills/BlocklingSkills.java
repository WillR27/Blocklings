package com.willr27.blocklings.skills;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingStats;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.gui.widgets.SkillWidget;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.skills.info.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlocklingSkills
{
    public static class General
    {
        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{

        }};
    }

    public static class Combat
    {
        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{

        }};
    }

    public static class Mining
    {
        private static final SkillGeneralInfo NOVICE_MINER_GENERAL = new SkillGeneralInfo(Skill.Type.AI, "mining.novice_miner");
        private static final SkillDefaultsInfo NOVICE_MINER_DEFAULTS = new SkillDefaultsInfo(Skill.State.UNLOCKED);
        private static final SkillRelationshipsInfo NOVICE_MINER_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] {  }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo NOVICE_MINER_REQUIREMENTS = new SkillRequirementsInfo(0, new HashMap<BlocklingStats.Level, Integer>() {{  }});
        private static final SkillCallbackInfo NOVICE_MINER_CALLBACKS = new SkillCallbackInfo(skill -> { skill.group.blockling.getTasks().setIsUnlocked(BlocklingTasks.MINE, true); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture NOVICE_MINER_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.MINING_ICONS, 0, 0);
        private static final SkillGuiInfo NOVICE_MINER_GUI = new SkillGuiInfo(0, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, NOVICE_MINER_TEXTURE);
        public static final SkillInfo NOVICE_MINER = new SkillInfo("dcbf7cc1-8bef-49aa-a5a0-cd70cb40cbac", NOVICE_MINER_GENERAL, NOVICE_MINER_DEFAULTS, NOVICE_MINER_RELATIONSHIPS, NOVICE_MINER_REQUIREMENTS, NOVICE_MINER_CALLBACKS, NOVICE_MINER_GUI);

        private static final SkillGeneralInfo MINING_WHITELIST_GENERAL = new SkillGeneralInfo(Skill.Type.OTHER, "mining.whitelist");
        private static final SkillDefaultsInfo MINING_WHITELIST_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo MINING_WHITELIST_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_MINER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo MINING_WHITELIST_REQUIREMENTS = new SkillRequirementsInfo(2, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.MINING, 10); }});
        private static final SkillCallbackInfo MINING_WHITELIST_CALLBACKS = new SkillCallbackInfo(skill -> true);
        private static final SkillGuiInfo.AbilityGuiTexture MINING_WHITELIST_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.MINING_ICONS, 2, 1);
        private static final SkillGuiInfo MINING_WHITELIST_GUI = new SkillGuiInfo(0, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, MINING_WHITELIST_TEXTURE);
        public static final SkillInfo MINING_WHITELIST = new SkillInfo("8963cddd-06dd-4b5a-8c1e-b1e38a99b25f", MINING_WHITELIST_GENERAL, MINING_WHITELIST_DEFAULTS, MINING_WHITELIST_RELATIONSHIPS, MINING_WHITELIST_REQUIREMENTS, MINING_WHITELIST_CALLBACKS, MINING_WHITELIST_GUI);

        private static final SkillGeneralInfo FASTER_MINING_GENERAL = new SkillGeneralInfo(Skill.Type.STAT, "mining.efficiency");
        private static final SkillDefaultsInfo FASTER_MINING_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo FASTER_MINING_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_MINER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo FASTER_MINING_REQUIREMENTS = new SkillRequirementsInfo(1, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.MINING, 5); }});
        private static final SkillCallbackInfo FASTER_MINING_CALLBACKS = new SkillCallbackInfo(skill -> true);
        private static final SkillGuiInfo.AbilityGuiTexture FASTER_MINING_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.MINING_ICONS, 1, 0);
        private static final SkillGuiInfo FASTER_MINING_GUI = new SkillGuiInfo(70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, FASTER_MINING_TEXTURE);
        public static final SkillInfo FASTER_MINING = new SkillInfo("19253148-ff6e-4395-9464-289e081b442b", FASTER_MINING_GENERAL, FASTER_MINING_DEFAULTS, FASTER_MINING_RELATIONSHIPS, FASTER_MINING_REQUIREMENTS, FASTER_MINING_CALLBACKS, FASTER_MINING_GUI);

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_MINER, group));
            add(group -> new Skill(MINING_WHITELIST, group));
            add(group -> new Skill(FASTER_MINING, group));
        }};
    }

    public static class Woodcutting
    {
        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{

        }};
    }

    public static class Farming
    {
        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{

        }};
    }

    private final BlocklingEntity blockling;

    private List<SkillGroup> skillGroups = new ArrayList<>();

    public BlocklingSkills(BlocklingEntity blockling)
    {
        this.blockling = blockling;

        reset();
    }

    public void reset()
    {
        skillGroups.clear();

        SkillGroup general = new SkillGroup(blockling, BlocklingSkillGroups.GENERAL);
        general.addSkills(General.SKILLS.stream().map(createSkill -> createSkill.apply(general)).collect(Collectors.toList()));
        skillGroups.add(general);

        SkillGroup combat = new SkillGroup(blockling, BlocklingSkillGroups.COMBAT);
        combat.addSkills(Combat.SKILLS.stream().map(createSkill -> createSkill.apply(combat)).collect(Collectors.toList()));
        skillGroups.add(combat);

        SkillGroup mining = new SkillGroup(blockling, BlocklingSkillGroups.MINING);
        mining.addSkills(Mining.SKILLS.stream().map(createSkill -> createSkill.apply(mining)).collect(Collectors.toList()));
        skillGroups.add(mining);

        SkillGroup woodcutting = new SkillGroup(blockling, BlocklingSkillGroups.WOODCUTTING);
        woodcutting.addSkills(Woodcutting.SKILLS.stream().map(createSkill -> createSkill.apply(woodcutting)).collect(Collectors.toList()));
        skillGroups.add(woodcutting);

        SkillGroup farming = new SkillGroup(blockling, BlocklingSkillGroups.FARMING);
        farming.addSkills(Farming.SKILLS.stream().map(createSkill -> createSkill.apply(farming)).collect(Collectors.toList()));
        skillGroups.add(farming);
    }

    public void writeToNBT(CompoundNBT c)
    {
        CompoundNBT tag = new CompoundNBT();

        for (SkillGroup skillGroup : skillGroups)
        {
            CompoundNBT groupTag = new CompoundNBT();

            for (Skill skill : skillGroup.getSkills())
            {
                CompoundNBT skillTag = new CompoundNBT();

                skillTag.putInt("state", skill.getState().ordinal());

                groupTag.put(skill.info.id.toString(), skillTag);
            }

            tag.put(skillGroup.info.id.toString(), groupTag);
        }

        c.put("skills", tag);
    }

    public void readFromNBT(CompoundNBT c)
    {
        CompoundNBT tag = (CompoundNBT) c.get("skills");

        for (SkillGroup skillGroup : skillGroups)
        {
            CompoundNBT groupTag = (CompoundNBT) tag.get(skillGroup.info.id.toString());

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

    public void encode(PacketBuffer buf)
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            for (Skill skill : skillGroup.getSkills())
            {
                buf.writeEnum(skill.getState());
            }
        }
    }

    public void decode(PacketBuffer buf)
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            for (Skill skill : skillGroup.getSkills())
            {
                skill.setState(buf.readEnum(Skill.State.class), false);
            }
        }
    }

    public SkillGroup getGroup(SkillGroupInfo groupInfo)
    {
        return getGroup(groupInfo.id);
    }

    public SkillGroup getGroup(UUID groupId)
    {
        return skillGroups.stream().filter(group -> group.info.id.equals(groupId)).findFirst().get();
    }
}
