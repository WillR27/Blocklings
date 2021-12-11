package com.blocklings.abilities;

import java.util.List;
import java.util.UUID;

public enum AbilityGroupType
{
    GENERAL("685c18ac-b9f9-4355-8e74-4ed8bcb3b687", "General", AbilityHelper.generalAbilities),
    COMBAT("2c1a0858-75ad-49c0-b19c-37819376e908", "Combat", AbilityHelper.combatAbilities),
    MINING("62c97722-5f08-49be-b17a-cb5d4b22c37d", "Mining", AbilityHelper.miningAbilities),
    WOODCUTTING("3745173a-3743-415b-99d0-6fdd741a23f1", "Woodcutting", AbilityHelper.woodcuttingAbilities),
    FARMING("96683da3-fd61-4530-8e04-a7a2be1d776a", "Farming", AbilityHelper.farmingAbilities);

    public UUID uuid;
    public String name;
    public List<Ability> abilities;

    AbilityGroupType(String uuidString, String name, List<Ability> abilities)
    {
        this.uuid = UUID.fromString(uuidString);
        this.name = name;
        this.abilities = abilities;
    }

    public static AbilityGroupType getTypeFromUUID(UUID uuid)
    {
        for (AbilityGroupType type : values())
        {
            if (type.uuid.equals(uuid))
            {
                return type;
            }
        }

        return null;
    }
}