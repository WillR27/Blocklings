package com.willr27.blocklings.attribute;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Attribute<T>
{
    public final UUID id;
    public final String key;
    public final BlocklingEntity blockling;
    public final World world;
    public final Supplier<String> displayStringSupplier;

    protected final List<Consumer<T>> updateCallbacks = new ArrayList<>();

    public Attribute(String id, String key, BlocklingEntity blockling)
    {
        this(id, key, blockling, null);
    }

    public Attribute(String id, String key, BlocklingEntity blockling, Supplier<String> displayStringSupplier)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.blockling = blockling;
        this.world = blockling.level;
        this.displayStringSupplier = displayStringSupplier == null ? () -> createTranslation("name").getString() : displayStringSupplier;
    }

    public abstract void writeToNBT(CompoundNBT tag);

    public abstract void readFromNBT(CompoundNBT tag);

    public abstract void encode(PacketBuffer buf);

    public abstract void decode(PacketBuffer buf);

    public abstract T getValue();

    public void callUpdateCallbacks()
    {
        updateCallbacks.forEach(floatConsumer -> floatConsumer.accept(getValue()));
    }

    public void addUpdateCallback(Consumer<T> callback)
    {
        updateCallbacks.add(callback);
    }

    public String formatValue(String format)
    {
        return String.format(format, getValue());
    }

    public TranslationTextComponent createTranslation(String key, Object... objects)
    {
        return new AttributeTranslationTextComponent(this.key + "." + key, objects);
    }

    public static class AttributeTranslationTextComponent extends BlocklingsTranslationTextComponent
    {
        public AttributeTranslationTextComponent(String key)
        {
            super("attribute." + key);
        }

        public AttributeTranslationTextComponent(String key, Object... objects)
        {
            super("attribute." + key, objects);
        }
    }
}
