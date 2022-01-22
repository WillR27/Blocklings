package com.willr27.blocklings.attribute;

import com.willr27.blocklings.attribute.attributes.EnumAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.*;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.skills.BlocklingSkills;
import com.willr27.blocklings.skills.info.SkillInfo;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Used to manage the attributes associated with a blockling.
 */
public class BlocklingAttributes
{
    /**
     * The list of attributes added via add attribute.
     * Used to save/load nbt data and sync to client/server.
     */
    @Nonnull
    public final List<Attribute<?>> attributes = new ArrayList<>();

    /**
     * The list of attributes added via add modifier.
     * Used to save/load nbt data and sync to client/server.
     */
    @Nonnull
    public final List<IModifier<?>> modifiers = new ArrayList<>();

    @Nonnull
    public final IntAttribute combatLevel;
    @Nonnull
    public final IntAttribute miningLevel;
    @Nonnull
    public final IntAttribute woodcuttingLevel;
    @Nonnull
    public final IntAttribute farmingLevel;
    @Nonnull
    public final Attribute<Integer> totalLevel;

    @Nonnull
    public final IntAttribute combatXp;
    @Nonnull
    public final IntAttribute miningXp;
    @Nonnull
    public final IntAttribute woodcuttingXp;
    @Nonnull
    public final IntAttribute farmingXp;

    @Nonnull
    public final EnumAttribute<BlocklingHand> hand;

    @Nonnull
    public final ModifiableFloatAttribute maxHealth;
    @Nonnull
    public final ModifiableFloatAttributeModifier maxHealthBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier maxHealthTypeModifier;
    @Nonnull
    public final FloatAttributeModifier maxHealthCombatLevelModifier;

    @Nonnull
    public final ModifiableFloatAttribute mainHandAttackDamage;
    @Nonnull
    public final ModifiableFloatAttributeModifier mainHandAttackDamageBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier mainHandAttackDamageTypeModifier;
    @Nonnull
    public final FloatAttributeModifier mainHandAttackDamageCombatLevelModifier;
    @Nonnull
    public final FloatAttributeModifier mainHandAttackDamageToolModifier;

    @Nonnull
    public final ModifiableFloatAttribute offHandAttackDamage;
    @Nonnull
    public final ModifiableFloatAttributeModifier offHandAttackDamageBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier offHandAttackDamageTypeModifier;
    @Nonnull
    public final FloatAttributeModifier offHandAttackDamageCombatLevelModifier;
    @Nonnull
    public final FloatAttributeModifier offHandAttackDamageToolModifier;

    @Nonnull
    public final AveragedAttribute attackSpeed;
    @Nonnull
    public final ModifiableFloatAttributeModifier attackSpeedBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier attackSpeedTypeModifier;
    @Nonnull
    public final FloatAttributeModifier attackSpeedLevelModifier;
    @Nonnull
    public final FloatAttributeModifier attackSpeedMainHandModifier;
    @Nonnull
    public final FloatAttributeModifier attackSpeedOffHandModifier;

    @Nonnull
    public final ModifiableFloatAttribute armour;
    @Nonnull
    public final ModifiableFloatAttributeModifier armourBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier armourCombatLevelModifier;
    @Nonnull
    public final FloatAttributeModifier armourTypeModifier;

    @Nonnull
    public final ModifiableFloatAttribute armourToughness;
    @Nonnull
    public final ModifiableFloatAttributeModifier armourToughnessBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier armourToughnessCombatLevelModifier;
    @Nonnull
    public final FloatAttributeModifier armourToughnessTypeModifier;

    @Nonnull
    public final ModifiableFloatAttribute knockbackResistance;
    @Nonnull
    public final ModifiableFloatAttributeModifier knockbackResistanceBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier knockbackResistanceCombatLevelModifier;
    @Nonnull
    public final FloatAttributeModifier knockbackResistanceTypeModifier;

    @Nonnull
    public final ModifiableFloatAttribute moveSpeed;
    @Nonnull
    public final ModifiableFloatAttributeModifier moveSpeedBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier moveSpeedTypeModifier;

    @Nonnull
    public final ModifiableFloatAttribute miningRange;
    @Nonnull
    public final ModifiableFloatAttribute miningRangeSq;
    @Nonnull
    public final ModifiableFloatAttribute woodcuttingRange;
    @Nonnull
    public final ModifiableFloatAttribute woodcuttingRangeSq;
    @Nonnull
    public final ModifiableFloatAttribute farmingRange;
    @Nonnull
    public final ModifiableFloatAttribute farmingRangeSq;

