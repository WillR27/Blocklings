package com.willr27.blocklings.entity.blockling.attribute.attributes.numbers;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.Attribute;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple float attribute.
 */
public class FloatAttribute extends NumberAttribute<Float>
{
    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialValue the initial value of the attribute.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public FloatAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, float initialValue, @Nullable Function<Float, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        super(id, key, blockling, initialValue, displayStringValueFunction, displayStringNameSupplier, isEnabled);
    }

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag attributeTag)
    {
        attributeTag.putFloat("value", value);

        return super.writeToNBT(attributeTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag attributeTag, @Nonnull Version tagVersion)
    {
        super.readFromNBT(attributeTag, tagVersion);

        value = attributeTag.getFloat("value");
    }

    @Override
    public void encode(@Nonnull FriendlyByteBuf buf)
    {
        super.encode(buf);

        buf.writeFloat(value);
    }

    @Override
    public void decode(@Nonnull FriendlyByteBuf buf)
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

        onValueChanged();

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
        protected void encodeValue(@Nonnull FriendlyByteBuf buf)
        {
            buf.writeFloat(value);
        }

        @Override
        protected void decodeValue(@Nonnull FriendlyByteBuf buf)
        {
            value = buf.readFloat();
        }
    }
}
