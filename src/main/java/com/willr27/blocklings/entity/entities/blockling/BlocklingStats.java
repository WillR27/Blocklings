package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.attribute.*;
import com.willr27.blocklings.attribute.attributes.EnumAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.FloatAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.IntAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.ModifiableFloatAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.ModifiableIntAttribute;
import com.willr27.blocklings.attribute.modifier.AttributeModifier;
import com.willr27.blocklings.attribute.modifier.modifiers.numbers.FloatAttributeModifier;
import com.willr27.blocklings.attribute.modifier.modifiers.numbers.IntAttributeModifier;
import com.willr27.blocklings.item.ToolType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class BlocklingStats
{
    public final List<Attribute<?>> attributes = new ArrayList<>();
    public final List<IntAttribute> levels = new ArrayList<>();
    public final List<AttributeModifier<?>> modifiers = new ArrayList<>();

    public final EnumAttribute<BlocklingHand> hand;

    public final ModifiableFloatAttribute combatSpeed;
    public final FloatAttributeModifier combatIntervalLevelModifier;
    public final ModifiableFloatAttribute miningSpeed;
    public final ModifiableFloatAttribute woodcuttingInterval;
    public final FloatAttributeModifier woodcuttingIntervalLevelModifier;
    public final FloatAttributeModifier woodcuttingIntervalToolModifier;
    public final ModifiableFloatAttribute farmingInterval;
    public final FloatAttributeModifier farmingIntervalLevelModifier;
    public final FloatAttributeModifier farmingIntervalToolModifier;

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
    public final ModifiableFloatAttribute damage;
    public final FloatAttributeModifier damageCombatLevelModifier;
    public final FloatAttributeModifier damageTypeModifier;
    public final ModifiableFloatAttribute armour;
    public final FloatAttributeModifier armourCombatLevelModifier;
    public final FloatAttributeModifier armourTypeModifier;
    public final ModifiableFloatAttribute movementSpeed;
    public final FloatAttributeModifier movementSpeedTypeModifier;

    public final BlocklingEntity blockling;
    public final World world;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.world = blockling.level;

        hand = createEnumAttribute("f21fcbaa-f800-468e-8c22-ec4b4fd0fdc2", "hand", BlocklingHand.NONE, (i) -> BlocklingHand.values()[i]);

        // Default set to 4.0f (seems to be the same for a player too)
        // A sword is 1.6f * 2.0f
        // An axe is 0.8f * 2.0f
        // So any tool use should be slower than fists
        combatSpeed = createModifiableFloatAttribute("4cbc129d-281d-410e-bba0-45d4e064932a", "combat_speed", 4.0f);
        combatIntervalLevelModifier = createFloatAttributeModifier("2fa17860-5292-47c5-91e3-642848701ec4", "combat_speed_level", combatSpeed, 0.0f, AttributeModifier.Operation.ADD);

        // Default mining speed for an item/hand is 1.0f
        // A wooden pickaxe is 2.0f
        // A diamond pickaxe is 8.0f
        // Our default mining speed can be 0.0f as it will be determined by the mining level
        miningSpeed = createModifiableFloatAttribute("0d918c08-2e94-481b-98e1-c2ff3ae395de", "mining_speed", 0.0f);

        woodcuttingInterval = createModifiableFloatAttribute("5ff5bfe0-09ae-4367-9473-d16efb774907", "woodcutting_interval", 10.0f);
        woodcuttingIntervalLevelModifier = createFloatAttributeModifier("2b5856d2-b451-4bb4-8897-6cb0cfb6e930", "woodcutting_interval_level", woodcuttingInterval, 0.0f, AttributeModifier.Operation.ADD);
        woodcuttingIntervalToolModifier = createFloatAttributeModifier("d62d6675-dc0f-4682-8b62-d8b8920b5c90", "woodcutting_interval_tool", woodcuttingInterval, 1.0f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        farmingInterval = createModifiableFloatAttribute("b51399e0-d297-4a34-bfa1-2c51d1a034fa", "farming_interval", 10.0f);
        farmingIntervalLevelModifier = createFloatAttributeModifier("cbe2c238-ca0e-41b1-be8b-e53d1ceee9f1", "farming_interval_level", farmingInterval, 0.0f, AttributeModifier.Operation.ADD);
        farmingIntervalToolModifier = createFloatAttributeModifier("2f613528-92f7-4dca-904f-a60b833cf830", "farming_interval_tool", farmingInterval, 1.0f, AttributeModifier.Operation.MULTIPLY_TOTAL);

        combatLevel = createIntAttribute("17beee8e-3fca-4601-a766-46f811ad6b69", "combat_level", 10);
        combatLevel.addUpdateCallback((i) -> { combatIntervalLevelModifier.setValue(calcBreakSpeedFromLevel(combatLevel.getValue()), false); updateCombatLevelBonuses(false); });
        levels.add(combatLevel);
        miningLevel = createIntAttribute("a2a62308-0a6e-41bb-9844-4645eeb72fb7", "mining_level", 10);
        miningLevel.addUpdateCallback((i) -> miningSpeed.setBaseValue(calcBreakSpeedFromLevel(miningLevel.getValue()), false));
        levels.add(miningLevel);
        woodcuttingLevel = createIntAttribute("c6d3ce7c-52af-44df-833b-fede277eec7f", "woodcutting_level", 10);
        woodcuttingLevel.addUpdateCallback((i) -> woodcuttingIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) woodcuttingLevel.getValue()), false));
        levels.add(woodcuttingLevel);
        farmingLevel = createIntAttribute("ac1c6d1b-18bb-435a-ad93-c24d6fa90816", "farming_level", 10);
        farmingLevel.addUpdateCallback((i) -> farmingIntervalLevelModifier.setValue(calcBreakSpeedFromLevel(farmingLevel.getValue()), false));
        levels.add(farmingLevel);

        combatXp = createIntAttribute("ec56a177-2a08-4f43-b77a-b1d4544a8656", "combat_xp", getXpUntilNextLevel(combatLevel.getValue()));
        combatXp.addUpdateCallback((i) -> checkForLevelUp(false));
        miningXp = createIntAttribute("ce581807-3fad-45b1-9aec-43ed0cb53c8f", "mining_xp", getXpUntilNextLevel(miningLevel.getValue()));
        miningXp.addUpdateCallback((i) -> checkForLevelUp(false));
        woodcuttingXp = createIntAttribute("82165063-6d47-4534-acc9-db3543c3db74", "woodcutting_xp", getXpUntilNextLevel(woodcuttingLevel.getValue()));
        woodcuttingXp.addUpdateCallback((i) -> checkForLevelUp(false));
        farmingXp = createIntAttribute("1f1e4cbc-358e-4477-92d8-03e818d5272c", "farming_xp", getXpUntilNextLevel(farmingLevel.getValue()));
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
        maxHealthCombatLevelModifier = createFloatAttributeModifier("a78160fa-7bc3-493e-b74b-27af4206d111", "max_health_combat_level", maxHealth, 0.0f, AttributeModifier.Operation.ADD);
        maxHealthTypeModifier  = createFloatAttributeModifier("79043f39-6f44-4077-a358-0f75a0a1e995", "max_health_type", maxHealth, 0.0f, AttributeModifier.Operation.ADD);
        damage = createModifiableFloatAttribute("e8549f17-e473-4849-8f48-ae624ee0c242", "damage", 1.0f);
        damage.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(f));
        damageCombatLevelModifier = createFloatAttributeModifier("406a98f7-df1f-4c7f-93e4-990d71c7747f", "damage_combat_level", damage, 0.0f, AttributeModifier.Operation.ADD);
        damageTypeModifier = createFloatAttributeModifier("ddb441fc-2d8c-4950-b0a9-b96b60680ac1", "damage_type", damage, 0.0f, AttributeModifier.Operation.ADD);
        armour = createModifiableFloatAttribute("6b34a986-f1ad-4476-a1c6-700d841fb1ec", "armour", 2.0f);
        armour.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR)).setBaseValue(f));
        armourCombatLevelModifier = createFloatAttributeModifier("15f2f2ce-cdf1-4188-882e-67ceab22df41", "armour_combat_level", armour, 0.0f, AttributeModifier.Operation.ADD);
        armourTypeModifier = createFloatAttributeModifier("a72fb401-abb7-4d95-ad7e-e83fc6a399d1", "armour_type", armour, 0.0f, AttributeModifier.Operation.ADD);
        movementSpeed = createModifiableFloatAttribute("9a0bb639-8543-4725-9be1-8a8ce688da70", "movement_speed", 0.3f);
        movementSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(f));
        movementSpeedTypeModifier = createFloatAttributeModifier("1391f012-6482-420e-ae77-5178b7ed77c1", "movement_speed_type", movementSpeed, 0.0f, AttributeModifier.Operation.ADD);
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

    public <T extends Enum<?>> EnumAttribute<T> createEnumAttribute(String id, String key, T value, Function<Integer, T> ordinalConverter)
    {
        EnumAttribute<T> attribute = new EnumAttribute<>(id, key, blockling, value, ordinalConverter);
        attributes.add(attribute);

        return attribute;
    }

    public FloatAttributeModifier createFloatAttributeModifier(String id, String key, ModifiableAttribute<Float> attribute, float value, AttributeModifier.Operation operation)
    {
        FloatAttributeModifier modifier = new FloatAttributeModifier(id, key, attribute, value, operation);
        attribute.addModifier(modifier);
        modifiers.add(modifier);

        return modifier;
    }

    public IntAttributeModifier createIntAttributeModifier(String id, String key, ModifiableAttribute<Integer> attribute, int value, AttributeModifier.Operation operation)
    {
        IntAttributeModifier modifier = new IntAttributeModifier(id, key, attribute, value, operation);
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

        blockling.setHealth(blockling.getMaxHealth());

//        updateCombatLevelBonuses(false);
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
        damageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel(), sync);
        armourCombatLevelModifier.setValue(calcBonusArmorFromCombatLevel(), sync);
    }

    public void updateTypeBonuses(boolean sync)
    {
        BlocklingType type = blockling.getBlocklingType();
        maxHealthTypeModifier.setValue(type.getBonusHealth(), sync);
        damageTypeModifier.setValue(type.getBonusDamage(), sync);
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


    public double getAttackDamage() { return blockling.getAttribute(Attributes.ATTACK_DAMAGE).getValue(); }
    public double getArmour() { return blockling.getAttribute(Attributes.ARMOR).getValue(); }
    public double getMovementSpeed() { return blockling.getAttribute(Attributes.MOVEMENT_SPEED).getValue(); }



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
