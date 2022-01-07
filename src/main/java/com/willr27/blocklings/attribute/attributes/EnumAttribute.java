package com.willr27.blocklings.attribute.attributes;

import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnumAttribute<T extends Enum<?>> extends Attribute<T>
{
    private T value;
    private final Function<Integer, T> ordinalConverter;

    public EnumAttribute(String id, String key, BlocklingEntity blockling, T value, Function<Integer, T> ordinalConverter, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, displayStringValueSupplier, displayStringNameSupplier);
        this.value = value;
        this.ordinalConverter = ordinalConverter;
    }

    @Override
    public void writeToNBT(CompoundNBT attributeTag)
    {
        super.writeToNBT(attributeTag);

        attributeTag.putInt("value", value.ordinal());
    }

    @Override
    public void readFromNBT(CompoundNBT attributeTag)
    {
        super.readFromNBT(attributeTag);

        setValue(ordinalConverter.apply(attributeTag.getInt("value")), false);
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(value.ordinal());
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        super.decode(buf);

        setValue(ordinalConverter.apply(buf.readInt()), false);
    }

    @Override
    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        setValue(value, true);
    }

    public void setValue(T value, boolean sync)
    {
        this.value = value;

        callUpdateCallbacks();

        if (sync)
        {
            new Message<T>(blockling, blockling.getStats().attributes.indexOf(this), value).sync();
        }
    }

    public static class Message<T extends Enum<?>> extends BlocklingMessage<Message<T>>
    {
        /**
         * The index of the attribute.
         */
        private int index;

        /**
         * The ordinal value of the enum.
         */
        private int value;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public Message()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param value the enum value.
         */
        public Message(@Nonnull BlocklingEntity blockling, int index, @Nonnull T value)
        {
            super(blockling);
            this.index = index;
            this.value = value.ordinal();
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            buf.writeInt(value);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
            value = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            EnumAttribute<T> attribute = (EnumAttribute<T>) blockling.getStats().attributes.get(index);
            attribute.setValue(attribute.ordinalConverter.apply(value), false);
        }
    }
}