    @Nonnull
    public final ModifiableFloatAttribute miningSpeed;
    @Nonnull
    public final ModifiableFloatAttributeModifier miningSpeedBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedTypeModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedLevelModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedMainHandModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedOffHandModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedSkillEfficiencyModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedSkillAdrenalineModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedSkillMomentumModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedSkillHastyModifier;
    @Nonnull
    public final FloatAttributeModifier miningSpeedSkillNightOwlModifier;

    @Nonnull
    public final ModifiableFloatAttribute woodcuttingSpeed;
    @Nonnull
    public final ModifiableFloatAttributeModifier woodcuttingSpeedBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedTypeModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedLevelModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedMainHandModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedOffHandModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedSkillEfficiencyModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedSkillAdrenalineModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedSkillMomentumModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedSkillHastyModifier;
    @Nonnull
    public final FloatAttributeModifier woodcuttingSpeedSkillNightOwlModifier;

    @Nonnull
    public final ModifiableFloatAttribute farmingSpeed;
    @Nonnull
    public final ModifiableFloatAttributeModifier farmingSpeedBlocklingModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedTypeModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedLevelModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedMainHandModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedOffHandModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedSkillEfficiencyModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedSkillAdrenalineModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedSkillMomentumModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedSkillHastyModifier;
    @Nonnull
    public final FloatAttributeModifier farmingSpeedSkillNightOwlModifier;

    /**
     * The associated blockling.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The world the associated blockling is in.
     */
    @Nonnull
    public final World world;

