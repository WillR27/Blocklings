package com.blocklings.abilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.jline.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbilityGroup
{
    public UUID uuid;
    public String groupName;
    public List<Ability> abilities = new ArrayList<Ability>();

    public AbilityGroup()
    {

    }

    public AbilityGroup(AbilityGroupType type)
    {
        this.uuid = type.uuid;
        this.groupName = type.name;

        for (Ability ability : type.abilities)
        {
            abilities.add(ability.copy());
        }
    }

    public boolean contains(Ability ability)
    {
        for (Ability testAbility : abilities)
        {
            if (testAbility.equals(ability))
            {
                return true;
            }
        }

        return false;
    }

    private Ability getMatchingAbility(Ability ability)
    {
        for (Ability testAbility : abilities)
        {
            if (testAbility.equals(ability))
            {
                return testAbility;
            }
        }

        return null;
    }

    public boolean isAbilityAcquired(Ability ability)
    {
        Ability matchingAbility = getMatchingAbility(ability);
        return matchingAbility != null && matchingAbility.state == Ability.State.ACQUIRED;
    }

    public void writeToNBT(NBTTagCompound c)
    {
        for (Ability ability : abilities)
        {
            ability.writeToNBT(c);
        }
    }

    public void initFromNBT(NBTTagCompound c)
    {
        for (Ability ability : abilities)
        {
            ability.initFromNBT(c, ability.uuid);
        }

        for (Ability ability : abilities)
        {
            for (Ability ability2 : abilities)
            {
                if (ability.parentUuid == null) continue;

                if (ability.parentUuid.equals(ability2.uuid))
                {
                    ability.parentAbility = ability2;
                }
            }
        }
    }

    public void writeToBuf(ByteBuf buf)
    {
        for (Ability ability : abilities)
        {
            buf.writeInt(ability.state.ordinal());
        }
    }

    public void readFromBuf(ByteBuf buf)
    {
        for (Ability ability : abilities)
        {
            ability.state = Ability.State.values()[buf.readInt()];
        }

        for (Ability ability : abilities)
        {
            for (Ability ability2 : abilities)
            {
                if (ability.parentUuid == null) continue;

                if (ability.parentUuid.equals(ability2.uuid))
                {
                    ability.parentAbility = ability2;
                }
            }
        }
    }
}
