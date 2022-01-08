package com.willr27.blocklings.skills;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.gui.widgets.SkillWidget;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.skills.info.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.*;
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
        public static final SkillInfo NOVICE_GUARD = new SkillInfo("dcbf7cc1-8bef-49aa-a5a0-cd70cb40cbac",
                new SkillGeneralInfo(Skill.Type.AI, "combat.novice_guard"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT_BY, true); skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT, true); return true; }),
                new SkillGuiInfo(0, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillGuiTexture(GuiUtil.COMBAT_ICONS, 0, 0)));

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_GUARD, group));
        }};
    }

    public static class Mining
    {
        public static final SkillInfo NOVICE_MINER = new SkillInfo("dcbf7cc1-8be-49aa-a5a0-cd70cb40cbac",
                new SkillGeneralInfo(Skill.Type.AI, "mining.novice_miner"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MINE, true); return true; }),
                new SkillGuiInfo(0, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillGuiTexture(GuiUtil.MINING_ICONS, 0, 0)));

        public static final SkillInfo WHITELIST = new SkillInfo("8963cddd-06dd-4b5a-8c1e-b1e38a99b25f",
                new SkillGeneralInfo(Skill.Type.OTHER, "mining.whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 5); }}),
                new SkillCallbackInfo(skill -> { unlockExistingWhitelists(skill, "24d7135e-607b-413b-a2a7-00d19119b9de"); return true; }),
                new SkillGuiInfo(0, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillGuiTexture(GuiUtil.MINING_ICONS, 1, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(NOVICE_MINER);
            }
        };

        public static final SkillInfo EFFICIENCY = new SkillInfo("19253148-ff6e-4395-9464-289e081b442b",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.efficiency"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 10); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().miningSpeedSkillEfficiencyModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillGuiTexture(GuiUtil.MINING_ICONS, 2, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(NOVICE_MINER);
            }
        };

        public static final SkillInfo ADRENALINE = new SkillInfo("9cd9212d-0f3b-47c3-85f1-9fe18388a42b",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.adrenaline"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().miningSpeedSkillAdrenalineModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(140, -50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillGuiTexture(GuiUtil.MINING_ICONS, 3, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(MOMENTUM, HASTY, NIGHT_OWL);
            }

            @Override
            public void tick(Skill skill)
            {
                skill.blockling.getStats().miningSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
            }
        };

        public static final SkillInfo MOMENTUM = new SkillInfo("656aaacb-3c87-4b36-b12c-6ab970f09279",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.momentum"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().miningSpeedSkillMomentumModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(140, 50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xad79b5, new SkillGuiInfo.SkillGuiTexture(GuiUtil.MINING_ICONS, 4, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo HASTY = new SkillInfo("1cfca8ba-d518-4403-b0ed-8da83e350de3",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.hasty"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().miningSpeedSkillHastyModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(210, -50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillGuiTexture(GuiUtil.MINING_ICONS, 5, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, NIGHT_OWL);
            }
        };

        public static final SkillInfo NIGHT_OWL = new SkillInfo("1a2fca9b-c745-4274-9f35-a577dfe65c8d",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.night_owl"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().miningSpeedSkillNightOwlModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(210, 50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillGuiTexture(GuiUtil.MINING_ICONS, 6, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, HASTY);
            }

            @Override
            public void tick(Skill skill)
            {
                if (!skill.blockling.level.isClientSide)
                {
                    float value = 15.0f * (1.0f - (skill.blockling.level.getMaxLocalRawBrightness(skill.blockling.blockPosition())) / 15.0f);

                    if (value != skill.blockling.getStats().miningSpeedSkillNightOwlModifier.getValue())
                    {
                        skill.blockling.getStats().miningSpeedSkillNightOwlModifier.setValue(value, true);
                    }
                }
            }
        };

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_MINER, group));
            add(group -> new Skill(WHITELIST, group));
            add(group -> new Skill(EFFICIENCY, group));
            add(group -> new Skill(ADRENALINE, group));
            add(group -> new Skill(MOMENTUM, group));
            add(group -> new Skill(HASTY, group));
            add(group -> new Skill(NIGHT_OWL, group));
        }};
    }

    public static class Woodcutting
    {
        public static final SkillInfo NOVICE_LUMBERJACK = new SkillInfo("c70f6e84-b82f-4a2d-8cbf-5914c589e8b6",
                new SkillGeneralInfo(Skill.Type.AI, "woodcutting.novice_lumberjack"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.WOODCUT, true, false); return true; }),
                new SkillGuiInfo(0, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillGuiTexture(GuiUtil.WOODCUTTING_ICONS, 0, 0)));

        public static final SkillInfo WHITELIST = new SkillInfo("6c1c96c3-c784-4022-bcdd-432618f5d33d",
                new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 5); }}),
                new SkillCallbackInfo(skill -> { unlockExistingWhitelists(skill, "fbfbfd44-c1b0-4420-824a-270b34c866f7"); return true; }),
                new SkillGuiInfo(0, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillGuiTexture(GuiUtil.WOODCUTTING_ICONS, 1, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(NOVICE_LUMBERJACK);
            }
        };

        public static final SkillInfo EFFICIENCY = new SkillInfo("3dae8614-511c-4378-9c8a-2ae0d3cddc97",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.efficiency"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 10); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().woodcuttingSpeedSkillEfficiencyModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillGuiTexture(GuiUtil.WOODCUTTING_ICONS, 2, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(NOVICE_LUMBERJACK);
            }
        };

        public static final SkillInfo ADRENALINE = new SkillInfo("bdb58fa2-174e-4be6-880b-c355ee76aab6",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.adrenaline"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().woodcuttingSpeedSkillAdrenalineModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(140, -50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillGuiTexture(GuiUtil.WOODCUTTING_ICONS, 3, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(MOMENTUM, HASTY, NIGHT_OWL);
            }

            @Override
            public void tick(Skill skill)
            {
                skill.blockling.getStats().woodcuttingSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
            }
        };

        public static final SkillInfo MOMENTUM = new SkillInfo("7b7ce4aa-8f05-48b9-a2c1-f3b714ba339a",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.momentum"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().woodcuttingSpeedSkillMomentumModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(140, 50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x9f6a16, new SkillGuiInfo.SkillGuiTexture(GuiUtil.WOODCUTTING_ICONS, 4, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo HASTY = new SkillInfo("a7f1ab81-e057-4f6a-a978-f10e8ee98005",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.hasty"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().woodcuttingSpeedSkillHastyModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(210, -50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillGuiTexture(GuiUtil.WOODCUTTING_ICONS, 5, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, NIGHT_OWL);
            }
        };

        public static final SkillInfo NIGHT_OWL = new SkillInfo("1476e2be-9d0f-40cf-901d-b5ba18dea16f",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.night_owl"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().woodcuttingSpeedSkillNightOwlModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(210, 50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillGuiTexture(GuiUtil.WOODCUTTING_ICONS, 6, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, HASTY);
            }

            @Override
            public void tick(Skill skill)
            {
                if (!skill.blockling.level.isClientSide)
                {
                    float value = 15.0f * (1.0f - (skill.blockling.level.getMaxLocalRawBrightness(skill.blockling.blockPosition())) / 15.0f);

                    if (value != skill.blockling.getStats().woodcuttingSpeedSkillNightOwlModifier.getValue())
                    {
                        skill.blockling.getStats().woodcuttingSpeedSkillNightOwlModifier.setValue(value, true);
                    }
                }
            }
        };

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_LUMBERJACK, group));
            add(group -> new Skill(WHITELIST, group));
            add(group -> new Skill(EFFICIENCY, group));
            add(group -> new Skill(ADRENALINE, group));
            add(group -> new Skill(MOMENTUM, group));
            add(group -> new Skill(HASTY, group));
            add(group -> new Skill(NIGHT_OWL, group));
        }};
    }

    public static class Farming
    {
        public static final SkillInfo NOVICE_FARMER = new SkillInfo("d70e08ef-25e0-4639-8af5-4b7d55893568",
                new SkillGeneralInfo(Skill.Type.AI, "farming.novice_farmer"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.FARM, true, false); return true; }),
                new SkillGuiInfo(0, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 0, 0)));

        public static final SkillInfo CROP_WHITELIST = new SkillInfo("0d9f7b71-3930-4848-9329-8994b0ce7cd1",
                new SkillGeneralInfo(Skill.Type.OTHER, "farming.crop_whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 5); }}),
                new SkillCallbackInfo(skill -> { unlockExistingWhitelists(skill, "25140edf-f60e-459e-b1f0-9ff82108ec0b"); return true; }),
                new SkillGuiInfo(0, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 1, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(NOVICE_FARMER);
            }
        };

        public static final SkillInfo EFFICIENCY = new SkillInfo("a7a02e05-c349-4a6c-9822-f05025c73bb5",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.efficiency"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 10); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().farmingSpeedSkillEfficiencyModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 2, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(NOVICE_FARMER);
            }
        };

        public static final SkillInfo REPLANTER = new SkillInfo("25e708f5-fc53-452f-b882-9d31f754235c",
                new SkillGeneralInfo(Skill.Type.AI, "farming.replanter"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 10); }}),
                new SkillCallbackInfo(skill -> true),
                new SkillGuiInfo(-70, 0, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x64de10, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 3, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(NOVICE_FARMER);
            }
        };

        public static final SkillInfo SEED_WHITELIST = new SkillInfo("8595c654-a19c-4c58-a9c1-a7a5087c397f",
                new SkillGeneralInfo(Skill.Type.OTHER, "farming.seed_whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 15); }}),
                new SkillCallbackInfo(skill -> { unlockExistingWhitelists(skill, "d77bf1c1-7718-4733-b763-298b03340eea"); return true; }),
                new SkillGuiInfo(-70, 70, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 1, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(REPLANTER);
            }
        };

        public static final SkillInfo ADRENALINE = new SkillInfo("51bb0230-e484-47ae-9c7f-8a4ec7868683",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.adrenaline"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().farmingSpeedSkillAdrenalineModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(140, -50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 4, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(MOMENTUM, HASTY, NIGHT_OWL);
            }

            @Override
            public void tick(Skill skill)
            {
                skill.blockling.getStats().farmingSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
            }
        };

        public static final SkillInfo MOMENTUM = new SkillInfo("e2c8db1a-bc32-482e-9225-54027196f7d2",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.momentum"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().farmingSpeedSkillMomentumModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(140, 50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x39bb39, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 5, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo HASTY = new SkillInfo("da1bd12f-044b-434c-a627-ef7146013d9a",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.hasty"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().farmingSpeedSkillHastyModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(210, -50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 6, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, NIGHT_OWL);
            }
        };

        public static final SkillInfo NIGHT_OWL = new SkillInfo("b06bfa1b-8b01-4802-a980-cad92b537273",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.night_owl"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillCallbackInfo(skill -> { skill.blockling.getStats().farmingSpeedSkillNightOwlModifier.setIsEnabled(true, false); return true; }),
                new SkillGuiInfo(210, 50, SkillWidget.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillGuiTexture(GuiUtil.FARMING_ICONS, 7, 0)))
        {
            @Override
            public List<SkillInfo> parents()
            {
                return Arrays.asList(EFFICIENCY);
            }

            @Override
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, HASTY);
            }

            @Override
            public void tick(Skill skill)
            {
                if (!skill.blockling.level.isClientSide)
                {
                    float value = 15.0f * (1.0f - (skill.blockling.level.getMaxLocalRawBrightness(skill.blockling.blockPosition())) / 15.0f);

                    if (value != skill.blockling.getStats().farmingSpeedSkillNightOwlModifier.getValue())
                    {
                        skill.blockling.getStats().farmingSpeedSkillNightOwlModifier.setValue(value, true);
                    }
                }
            }
        };

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_FARMER, group));
            add(group -> new Skill(CROP_WHITELIST, group));
            add(group -> new Skill(EFFICIENCY, group));
            add(group -> new Skill(REPLANTER, group));
            add(group -> new Skill(SEED_WHITELIST, group));
            add(group -> new Skill(ADRENALINE, group));
            add(group -> new Skill(MOMENTUM, group));
            add(group -> new Skill(HASTY, group));
            add(group -> new Skill(NIGHT_OWL, group));
        }};
    }

    private static void unlockExistingWhitelists(Skill skill, String whitelistId)
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

    private final BlocklingEntity blockling;

    private final List<SkillGroup> skillGroups = new ArrayList<>();

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

    public void tick()
    {
        for (SkillGroup skillGroup : skillGroups)
        {
            skillGroup.getSkills().stream().filter(Skill::isBought).forEach(skill -> skill.info.tick(skill));
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
