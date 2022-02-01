package com.willr27.blocklings.skills;

import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.controls.skills.SkillControl;
import com.willr27.blocklings.skills.info.*;
import com.willr27.blocklings.task.BlocklingTasks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The blockling's skills.
 */
public class BlocklingSkills
{
    /**
     * The general skills.
     */
    public static class General
    {
        public static final SkillInfo HEAL = new SkillInfo("e6361ca8-a0c5-4a64-8be9-6928a98a4594",
                new SkillGeneralInfo(Skill.Type.OTHER, "general.heal"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.TOTAL, 10); }}),
                new SkillGuiInfo(0, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xa8f4a1, new SkillGuiInfo.SkillIconTexture(GuiTextures.GENERAL_ICONS, 0, 0)));

        public static final SkillInfo PACKLING = new SkillInfo("5cd54257-954f-4962-b248-99f58fb11d5d",
                new SkillGeneralInfo(Skill.Type.OTHER, "general.packling"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.TOTAL, 25); }}),
                new SkillGuiInfo(-50, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xcca58a, new SkillGuiInfo.SkillIconTexture(GuiTextures.GENERAL_ICONS, 1, 0)));

        public static final SkillInfo ARMADILLO = new SkillInfo("28ae60b1-1e8a-4c73-b1a1-5519be35d0ea",
                new SkillGeneralInfo(Skill.Type.OTHER, "general.armadillo"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.TOTAL, 50); }}),
                new SkillGuiInfo(50, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xa8924f, new SkillGuiInfo.SkillIconTexture(GuiTextures.GENERAL_ICONS, 2, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(PACKLING);
            }
        };

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(HEAL, group));
            add(group -> new Skill(PACKLING, group));
            add(group -> new Skill(ARMADILLO, group));
        }};
    }

    /**
     * The combat skills.
     */
    public static class Combat
    {
        public static final SkillInfo NOVICE_GUARD = new SkillInfo("dcbf7cc1-8bef-49aa-a5a0-cd70cb40cbac",
                new SkillGeneralInfo(Skill.Type.AI, "combat.novice_guard"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillGuiInfo(0, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 0, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT_BY, true);
                skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT, true);

                return true;
            }
        };

        public static final SkillInfo WHITELIST = new SkillInfo("9065aad7-70df-4ae1-88f3-40ef702b7212",
                new SkillGeneralInfo(Skill.Type.OTHER, "combat.whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 5); }}),
                new SkillGuiInfo(0, 70, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 1, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                unlockExistingWhitelists(skill, "540241cd-085a-4c1f-9e90-8aea973568a8");

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_GUARD);
            }
        };

        public static final SkillInfo SHARPNESS = new SkillInfo("1aac1132-6cde-4c1d-8292-1cffe93a7f5a",
                new SkillGeneralInfo(Skill.Type.STAT, "combat.sharpness"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 10); }}),
                new SkillGuiInfo(70, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 2, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().mainHandAttackDamageSkillSharpnessModifier.setIsEnabled(true, false);
                skill.blockling.getStats().offHandAttackDamageSkillSharpnessModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_GUARD);
            }
        };

        public static final SkillInfo BERSERKER = new SkillInfo("dfcfc9df-608b-4d6e-b6a7-ba20c55191b6",
                new SkillGeneralInfo(Skill.Type.STAT, "combat.berserker"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 25); }}),
                new SkillGuiInfo(140, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 3, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().mainHandAttackDamageSkillBerserkerModifier.setIsEnabled(true, false);
                skill.blockling.getStats().offHandAttackDamageSkillBerserkerModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
            {
                skill.blockling.getStats().mainHandAttackDamageSkillBerserkerModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
                skill.blockling.getStats().offHandAttackDamageSkillBerserkerModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(SHARPNESS);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(MOMENTUM, WRECKLESS, PHOTOPHILE);
            }
        };

        public static final SkillInfo MOMENTUM = new SkillInfo("31c6169c-605a-4d88-b4ea-71210afcd028",
                new SkillGeneralInfo(Skill.Type.STAT, "combat.momentum"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 25); }}),
                new SkillGuiInfo(140, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xad79b5, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 4, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().attackSpeedSkillMomentumModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(SHARPNESS);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(BERSERKER, WRECKLESS, PHOTOPHILE);
            }
        };

        public static final SkillInfo WRECKLESS = new SkillInfo("662b0efa-e150-44c7-aaf8-2cf61bcef330",
                new SkillGeneralInfo(Skill.Type.STAT, "combat.wreckless"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 25); }}),
                new SkillGuiInfo(210, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 5, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().mainHandAttackDamageSkillWrecklessModifier.setIsEnabled(true, false);
                skill.blockling.getStats().offHandAttackDamageSkillWrecklessModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(SHARPNESS);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(BERSERKER, MOMENTUM, PHOTOPHILE);
            }
        };

        public static final SkillInfo PHOTOPHILE = new SkillInfo("07ccf340-a3c1-406e-81fc-3622ee0e9463",
                new SkillGeneralInfo(Skill.Type.STAT, "combat.photophile"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 25); }}),
                new SkillGuiInfo(210, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xd3b630, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 6, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().attackSpeedSkillPhotophileModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
            {
                if (!skill.blockling.level.isClientSide)
                {
                    float value = 15.0f * (skill.blockling.level.getMaxLocalRawBrightness(skill.blockling.blockPosition()) / 15.0f);

                    if (value != skill.blockling.getStats().attackSpeedSkillPhotophileModifier.getValue())
                    {
                        skill.blockling.getStats().attackSpeedSkillPhotophileModifier.setValue(value, true);
                    }
                }
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(SHARPNESS);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(BERSERKER, MOMENTUM, WRECKLESS);
            }
        };

        public static final SkillInfo REGENERATION_1 = new SkillInfo("0b1a72e4-486a-46a6-80f3-4a98694d99ea",
                new SkillGeneralInfo(Skill.Type.OTHER, "combat.regeneration_1"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 10); }}),
                new SkillGuiInfo(-70, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xabce61, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 7, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_GUARD);
            }
        };

        public static final SkillInfo REGENERATION_2 = new SkillInfo("f790d1a7-f680-4ea1-a3e1-18bac656614d",
                new SkillGeneralInfo(Skill.Type.OTHER, "combat.regeneration_2"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 30); }}),
                new SkillGuiInfo(-140, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xc4ff47, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 8, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(REGENERATION_1);
            }
        };

        public static final SkillInfo REGENERATION_3 = new SkillInfo("1330ddfe-9405-4bde-b870-831b999bce18",
                new SkillGeneralInfo(Skill.Type.OTHER, "combat.regeneration_3"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.COMBAT, 50); }}),
                new SkillGuiInfo(-210, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x75ff35, new SkillGuiInfo.SkillIconTexture(GuiTextures.COMBAT_ICONS, 9, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(REGENERATION_2);
            }
        };

        public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>()
        {{
            add(group -> new Skill(NOVICE_GUARD, group));
            add(group -> new Skill(WHITELIST, group));
            add(group -> new Skill(SHARPNESS, group));
            add(group -> new Skill(BERSERKER, group));
            add(group -> new Skill(MOMENTUM, group));
            add(group -> new Skill(WRECKLESS, group));
            add(group -> new Skill(PHOTOPHILE, group));
            add(group -> new Skill(REGENERATION_1, group));
            add(group -> new Skill(REGENERATION_2, group));
            add(group -> new Skill(REGENERATION_3, group));
        }};
    }

    /**
     * The mining skills.
     */
    public static class Mining
    {
        public static final SkillInfo NOVICE_MINER = new SkillInfo("dcbf7cc1-8be-49aa-a5a0-cd70cb40cbac",
                new SkillGeneralInfo(Skill.Type.AI, "mining.novice_miner"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillGuiInfo(0, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 0, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MINE, true);

                return true;
            }
        };

        public static final SkillInfo WHITELIST = new SkillInfo("8963cddd-06dd-4b5a-8c1e-b1e38a99b25f",
                new SkillGeneralInfo(Skill.Type.OTHER, "mining.whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 5); }}),
                new SkillGuiInfo(0, 70, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 1, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                unlockExistingWhitelists(skill, "24d7135e-607b-413b-a2a7-00d19119b9de");

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_MINER);
            }
        };

        public static final SkillInfo EFFICIENCY = new SkillInfo("19253148-ff6e-4395-9464-289e081b442b",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.efficiency"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 10); }}),
                new SkillGuiInfo(70, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 2, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().miningSpeedSkillEfficiencyModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_MINER);
            }
        };

        public static final SkillInfo ADRENALINE = new SkillInfo("9cd9212d-0f3b-47c3-85f1-9fe18388a42b",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.adrenaline"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillGuiInfo(140, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 3, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().miningSpeedSkillAdrenalineModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
            {
                skill.blockling.getStats().miningSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(MOMENTUM, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo MOMENTUM = new SkillInfo("656aaacb-3c87-4b36-b12c-6ab970f09279",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.momentum"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillGuiInfo(140, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xad79b5, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 4, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().miningSpeedSkillMomentumModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo HASTY = new SkillInfo("1cfca8ba-d518-4403-b0ed-8da83e350de3",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.hasty"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillGuiInfo(210, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 5, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().miningSpeedSkillHastyModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, NIGHT_OWL);
            }
        };

        public static final SkillInfo NIGHT_OWL = new SkillInfo("1a2fca9b-c745-4274-9f35-a577dfe65c8d",
                new SkillGeneralInfo(Skill.Type.STAT, "mining.night_owl"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 25); }}),
                new SkillGuiInfo(210, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 6, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().miningSpeedSkillNightOwlModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
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

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, HASTY);
            }
        };

        public static final SkillInfo HOT_HANDS = new SkillInfo("6ddccec1-af7b-4e8a-90a0-3962ef422858",
                new SkillGeneralInfo(Skill.Type.OTHER, "mining.hot_hands"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.MINING, 40); }}),
                new SkillGuiInfo(0, -70, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xdd3355, new SkillGuiInfo.SkillIconTexture(GuiTextures.MINING_ICONS, 7, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_MINER);
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
            add(group -> new Skill(HOT_HANDS, group));
        }};
    }

    /**
     * The woodcutting skills.
     */
    public static class Woodcutting
    {
        public static final SkillInfo NOVICE_LUMBERJACK = new SkillInfo("c70f6e84-b82f-4a2d-8cbf-5914c589e8b6",
                new SkillGeneralInfo(Skill.Type.AI, "woodcutting.novice_lumberjack"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillGuiInfo(0, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 0, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.WOODCUT, true, false);

                return true;
            }
        };

        public static final SkillInfo WHITELIST = new SkillInfo("6c1c96c3-c784-4022-bcdd-432618f5d33d",
                new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 5); }}),
                new SkillGuiInfo(0, 70, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 1, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                unlockExistingWhitelists(skill, "fbfbfd44-c1b0-4420-824a-270b34c866f7");

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_LUMBERJACK);
            }
        };

        public static final SkillInfo EFFICIENCY = new SkillInfo("3dae8614-511c-4378-9c8a-2ae0d3cddc97",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.efficiency"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 10); }}),
                new SkillGuiInfo(70, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 2, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().woodcuttingSpeedSkillEfficiencyModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_LUMBERJACK);
            }
        };

        public static final SkillInfo ADRENALINE = new SkillInfo("bdb58fa2-174e-4be6-880b-c355ee76aab6",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.adrenaline"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillGuiInfo(140, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 3, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().woodcuttingSpeedSkillAdrenalineModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
            {
                skill.blockling.getStats().woodcuttingSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(MOMENTUM, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo MOMENTUM = new SkillInfo("7b7ce4aa-8f05-48b9-a2c1-f3b714ba339a",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.momentum"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillGuiInfo(140, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x9f6a16, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 4, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().woodcuttingSpeedSkillMomentumModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo HASTY = new SkillInfo("a7f1ab81-e057-4f6a-a978-f10e8ee98005",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.hasty"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillGuiInfo(210, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 5, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().woodcuttingSpeedSkillHastyModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, NIGHT_OWL);
            }
        };

        public static final SkillInfo NIGHT_OWL = new SkillInfo("1476e2be-9d0f-40cf-901d-b5ba18dea16f",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.night_owl"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 25); }}),
                new SkillGuiInfo(210, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 6, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().woodcuttingSpeedSkillNightOwlModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
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

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, HASTY);
            }
        };

        public static final SkillInfo LEAF_BLOWER = new SkillInfo("c4443300-1004-4527-904f-ef097b65e816",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.leaf_blower"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 15); }}),
                new SkillGuiInfo(-210, -100, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x227010, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 7, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_LUMBERJACK);
            }
        };

        public static final SkillInfo TREE_SURGEON = new SkillInfo("07168fe8-6434-446a-ad37-09f41fa9b9d9",
                new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.tree_surgeon"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.WOODCUTTING, 30); }}),
                new SkillGuiInfo(-210, -170, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x5f6d18, new SkillGuiInfo.SkillIconTexture(GuiTextures.WOODCUTTING_ICONS, 8, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(LEAF_BLOWER);
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
            add(group -> new Skill(LEAF_BLOWER, group));
            add(group -> new Skill(TREE_SURGEON, group));
        }};
    }

    /**
     * The farming skills.
     */
    public static class Farming
    {
        public static final SkillInfo NOVICE_FARMER = new SkillInfo("d70e08ef-25e0-4639-8af5-4b7d55893568",
                new SkillGeneralInfo(Skill.Type.AI, "farming.novice_farmer"),
                new SkillDefaultsInfo(Skill.State.UNLOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{  }}),
                new SkillGuiInfo(0, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 0, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.FARM, true, false);

                return true;
            }
        };

        public static final SkillInfo CROP_WHITELIST = new SkillInfo("0d9f7b71-3930-4848-9329-8994b0ce7cd1",
                new SkillGeneralInfo(Skill.Type.OTHER, "farming.crop_whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 5); }}),
                new SkillGuiInfo(0, 70, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 1, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                unlockExistingWhitelists(skill, "25140edf-f60e-459e-b1f0-9ff82108ec0b");

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_FARMER);
            }
        };

        public static final SkillInfo EFFICIENCY = new SkillInfo("a7a02e05-c349-4a6c-9822-f05025c73bb5",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.efficiency"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 10); }}),
                new SkillGuiInfo(70, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 2, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().farmingSpeedSkillEfficiencyModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_FARMER);
            }
        };

        public static final SkillInfo REPLANTER = new SkillInfo("25e708f5-fc53-452f-b882-9d31f754235c",
                new SkillGeneralInfo(Skill.Type.AI, "farming.replanter"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 10); }}),
                new SkillGuiInfo(-70, 0, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x64de10, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 3, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(NOVICE_FARMER);
            }
        };

        public static final SkillInfo SEED_WHITELIST = new SkillInfo("8595c654-a19c-4c58-a9c1-a7a5087c397f",
                new SkillGeneralInfo(Skill.Type.OTHER, "farming.seed_whitelist"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 15); }}),
                new SkillGuiInfo(-70, 70, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 1, 0)))
        {
            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(REPLANTER);
            }

            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                unlockExistingWhitelists(skill, "d77bf1c1-7718-4733-b763-298b03340eea");

                return true;
            }
        };

        public static final SkillInfo ADRENALINE = new SkillInfo("51bb0230-e484-47ae-9c7f-8a4ec7868683",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.adrenaline"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillGuiInfo(140, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 4, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().farmingSpeedSkillAdrenalineModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
            {
                skill.blockling.getStats().farmingSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(MOMENTUM, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo MOMENTUM = new SkillInfo("e2c8db1a-bc32-482e-9225-54027196f7d2",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.momentum"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillGuiInfo(140, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x39bb39, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 5, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().farmingSpeedSkillMomentumModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, HASTY, NIGHT_OWL);
            }
        };

        public static final SkillInfo HASTY = new SkillInfo("da1bd12f-044b-434c-a627-ef7146013d9a",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.hasty"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillGuiInfo(210, -50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 6, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().farmingSpeedSkillHastyModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, NIGHT_OWL);
            }
        };

        public static final SkillInfo NIGHT_OWL = new SkillInfo("b06bfa1b-8b01-4802-a980-cad92b537273",
                new SkillGeneralInfo(Skill.Type.STAT, "farming.night_owl"),
                new SkillDefaultsInfo(Skill.State.LOCKED),
                new SkillRequirementsInfo(new HashMap<BlocklingAttributes.Level, Integer>() {{ put(BlocklingAttributes.Level.FARMING, 25); }}),
                new SkillGuiInfo(210, 50, SkillControl.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillIconTexture(GuiTextures.FARMING_ICONS, 7, 0)))
        {
            @Override
            public boolean onTryBuy(@Nonnull Skill skill)
            {
                skill.blockling.getStats().farmingSpeedSkillNightOwlModifier.setIsEnabled(true, false);

                return true;
            }

            @Override
            public void tick(@Nonnull Skill skill)
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

            @Override
            @Nonnull
            public List<SkillInfo> parents()
            {
                return Collections.singletonList(EFFICIENCY);
            }

            @Override
            @Nonnull
            public List<SkillInfo> conflicts()
            {
                return Arrays.asList(ADRENALINE, MOMENTUM, HASTY);
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

    /**
     * Helper method to unlock any existing whitelist with the given id.
     *
     * @param skill the skill that has been unlocked.
     * @param whitelistId the whitelist id to unlock.
     */
    private static void unlockExistingWhitelists(@Nonnull Skill skill, @Nonnull String whitelistId)
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

    /**
     * Writes the skills to the given tag.
     *
     * @param tag the tag to write to.
     */
    public void writeToNBT(@Nonnull CompoundNBT tag)
    {
        CompoundNBT skillsTag = new CompoundNBT();

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

        tag.put("skills", skillsTag);
    }

    /**
     * Reads the skills from the given tag.
     *
     * @param tag the tag to read from.
     */
    public void readFromNBT(@Nonnull CompoundNBT tag)
    {
        CompoundNBT skillsTag = (CompoundNBT) tag.get("skills");

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
