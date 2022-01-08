package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A simple float attribute.
 */
public class FloatAttribute extends Attribute<Float>
{
    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialValue the initial value of the attribute.
     * @param displayStringValueSupplier the supplier used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public FloatAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, float initialValue, @Nullable Supplier<String> displayStringValueSupplier, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        super(id, key, blockling, displayStringValueSupplier, displayStringNameSupplier, isEnabled);
        this.value = initialValue;
    }

    @Override
    public void writeToNBT(@Nonnull CompoundNBT attributeTag)
    {
        super.writeToNBT(attributeTag);

        attributeTag.putFloat("value", value);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT attributeTag)
    {
        super.readFromNBT(attributeTag);

        value = attributeTag.getFloat("value");
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeFloat(value);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        value = buf.readFloat();
    }

    @Override
    public Float getValue()
    {
        return value;
    }

    /**
     * Increments the value by the given amount.
     * Syncs to the client/server.
     *
     * @param amount the amount to increment the value by.
     */
    public void incrementValue(float amount)
    {
        incrementValue(amount, true);
    }

    /**
     * Increments the value by the given amount.
     * Syncs to the client/server if sync is true.
     *
     * @param amount the amount to increment the value by.
     * @param sync whether to sync to the client/server.
     */
    public void incrementValue(float amount, boolean sync)
    {
        setValue(value + amount, sync);
    }

    @Override
    public void setValue(Float value)
    {
        setValue(value, true);
    }

    @Override
    public void setValue(Float value, boolean sync)
    {
        this.value = value;

        callUpdateCallbacks();

        if (sync)
        {
            new ValueMessage(blockling, blockling.getStats().attributes.indexOf(this), value).sync();
        }
    }

    /**
     * The message used to sync the attribute value to the client/server.
     */
    public static class ValueMessage extends Attribute.ValueMessage<Float, ValueMessage>
    {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public ValueMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param value the value of the attribute.
         */
        public ValueMessage(@Nonnull BlocklingEntity blockling, int index, float value)
        {
            super(blockling, index, value);
        }

        @Override
        protected void encodeValue(@Nonnull PacketBuffer buf)
        {
            buf.writeFloat(value);
        }

        @Override
        protected void decodeValue(@Nonnull PacketBuffer buf)
        {
            value = buf.readFloat();
        }
    }
}
