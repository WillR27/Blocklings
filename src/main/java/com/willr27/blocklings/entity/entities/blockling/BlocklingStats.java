package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.attribute.*;
import com.willr27.blocklings.attribute.attributes.AveragedAttribute;
import com.willr27.blocklings.attribute.attributes.EnumAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.*;
import com.willr27.blocklings.skills.BlocklingSkills;
import com.willr27.blocklings.skills.info.SkillInfo;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlocklingStats
{
    public final List<Attribute<?>> attributes = new ArrayList<>();
    public final List<IModifier<?>> modifiers = new ArrayList<>();

    public final IntAttribute combatLevel;
    public final IntAttribute miningLevel;
    public final IntAttribute woodcuttingLevel;
    public final IntAttribute farmingLevel;
    public final Attribute<Integer> totalLevel;

    public final IntAttribute combatXp;
    public final IntAttribute miningXp;
    public final IntAttribute woodcuttingXp;
    public final IntAttribute farmingXp;

    public final EnumAttribute<BlocklingHand> hand;

    public final ModifiableFloatAttribute maxHealth;
    public final FloatAttributeModifier maxHealthCombatLevelModifier;
    public final FloatAttributeModifier maxHealthTypeModifier;

    public final ModifiableFloatAttribute mainHandAttackDamage;
    public final ModifiableFloatAttributeModifier mainHandAttackDamageBlocklingModifier;
    public final FloatAttributeModifier mainHandAttackDamageTypeModifier;
    public final FloatAttributeModifier mainHandAttackDamageCombatLevelModifier;
    public final FloatAttributeModifier mainHandAttackDamageToolModifier;

    public final ModifiableFloatAttribute offHandAttackDamage;
    public final ModifiableFloatAttributeModifier offHandAttackDamageBlocklingModifier;
    public final FloatAttributeModifier offHandAttackDamageTypeModifier;
    public final FloatAttributeModifier offHandAttackDamageCombatLevelModifier;
    public final FloatAttributeModifier offHandAttackDamageToolModifier;

    public final AveragedAttribute attackSpeed;
    public final ModifiableFloatAttributeModifier attackSpeedBlocklingModifier;
    public final FloatAttributeModifier attackSpeedTypeModifier;
    public final FloatAttributeModifier attackSpeedLevelModifier;
    public final FloatAttributeModifier attackSpeedMainHandModifier;
    public final FloatAttributeModifier attackSpeedOffHandModifier;

    public final ModifiableFloatAttribute armour;
    public final ModifiableFloatAttributeModifier armourBlocklingModifier;
    public final FloatAttributeModifier armourCombatLevelModifier;
    public final FloatAttributeModifier armourTypeModifier;

    public final ModifiableFloatAttribute armourToughness;
    public final ModifiableFloatAttributeModifier armourToughnessBlocklingModifier;
    public final FloatAttributeModifier armourToughnessCombatLevelModifier;
    public final FloatAttributeModifier armourToughnessTypeModifier;

    public final ModifiableFloatAttribute knockbackResistance;
    public final ModifiableFloatAttributeModifier knockbackResistanceBlocklingModifier;
    public final FloatAttributeModifier knockbackResistanceCombatLevelModifier;
    public final FloatAttributeModifier knockbackResistanceTypeModifier;

    public final ModifiableFloatAttribute moveSpeed;
    public final ModifiableFloatAttributeModifier moveSpeedBlocklingModifier;
    public final FloatAttributeModifier moveSpeedTypeModifier;

    public final ModifiableFloatAttribute miningRange;
    public final ModifiableFloatAttribute miningRangeSq;
    public final ModifiableFloatAttribute woodcuttingRange;
    public final ModifiableFloatAttribute woodcuttingRangeSq;
    public final ModifiableFloatAttribute farmingRange;
    public final ModifiableFloatAttribute farmingRangeSq;

    public final ModifiableFloatAttribute miningSpeed;
    public final ModifiableFloatAttributeModifier miningSpeedBlocklingModifier;
    public final FloatAttributeModifier miningSpeedTypeModifier;
    public final FloatAttributeModifier miningSpeedLevelModifier;
    public final FloatAttributeModifier miningSpeedMainHandModifier;
    public final FloatAttributeModifier miningSpeedOffHandModifier;
    public final FloatAttributeModifier miningSpeedSkillEfficiencyModifier;
    public final FloatAttributeModifier miningSpeedSkillAdrenalineModifier;
    public final FloatAttributeModifier miningSpeedSkillMomentumModifier;
    public final FloatAttributeModifier miningSpeedSkillHastyModifier;
    public final FloatAttributeModifier miningSpeedSkillNightOwlModifier;

    public final ModifiableFloatAttribute woodcuttingSpeed;
    public final ModifiableFloatAttributeModifier woodcuttingSpeedBlocklingModifier;
    public final FloatAttributeModifier woodcuttingSpeedTypeModifier;
    public final FloatAttributeModifier woodcuttingSpeedLevelModifier;
    public final FloatAttributeModifier woodcuttingSpeedMainHandModifier;
    public final FloatAttributeModifier woodcuttingSpeedOffHandModifier;
    public final FloatAttributeModifier woodcuttingSpeedSkillEfficiencyModifier;

    public final ModifiableFloatAttribute farmingSpeed;
    public final ModifiableFloatAttributeModifier farmingSpeedBlocklingModifier;
    public final FloatAttributeModifier farmingSpeedTypeModifier;
    public final FloatAttributeModifier farmingSpeedLevelModifier;
    public final FloatAttributeModifier farmingSpeedMainHandModifier;
    public final FloatAttributeModifier farmingSpeedOffHandModifier;
    public final FloatAttributeModifier farmingSpeedSkillEfficiencyModifier;

    public final BlocklingEntity blockling;
    public final World world;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.world = blockling.level;

        combatLevel = createIntAttribute("17beee8e-3fca-4601-a766-46f811ad6b69", "combat_level", blockling.getRandom().nextInt(5) + 1, null, null);
        miningLevel = createIntAttribute("a2a62308-0a6e-41bb-9844-4645eeb72fb7", "mining_level", blockling.getRandom().nextInt(5) + 1, null, null);
        woodcuttingLevel = createIntAttribute("c6d3ce7c-52af-44df-833b-fede277eec7f", "woodcutting_level", blockling.getRandom().nextInt(5) + 1, null, null);
        farmingLevel = createIntAttribute("ac1c6d1b-18bb-435a-ad93-c24d6fa90816", "farming_level", blockling.getRandom().nextInt(5) + 1, null, null);
        totalLevel = new Attribute<Integer>("ecd6a307-2f55-4d9b-96ab-f5efc82f8e5a", "total_level", blockling)
        {
            @Override
            public Integer getValue()
            {
                return combatLevel.getValue() + miningLevel.getValue() + woodcuttingLevel.getValue() + farmingLevel.getValue();
            }
        };

        combatXp = createIntAttribute("ec56a177-2a08-4f43-b77a-b1d4544a8656", "combat_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(combatLevel.getValue())), null, null);
        miningXp = createIntAttribute("ce581807-3fad-45b1-9aec-43ed0cb53c8f", "mining_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(miningLevel.getValue())), null, null);
        woodcuttingXp = createIntAttribute("82165063-6d47-4534-acc9-db3543c3db74", "woodcutting_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(woodcuttingLevel.getValue())), null, null);
        farmingXp = createIntAttribute("1f1e4cbc-358e-4477-92d8-03e818d5272c", "farming_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(farmingLevel.getValue())), null, null);

        hand = createEnumAttribute("f21fcbaa-f800-468e-8c22-ec4b4fd0fdc2", "hand", BlocklingHand.NONE, (i) -> BlocklingHand.values()[i], null, null);

        maxHealth = createModifiableFloatAttribute("9c6eb101-f025-4f8f-895b-10868b7d06b2", "max_health",10.0f, null, null);
        maxHealthCombatLevelModifier = createFloatAttributeModifier("a78160fa-7bc3-493e-b74b-27af4206d111", "max_health_combat_level", maxHealth, 0.0f, Operation.ADD, null, null);
        maxHealthTypeModifier  = createFloatAttributeModifier("79043f39-6f44-4077-a358-0f75a0a1e995", "max_health_type", maxHealth, 0.0f, Operation.ADD, null, null);

        mainHandAttackDamage = createModifiableFloatAttribute("e8549f17-e473-4849-8f48-ae624ee0c242", "main_hand_attack_damage", 0.0f, null, null);
        mainHandAttackDamageBlocklingModifier = createModifiableFloatAttributeModifier("9bfdfe35-c6c4-4364-8535-7aa50927f484", "main_hand_attack_blockling", mainHandAttackDamage, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        mainHandAttackDamageTypeModifier = createFloatAttributeModifier("ddb441fc-2d8c-4950-b0a9-b96b60680ac1", "main_hand_attack_damage_type", mainHandAttackDamageBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        mainHandAttackDamageCombatLevelModifier = createFloatAttributeModifier("406a98f7-df1f-4c7f-93e4-990d71c7747f", "main_hand_attack_damage_combat_level", mainHandAttackDamageBlocklingModifier, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier);
        mainHandAttackDamageToolModifier = createFloatAttributeModifier("2ae58b89-fed6-4b2f-90bc-e7dbc9d7b249", "main_hand_attack_damage_tool", mainHandAttackDamage, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString());

        offHandAttackDamage = createModifiableFloatAttribute("519e98de-c213-4c1c-8a07-cd659bc9982c", "off_hand_attack_damage", 0.0f, null, null);
        offHandAttackDamageBlocklingModifier = createModifiableFloatAttributeModifier("806bd9bf-86de-416c-9e11-7beb7f482f11", "off_hand_attack_blockling", offHandAttackDamage, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        offHandAttackDamageTypeModifier = createFloatAttributeModifier("c4bc950e-65c4-49c5-94e1-08e809788104", "off_hand_attack_damage_type", offHandAttackDamageBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        offHandAttackDamageCombatLevelModifier = createFloatAttributeModifier("8d1c1da2-7a92-4142-8fde-306d21010a92", "off_hand_attack_damage_combat_level", offHandAttackDamageBlocklingModifier, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier);
        offHandAttackDamageToolModifier = createFloatAttributeModifier("c1aa1629-fe40-47eb-8c5c-a02d0b82e636", "off_hand_attack_damage_tool", offHandAttackDamage, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString());

        // Default set to 4.0f (seems to be the same for a player too)
        // A sword is 1.6f * 2.0f
        // An axe is 0.8f * 2.0f
        // So any tool use should be slower than fists
        attackSpeed = createAveragedAttribute("4cbc129d-281d-410e-bba0-45d4e064932a", "attack_speed", 0.0f, null, null);
        attackSpeedBlocklingModifier = createModifiableFloatAttributeModifier("8c4e3d41-2a17-4cd1-8dbb-8866008960a5", "attack_speed_blockling", attackSpeed, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        attackSpeedTypeModifier = createFloatAttributeModifier("f40d211d-c6fd-449a-a2f8-8bffd24ac810", "attack_speed_type", attackSpeedBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        attackSpeedLevelModifier = createFloatAttributeModifier("bfeb22fe-aaaf-4294-9850-27449e27e44f", "attack_speed_level", attackSpeedBlocklingModifier, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier);
        attackSpeedMainHandModifier = createFloatAttributeModifier("87343a1e-7a0b-4963-8d7e-e95f809e90ee", "attack_speed_main_hand", attackSpeed, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString());
        attackSpeedOffHandModifier = createFloatAttributeModifier("3566961d-db2b-4833-8c0c-cf6813ade8cc", "attack_speed_off_hand", attackSpeed, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString());

        armour = createModifiableFloatAttribute("6b34a986-f1ad-4476-a1c6-700d841fb1ec", "armour", 2.0f, null, null);
        armourBlocklingModifier = createModifiableFloatAttributeModifier("1e5b8f41-bab6-41f9-9a3d-5303f2f1ed6e", "armour_blockling", armour, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        armourTypeModifier = createFloatAttributeModifier("a72fb401-abb7-4d95-ad7e-e83fc6a399d1", "armour_type", armourBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        armourCombatLevelModifier = createFloatAttributeModifier("15f2f2ce-cdf1-4188-882e-67ceab22df41", "armour_combat_level", armourBlocklingModifier, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier);

        armourToughness = createModifiableFloatAttribute("1cfdad6a-0bd3-461f-8007-c0a591a30783", "armour_toughness", 0.0f, null, null);
        armourToughnessBlocklingModifier = createModifiableFloatAttributeModifier("9f08c15b-fa91-4c1e-97ef-4b465936c5ab", "armour_toughness_blockling", armourToughness, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        armourToughnessTypeModifier = createFloatAttributeModifier("4f806fa7-1ebe-4426-99c6-5c0d0f41be25", "armour_toughness_type", armourToughnessBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        armourToughnessCombatLevelModifier = createFloatAttributeModifier("0f438ab0-2e72-4555-840f-3b3dc2335014", "armour_toughness_combat_level", armourToughnessBlocklingModifier, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier);

        knockbackResistance = createModifiableFloatAttribute("ddc90fc2-4a68-4c30-8701-d2d9dbe8b94a", "knockback_resistance", 0.0f, this::knockbackResistanceDisplayStringValueProvider, null);
        knockbackResistanceBlocklingModifier = createModifiableFloatAttributeModifier("3b6bf894-3beb-42f5-b516-759bacf9acab", "knockback_resistance_blockling", knockbackResistance, 0.0f, Operation.ADD, this::knockbackResistanceBlocklingModifierDisplayStringValueProvider, () -> blockling.getCustomName().getString());
        knockbackResistanceTypeModifier = createFloatAttributeModifier("eb217d5e-6e7d-4ef0-9ca1-153a7bc18593", "knockback_resistance_type", knockbackResistanceBlocklingModifier, 0.0f, Operation.ADD, this::knockbackResistanceTypeModifierDisplayStringValueProvider, () -> blockling.getBlocklingType().name.getString());
        knockbackResistanceCombatLevelModifier = createFloatAttributeModifier("711cf234-2f57-413c-be3d-ce4c5f809b86", "knockback_resistance_combat_level", knockbackResistanceBlocklingModifier, 0.0f, Operation.ADD, this::knockbackResistanceCombatLevelModifierDisplayStringValueProvider, combatLevel.displayStringNameSupplier);

        moveSpeed = createModifiableFloatAttribute("9a0bb639-8543-4725-9be1-8a8ce688da70", "move_speed", 0.0f, null, null);
        moveSpeedBlocklingModifier = createModifiableFloatAttributeModifier("f4300b1a-ee93-4d36-a457-4d71c349a4ab", "move_speed_blockling", moveSpeed, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        moveSpeedTypeModifier = createFloatAttributeModifier("6f685317-7be6-4ea8-ae63-b1c907209040", "move_speed_type", moveSpeedBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());

        miningRange = createModifiableFloatAttribute("76e044ca-e73e-4004-b576-920a8446612d", "mining_range", 2.5f, null, null);
        miningRangeSq = createModifiableFloatAttribute("55af3992-cf8d-4d5d-8634-fbc1e05d30fe", "mining_range_sq", miningRange.getValue() * miningRange.getValue(), null, null);
        woodcuttingRange = createModifiableFloatAttribute("bc50cc2d-2323-4743-a175-5af87e61e04e", "woodcutting_range", 2.5f, null, null);
        woodcuttingRangeSq = createModifiableFloatAttribute("8ba7fea6-6790-4010-b210-fa69b1effad8", "woodcutting_range_sq", woodcuttingRange.getValue() * woodcuttingRange.getValue(), null, null);
        farmingRange = createModifiableFloatAttribute("c549a710-62d9-4d79-8d9d-ba3690752d08", "farming_range", 2.5f, null, null);
        farmingRangeSq = createModifiableFloatAttribute("bc3a8f41-d033-437f-bce2-840df7a55fad", "farming_range_sq", farmingRange.getValue() * farmingRange.getValue(), null, null);

        // Default mining speed for an item/hand is 1.0f
        // A wooden pickaxe is 2.0f
        // A diamond pickaxe is 8.0f
        // Our default mining speed can be 0.0f as it will be determined by the level
        miningSpeed = createModifiableFloatAttribute("0d918c08-2e94-481b-98e1-c2ff3ae395de", "mining_speed", 0.0f, null, null);
        miningSpeedBlocklingModifier = createModifiableFloatAttributeModifier("1e9d4f59-e3a5-410b-a3dd-c9dce952f22d", "mining_speed_blockling", miningSpeed, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        miningSpeedTypeModifier = createFloatAttributeModifier("565dea40-53ac-4861-bc6c-1eafce77f80f", "mining_speed_type", miningSpeedBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        miningSpeedLevelModifier = createFloatAttributeModifier("f0914966-d53a-4292-b48c-5595f944f5d2", "mining_speed_level", miningSpeedBlocklingModifier, 0.0f, Operation.ADD, null, miningLevel.displayStringNameSupplier);
        miningSpeedMainHandModifier = createFloatAttributeModifier("fc0dd885-273b-4465-a9ff-e801dcaf07e2", "mining_speed_main_hand", miningSpeed, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString());
        miningSpeedOffHandModifier = createFloatAttributeModifier("aada86a0-4233-47cf-b5ab-aa208a216bb5", "mining_speed_off_hand", miningSpeed, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString());
        miningSpeedSkillEfficiencyModifier = createFloatAttributeModifier("9464fc16-0f3c-438f-ac0b-8715a3542aaa", "mining_speed_skill_efficiency", miningSpeed, 1.1f, Operation.MULTIPLY_TOTAL, this::miningSpeedSkillEfficiencymodifierDisplayStringValueProvider, () -> skillDisplayNameProvider(BlocklingSkills.Mining.EFFICIENCY), false);
        miningSpeedSkillAdrenalineModifier = createFloatAttributeModifier("1543fadc-3a9e-412b-819b-a6379a0911ca", "mining_speed_skill_adrenaline", miningSpeed, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.ADRENALINE), false);
        miningSpeedSkillMomentumModifier = createFloatAttributeModifier("1ca4d69f-05b8-4598-97c5-95f6bc750b7a", "mining_speed_skill_momentum", miningSpeed, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.MOMENTUM), false);
        miningSpeedSkillHastyModifier = createFloatAttributeModifier("035c4e96-a628-4b20-9699-28ade0fa5a80", "mining_speed_skill_hasty", miningSpeed, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.HASTY), false);
        miningSpeedSkillNightOwlModifier = createFloatAttributeModifier("f858c34f-d215-450a-847d-a54525d2f82f", "mining_speed_skill_night_owl", miningSpeed, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(BlocklingSkills.Mining.NIGHT_OWL), false);

        woodcuttingSpeed = createModifiableFloatAttribute("e1e3ecb3-ae1d-46c5-8ea8-a7180641910b", "woodcutting_speed", 0.0f, null, null);
        woodcuttingSpeedBlocklingModifier = createModifiableFloatAttributeModifier("51a21884-8c41-49d8-bae4-f21866b58718", "woodcutting_speed_blockling", woodcuttingSpeed, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        woodcuttingSpeedTypeModifier = createFloatAttributeModifier("a66110e2-3a7f-4907-b85c-05c65341cd2e", "woodcutting_speed_type", woodcuttingSpeedBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        woodcuttingSpeedLevelModifier = createFloatAttributeModifier("6b71ee16-7d04-442e-9d49-9373833f5539", "woodcutting_speed_level", woodcuttingSpeedBlocklingModifier, 0.0f, Operation.ADD, null, woodcuttingLevel.displayStringNameSupplier);
        woodcuttingSpeedMainHandModifier = createFloatAttributeModifier("978f9dd4-3fbb-41ee-9bba-eddcfb42b6ff", "woodcutting_speed_main_hand", woodcuttingSpeed, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString());
        woodcuttingSpeedOffHandModifier = createFloatAttributeModifier("80fd0028-a793-491c-bc9c-fe94071f91c7", "woodcutting_speed_off_hand", woodcuttingSpeed, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString());
        woodcuttingSpeedSkillEfficiencyModifier = createFloatAttributeModifier("38a9d80c-4f96-4929-bd34-8c03156dec6d", "woodcutting_speed_skill_efficiency", woodcuttingSpeed, 1.1f, Operation.MULTIPLY_TOTAL, this::woodcuttingSpeedSkillEfficiencymodifierDisplayStringValueProvider, () -> skillDisplayNameProvider(BlocklingSkills.Woodcutting.EFFICIENCY), false);

        farmingSpeed = createModifiableFloatAttribute("f6c026b6-1fa9-432f-aca3-d97af784f6d0", "farming_speed", 0.0f, null, null);
        farmingSpeedBlocklingModifier = createModifiableFloatAttributeModifier("39548773-84ee-42ca-8ad6-681d64eaee54", "farming_speed_blockling", farmingSpeed, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString());
        farmingSpeedTypeModifier = createFloatAttributeModifier("f99bbc83-30fc-451f-a53b-fa343cc9244a", "farming_speed_type", farmingSpeedBlocklingModifier, 0.0f, Operation.ADD, null, () -> blockling.getBlocklingType().name.getString());
        farmingSpeedLevelModifier = createFloatAttributeModifier("3b3079cf-8640-436b-bf0a-3aae4deb29be", "farming_speed_level", farmingSpeedBlocklingModifier, 0.0f, Operation.ADD, null, farmingLevel.displayStringNameSupplier);
        farmingSpeedMainHandModifier = createFloatAttributeModifier("5ece4240-17b3-4983-bd0d-67f962a0a838", "farming_speed_main_hand", farmingSpeed, 0.0f, Operation.ADD, null, () -> blockling.getMainHandItem().getHoverName().getString());
        farmingSpeedOffHandModifier = createFloatAttributeModifier("b4bb7131-f2ce-41cd-88ed-ee27e3837679", "farming_speed_off_hand", farmingSpeed, 0.0f, Operation.ADD, null, () -> blockling.getOffhandItem().getHoverName().getString());
        farmingSpeedSkillEfficiencyModifier = createFloatAttributeModifier("792be316-19cb-49ec-a24a-ee224312c60f", "farming_speed_skill_efficiency", farmingSpeed, 1.1f, Operation.MULTIPLY_TOTAL, this::farmingSpeedSkillEfficiencymodifierDisplayStringValueProvider, () -> skillDisplayNameProvider(BlocklingSkills.Farming.EFFICIENCY), false);
    }

    private String skillDisplayNameProvider(SkillInfo skillInfo)
    {
        return skillInfo.general.name.getString() + " ("+ new BlocklingsTranslationTextComponent("skill.name").getString() +")";
    }

    private String knockbackResistanceDisplayStringValueProvider()
    {
        return String.format("%.0f%%", knockbackResistance.getValue() * 100.0f);
    }

    private String knockbackResistanceBlocklingModifierDisplayStringValueProvider()
    {
        return String.format("%.0f%%", knockbackResistanceBlocklingModifier.getValue() * 100.0f);
    }

    private String knockbackResistanceTypeModifierDisplayStringValueProvider()
    {
        return String.format("%.0f%%", knockbackResistanceTypeModifier.getValue() * 100.0f);
    }

    private String knockbackResistanceCombatLevelModifierDisplayStringValueProvider()
    {
        return String.format("%.0f%%", knockbackResistanceCombatLevelModifier.getValue() * 100.0f);
    }

    private String miningSpeedSkillEfficiencymodifierDisplayStringValueProvider()
    {
        return String.format("%.0f%%", (miningSpeedSkillEfficiencyModifier.getValue() - 1.0f) * 100.0f);
    }

    private String woodcuttingSpeedSkillEfficiencymodifierDisplayStringValueProvider()
    {
        return String.format("%.0f%%", (woodcuttingSpeedSkillEfficiencyModifier.getValue() - 1.0f) * 100.0f);
    }

    private String farmingSpeedSkillEfficiencymodifierDisplayStringValueProvider()
    {
        return String.format("%.0f%%", (farmingSpeedSkillEfficiencyModifier.getValue() - 1.0f) * 100.0f);
    }

    public IntAttribute createIntAttribute(String id, String key, int value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        IntAttribute attribute = new IntAttribute(id, key, blockling, value, displayStringValueSupplier, displayStringNameSupplier);
        attributes.add(attribute);

        return attribute;
    }

    public FloatAttribute createFloatAttribute(String id, String key, float value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        FloatAttribute attribute = new FloatAttribute(id, key, blockling, value, displayStringValueSupplier, displayStringNameSupplier);
        attributes.add(attribute);

        return attribute;
    }

    public ModifiableIntAttribute createModifiableIntAttribute(String id, String key, int value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        ModifiableIntAttribute attribute = new ModifiableIntAttribute(id, key, blockling, value, displayStringValueSupplier, displayStringNameSupplier);
        attributes.add(attribute);

        return attribute;
    }

    public ModifiableFloatAttribute createModifiableFloatAttribute(String id, String key, float value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        ModifiableFloatAttribute attribute = new ModifiableFloatAttribute(id, key, blockling, value, displayStringValueSupplier, displayStringNameSupplier);
        attributes.add(attribute);

        return attribute;
    }

    public AveragedAttribute createAveragedAttribute(String id, String key, float value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        AveragedAttribute attribute = new AveragedAttribute(id, key, blockling, value, displayStringValueSupplier, displayStringNameSupplier);
        attributes.add(attribute);

        return attribute;
    }

    public <T extends Enum<?>> EnumAttribute<T> createEnumAttribute(String id, String key, T value, Function<Integer, T> ordinalConverter, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        EnumAttribute<T> attribute = new EnumAttribute<>(id, key, blockling, value, ordinalConverter, displayStringValueSupplier, displayStringNameSupplier);
        attributes.add(attribute);

        return attribute;
    }

    public FloatAttributeModifier createFloatAttributeModifier(String id, String key, IModifiable<Float> attribute, float value, Operation operation, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        return createFloatAttributeModifier(id, key, attribute, value, operation, displayStringValueSupplier, displayStringNameSupplier, true);
    }

    public FloatAttributeModifier createFloatAttributeModifier(String id, String key, IModifiable<Float> attribute, float value, Operation operation, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        FloatAttributeModifier modifier = new FloatAttributeModifier(id, key, attribute, blockling, value, operation, displayStringValueSupplier, displayStringNameSupplier);
        modifier.setIsEnabled(isEnabled, false);
        attribute.addModifier(modifier);
        attributes.add(modifier);
        modifiers.add(modifier);

        return modifier;
    }

    public IntAttributeModifier createIntAttributeModifier(String id, String key, IModifiable<Integer> attribute, int value, Operation operation, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        IntAttributeModifier modifier = new IntAttributeModifier(id, key, attribute, blockling, value, operation, displayStringValueSupplier, displayStringNameSupplier);
        attribute.addModifier(modifier);
        attributes.add(modifier);
        modifiers.add(modifier);

        return modifier;
    }

    public ModifiableFloatAttributeModifier createModifiableFloatAttributeModifier(String id, String key, IModifiable<Float> attribute, float value, Operation operation, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        ModifiableFloatAttributeModifier modifier = new ModifiableFloatAttributeModifier(id, key, attribute, blockling, value, operation, displayStringValueSupplier, displayStringNameSupplier);
        attribute.addModifier(modifier);
        attributes.add(modifier);
        modifiers.add(modifier);

        return modifier;
    }

    public ModifiableIntAttributeModifier createModifiableIntAttributeModifier(String id, String key, IModifiable<Integer> attribute, int value, Operation operation, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        ModifiableIntAttributeModifier modifier = new ModifiableIntAttributeModifier(id, key, attribute, blockling, value, operation, displayStringValueSupplier, displayStringNameSupplier);
        attribute.addModifier(modifier);
        attributes.add(modifier);
        modifiers.add(modifier);

        return modifier;
    }

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

    public void initCallbacks()
    {
        combatLevel.addUpdateCallback((i) -> { updateCombatLevelBonuses(false); });
        attackSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ATTACK_SPEED)).setBaseValue(f));
        miningLevel.addUpdateCallback((i) -> { miningSpeedLevelModifier.setValue(calcBreakSpeedFromLevel(i), false); });
        woodcuttingLevel.addUpdateCallback((i) -> { woodcuttingSpeedLevelModifier.setValue(calcBreakSpeedFromLevel(i), false); });
        farmingLevel.addUpdateCallback((i) -> { farmingSpeedLevelModifier.setValue(calcBreakSpeedFromLevel(i), false); });
        combatXp.addUpdateCallback((i) -> checkForLevelUp(false));
        miningXp.addUpdateCallback((i) -> checkForLevelUp(false));
        woodcuttingXp.addUpdateCallback((i) -> checkForLevelUp(false));
        farmingXp.addUpdateCallback((i) -> checkForLevelUp(false));
        miningRange.addUpdateCallback((f) -> miningRangeSq.setBaseValue(miningRange.getValue() * miningRange.getValue(), false));
        woodcuttingRange.addUpdateCallback((f) -> woodcuttingRangeSq.setBaseValue(woodcuttingRange.getValue() * woodcuttingRange.getValue(), false));
        farmingRange.addUpdateCallback((f) -> farmingRangeSq.setBaseValue(farmingRange.getValue() * farmingRange.getValue(), false));
        maxHealth.addUpdateCallback((f) -> { Objects.requireNonNull(blockling.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(f); updateHealth(); });
        mainHandAttackDamage.addUpdateCallback((f) -> setVanillaAttackDamageAttribute());
        offHandAttackDamage.addUpdateCallback((f) -> setVanillaAttackDamageAttribute());
        armour.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR)).setBaseValue(f));
        armourToughness.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR_TOUGHNESS)).setBaseValue(f));
        knockbackResistance.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(f));
        moveSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(f / 10.0f));
    }

    public void writeToNBT(CompoundNBT c)
    {
        CompoundNBT attributesNBT = new CompoundNBT();

        for (Attribute<?> attribute : attributes)
        {
            CompoundNBT attributeTag = new CompoundNBT();

            attribute.writeToNBT(attributeTag);

            attributesNBT.put(attribute.id.toString(), attributeTag);
        }

        c.put("attributes", attributesNBT);
    }

    public void readFromNBT(CompoundNBT c)
    {
        CompoundNBT attributesNBT = (CompoundNBT) c.get("attributes");

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

    public void encode(PacketBuffer buf)
    {
        for (Attribute<?> attribute : attributes)
        {
            attribute.encode(buf);
        }
    }

    public void decode(PacketBuffer buf)
    {
        for (Attribute<?> attribute : attributes)
        {
            attribute.decode(buf);
        }

        init();
    }

    public static int getXpUntilNextLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

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

    public void checkForLevelUp(boolean sync)
    {
        if (combatLevel.getValue() < 99)
        {
            int combatLevel = this.combatLevel.getValue();
            int combatXp = this.combatXp.getValue();
            int combatXpReq = getXpUntilNextLevel(combatLevel);
            if (combatXp >= combatXpReq)
            {
                this.combatLevel.setValue(combatLevel + 1, sync);
                this.combatXp.setValue(combatXp - combatXpReq, sync);
            }
        }

        if (miningLevel.getValue() < 99)
        {
            int miningLevel = this.miningLevel.getValue();
            int miningXp = this.miningXp.getValue();
            int miningXpReq = getXpUntilNextLevel(miningLevel);
            if (miningXp >= miningXpReq)
            {
                this.miningLevel.setValue(miningLevel + 1, sync);
                this.miningXp.setValue(miningXp - miningXpReq, sync);
            }
        }

        if (woodcuttingLevel.getValue() < 99)
        {
            int woodcuttingLevel = this.woodcuttingLevel.getValue();
            int woodcuttingXp = this.woodcuttingXp.getValue();
            int woodcuttingXpReq = getXpUntilNextLevel(woodcuttingLevel);
            if (woodcuttingXp >= woodcuttingXpReq)
            {
                this.woodcuttingLevel.setValue(woodcuttingLevel + 1, sync);
                this.woodcuttingXp.setValue(woodcuttingXp - woodcuttingXpReq, sync);
            }
        }

        if (farmingLevel.getValue() < 99)
        {
            int farmingLevel = this.farmingLevel.getValue();
            int farmingXp = this.farmingXp.getValue();
            int farmingXpReq = getXpUntilNextLevel(farmingLevel);
            if (farmingXp >= farmingXpReq)
            {
                this.farmingLevel.setValue(farmingLevel + 1, sync);
                this.farmingXp.setValue(farmingXp - farmingXpReq, sync);
            }
        }
    }

    public void updateCombatLevelBonuses(boolean sync)
    {
        maxHealthCombatLevelModifier.setValue(calcBonusHealthFromCombatLevel(), sync);
        mainHandAttackDamageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel(), sync);
        offHandAttackDamageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel(), sync);
        attackSpeedLevelModifier.setValue(calcBreakSpeedFromLevel(combatLevel.getValue()), sync);
        armourCombatLevelModifier.setValue(calcBonusArmorFromCombatLevel(), sync);
        armourToughnessCombatLevelModifier.setValue(calcBonusArmorToughnessFromCombatLevel(), sync);
        knockbackResistanceCombatLevelModifier.setValue(calcBonusKnockbackResistanceFromCombatLevel(), sync);
    }

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

    private float calcBreakSpeedFromLevel(int level)
    {
        return (float) (10.0f * Math.tan((level / 99.0) * (Math.PI / 4.0)));
    }

    private float calcBonusHealthFromCombatLevel()
    {
        return (float) (50.0f * Math.tan((combatLevel.getValue() / 99.0) * (Math.PI / 4.0)));
    }

    private float calcBonusDamageFromCombatLevel()
    {
        return (float) (20.0f * Math.tan((combatLevel.getValue() / 99.0) * (Math.PI / 4.0)));
    }

    private float calcBonusArmorFromCombatLevel()
    {
        return (float) (10.0f * Math.tan((combatLevel.getValue() / 99.0) * (Math.PI / 4.0)));
    }

    private float calcBonusArmorToughnessFromCombatLevel()
    {
        return (float) (5.0f * Math.tan((combatLevel.getValue() / 99.0) * (Math.PI / 4.0)));
    }

    private float calcBonusKnockbackResistanceFromCombatLevel()
    {
        return (float) (0.5f * Math.tan((combatLevel.getValue() / 99.0) * (Math.PI / 4.0)));
    }

    public void updateHealth()
    {
        if (blockling.getHealth() > blockling.getMaxHealth())
        {
            blockling.setHealth(blockling.getMaxHealth());
        }
    }

    public Attribute<?> getAttribute(String key)
    {
        for (Attribute<?> attribute : attributes)
        {
            if (attribute.key.equals(key))
            {
                return attribute;
            }
        }

        return null;
    }

    public int getHealth()
    {
        return (int) Math.ceil(blockling.getHealth());
    }

    public int getMaxHealth()
    {
        return (int) Math.ceil(blockling.getMaxHealth());
    }

    public float getHealthPercentage()
    {
        return blockling.getHealth() / blockling.getMaxHealth();
    }

    public Attribute<Integer> getLevelAttribute(Level level)
    {
        switch (level)
        {
            case COMBAT: return combatLevel;
            case MINING: return miningLevel;
            case WOODCUTTING: return woodcuttingLevel;
            case FARMING: return farmingLevel;
            case TOTAL: return totalLevel;
        }

        return null;
    }

    public enum Level
    {
        COMBAT, MINING, WOODCUTTING, FARMING, TOTAL;
    }
}
