package com.blocklings.abilities;

import net.minecraft.nbt.NBTTagCompound;
import org.jline.utils.Log;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Ability implements Cloneable
{
    public enum State
    {
        LOCKED(new Color(0x444444)),
        UNLOCKED(new Color(0xF4F4F4)),
        ACQUIRED(new Color(0xFFC409));

        public Color colour;

        State(Color colour)
        {
            this.colour = colour;
        }
    }

    public UUID uuid, parentUuid;
    public int x, y, width = 24, height = 24, iconX, iconY, shapeX, shapeY, skillPointCost;
    public String name = "Name";
    public List<Ability> conflictingAbilities = new ArrayList<>();
    public List<String> description = new ArrayList<>();
    public HashMap<String, Integer> levelRequirements = new HashMap<>();
    public State state = State.LOCKED;
    public Ability parentAbility;
    public Color highlightColour = new Color(0x036A96);

    public Ability()
    {

    }

    public Ability initInfo(String uuid, String name, String[] description, Ability parentAbility, int skillPointCost)
    {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.description = Arrays.asList(description);
        this.parentAbility = parentAbility;
        this.parentUuid = parentAbility != null ? parentAbility.uuid : null;
        this.skillPointCost = skillPointCost;
        return this;
    }

    public Ability initGui(int x, int y, int iconX, int iconY, int shapeX, int shapeY, Color highlightColour)
    {
        this.x = x;
        this.y = y;
        this.iconX = iconX;
        this.iconY = iconY;
        this.shapeX = shapeX;
        this.shapeY = shapeY;
        this.highlightColour = highlightColour;
        return this;
    }

    public Ability initConflictingAbilities(Ability[] conflictingAbilities)
    {
        this.conflictingAbilities = Arrays.asList(conflictingAbilities);
        return this;
    }

    public void addLevelRequirement(String skill, int level)
    {
        this.levelRequirements.put(skill, level);
    }

    public void initFromDefaults()
    {
        checkList(AbilityHelper.generalAbilities);
        checkList(AbilityHelper.combatAbilities);
        checkList(AbilityHelper.miningAbilities);
        checkList(AbilityHelper.woodcuttingAbilities);
        checkList(AbilityHelper.farmingAbilities);
    }

    private void checkList(List<Ability> abilities)
    {
        if (abilities.contains(this))
        {
            for (Ability ability : abilities)
            {
                if (this.equals(ability))
                {
                    this.uuid = ability.uuid;
                    this.name = ability.name;
                    this.description = ability.description;
                    this.parentUuid = ability.parentAbility != null ? ability.parentAbility.uuid : null;
                    this.x = ability.x;
                    this.y = ability.y;
                    this.iconX = ability.iconX;
                    this.iconY = ability.iconY;
                    this.shapeX = ability.shapeX;
                    this.shapeY = ability.shapeY;
                    this.skillPointCost = ability.skillPointCost;
                    this.highlightColour = ability.highlightColour;
                    this.conflictingAbilities = ability.conflictingAbilities;
                    this.levelRequirements = ability.levelRequirements;
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound c)
    {
        c.setInteger(uuid.toString() + ":StateId", state.ordinal());
    }

    public void initFromNBT(NBTTagCompound c, UUID uuid)
    {
        this.state = State.values()[c.getInteger(uuid.toString() + ":StateId")];
        initFromDefaults();
    }


    public boolean tryCycleState(List<Ability> baseList)
    {
        if (!hasConflictingAbility(baseList))
        {
            if (state.ordinal() == State.values().length - 1)
            {
                for (Ability child : getChildren(baseList))
                {
                    if (child.state.ordinal() >= state.ordinal())
                    {
                        return false;
                    }
                }

                state = State.UNLOCKED;
                return true;
            }
            else if (parentAbility == null)
            {
                state = State.values()[state.ordinal() + 1];
                return true;
            }
            else if (parentAbility.state.ordinal() > state.ordinal())
            {
                state = State.values()[state.ordinal() + 1];
                return true;
            }
        }

        return false;
    }

    public boolean hasConflictingAbility(List<Ability> baseList)
    {
        if (state == State.UNLOCKED)
        {
            for (Ability ability : baseList)
            {
                if (conflictingAbilities.contains(ability))
                {
                    if (ability.state == State.ACQUIRED)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<Ability> getChildren(List<Ability> baseList)
    {
        List<Ability> list = new ArrayList<Ability>();

        for (Ability ability : baseList)
        {
            if (this == ability.parentAbility)
            {
                list.add(ability);
            }
        }

        return list;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public Ability copy()
    {
        try
        {
            return (Ability)clone();
        }
        catch (CloneNotSupportedException e)
        {
            Log.error("Couldn't clone ability: " + this.name);
            Log.error(e.getStackTrace());
        }

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        Ability ability = (Ability)o;

        return ability.uuid.equals(this.uuid);
    }
}
