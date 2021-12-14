package com.willr27.blocklings.attribute.modifier;

import com.willr27.blocklings.attribute.ModifiableAttribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class AttributeModifier<T>
{
    public UUID id;
    public final String key;
    public final ModifiableAttribute<T> attribute;
    public T value;
    public final Operation operation;
    public final BlocklingEntity blockling;

    public AttributeModifier(String id, String key, ModifiableAttribute<T> attribute, T value, Operation operation)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.attribute = attribute;
        this.value = value;
        this.operation = operation;
        this.blockling = attribute.blockling;
    }

    public abstract void writeToNBT(CompoundNBT tag);

    public abstract void readFromNBT(CompoundNBT tag);

    public abstract void encode(PacketBuffer buf);

    public abstract void decode(PacketBuffer buf);

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        setValue(value, true);
    }

    public abstract void setValue(T value, boolean sync);

    public enum Operation
    {
        ADD,
        MULTIPLY_BASE,
        MULTIPLY_TOTAL
    }
}
