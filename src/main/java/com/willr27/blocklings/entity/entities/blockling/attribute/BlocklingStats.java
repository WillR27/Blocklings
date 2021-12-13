package com.willr27.blocklings.entity.entities.blockling.attribute;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlocklingStats
{
    public final List<Attribute> attributes = new ArrayList<>();
    public final List<AttributeModifier> modifiers = new ArrayList<>();
    public final List<Attribute> levels = new ArrayList<>();

    public final Attribute hand;

    public final Attribute combatInterval;
    public final AttributeModifier combatIntervalLevelModifier;
    public final AttributeModifier combatIntervalToolModifier;
    public final Attribute miningSpeed;
    public final Attribute woodcuttingInterval;
    public final AttributeModifier woodcuttingIntervalLevelModifier;
    public final AttributeModifier woodcuttingIntervalToolModifier;
    public final Attribute farmingInterval;
    public final AttributeModifier farmingIntervalLevelModifier;
    public final AttributeModifier farmingIntervalToolModifier;

    public final Attribute combatLevel;
    public final Attribute miningLevel;
    public final Attribute woodcuttingLevel;
    public final Attribute farmingLevel;

    public final Attribute combatXp;
    public final Attribute miningXp;
    public final Attribute woodcuttingXp;
    public final Attribute farmingXp;

    public final Attribute skillPoints;

    public final Attribute miningRange;
    public final Attribute miningRangeSq;
    public final Attribute woodcuttingRange;
    public final Attribute woodcuttingRangeSq;
    public final Attribute farmingRange;
    public final Attribute farmingRangeSq;

    public final Attribute maxHealth;
    public final AttributeModifier maxHealthCombatLevelModifier;
    public final AttributeModifier maxHealthTypeModifier;
    public final Attribute damage;
    public final AttributeModifier damageCombatLevelModifier;
    public final AttributeModifier damageTypeModifier;
    public final Attribute armour;
    public final AttributeModifier armourCombatLevelModifier;
    public final AttributeModifier armourTypeModifier;
    public final Attribute movementSpeed;
    public final AttributeModifier movementSpeedTypeModifier;

    public final BlocklingEntity blockling;
    public final World world;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.world = blockling.level;

        hand = createAttribute("hand", BlocklingHand.NONE.ordinal());

        combatInterval = createAttribute("combat_interval", 5.0f);
        combatIntervalLevelModifier = createAttributeModifier(combatInterval, "combat_interval_level", 0.0f, AttributeModifier.Operation.ADD);
        combatIntervalToolModifier = createAttributeModifier(combatInterval, "combat_interval_tool", 1.0f, AttributeModifier.Operation.MULTIPLY_TOTAL);

        // Default mining speed for an item/hand is 1.0f
        // A wooden pickaxe is 2.0f
        // A diamond pickaxe is 8.0f
        miningSpeed = createAttribute("mining_speed", 1.0f);

        woodcuttingInterval = createAttribute("woodcutting_interval", 10.0f);
        woodcuttingIntervalLevelModifier = createAttributeModifier(woodcuttingInterval, "woodcutting_interval_level", 0.0f, AttributeModifier.Operation.ADD);
        woodcuttingIntervalToolModifier = createAttributeModifier(woodcuttingInterval, "woodcutting_interval_tool", 1.0f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        farmingInterval = createAttribute("farming_interval", 10.0f);
        farmingIntervalLevelModifier = createAttributeModifier(farmingInterval, "farming_interval_level", 0.0f, AttributeModifier.Operation.ADD);
        farmingIntervalToolModifier = createAttributeModifier(farmingInterval, "farming_interval_tool", 1.0f, AttributeModifier.Operation.MULTIPLY_TOTAL);

        combatLevel = createAttribute("combat_level", 10);
        combatLevel.setOnCalculate(() -> { combatIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) combatLevel.getFloat()), false); updateCombatLevelBonuses(false); });
        levels.add(combatLevel);
        miningLevel = createAttribute("mining_level", 10);
        miningLevel.setOnCalculate(() -> miningSpeed.setBaseValue(calcBreakSpeedFromLevel(miningLevel.getInt()), false));
        levels.add(miningLevel);
        woodcuttingLevel = createAttribute("woodcutting_level", 10);
        woodcuttingLevel.setOnCalculate(() -> { woodcuttingIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) woodcuttingLevel.getFloat()), false); });
        levels.add(woodcuttingLevel);
        farmingLevel = createAttribute("farming_level", 10);
        farmingLevel.setOnCalculate(() -> { farmingIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) farmingLevel.getFloat()), false); });
        levels.add(farmingLevel);

        combatXp = createAttribute("combat_xp", getXpUntilNextLevel(combatLevel.getInt()));
        combatXp.setOnCalculate(() -> { checkForLevelUp(false); });
        miningXp = createAttribute("mining_xp", getXpUntilNextLevel(miningLevel.getInt()));
        miningXp.setOnCalculate(() -> { checkForLevelUp(false); });
        woodcuttingXp = createAttribute("woodcutting_xp", getXpUntilNextLevel(woodcuttingLevel.getInt()));
        woodcuttingXp.setOnCalculate(() -> { checkForLevelUp(false); });
        farmingXp = createAttribute("farming_xp", getXpUntilNextLevel(farmingLevel.getInt()));
        farmingXp.setOnCalculate(() -> { checkForLevelUp(false); });

        skillPoints = createAttribute("skill_points", 50.0f);

        miningRange = createAttribute("mining_range", 2.5f);
        miningRangeSq = createAttribute("mining_range_sq", miningRange.getFloat() * miningRange.getFloat());
        miningRange.setOnCalculate(() -> { miningRangeSq.setBaseValue(miningRange.getFloat() * miningRange.getFloat(), false); });
        woodcuttingRange = createAttribute("woodcutting_range", 2.5f);
        woodcuttingRangeSq = createAttribute("woodcutting_range_sq", woodcuttingRange.getFloat() * woodcuttingRange.getFloat());
        woodcuttingRange.setOnCalculate(() -> { woodcuttingRangeSq.setBaseValue(woodcuttingRange.getFloat() * woodcuttingRange.getFloat(), false); });
        farmingRange = createAttribute("farming_range", 2.5f);
        farmingRangeSq = createAttribute("farming_range_sq", farmingRange.getFloat() * farmingRange.getFloat());
        farmingRange.setOnCalculate(() -> { farmingRangeSq.setBaseValue(farmingRange.getFloat() * farmingRange.getFloat(), false); });

        maxHealth = createAttribute("max_health", 10.0f);
        maxHealth.setOnCalculate(() -> { Objects.requireNonNull(blockling.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(maxHealth.getFloat()); updateHealth(); });
        maxHealthCombatLevelModifier = createAttributeModifier(maxHealth, "max_health_combat_level", 0.0f, AttributeModifier.Operation.ADD);
        maxHealthTypeModifier  = createAttributeModifier(maxHealth, "max_health_type", 0.0f, AttributeModifier.Operation.ADD);
        damage = createAttribute("damage", 1.0f);
        damage.setOnCalculate(() -> { Objects.requireNonNull(blockling.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(damage.getFloat()); });
        damageCombatLevelModifier = createAttributeModifier(damage, "damage_combat_level", 0.0f, AttributeModifier.Operation.ADD);
        damageTypeModifier = createAttributeModifier(damage, "damage_type", 0.0f, AttributeModifier.Operation.ADD);
        armour = createAttribute("armour", 2.0f);
        armour.setOnCalculate(() -> { Objects.requireNonNull(blockling.getAttribute(Attributes.ARMOR)).setBaseValue(armour.getFloat()); });
        armourCombatLevelModifier = createAttributeModifier(armour, "armour_combat_level", 0.0f, AttributeModifier.Operation.ADD);
        armourTypeModifier = createAttributeModifier(armour, "armour_type", 0.0f, AttributeModifier.Operation.ADD);
        movementSpeed = createAttribute("movement_speed", 0.3f);
        movementSpeed.setOnCalculate(() -> { Objects.requireNonNull(blockling.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(movementSpeed.getFloat()); });
        movementSpeedTypeModifier = createAttributeModifier(movementSpeed, "movement_speed_type", 0.0f, AttributeModifier.Operation.ADD);
    }

    public Attribute createAttribute(String name, float baseValue)
    {
        Attribute attribute = new Attribute(blockling, name, baseValue);
        attributes.add(attribute);

        return attribute;
    }

    public AttributeModifier createAttributeModifier(Attribute attribute, String name, float value, AttributeModifier.Operation operation)
    {
        AttributeModifier modifier = new AttributeModifier(attribute, name, value, operation);
        attribute.addModifier(modifier);
        modifiers.add(modifier);

        return modifier;
    }

    public void init()
    {
        for (Attribute attribute : attributes)
        {
            attribute.calculateValue();
        }

        blockling.setHealth(blockling.getMaxHealth());

//        updateCombatLevelBonuses(false);
//        updateTypeBonuses(false);
//        updateToolModifiers(false);
    }

    public void writeToNBT(CompoundNBT c)
    {
        CompoundNBT attributesNBT = new CompoundNBT();

        for (Attribute attribute : attributes)
        {
            attributesNBT.putFloat(attribute.key, attribute.getBaseValue());
        }

        c.put("attributes", attributesNBT);

        CompoundNBT modifiersNBT = new CompoundNBT();

        for (AttributeModifier modifier : modifiers)
        {
            modifiersNBT.putFloat(modifier.key, modifier.getValue());
        }

        c.put("modifiers", modifiersNBT);
    }

    public void readFromNBT(CompoundNBT c)
    {
        CompoundNBT attributesNBT = (CompoundNBT) c.get("attributes");

        for (Attribute attribute : attributes)
        {
            attribute.setBaseValue(attributesNBT.getFloat(attribute.key), false);
        }

        CompoundNBT modifiersNBT = (CompoundNBT) c.get("modifiers");

        for (AttributeModifier modifier : modifiers)
        {
            modifier.setValue(modifiersNBT.getFloat(modifier.key), false);
        }
    }

    public void encode(PacketBuffer buf)
    {
        for (Attribute attribute : attributes)
        {
            buf.writeFloat(attribute.getBaseValue());
        }

        for (AttributeModifier modifier : modifiers)
        {
            buf.writeFloat(modifier.getValue());
        }
    }

    public void decode(PacketBuffer buf)
    {
        for (Attribute attribute : attributes)
        {
            attribute.setBaseValue(buf.readFloat(), false);
        }

        for (AttributeModifier modifier : modifiers)
        {
            modifier.setValue(buf.readFloat(), false);
        }
    }

    public static int getXpUntilNextLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    public void checkForLevelUp(boolean sync)
    {
        int combatLevel = this.combatLevel.getInt();
        int combatXp = this.combatXp.getInt();
        int combatXpReq = getXpUntilNextLevel(combatLevel);
        if (combatXp >= combatXpReq)
        {
            this.combatLevel.setBaseValue(combatLevel + 1, sync);
            this.combatXp.setBaseValue(combatXp - combatXpReq, sync);
        }

        int miningLevel = this.miningLevel.getInt();
        int miningXp = this.miningXp.getInt();
        int miningXpReq = getXpUntilNextLevel(miningLevel);
        if (miningXp >= miningXpReq)
        {
            this.miningLevel.setBaseValue(miningLevel + 1, sync);
            this.miningXp.setBaseValue(miningXp - miningXpReq, sync);
        }

        int woodcuttingLevel = this.woodcuttingLevel.getInt();
        int woodcuttingXp = this.woodcuttingXp.getInt();
        int woodcuttingXpReq = getXpUntilNextLevel(woodcuttingLevel);
        if (woodcuttingXp >= woodcuttingXpReq)
        {
            this.woodcuttingLevel.setBaseValue(woodcuttingLevel + 1, sync);
            this.woodcuttingXp.setBaseValue(woodcuttingXp - woodcuttingXpReq, sync);
        }

        int farmingLevel = this.farmingLevel.getInt();
        int farmingXp = this.farmingXp.getInt();
        int farmingXpReq = getXpUntilNextLevel(farmingLevel);
        if (farmingXp >= farmingXpReq)
        {
            this.farmingLevel.setBaseValue(farmingLevel + 1, sync);
            this.farmingXp.setBaseValue(farmingXp - farmingXpReq, sync);
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

    public void updateToolModifiers(boolean sync)
    {
        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        BlocklingHand pickaxeHand = blockling.getEquipment().findHandToolEquipped(ToolType.PICKAXE);
        BlocklingHand axeHand = blockling.getEquipment().findHandToolEquipped(ToolType.AXE);
        BlocklingHand hoeHand = blockling.getEquipment().findHandToolEquipped(ToolType.HOE);
    }

    private float calcBreakSpeedFromLevel(int level)
    {
        return level / 10.0f;
    }

    private float calcBonusHealthFromCombatLevel()
    {
        return (float) (3.0f * Math.log(combatLevel.getFloat()));
    }

    private float calcBonusDamageFromCombatLevel()
    {
        return (float) (2.0f * Math.log(combatLevel.getFloat()));
    }

    private float calcBonusArmorFromCombatLevel()
    {
        return (float) (0.5f * Math.log(combatLevel.getFloat()));
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



    public Attribute getAttribute(String name)
    {
        for (Attribute attribute : attributes)
        {
            if (attribute.key.equals(name))
            {
                return attribute;
            }
        }

        return null;
    }

    public Attribute getLevel(Level level)
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