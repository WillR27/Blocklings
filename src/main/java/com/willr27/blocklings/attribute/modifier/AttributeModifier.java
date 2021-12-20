package com.willr27.blocklings.attribute.modifier;

import com.willr27.blocklings.attribute.ModifiableAttribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
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
    public final Supplier<String> displayStringSupplier;

    public AttributeModifier(String id, String key, ModifiableAttribute<T> attribute, T value, Operation operation)
    {
        this(id, key, attribute, value, operation, null);
    }

    public AttributeModifier(String id, String key, ModifiableAttribute<T> attribute, T value, Operation operation, Supplier<String> displayStringSupplier)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.attribute = attribute;
        this.value = value;
        this.operation = operation;
        this.blockling = attribute.blockling;
        this.displayStringSupplier = displayStringSupplier == null ? () -> createTranslation("name").getString() : displayStringSupplier;
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

    public String formatValue(String format)
    {
        return String.format(format, getValue());
    }

    public TranslationTextComponent createTranslation(String key, Object... objects)
    {
        return new Attribute.AttributeTranslationTextComponent(this.key + "." + key, objects);
    }

    public static class AttributeModifierTranslationTextComponent extends BlocklingsTranslationTextComponent
    {
        public AttributeModifierTranslationTextComponent(String key)
        {
            super("attribute.modifier." + key);
        }

        public AttributeModifierTranslationTextComponent(String key, Object... objects)
        {
            super("attribute.modifier" + key, objects);
        }
    }

    public enum Operation
    {
        ADD,
        MULTIPLY_BASE,
        MULTIPLY_TOTAL
    }
}
