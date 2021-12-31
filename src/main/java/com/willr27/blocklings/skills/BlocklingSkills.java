package com.willr27.blocklings.skills;

import com.sun.org.apache.regexp.internal.RE;
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

        private static final SkillGeneralInfo WHITELIST_GENERAL = new SkillGeneralInfo(Skill.Type.OTHER, "mining.whitelist");
        private static final SkillDefaultsInfo WHITELIST_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo WHITELIST_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_MINER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo WHITELIST_REQUIREMENTS = new SkillRequirementsInfo(2, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.MINING, 5); }});
        private static final SkillCallbackInfo WHITELIST_CALLBACKS = new SkillCallbackInfo(skill -> { unlockExistingOreWhitelists(skill); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture WHITELIST_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.MINING_ICONS, 7, 0);
        private static final SkillGuiInfo WHITELIST_GUI = new SkillGuiInfo(0, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, WHITELIST_TEXTURE);
        public static final SkillInfo WHITELIST = new SkillInfo("8963cddd-06dd-4b5a-8c1e-b1e38a99b25f", WHITELIST_GENERAL, WHITELIST_DEFAULTS, WHITELIST_RELATIONSHIPS, WHITELIST_REQUIREMENTS, WHITELIST_CALLBACKS, WHITELIST_GUI);

        private static final SkillGeneralInfo EFFICIENCY_GENERAL = new SkillGeneralInfo(Skill.Type.STAT, "mining.efficiency");
        private static final SkillDefaultsInfo EFFICIENCY_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo EFFICIENCY_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_MINER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo EFFICIENCY_REQUIREMENTS = new SkillRequirementsInfo(1, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.MINING, 10); }});
        private static final SkillCallbackInfo EFFICIENCY_CALLBACKS = new SkillCallbackInfo(skill -> { skill.group.blockling.getStats().miningSpeedSkillEfficiencyModifier.setValue(1.1f, false); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture EFFICIENCY_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.MINING_ICONS, 1, 0);
        private static final SkillGuiInfo EFFICIENCY_GUI = new SkillGuiInfo(70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, EFFICIENCY_TEXTURE);
        public static final SkillInfo EFFICIENCY = new SkillInfo("19253148-ff6e-4395-9464-289e081b442b", EFFICIENCY_GENERAL, EFFICIENCY_DEFAULTS, EFFICIENCY_RELATIONSHIPS, EFFICIENCY_REQUIREMENTS, EFFICIENCY_CALLBACKS, EFFICIENCY_GUI);

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_MINER, group));
            add(group -> new Skill(WHITELIST, group));
            add(group -> new Skill(EFFICIENCY, group));
        }};
    }

    private static void unlockExistingOreWhitelists(Skill skill)
    {
        skill.group.blockling.getTasks().getPrioritisedTasks().forEach(task ->
        {
            if (task.isConfigured())
            {
                task.getGoal().whitelists.forEach(goalWhitelist ->
                {
                    if (goalWhitelist.id.toString().equals("24d7135e-607b-413b-a2a7-00d19119b9de"))
                    {
                        goalWhitelist.setIsUnlocked(true, false);
                    }
                });
            }
        });
    }

    public static class Woodcutting
    {
        private static final SkillGeneralInfo NOVICE_LUMBERJACK_GENERAL = new SkillGeneralInfo(Skill.Type.AI, "woodcutting.novice_lumberjack");
        private static final SkillDefaultsInfo NOVICE_LUMBERJACK_DEFAULTS = new SkillDefaultsInfo(Skill.State.UNLOCKED);
        private static final SkillRelationshipsInfo NOVICE_LUMBERJACK_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] {  }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo NOVICE_LUMBERJACK_REQUIREMENTS = new SkillRequirementsInfo(0, new HashMap<BlocklingStats.Level, Integer>() {{  }});
        private static final SkillCallbackInfo NOVICE_LUMBERJACK_CALLBACKS = new SkillCallbackInfo(skill -> { skill.group.blockling.getTasks().setIsUnlocked(BlocklingTasks.WOODCUT, true, false); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture NOVICE_LUMBERJACK_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.WOODCUTTING_ICONS, 0, 0);
        private static final SkillGuiInfo NOVICE_LUMBERJACK_GUI = new SkillGuiInfo(0, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, NOVICE_LUMBERJACK_TEXTURE);
        public static final SkillInfo NOVICE_LUMBERJACK = new SkillInfo("c70f6e84-b82f-4a2d-8cbf-5914c589e8b6", NOVICE_LUMBERJACK_GENERAL, NOVICE_LUMBERJACK_DEFAULTS, NOVICE_LUMBERJACK_RELATIONSHIPS, NOVICE_LUMBERJACK_REQUIREMENTS, NOVICE_LUMBERJACK_CALLBACKS, NOVICE_LUMBERJACK_GUI);

        private static final SkillGeneralInfo WHITELIST_GENERAL = new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.whitelist");
        private static final SkillDefaultsInfo WHITELIST_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo WHITELIST_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_LUMBERJACK }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo WHITELIST_REQUIREMENTS = new SkillRequirementsInfo(2, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.WOODCUTTING, 5); }});
        private static final SkillCallbackInfo WHITELIST_CALLBACKS = new SkillCallbackInfo(skill -> { unlockExistingLogWhitelists(skill); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture WHITELIST_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.WOODCUTTING_ICONS, 1, 0);
        private static final SkillGuiInfo WHITELIST_GUI = new SkillGuiInfo(0, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, WHITELIST_TEXTURE);
        public static final SkillInfo WHITELIST = new SkillInfo("6c1c96c3-c784-4022-bcdd-432618f5d33d", WHITELIST_GENERAL, WHITELIST_DEFAULTS, WHITELIST_RELATIONSHIPS, WHITELIST_REQUIREMENTS, WHITELIST_CALLBACKS, WHITELIST_GUI);

        private static final SkillGeneralInfo EFFICIENCY_GENERAL = new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.efficiency");
        private static final SkillDefaultsInfo EFFICIENCY_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo EFFICIENCY_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_LUMBERJACK }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo EFFICIENCY_REQUIREMENTS = new SkillRequirementsInfo(1, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.WOODCUTTING, 10); }});
        private static final SkillCallbackInfo EFFICIENCY_CALLBACKS = new SkillCallbackInfo(skill -> { skill.group.blockling.getStats().woodcuttingSpeedSkillEfficiencyModifier.setValue(1.1f, false); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture EFFICIENCY_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.WOODCUTTING_ICONS, 2, 0);
        private static final SkillGuiInfo EFFICIENCY_GUI = new SkillGuiInfo(70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, EFFICIENCY_TEXTURE);
        public static final SkillInfo EFFICIENCY = new SkillInfo("3dae8614-511c-4378-9c8a-2ae0d3cddc97", EFFICIENCY_GENERAL, EFFICIENCY_DEFAULTS, EFFICIENCY_RELATIONSHIPS, EFFICIENCY_REQUIREMENTS, EFFICIENCY_CALLBACKS, EFFICIENCY_GUI);

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_LUMBERJACK, group));
            add(group -> new Skill(WHITELIST, group));
            add(group -> new Skill(EFFICIENCY, group));
        }};
    }

    private static void unlockExistingLogWhitelists(Skill skill)
    {
        skill.group.blockling.getTasks().getPrioritisedTasks().forEach(task ->
        {
            if (task.isConfigured())
            {
                task.getGoal().whitelists.forEach(goalWhitelist ->
                {
                    if (goalWhitelist.id.toString().equals("fbfbfd44-c1b0-4420-824a-270b34c866f7"))
                    {
                        goalWhitelist.setIsUnlocked(true, false);
                    }
                });
            }
        });
    }

    public static class Farming
    {
        private static final SkillGeneralInfo NOVICE_FARMER_GENERAL = new SkillGeneralInfo(Skill.Type.AI, "farming.novice_farmer");
        private static final SkillDefaultsInfo NOVICE_FARMER_DEFAULTS = new SkillDefaultsInfo(Skill.State.UNLOCKED);
        private static final SkillRelationshipsInfo NOVICE_FARMER_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] {  }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo NOVICE_FARMER_REQUIREMENTS = new SkillRequirementsInfo(0, new HashMap<BlocklingStats.Level, Integer>() {{  }});
        private static final SkillCallbackInfo NOVICE_FARMER_CALLBACKS = new SkillCallbackInfo(skill -> { skill.group.blockling.getTasks().setIsUnlocked(BlocklingTasks.FARM, true, false); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture NOVICE_FARMER_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.FARMING_ICONS, 0, 0);
        private static final SkillGuiInfo NOVICE_FARMER_GUI = new SkillGuiInfo(0, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, NOVICE_FARMER_TEXTURE);
        public static final SkillInfo NOVICE_FARMER = new SkillInfo("d70e08ef-25e0-4639-8af5-4b7d55893568", NOVICE_FARMER_GENERAL, NOVICE_FARMER_DEFAULTS, NOVICE_FARMER_RELATIONSHIPS, NOVICE_FARMER_REQUIREMENTS, NOVICE_FARMER_CALLBACKS, NOVICE_FARMER_GUI);

        private static final SkillGeneralInfo CROP_WHITELIST_GENERAL = new SkillGeneralInfo(Skill.Type.OTHER, "farming.crop_whitelist");
        private static final SkillDefaultsInfo CROP_WHITELIST_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo CROP_WHITELIST_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_FARMER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo CROP_WHITELIST_REQUIREMENTS = new SkillRequirementsInfo(2, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.FARMING, 5); }});
        private static final SkillCallbackInfo CROP_WHITELIST_CALLBACKS = new SkillCallbackInfo(skill -> { unlockExistingCropWhitelists(skill); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture CROP_WHITELIST_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.FARMING_ICONS, 1, 0);
        private static final SkillGuiInfo CROP_WHITELIST_GUI = new SkillGuiInfo(0, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, CROP_WHITELIST_TEXTURE);
        public static final SkillInfo CROP_WHITELIST = new SkillInfo("0d9f7b71-3930-4848-9329-8994b0ce7cd1", CROP_WHITELIST_GENERAL, CROP_WHITELIST_DEFAULTS, CROP_WHITELIST_RELATIONSHIPS, CROP_WHITELIST_REQUIREMENTS, CROP_WHITELIST_CALLBACKS, CROP_WHITELIST_GUI);

        private static final SkillGeneralInfo EFFICIENCY_GENERAL = new SkillGeneralInfo(Skill.Type.STAT, "farming.efficiency");
        private static final SkillDefaultsInfo EFFICIENCY_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo EFFICIENCY_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_FARMER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo EFFICIENCY_REQUIREMENTS = new SkillRequirementsInfo(1, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.FARMING, 10); }});
        private static final SkillCallbackInfo EFFICIENCY_CALLBACKS = new SkillCallbackInfo(skill -> { skill.group.blockling.getStats().farmingSpeedSkillEfficiencyModifier.setValue(1.1f, false); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture EFFICIENCY_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.FARMING_ICONS, 2, 0);
        private static final SkillGuiInfo EFFICIENCY_GUI = new SkillGuiInfo(70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, EFFICIENCY_TEXTURE);
        public static final SkillInfo EFFICIENCY = new SkillInfo("a7a02e05-c349-4a6c-9822-f05025c73bb5", EFFICIENCY_GENERAL, EFFICIENCY_DEFAULTS, EFFICIENCY_RELATIONSHIPS, EFFICIENCY_REQUIREMENTS, EFFICIENCY_CALLBACKS, EFFICIENCY_GUI);

        private static final SkillGeneralInfo REPLANTER_GENERAL = new SkillGeneralInfo(Skill.Type.STAT, "farming.replanter");
        private static final SkillDefaultsInfo REPLANTER_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo REPLANTER_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { NOVICE_FARMER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo REPLANTER_REQUIREMENTS = new SkillRequirementsInfo(2, new HashMap<BlocklingStats.Level, Integer>() {{ put(BlocklingStats.Level.FARMING, 10); }});
        private static final SkillCallbackInfo REPLANTER_CALLBACKS = new SkillCallbackInfo(skill -> true);
        private static final SkillGuiInfo.AbilityGuiTexture REPLANTER_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.FARMING_ICONS, 3, 0);
        private static final SkillGuiInfo REPLANTER_GUI = new SkillGuiInfo(-70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x64de10, REPLANTER_TEXTURE);
        public static final SkillInfo REPLANTER = new SkillInfo("25e708f5-fc53-452f-b882-9d31f754235c", REPLANTER_GENERAL, REPLANTER_DEFAULTS, REPLANTER_RELATIONSHIPS, REPLANTER_REQUIREMENTS, REPLANTER_CALLBACKS, REPLANTER_GUI);

        private static final SkillGeneralInfo SEED_WHITELIST_GENERAL = new SkillGeneralInfo(Skill.Type.OTHER, "farming.seed_whitelist");
        private static final SkillDefaultsInfo SEED_WHITELIST_DEFAULTS = new SkillDefaultsInfo(Skill.State.LOCKED);
        private static final SkillRelationshipsInfo SEED_WHITELIST_RELATIONSHIPS = new SkillRelationshipsInfo(new SkillInfo[] { REPLANTER }, new SkillInfo[] {  });
        private static final SkillRequirementsInfo SEED_WHITELIST_REQUIREMENTS = new SkillRequirementsInfo(2, new HashMap<BlocklingStats.Level, Integer>() {  });
        private static final SkillCallbackInfo SEED_WHITELIST_CALLBACKS = new SkillCallbackInfo(skill -> { unlockExistingSeedWhitelists(skill); return true; });
        private static final SkillGuiInfo.AbilityGuiTexture SEED_WHITELIST_TEXTURE = new SkillGuiInfo.AbilityGuiTexture(GuiUtil.FARMING_ICONS, 1, 0);
        private static final SkillGuiInfo SEED_WHITELIST_GUI = new SkillGuiInfo(-70, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, SEED_WHITELIST_TEXTURE);
        public static final SkillInfo SEED_WHITELIST = new SkillInfo("8595c654-a19c-4c58-a9c1-a7a5087c397f", SEED_WHITELIST_GENERAL, SEED_WHITELIST_DEFAULTS, SEED_WHITELIST_RELATIONSHIPS, SEED_WHITELIST_REQUIREMENTS, SEED_WHITELIST_CALLBACKS, SEED_WHITELIST_GUI);

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_FARMER, group));
            add(group -> new Skill(CROP_WHITELIST, group));
            add(group -> new Skill(EFFICIENCY, group));
            add(group -> new Skill(REPLANTER, group));
            add(group -> new Skill(SEED_WHITELIST, group));
        }};
    }

    private static void unlockExistingCropWhitelists(Skill skill)
    {
        skill.group.blockling.getTasks().getPrioritisedTasks().forEach(task ->
        {
            if (task.isConfigured())
            {
                task.getGoal().whitelists.forEach(goalWhitelist ->
                {
                    if (goalWhitelist.id.toString().equals("25140edf-f60e-459e-b1f0-9ff82108ec0b"))
                    {
                        goalWhitelist.setIsUnlocked(true, false);
                    }
                });
            }
        });
    }

    private static void unlockExistingSeedWhitelists(Skill skill)
    {
        skill.group.blockling.getTasks().getPrioritisedTasks().forEach(task ->
        {
            if (task.isConfigured())
            {
                task.getGoal().whitelists.forEach(goalWhitelist ->
                {
                    if (goalWhitelist.id.toString().equals("d77bf1c1-7718-4733-b763-298b03340eea"))
                    {
                        goalWhitelist.setIsUnlocked(true, false);
                    }
                });
            }
        });
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

    public SkillGroup findGroup(SkillInfo skillInfo)
    {
        return skillGroups.stream().filter(group -> group.getSkills().stream().filter(skill -> skill.info == skillInfo).findFirst().orElse(null) != null).findFirst().get();
    }

    public SkillGroup getGroup(SkillGroupInfo groupInfo)
    {
        return getGroup(groupInfo.id);
    }

    public SkillGroup getGroup(UUID groupId)
    {
        return skillGroups.stream().filter(group -> group.info.id.equals(groupId)).findFirst().get();
    }

    public Skill getSkill(SkillInfo skillInfo)
    {
        return findGroup(skillInfo).getSkill(skillInfo);
    }
}
