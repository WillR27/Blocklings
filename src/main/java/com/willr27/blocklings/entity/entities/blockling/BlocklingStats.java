package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.attribute.*;
import com.willr27.blocklings.attribute.attributes.AveragedAttribute;
import com.willr27.blocklings.attribute.attributes.EnumAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.FloatAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.IntAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.ModifiableFloatAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.ModifiableIntAttribute;
import com.willr27.blocklings.attribute.modifier.AttributeModifier;
import com.willr27.blocklings.attribute.modifier.modifiers.numbers.FloatAttributeModifier;
import com.willr27.blocklings.attribute.modifier.modifiers.numbers.IntAttributeModifier;
import com.willr27.blocklings.item.ToolUtil;
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
    public final List<IntAttribute> levels = new ArrayList<>();
    public final List<AttributeModifier<?>> modifiers = new ArrayList<>();

    public final EnumAttribute<BlocklingHand> hand;

    public final AveragedAttribute attackSpeed;
    public final FloatAttributeModifier attackSpeedLevelModifier;
    public final FloatAttributeModifier attackSpeedMainHandModifier;
    public final FloatAttributeModifier attackSpeedOffHandModifier;
    public final ModifiableFloatAttribute miningSpeed;
//    public final FloatAttributeModifier miningSpeedLevelModifier;
//    public final FloatAttributeModifier miningSpeedMainHandModifier;
//    public final FloatAttributeModifier miningSpeedOffHandModifier;
    public final ModifiableFloatAttribute woodcuttingSpeed;
//    public final FloatAttributeModifier woodcuttingSpeedLevelModifier;
//    public final FloatAttributeModifier woodcuttingSpeedMainHandModifier;
//    public final FloatAttributeModifier woodcuttingSpeedOffHandModifier;
    public final ModifiableFloatAttribute farmingSpeed;
