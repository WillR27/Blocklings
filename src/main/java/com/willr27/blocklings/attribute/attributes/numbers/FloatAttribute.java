package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class FloatAttribute extends Attribute<Float>
{
    protected float value;

    public FloatAttribute(String id, String key, BlocklingEntity blockling, float value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, displayStringValueSupplier, displayStringNameSupplier);
        this.value = value;
    }

    @Override
    public void writeToNBT(CompoundNBT attributeTag)
    {
        super.writeToNBT(attributeTag);

        attributeTag.putFloat("value", value);
    }

    @Override
    public void readFromNBT(CompoundNBT attributeTag)
    {
        super.readFromNBT(attributeTag);

        value = attributeTag.getFloat("value");
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeFloat(value);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        super.decode(buf);

        value = buf.readFloat();
    }

    @Override
    public Float getValue()
    {
        return value;
    }

    public void incValue(float amount)
    {
        incValue(amount, true);
    }

    public void incValue(float amount, boolean sync)
    {
        setValue(value + amount, sync);
    }

    public void setValue(float value)
    {
        setValue(value, true);
    }

    public void setValue(float value, boolean sync)
    {
        this.value = value;

        callUpdateCallbacks();

        if (sync)
        {
            new ValueMessage(blockling, blockling.getStats().attributes.indexOf(this), value).sync();
        }
    }

    public static class ValueMessage extends BlocklingMessage<ValueMessage>
    {
        /**
         * The index of the attribute.
         */
        private int index;

        /**
         * The value of the attribute.
         */
        private float value;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ValueMessage()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param value the value of the attribute.
         */
        public ValueMessage(@Nonnull BlocklingEntity blockling, int index, float value)
        {
            super(blockling);
            this.index = index;
            this.value = value;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            buf.writeFloat(value);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
            value = buf.readFloat();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            ((FloatAttribute) blockling.getStats().attributes.get(index)).setValue(value, false);
        }
    }
}