    /**
     * @param blockling the associated blockling.
     */
    public BlocklingAttributes(@Nonnull BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.world = blockling.level;

        addAttribute(combatLevel = new IntAttribute("17beee8e-3fca-4601-a766-46f811ad6b69", "combat_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        addAttribute(miningLevel = new IntAttribute("a2a62308-0a6e-41bb-9844-4645eeb72fb7", "mining_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        addAttribute(woodcuttingLevel = new IntAttribute("c6d3ce7c-52af-44df-833b-fede277eec7f", "woodcutting_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        addAttribute(farmingLevel = new IntAttribute("ac1c6d1b-18bb-435a-ad93-c24d6fa90816", "farming_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        totalLevel = new Attribute<Integer>("ecd6a307-2f55-4d9b-96ab-f5efc82f8e5a", "total_level", blockling, true)
        {
            @Override
            public Integer getValue()
            {
                return combatLevel.getValue() + miningLevel.getValue() + woodcuttingLevel.getValue() + farmingLevel.getValue();
            }

            @Override
            protected void setValue(Integer value, boolean sync)
            {
                // Value is always calculated on get
            }
        };

        addAttribute(combatXp = new IntAttribute("ec56a177-2a08-4f43-b77a-b1d4544a8656", "combat_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(combatLevel.getValue())), null, null, true));
        addAttribute(miningXp = new IntAttribute("ce581807-3fad-45b1-9aec-43ed0cb53c8f", "mining_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(miningLevel.getValue())), null, null, true));
        addAttribute(woodcuttingXp = new IntAttribute("82165063-6d47-4534-acc9-db3543c3db74", "woodcutting_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(woodcuttingLevel.getValue())), null, null, true));
        addAttribute(farmingXp = new IntAttribute("1f1e4cbc-358e-4477-92d8-03e818d5272c", "farming_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(farmingLevel.getValue())), null, null, true));

        addAttribute(hand = new EnumAttribute<>("f21fcbaa-f800-468e-8c22-ec4b4fd0fdc2", "hand", blockling, BlocklingHand.class, BlocklingHand.NONE, null, null, true));

        addAttribute(maxHealth = new ModifiableFloatAttribute("9c6eb101-f025-4f8f-895b-10868b7d06b2", "max_health",blockling, 10.0f, null, null, true));
        addModifier(maxHealthBlocklingModifier = new ModifiableFloatAttributeModifier("42418962-175b-4c06-84a2-f770ebd00a88", "max_health_blockling", maxHealth, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(maxHealthTypeModifier  = new FloatAttributeModifier("79043f39-6f44-4077-a358-0f75a0a1e995", "max_health_type", maxHealthBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(maxHealthCombatLevelModifier = new FloatAttributeModifier("a78160fa-7bc3-493e-b74b-27af4206d111", "max_health_combat_level", maxHealthBlocklingModifier, blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));

        addAttribute(mainHandAttackDamage = new ModifiableFloatAttribute("e8549f17-e473-4849-8f48-ae624ee0c242", "main_hand_attack_damage", blockling, 0.0f, null, null, true));
        addModifier(mainHandAttackDamageBlocklingModifier = new ModifiableFloatAttributeModifier("9bfdfe35-c6c4-4364-8535-7aa50927f484", "main_hand_attack_blockling", mainHandAttackDamage, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(mainHandAttackDamageTypeModifier = new FloatAttributeModifier("ddb441fc-2d8c-4950-b0a9-b96b60680ac1", "main_hand_attack_damage_type", mainHandAttackDamageBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(mainHandAttackDamageCombatLevelModifier = new FloatAttributeModifier("406a98f7-df1f-4c7f-93e4-990d71c7747f", "main_hand_attack_damage_combat_level", mainHandAttackDamageBlocklingModifier, blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(mainHandAttackDamageToolModifier = new FloatAttributeModifier("2ae58b89-fed6-4b2f-90bc-e7dbc9d7b249", "main_hand_attack_damage_tool", mainHandAttackDamage, blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString(), true));

        addAttribute(offHandAttackDamage = new ModifiableFloatAttribute("519e98de-c213-4c1c-8a07-cd659bc9982c", "off_hand_attack_damage", blockling, 0.0f, null, null, true));
        addModifier(offHandAttackDamageBlocklingModifier = new ModifiableFloatAttributeModifier("806bd9bf-86de-416c-9e11-7beb7f482f11", "off_hand_attack_blockling", offHandAttackDamage, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(offHandAttackDamageTypeModifier = new FloatAttributeModifier("c4bc950e-65c4-49c5-94e1-08e809788104", "off_hand_attack_damage_type", offHandAttackDamageBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(offHandAttackDamageCombatLevelModifier = new FloatAttributeModifier("8d1c1da2-7a92-4142-8fde-306d21010a92", "off_hand_attack_damage_combat_level", offHandAttackDamageBlocklingModifier, blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(offHandAttackDamageToolModifier = new FloatAttributeModifier("c1aa1629-fe40-47eb-8c5c-a02d0b82e636", "off_hand_attack_damage_tool", offHandAttackDamage, blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString(), true));

        // Default set to 4.0f (seems to be the same for a player too)
        // A sword is 1.6f * 2.0f
        // An axe is 0.8f * 2.0f
        // So any tool use should be slower than fists
        addAttribute(attackSpeed = new AveragedAttribute("4cbc129d-281d-410e-bba0-45d4e064932a", "attack_speed", blockling, 0.0f, null, null, true));
        addModifier(attackSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("8c4e3d41-2a17-4cd1-8dbb-8866008960a5", "attack_speed_blockling", attackSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(attackSpeedTypeModifier = new FloatAttributeModifier("f40d211d-c6fd-449a-a2f8-8bffd24ac810", "attack_speed_type", attackSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(attackSpeedLevelModifier = new FloatAttributeModifier("bfeb22fe-aaaf-4294-9850-27449e27e44f", "attack_speed_level", attackSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(attackSpeedMainHandModifier = new FloatAttributeModifier("87343a1e-7a0b-4963-8d7e-e95f809e90ee", "attack_speed_main_hand", attackSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString(), true));
        addModifier(attackSpeedOffHandModifier = new FloatAttributeModifier("3566961d-db2b-4833-8c0c-cf6813ade8cc", "attack_speed_off_hand", attackSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString(), true));

        addAttribute(armour = new ModifiableFloatAttribute("6b34a986-f1ad-4476-a1c6-700d841fb1ec", "armour", blockling, 0.0f, null, null, true));
        addModifier(armourBlocklingModifier = new ModifiableFloatAttributeModifier("1e5b8f41-bab6-41f9-9a3d-5303f2f1ed6e", "armour_blockling", armour, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(armourTypeModifier = new FloatAttributeModifier("a72fb401-abb7-4d95-ad7e-e83fc6a399d1", "armour_type", armourBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(armourCombatLevelModifier = new FloatAttributeModifier("15f2f2ce-cdf1-4188-882e-67ceab22df41", "armour_combat_level", armourBlocklingModifier, blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));

        addAttribute(armourToughness = new ModifiableFloatAttribute("1cfdad6a-0bd3-461f-8007-c0a591a30783", "armour_toughness", blockling, 0.0f, null, null, true));
        addModifier(armourToughnessBlocklingModifier = new ModifiableFloatAttributeModifier("9f08c15b-fa91-4c1e-97ef-4b465936c5ab", "armour_toughness_blockling", armourToughness, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(armourToughnessTypeModifier = new FloatAttributeModifier("4f806fa7-1ebe-4426-99c6-5c0d0f41be25", "armour_toughness_type", armourToughnessBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(armourToughnessCombatLevelModifier = new FloatAttributeModifier("0f438ab0-2e72-4555-840f-3b3dc2335014", "armour_toughness_combat_level", armourToughnessBlocklingModifier, blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));

        addAttribute(knockbackResistance = new ModifiableFloatAttribute("ddc90fc2-4a68-4c30-8701-d2d9dbe8b94a", "knockback_resistance", blockling, 0.0f, (v) -> String.format("%.0f%%", v * 100.0f), null, true));
        addModifier(knockbackResistanceBlocklingModifier = new ModifiableFloatAttributeModifier("3b6bf894-3beb-42f5-b516-759bacf9acab", "knockback_resistance_blockling", knockbackResistance, blockling, 0.0f, Operation.ADD, (v) -> String.format("%.0f%%", v * 100.0f), () -> blockling.getCustomName().getString(), true));
        addModifier(knockbackResistanceTypeModifier = new FloatAttributeModifier("eb217d5e-6e7d-4ef0-9ca1-153a7bc18593", "knockback_resistance_type", knockbackResistanceBlocklingModifier, blockling, 0.0f, Operation.ADD, (v) -> String.format("%.0f%%", v * 100.0f), () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(knockbackResistanceCombatLevelModifier = new FloatAttributeModifier("711cf234-2f57-413c-be3d-ce4c5f809b86", "knockback_resistance_combat_level", knockbackResistanceBlocklingModifier, blockling, 0.0f, Operation.ADD, (v) -> String.format("%.0f%%", v * 100.0f), combatLevel.displayStringNameSupplier, true));

        addAttribute(moveSpeed = new ModifiableFloatAttribute("9a0bb639-8543-4725-9be1-8a8ce688da70", "move_speed", blockling, 0.0f, null, null, true));
        addModifier(moveSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("f4300b1a-ee93-4d36-a457-4d71c349a4ab", "move_speed_blockling", moveSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(moveSpeedTypeModifier = new FloatAttributeModifier("6f685317-7be6-4ea8-ae63-b1c907209040", "move_speed_type", moveSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));

        addAttribute(miningRange = new ModifiableFloatAttribute("76e044ca-e73e-4004-b576-920a8446612d", "mining_range", blockling, 2.5f, null, null, true));
        addAttribute(miningRangeSq = new ModifiableFloatAttribute("55af3992-cf8d-4d5d-8634-fbc1e05d30fe", "mining_range_sq", blockling, miningRange.getValue() * miningRange.getValue(), null, null, true));
        addAttribute(woodcuttingRange = new ModifiableFloatAttribute("bc50cc2d-2323-4743-a175-5af87e61e04e", "woodcutting_range", blockling, 2.5f, null, null, true));
        addAttribute(woodcuttingRangeSq = new ModifiableFloatAttribute("8ba7fea6-6790-4010-b210-fa69b1effad8", "woodcutting_range_sq", blockling, woodcuttingRange.getValue() * woodcuttingRange.getValue(), null, null, true));
        addAttribute(farmingRange = new ModifiableFloatAttribute("c549a710-62d9-4d79-8d9d-ba3690752d08", "farming_range", blockling, 2.5f, null, null, true));
        addAttribute(farmingRangeSq = new ModifiableFloatAttribute("bc3a8f41-d033-437f-bce2-840df7a55fad", "farming_range_sq", blockling, farmingRange.getValue() * farmingRange.getValue(), null, null, true));

        // Default mining speed for an item/hand is 1.0f
        // A wooden pickaxe is 2.0f
        // A diamond pickaxe is 8.0f
        // Our default mining speed can be 0.0f as it will be determined by the level
        addAttribute(miningSpeed = new ModifiableFloatAttribute("0d918c08-2e94-481b-98e1-c2ff3ae395de", "mining_speed", blockling, 0.0f, null, null, true));
        addModifier(miningSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("1e9d4f59-e3a5-410b-a3dd-c9dce952f22d", "mining_speed_blockling", miningSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(miningSpeedTypeModifier = new FloatAttributeModifier("565dea40-53ac-4861-bc6c-1eafce77f80f", "mining_speed_type", miningSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(miningSpeedLevelModifier = new FloatAttributeModifier("f0914966-d53a-4292-b48c-5595f944f5d2", "mining_speed_level", miningSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, miningLevel.displayStringNameSupplier, true));
        addModifier(miningSpeedMainHandModifier = new FloatAttributeModifier("fc0dd885-273b-4465-a9ff-e801dcaf07e2", "mining_speed_main_hand", miningSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString(), true));
        addModifier(miningSpeedOffHandModifier = new FloatAttributeModifier("aada86a0-4233-47cf-b5ab-aa208a216bb5", "mining_speed_off_hand", miningSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString(), true));
        addModifier(miningSpeedSkillEfficiencyModifier = new FloatAttributeModifier("9464fc16-0f3c-438f-ac0b-8715a3542aaa", "mining_speed_skill_efficiency", miningSpeed, blockling, 1.1f, Operation.MULTIPLY_TOTAL, (v) -> String.format("%.0f%%", (v - 1.0f) * 100.0f), () -> skillDisplayNameProvider(BlocklingSkills.Mining.EFFICIENCY), false));
        addModifier(miningSpeedSkillAdrenalineModifier = new FloatAttributeModifier("1543fadc-3a9e-412b-819b-a6379a0911ca", "mining_speed_skill_adrenaline", miningSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.ADRENALINE), false));
        addModifier(miningSpeedSkillMomentumModifier = new FloatAttributeModifier("1ca4d69f-05b8-4598-97c5-95f6bc750b7a", "mining_speed_skill_momentum", miningSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.MOMENTUM), false));
        addModifier(miningSpeedSkillHastyModifier = new FloatAttributeModifier("035c4e96-a628-4b20-9699-28ade0fa5a80", "mining_speed_skill_hasty", miningSpeed, blockling, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.HASTY), false));
        addModifier(miningSpeedSkillNightOwlModifier = new FloatAttributeModifier("f858c34f-d215-450a-847d-a54525d2f82f", "mining_speed_skill_night_owl", miningSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.NIGHT_OWL), false));

        addAttribute(woodcuttingSpeed = new ModifiableFloatAttribute("e1e3ecb3-ae1d-46c5-8ea8-a7180641910b", "woodcutting_speed", blockling, 0.0f, null, null, true));
        addModifier(woodcuttingSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("51a21884-8c41-49d8-bae4-f21866b58718", "woodcutting_speed_blockling", woodcuttingSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(woodcuttingSpeedTypeModifier = new FloatAttributeModifier("a66110e2-3a7f-4907-b85c-05c65341cd2e", "woodcutting_speed_type", woodcuttingSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(woodcuttingSpeedLevelModifier = new FloatAttributeModifier("6b71ee16-7d04-442e-9d49-9373833f5539", "woodcutting_speed_level", woodcuttingSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, woodcuttingLevel.displayStringNameSupplier, true));
        addModifier(woodcuttingSpeedMainHandModifier = new FloatAttributeModifier("978f9dd4-3fbb-41ee-9bba-eddcfb42b6ff", "woodcutting_speed_main_hand", woodcuttingSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString(), true));
        addModifier(woodcuttingSpeedOffHandModifier = new FloatAttributeModifier("80fd0028-a793-491c-bc9c-fe94071f91c7", "woodcutting_speed_off_hand", woodcuttingSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString(), true));
        addModifier(woodcuttingSpeedSkillEfficiencyModifier = new FloatAttributeModifier("38a9d80c-4f96-4929-bd34-8c03156dec6d", "woodcutting_speed_skill_efficiency", woodcuttingSpeed, blockling, 1.1f, Operation.MULTIPLY_TOTAL, (v) -> String.format("%.0f%%", (v - 1.0f) * 100.0f), () -> skillDisplayNameProvider(BlocklingSkills.Woodcutting.EFFICIENCY), false));
        addModifier(woodcuttingSpeedSkillAdrenalineModifier = new FloatAttributeModifier("e832c5da-0465-4a63-96b7-398eb32ba206", "woodcutting_speed_skill_adrenaline", woodcuttingSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Woodcutting.ADRENALINE), false));
        addModifier(woodcuttingSpeedSkillMomentumModifier = new FloatAttributeModifier("868bba90-a29c-4b70-b599-c3f56b00928e", "woodcutting_speed_skill_momentum", woodcuttingSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Woodcutting.MOMENTUM), false));
        addModifier(woodcuttingSpeedSkillHastyModifier = new FloatAttributeModifier("38709558-3da6-4f86-896f-195e84c18525", "woodcutting_speed_skill_hasty", woodcuttingSpeed, blockling, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Woodcutting.HASTY), false));
        addModifier(woodcuttingSpeedSkillNightOwlModifier = new FloatAttributeModifier("adbe5ab0-4a08-4335-a68a-964b9126c40f", "woodcutting_speed_skill_night_owl", woodcuttingSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Woodcutting.NIGHT_OWL), false));

        addAttribute(farmingSpeed = new ModifiableFloatAttribute("f6c026b6-1fa9-432f-aca3-d97af784f6d0", "farming_speed", blockling, 0.0f, null, null, true));
        addModifier(farmingSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("39548773-84ee-42ca-8ad6-681d64eaee54", "farming_speed_blockling", farmingSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true));
        addModifier(farmingSpeedTypeModifier = new FloatAttributeModifier("f99bbc83-30fc-451f-a53b-fa343cc9244a", "farming_speed_type", farmingSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString(), true));
        addModifier(farmingSpeedLevelModifier = new FloatAttributeModifier("3b3079cf-8640-436b-bf0a-3aae4deb29be", "farming_speed_level", farmingSpeedBlocklingModifier, blockling, 0.0f, Operation.ADD, null, farmingLevel.displayStringNameSupplier, true));
        addModifier(farmingSpeedMainHandModifier = new FloatAttributeModifier("5ece4240-17b3-4983-bd0d-67f962a0a838", "farming_speed_main_hand", farmingSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString(), true));
        addModifier(farmingSpeedOffHandModifier = new FloatAttributeModifier("b4bb7131-f2ce-41cd-88ed-ee27e3837679", "farming_speed_off_hand", farmingSpeed, blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString(), true));
        addModifier(farmingSpeedSkillEfficiencyModifier = new FloatAttributeModifier("792be316-19cb-49ec-a24a-ee224312c60f", "farming_speed_skill_efficiency", farmingSpeed, blockling, 1.1f, Operation.MULTIPLY_TOTAL, (v) -> String.format("%.0f%%", (v - 1.0f) * 100.0f), () -> skillDisplayNameProvider(BlocklingSkills.Farming.EFFICIENCY), false));
        addModifier(farmingSpeedSkillAdrenalineModifier = new FloatAttributeModifier("79f7d5ff-2fd7-4385-a795-c281f984445b", "farming_speed_skill_adrenaline", farmingSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Farming.ADRENALINE), false));
        addModifier(farmingSpeedSkillMomentumModifier = new FloatAttributeModifier("d58d5336-d2fe-447d-a237-bb0bbff313d7", "farming_speed_skill_momentum", farmingSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Farming.MOMENTUM), false));
        addModifier(farmingSpeedSkillHastyModifier = new FloatAttributeModifier("3f045e39-8185-4d2c-8980-54e06f8d548b", "farming_speed_skill_hasty", farmingSpeed, blockling, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Farming.HASTY), false));
        addModifier(farmingSpeedSkillNightOwlModifier = new FloatAttributeModifier("451f37a9-649e-44a3-a21f-d76e49f1afd0", "farming_speed_skill_night_owl", farmingSpeed, blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Farming.NIGHT_OWL), false));
    }

    /**
     * Used to produce a display name for an attribute relating to a skill.
     *
     * @param skillInfo the relate skill.
     * @return the display string.
     */
    @Nonnull
    private String skillDisplayNameProvider(@Nonnull SkillInfo skillInfo)
    {
        return skillInfo.general.name.getString() + " ("+ new BlocklingsTranslationTextComponent("skill.name").getString() +")";
    }

    /**
     * Adds the given attribute to the attributes list.
     *
     * @param attribute the attribute to add.
     */
    public void addAttribute(@Nonnull Attribute<?> attribute)
    {
        attributes.add(attribute);
    }

    /**
     * Adds the given modifier to the attributes and modifiers lists.
     *
     * @param modifier the modifier to add.
     */
    public <V, T extends Attribute<V> & IModifier<V>> void addModifier(@Nonnull T modifier)
    {
        attributes.add(modifier);
        modifiers.add(modifier);
    }

    /**
     * Initialised the attributes by calling any calculate methods and update callbacks.
     */
    public void init()
    {
        for (Attribute<?> attribute : attributes)
        {
            if (attribute instanceof IModifiable<?>)
            {
                ((ModifiableAttribute<?>) attribute).calculate();
            }
        }

        for (Attribute<?> attribute : attributes)
        {
            attribute.callUpdateCallbacks();
        }

        updateCombatLevelBonuses(false);
        updateTypeBonuses(false);
    }

    /**
     * Initialises any update callbacks for attributes.
     */
    public void initUpdateCallbacks()
    {
        combatLevel.addUpdateCallback((i) -> { updateCombatLevelBonuses(false); });
        attackSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ATTACK_SPEED)).setBaseValue(f));
        miningLevel.addUpdateCallback((i) -> { miningSpeedLevelModifier.setValue(calcBlockBreakSpeedFromLevel(i), false); });
        woodcuttingLevel.addUpdateCallback((i) -> { woodcuttingSpeedLevelModifier.setValue(calcBlockBreakSpeedFromLevel(i), false); });
        farmingLevel.addUpdateCallback((i) -> { farmingSpeedLevelModifier.setValue(calcBlockBreakSpeedFromLevel(i), false); });
        combatXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        miningXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        woodcuttingXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        farmingXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        miningRange.addUpdateCallback((f) -> miningRangeSq.setBaseValue(miningRange.getValue() * miningRange.getValue(), false));
        woodcuttingRange.addUpdateCallback((f) -> woodcuttingRangeSq.setBaseValue(woodcuttingRange.getValue() * woodcuttingRange.getValue(), false));
        farmingRange.addUpdateCallback((f) -> farmingRangeSq.setBaseValue(farmingRange.getValue() * farmingRange.getValue(), false));
        maxHealth.addUpdateCallback((f) -> { Objects.requireNonNull(blockling.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(f); checkAndCapHealth(); });
        mainHandAttackDamage.addUpdateCallback((f) -> setVanillaAttackDamageAttribute());
        offHandAttackDamage.addUpdateCallback((f) -> setVanillaAttackDamageAttribute());
        armour.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR)).setBaseValue(f));
        armourToughness.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR_TOUGHNESS)).setBaseValue(f));
        knockbackResistance.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(f));
        moveSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(f / 10.0f));
    }

    /**
     * Writes all the attributes' information to an attributes tag and adds it to the given tag.
     *
     * @param tag the tag to add the attributes tag to.
     */
    public void writeToNBT(@Nonnull CompoundNBT tag)
    {
        CompoundNBT attributesNBT = new CompoundNBT();

        for (Attribute<?> attribute : attributes)
        {
            CompoundNBT attributeTag = new CompoundNBT();

            attribute.writeToNBT(attributeTag);

            attributesNBT.put(attribute.id.toString(), attributeTag);
        }

        tag.put("attributes", attributesNBT);
    }

    /**
     * Reads all the attributes' information from the given tag.
     *
     * @param tag the tag to read the attributes' information from.
     */
    public void readFromNBT(@Nonnull CompoundNBT tag)
    {
        CompoundNBT attributesNBT = (CompoundNBT) tag.get("attributes");

        for (Attribute<?> attribute : attributes)
        {
            CompoundNBT attributeTag = (CompoundNBT) attributesNBT.get(attribute.id.toString());

            if (attributeTag != null)
            {
                attribute.readFromNBT(attributeTag);
            }
        }

        init();
    }

    /**
     * Writes all the attributes to the given buffer.
     *
     * @param buf the buffer to write to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        for (Attribute<?> attribute : attributes)
        {
            attribute.encode(buf);
        }
    }

    /**
     * Reads all the attributes from the given buffer.
     *
     * @param buf the buffer to read from.
     */
    public void decode(@Nonnull PacketBuffer buf)
    {
        for (Attribute<?> attribute : attributes)
        {
            attribute.decode(buf);
        }

        init();
    }

    /**
     * @param level the level to enquire about.
     * @return the xp needed to reach the next level.
     */
    public static int getXpForLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    /**
     * Sets the matching vanilla attributes in case any vanilla code needs to use them.
     */
    private void setVanillaAttackDamageAttribute()
    {
        if (blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN))
        {
            blockling.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(mainHandAttackDamage.getValue());
        }
        else if (blockling.getEquipment().isAttackingWith(BlocklingHand.OFF))
        {
            blockling.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(offHandAttackDamage.getValue());
        }
        else if (blockling.getEquipment().isAttackingWith(BlocklingHand.BOTH))
        {
            blockling.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(mainHandAttackDamage.getValue() + offHandAttackDamage.getValue());
        }
        else
        {
            blockling.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.0f);
        }
    }

    /**
     * Checks each level to see if the target xp has been reached and updates the state accordingly.
     *
     * @param sync whether to sync any resulting changes to the client/server.
     */
    public void checkForLevelUpAndUpdate(boolean sync)
    {
        if (combatLevel.getValue() < Level.MAX)
        {
            int combatLevel = this.combatLevel.getValue();
            int combatXp = this.combatXp.getValue();
            int combatXpReq = getXpForLevel(combatLevel);
            if (combatXp >= combatXpReq)
            {
                this.combatLevel.setValue(combatLevel + 1, sync);
                this.combatXp.setValue(combatXp - combatXpReq, sync);
            }
        }

        if (miningLevel.getValue() < Level.MAX)
        {
            int miningLevel = this.miningLevel.getValue();
            int miningXp = this.miningXp.getValue();
            int miningXpReq = getXpForLevel(miningLevel);
            if (miningXp >= miningXpReq)
            {
                this.miningLevel.setValue(miningLevel + 1, sync);
                this.miningXp.setValue(miningXp - miningXpReq, sync);
            }
        }

        if (woodcuttingLevel.getValue() < Level.MAX)
        {
            int woodcuttingLevel = this.woodcuttingLevel.getValue();
            int woodcuttingXp = this.woodcuttingXp.getValue();
            int woodcuttingXpReq = getXpForLevel(woodcuttingLevel);
            if (woodcuttingXp >= woodcuttingXpReq)
            {
                this.woodcuttingLevel.setValue(woodcuttingLevel + 1, sync);
                this.woodcuttingXp.setValue(woodcuttingXp - woodcuttingXpReq, sync);
            }
        }

        if (farmingLevel.getValue() < Level.MAX)
        {
            int farmingLevel = this.farmingLevel.getValue();
            int farmingXp = this.farmingXp.getValue();
            int farmingXpReq = getXpForLevel(farmingLevel);
            if (farmingXp >= farmingXpReq)
            {
                this.farmingLevel.setValue(farmingLevel + 1, sync);
                this.farmingXp.setValue(farmingXp - farmingXpReq, sync);
            }
        }
    }

    /**
     * Updates the bonuses provided by the combat level.
     *
     * @param sync whether to sync the changes to the client/server.
     */
    public void updateCombatLevelBonuses(boolean sync)
    {
        maxHealthCombatLevelModifier.setValue(calcBonusHealthFromCombatLevel(), sync);
        mainHandAttackDamageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel(), sync);
        offHandAttackDamageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel(), sync);
        attackSpeedLevelModifier.setValue(calcBlockBreakSpeedFromLevel(combatLevel.getValue()), sync);
        armourCombatLevelModifier.setValue(calcBonusArmourFromCombatLevel(), sync);
        armourToughnessCombatLevelModifier.setValue(calcBonusArmourToughnessFromCombatLevel(), sync);
        knockbackResistanceCombatLevelModifier.setValue(calcBonusKnockbackResistanceFromCombatLevel(), sync);
    }

    /**
     * Updates the bonuses provided by the blockling type.
     *
     * @param sync whether to sync the changes to the client/server.
     */
    public void updateTypeBonuses(boolean sync)
    {
        BlocklingType type = blockling.getBlocklingType();
        maxHealthTypeModifier.setValue(type.getMaxHealth(), sync);
        mainHandAttackDamageTypeModifier.setValue(type.getAttackDamage(), sync);
        offHandAttackDamageTypeModifier.setValue(type.getAttackDamage(), sync);
        attackSpeedTypeModifier.setValue(type.getAttackSpeed(), sync);
        armourTypeModifier.setValue(type.getArmour(), sync);
        armourToughnessTypeModifier.setValue(type.getArmourToughness(), sync);
        knockbackResistanceTypeModifier.setValue(type.getKnockbackResistance(), sync);
        moveSpeedTypeModifier.setValue(type.getMoveSpeed(), sync);
        miningSpeedTypeModifier.setValue(type.getMiningSpeed(), sync);
        woodcuttingSpeedTypeModifier.setValue(type.getWoodcuttingSpeed(), sync);
        farmingSpeedTypeModifier.setValue(type.getFarmingSpeed(), sync);
    }

    /**
     * @param level the level to enquire about.
     * @return the block break speed for that level.
     */
    private float calcBlockBreakSpeedFromLevel(int level)
    {
        return (float) (10.0f * Math.tan((level / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus health provided by the current combat level.
     */
    private float calcBonusHealthFromCombatLevel()
    {
        return (float) (50.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus damage provided by the current combat level.
     */
    private float calcBonusDamageFromCombatLevel()
    {
        return (float) (20.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus armour provided by the current combat level.
     */
    private float calcBonusArmourFromCombatLevel()
    {
        return (float) (10.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus armour toughness provided by the current combat level.
     */
    private float calcBonusArmourToughnessFromCombatLevel()
    {
        return (float) (5.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus knockback resistance provided by the current combat level.
     */
    private float calcBonusKnockbackResistanceFromCombatLevel()
    {
        return (float) (0.5f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * Ensures the blockling's health doesn't exceed its max health.
     * Can happen the max health is lowered below the current health.
     */
    public void checkAndCapHealth()
    {
        if (blockling.getHealth() > blockling.getMaxHealth())
        {
            blockling.setHealth(blockling.getMaxHealth());
        }
    }

    /**
     * @return the blockling's health as an integer rounded up.
     */
    public int getHealth()
    {
        return (int) Math.ceil(blockling.getHealth());
    }

    /**
     * @return the blockling's max health as an integer rounded up.
     */
    public int getMaxHealth()
    {
        return (int) Math.ceil(blockling.getMaxHealth());
    }

    /**
     * @return the blockling's health as a percentage of its max health.
     */
    public float getHealthPercentage()
    {
        return blockling.getHealth() / blockling.getMaxHealth();
    }

    /**
     * @param level the level to enquire about.
     * @return the corresponding attribute for that level.
     */
    @Nonnull
    public Attribute<Integer> getLevelAttribute(@Nonnull Level level)
    {
        switch (level)
        {
            case COMBAT: return combatLevel;
            case MINING: return miningLevel;
            case WOODCUTTING: return woodcuttingLevel;
            case FARMING: return farmingLevel;
            default: return totalLevel;
        }
    }

    /**
     * @param level the level to enquire about.
     * @return the corresponding xp attribute for that level.
     */
    @Nonnull
    public Attribute<Integer> getLevelXpAttribute(@Nonnull Level level)
    {
        switch (level)
        {
            case COMBAT: return combatXp;
            case MINING: return miningXp;
            case WOODCUTTING: return woodcuttingXp;
            case FARMING: return farmingXp;
            default: return totalLevel;
        }
    }

    /**
     * An enum used to identify each level.
     */
    public enum Level
    {
        COMBAT, MINING, WOODCUTTING, FARMING, TOTAL;

        public static int MAX = 100;
    }
}