//    public final FloatAttributeModifier farmingSpeedLevelModifier;
//    public final FloatAttributeModifier farmingSpeedMainHandModifier;
//    public final FloatAttributeModifier farmingSpeedOffHandModifier;

    public final IntAttribute combatLevel;
    public final IntAttribute miningLevel;
    public final IntAttribute woodcuttingLevel;
    public final IntAttribute farmingLevel;

    public final IntAttribute combatXp;
    public final IntAttribute miningXp;
    public final IntAttribute woodcuttingXp;
    public final IntAttribute farmingXp;

    public final IntAttribute skillPoints;

    public final ModifiableFloatAttribute miningRange;
    public final ModifiableFloatAttribute miningRangeSq;
    public final ModifiableFloatAttribute woodcuttingRange;
    public final ModifiableFloatAttribute woodcuttingRangeSq;
    public final ModifiableFloatAttribute farmingRange;
    public final ModifiableFloatAttribute farmingRangeSq;

    public final ModifiableFloatAttribute maxHealth;
    public final FloatAttributeModifier maxHealthCombatLevelModifier;
    public final FloatAttributeModifier maxHealthTypeModifier;
    public final ModifiableFloatAttribute attackDamage;
    public final FloatAttributeModifier attackDamageCombatLevelModifier;
    public final FloatAttributeModifier attackDamageTypeModifier;
    public final ModifiableFloatAttribute armour;
    public final FloatAttributeModifier armourCombatLevelModifier;
    public final FloatAttributeModifier armourTypeModifier;
    public final ModifiableFloatAttribute armourToughness;
    public final ModifiableFloatAttribute knockbackResistance;
    public final ModifiableFloatAttribute moveSpeed;
    public final FloatAttributeModifier movementSpeedTypeModifier;

    public final BlocklingEntity blockling;
    public final World world;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.world = blockling.level;

        hand = createEnumAttribute("f21fcbaa-f800-468e-8c22-ec4b4fd0fdc2", "hand", BlocklingHand.NONE, (i) -> BlocklingHand.values()[i]);

        combatLevel = createIntAttribute("17beee8e-3fca-4601-a766-46f811ad6b69", "combat_level", blockling.getRandom().nextInt(5));
        combatLevel.addUpdateCallback((i) -> { updateCombatLevelBonuses(false); });
        levels.add(combatLevel);

        // Default set to 4.0f (seems to be the same for a player too)
        // A sword is 1.6f * 2.0f
        // An axe is 0.8f * 2.0f
        // So any tool use should be slower than fists
        attackSpeed = createAveragedAttribute("4cbc129d-281d-410e-bba0-45d4e064932a", "attack_speed", 4.0f);
        attackSpeedLevelModifier = createFloatAttributeModifier("bfeb22fe-aaaf-4294-9850-27449e27e44f", "attack_speed_level", attackSpeed, 0.0f, AttributeModifier.Operation.ADD, combatLevel.displayStringSupplier);
        attackSpeedMainHandModifier = createFloatAttributeModifier("87343a1e-7a0b-4963-8d7e-e95f809e90ee", "attack_speed_main_hand", attackSpeed, 0.0f, AttributeModifier.Operation.ADD, () -> blockling.getMainHandItem().getHoverName().getString());
        attackSpeedOffHandModifier = createFloatAttributeModifier("3566961d-db2b-4833-8c0c-cf6813ade8cc", "attack_speed_off_hand", attackSpeed, 0.0f, AttributeModifier.Operation.ADD, () -> blockling.getOffhandItem().getHoverName().getString());

        // Default mining speed for an item/hand is 1.0f
        // A wooden pickaxe is 2.0f
        // A diamond pickaxe is 8.0f
        // Our default mining speed can be 0.0f as it will be determined by the level
        miningSpeed = createModifiableFloatAttribute("0d918c08-2e94-481b-98e1-c2ff3ae395de", "mining_speed", 0.0f);
        woodcuttingSpeed = createModifiableFloatAttribute("e1e3ecb3-ae1d-46c5-8ea8-a7180641910b", "woodcutting_speed", 0.0f);
        farmingSpeed = createModifiableFloatAttribute("f6c026b6-1fa9-432f-aca3-d97af784f6d0", "farming_speed", 0.0f);

        miningLevel = createIntAttribute("a2a62308-0a6e-41bb-9844-4645eeb72fb7", "mining_level", blockling.getRandom().nextInt(5));
        miningLevel.addUpdateCallback((i) -> miningSpeed.setBaseValue(calcBreakSpeedFromLevel(i), false));
        levels.add(miningLevel);
        woodcuttingLevel = createIntAttribute("c6d3ce7c-52af-44df-833b-fede277eec7f", "woodcutting_level", blockling.getRandom().nextInt(5));
        woodcuttingLevel.addUpdateCallback((i) -> woodcuttingSpeed.setBaseValue(calcBreakSpeedFromLevel(i), false));
        levels.add(woodcuttingLevel);
        farmingLevel = createIntAttribute("ac1c6d1b-18bb-435a-ad93-c24d6fa90816", "farming_level", blockling.getRandom().nextInt(5));
        farmingLevel.addUpdateCallback((i) -> farmingSpeed.setBaseValue(calcBreakSpeedFromLevel(i), false));
        levels.add(farmingLevel);

        combatXp = createIntAttribute("ec56a177-2a08-4f43-b77a-b1d4544a8656", "combat_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(combatLevel.getValue())));
        combatXp.addUpdateCallback((i) -> checkForLevelUp(false));
        miningXp = createIntAttribute("ce581807-3fad-45b1-9aec-43ed0cb53c8f", "mining_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(miningLevel.getValue())));
        miningXp.addUpdateCallback((i) -> checkForLevelUp(false));
        woodcuttingXp = createIntAttribute("82165063-6d47-4534-acc9-db3543c3db74", "woodcutting_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(woodcuttingLevel.getValue())));
        woodcuttingXp.addUpdateCallback((i) -> checkForLevelUp(false));
        farmingXp = createIntAttribute("1f1e4cbc-358e-4477-92d8-03e818d5272c", "farming_xp", blockling.getRandom().nextInt(getXpUntilNextLevel(farmingLevel.getValue())));
        farmingXp.addUpdateCallback((i) -> checkForLevelUp(false));

        skillPoints = createIntAttribute("a78f9d35-266e-4f86-836e-daaef073940e", "skill_points", 50);

        miningRange = createModifiableFloatAttribute("76e044ca-e73e-4004-b576-920a8446612d", "mining_range", 2.5f);
        miningRangeSq = createModifiableFloatAttribute("55af3992-cf8d-4d5d-8634-fbc1e05d30fe", "mining_range_sq", miningRange.getValue() * miningRange.getValue());
        miningRange.addUpdateCallback((f) -> miningRangeSq.setBaseValue(miningRange.getValue() * miningRange.getValue(), false));
        woodcuttingRange = createModifiableFloatAttribute("bc50cc2d-2323-4743-a175-5af87e61e04e", "woodcutting_range", 2.5f);
        woodcuttingRangeSq = createModifiableFloatAttribute("8ba7fea6-6790-4010-b210-fa69b1effad8", "woodcutting_range_sq", woodcuttingRange.getValue() * woodcuttingRange.getValue());
        woodcuttingRange.addUpdateCallback((f) -> woodcuttingRangeSq.setBaseValue(woodcuttingRange.getValue() * woodcuttingRange.getValue(), false));
        farmingRange = createModifiableFloatAttribute("c549a710-62d9-4d79-8d9d-ba3690752d08", "farming_range", 2.5f);
        farmingRangeSq = createModifiableFloatAttribute("bc3a8f41-d033-437f-bce2-840df7a55fad", "farming_range_sq", farmingRange.getValue() * farmingRange.getValue());
        farmingRange.addUpdateCallback((f) -> farmingRangeSq.setBaseValue(farmingRange.getValue() * farmingRange.getValue(), false));

        maxHealth = createModifiableFloatAttribute("9c6eb101-f025-4f8f-895b-10868b7d06b2", "max_health",10.0f);
        maxHealth.addUpdateCallback((f) -> { Objects.requireNonNull(blockling.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(f); updateHealth(); });
        maxHealthCombatLevelModifier = createFloatAttributeModifier("a78160fa-7bc3-493e-b74b-27af4206d111", "max_health_combat_level", maxHealth, 0.0f, AttributeModifier.Operation.ADD, null);
        maxHealthTypeModifier  = createFloatAttributeModifier("79043f39-6f44-4077-a358-0f75a0a1e995", "max_health_type", maxHealth, 0.0f, AttributeModifier.Operation.ADD, null);

        attackDamage = createModifiableFloatAttribute("e8549f17-e473-4849-8f48-ae624ee0c242", "attack_damage", 1.0f);
        attackDamage.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(f));
        attackDamageCombatLevelModifier = createFloatAttributeModifier("406a98f7-df1f-4c7f-93e4-990d71c7747f", "attack_damage_combat_level", attackDamage, 0.0f, AttributeModifier.Operation.ADD, null);
        attackDamageTypeModifier = createFloatAttributeModifier("ddb441fc-2d8c-4950-b0a9-b96b60680ac1", "attack_damage_type", attackDamage, 0.0f, AttributeModifier.Operation.ADD, null);

        armour = createModifiableFloatAttribute("6b34a986-f1ad-4476-a1c6-700d841fb1ec", "armour", 2.0f);
        armour.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR)).setBaseValue(f));
        armourCombatLevelModifier = createFloatAttributeModifier("15f2f2ce-cdf1-4188-882e-67ceab22df41", "armour_combat_level", armour, 0.0f, AttributeModifier.Operation.ADD, null);
        armourTypeModifier = createFloatAttributeModifier("a72fb401-abb7-4d95-ad7e-e83fc6a399d1", "armour_type", armour, 0.0f, AttributeModifier.Operation.ADD, null);

        armourToughness = createModifiableFloatAttribute("1cfdad6a-0bd3-461f-8007-c0a591a30783", "armour_toughness", 0.0f);
        armourToughness.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR_TOUGHNESS)).setBaseValue(f));

        knockbackResistance = createModifiableFloatAttribute("ddc90fc2-4a68-4c30-8701-d2d9dbe8b94a", "knockback_resistance", 1.0f);
        knockbackResistance.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(f));

        moveSpeed = createModifiableFloatAttribute("9a0bb639-8543-4725-9be1-8a8ce688da70", "move_speed", 0.3f);
        moveSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(f));
        movementSpeedTypeModifier = createFloatAttributeModifier("1391f012-6482-420e-ae77-5178b7ed77c1", "move_speed_type", moveSpeed, 0.0f, AttributeModifier.Operation.ADD, null);
    }

    public IntAttribute createIntAttribute(String id, String key, int value)
    {
        IntAttribute attribute = new IntAttribute(id, key, blockling, value);
        attributes.add(attribute);

        return attribute;
    }

    public FloatAttribute createFloatAttribute(String id, String key, float value)
    {
        FloatAttribute attribute = new FloatAttribute(id, key, blockling, value);
        attributes.add(attribute);

        return attribute;
    }

    public ModifiableIntAttribute createModifiableIntAttribute(String id, String key, int value)
    {
        ModifiableIntAttribute attribute = new ModifiableIntAttribute(id, key, blockling, value);
        attributes.add(attribute);

        return attribute;
    }

    public ModifiableFloatAttribute createModifiableFloatAttribute(String id, String key, float value)
    {
        ModifiableFloatAttribute attribute = new ModifiableFloatAttribute(id, key, blockling, value);
        attributes.add(attribute);

        return attribute;
    }

    public AveragedAttribute createAveragedAttribute(String id, String key, float value)
    {
        AveragedAttribute attribute = new AveragedAttribute(id, key, blockling, value);
        attributes.add(attribute);

        return attribute;
    }

    public <T extends Enum<?>> EnumAttribute<T> createEnumAttribute(String id, String key, T value, Function<Integer, T> ordinalConverter)
    {
        EnumAttribute<T> attribute = new EnumAttribute<>(id, key, blockling, value, ordinalConverter);
        attributes.add(attribute);

        return attribute;
    }

    public FloatAttributeModifier createFloatAttributeModifier(String id, String key, ModifiableAttribute<Float> attribute, float value, AttributeModifier.Operation operation, Supplier<String> displayStringSupplier)
    {
        FloatAttributeModifier modifier = new FloatAttributeModifier(id, key, attribute, value, operation, displayStringSupplier);
        attribute.addModifier(modifier);
        modifiers.add(modifier);

        return modifier;
    }

    public IntAttributeModifier createIntAttributeModifier(String id, String key, ModifiableAttribute<Integer> attribute, int value, AttributeModifier.Operation operation, Supplier<String> displayStringSupplier)
    {
        IntAttributeModifier modifier = new IntAttributeModifier(id, key, attribute, value, operation, displayStringSupplier);
        attribute.addModifier(modifier);
        modifiers.add(modifier);

        return modifier;
    }

    public void init()
    {
        for (Attribute<?> attribute : attributes)
        {
            if (attribute instanceof ModifiableAttribute<?>)
            {
                ((ModifiableAttribute<?>) attribute).calculate();
            }
        }

        for (Attribute<?> attribute : attributes)
        {
            attribute.callUpdateCallbacks();
        }

        updateCombatLevelBonuses(false);
//        updateTypeBonuses(false);
//        updateToolModifiers(false);
    }

    public void writeToNBT(CompoundNBT c)
    {
        CompoundNBT attributesNBT = new CompoundNBT();

        for (Attribute<?> attribute : attributes)
        {
            attribute.writeToNBT(attributesNBT);
        }

        c.put("attributes", attributesNBT);

        CompoundNBT modifiersNBT = new CompoundNBT();

        for (AttributeModifier<?> modifier : modifiers)
        {
            modifier.writeToNBT(modifiersNBT);
        }

        c.put("modifiers", modifiersNBT);
    }

    public void readFromNBT(CompoundNBT c)
    {
        CompoundNBT attributesNBT = (CompoundNBT) c.get("attributes");

        for (Attribute<?> attribute : attributes)
        {
            attribute.readFromNBT(attributesNBT);
        }

        CompoundNBT modifiersNBT = (CompoundNBT) c.get("modifiers");

        for (AttributeModifier<?> modifier : modifiers)
        {
            modifier.readFromNBT(modifiersNBT);
        }

        init();
    }

    public void encode(PacketBuffer buf)
    {
        for (Attribute<?> attribute : attributes)
        {
            attribute.encode(buf);
        }

        for (AttributeModifier<?> modifier : modifiers)
        {
            modifier.encode(buf);
        }
    }

    public void decode(PacketBuffer buf)
    {
        for (Attribute<?> attribute : attributes)
        {
            attribute.decode(buf);
        }

        for (AttributeModifier<?> modifier : modifiers)
        {
            modifier.decode(buf);
        }

        init();
    }

    public static int getXpUntilNextLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    public void checkForLevelUp(boolean sync)
    {
        int combatLevel = this.combatLevel.getValue();
        int combatXp = this.combatXp.getValue();
        int combatXpReq = getXpUntilNextLevel(combatLevel);
        if (combatXp >= combatXpReq)
        {
            this.combatLevel.setValue(combatLevel + 1, sync);
            this.combatXp.setValue(combatXp - combatXpReq, sync);
        }

        int miningLevel = this.miningLevel.getValue();
        int miningXp = this.miningXp.getValue();
        int miningXpReq = getXpUntilNextLevel(miningLevel);
        if (miningXp >= miningXpReq)
        {
            this.miningLevel.setValue(miningLevel + 1, sync);
            this.miningXp.setValue(miningXp - miningXpReq, sync);
        }

        int woodcuttingLevel = this.woodcuttingLevel.getValue();
        int woodcuttingXp = this.woodcuttingXp.getValue();
        int woodcuttingXpReq = getXpUntilNextLevel(woodcuttingLevel);
        if (woodcuttingXp >= woodcuttingXpReq)
        {
            this.woodcuttingLevel.setValue(woodcuttingLevel + 1, sync);
            this.woodcuttingXp.setValue(woodcuttingXp - woodcuttingXpReq, sync);
        }

        int farmingLevel = this.farmingLevel.getValue();
        int farmingXp = this.farmingXp.getValue();
        int farmingXpReq = getXpUntilNextLevel(farmingLevel);
        if (farmingXp >= farmingXpReq)
        {
            this.farmingLevel.setValue(farmingLevel + 1, sync);
            this.farmingXp.setValue(farmingXp - farmingXpReq, sync);
        }
    }

    public void updateCombatLevelBonuses(boolean sync)
    {
        maxHealthCombatLevelModifier.setValue(calcBonusHealthFromCombatLevel(), sync);
        attackDamageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel(), sync);
        attackSpeedLevelModifier.setValue(calcBreakSpeedFromLevel(combatLevel.getValue()), sync);
        armourCombatLevelModifier.setValue(calcBonusArmorFromCombatLevel(), sync);
    }

    public void updateTypeBonuses(boolean sync)
    {
        BlocklingType type = blockling.getBlocklingType();
        maxHealthTypeModifier.setValue(type.getBonusHealth(), sync);
        attackDamageTypeModifier.setValue(type.getBonusDamage(), sync);
        armourTypeModifier.setValue(type.getBonusArmour(), sync);
        movementSpeedTypeModifier.setValue(type.getBonusSpeed(), sync);
    }

    private float calcBreakSpeedFromLevel(int level)
    {
        return level / 10.0f;
    }

    private float calcBonusHealthFromCombatLevel()
    {
        return (float) (3.0f * Math.log(combatLevel.getValue()));
    }

    private float calcBonusDamageFromCombatLevel()
    {
        return (float) (2.0f * Math.log(combatLevel.getValue()));
    }

    private float calcBonusArmorFromCombatLevel()
    {
        return (float) (0.5f * Math.log(combatLevel.getValue()));
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

    /**
     * Returns the attack damage for the given hand, including blockling and weapon damage.
     */
    public float getAttackDamage(BlocklingHand hand)
    {
        float blocklingAttackDamage = attackDamage.getValue();
        float mainAttackDamage = ToolUtil.getBaseDamageIfTool(blockling.getMainHandItem());
        float offAttackDamage = ToolUtil.getBaseDamageIfTool(blockling.getOffhandItem());

        float attackDamage = 0.0f;

        if (hand == BlocklingHand.BOTH)
        {
            attackDamage = blocklingAttackDamage;
            attackDamage += mainAttackDamage;
            attackDamage += offAttackDamage;
        }
        else if (hand == BlocklingHand.MAIN)
        {
            attackDamage = blocklingAttackDamage;
            attackDamage += mainAttackDamage;
        }
        else if (hand == BlocklingHand.OFF)
        {
            attackDamage = blocklingAttackDamage;
            attackDamage += offAttackDamage;
        }

        return attackDamage;
    }

    /**
     * Returns the attack damage for the given hand, including blockling and weapon damage.
     * If the hand is not being used to attack it will return 0.0f.
     */
    public float getActualAttackDamage(BlocklingHand hand)
    {
        float blocklingAttackDamage = attackDamage.getValue();
        float mainAttackDamage = ToolUtil.getBaseDamageIfTool(blockling.getMainHandItem());
        float offAttackDamage = ToolUtil.getBaseDamageIfTool(blockling.getOffhandItem());

        float attackDamage = 0.0f;

        if (hand == BlocklingHand.BOTH)
        {
            attackDamage = blocklingAttackDamage;

            if (blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN))
            {
                attackDamage += mainAttackDamage;
            }

            if (blockling.getEquipment().isAttackingWith(BlocklingHand.OFF))
            {
                attackDamage += offAttackDamage;
            }
        }
        else if (hand == BlocklingHand.MAIN)
        {
            attackDamage = blocklingAttackDamage;

            if (blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN))
            {
                attackDamage += mainAttackDamage;
            }
        }
        else if (hand == BlocklingHand.OFF)
        {
            attackDamage = blocklingAttackDamage;

            if (blockling.getEquipment().isAttackingWith(BlocklingHand.OFF))
            {
                attackDamage += offAttackDamage;
            }
        }

        return attackDamage;
    }

    public IntAttribute getLevel(Level level)
    {
        switch (level)
        {
            case COMBAT: return combatLevel;
            case MINING: return miningLevel;
            case WOODCUTTING: return woodcuttingLevel;
            case FARMING: return farmingLevel;
        }

        return null;
    }

    public enum Level
    {
        COMBAT, MINING, WOODCUTTING, FARMING;

        public String getDisplayName()
        {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}
