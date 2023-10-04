package com.willr27.blocklings.entity.blockling.attribute.attributes.numbers;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.IModifier;
import com.willr27.blocklings.entity.blockling.attribute.ModifiableAttribute;
import com.willr27.blocklings.entity.blockling.attribute.Operation;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A modifiable float attribute.
 */
public class ModifiableFloatAttribute extends ModifiableNumberAttribute<Float>
{
    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialBaseValue the initial base value.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     * @param modifiers the initial list of modifiers associated with the attribute.
     */
    public ModifiableFloatAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, float initialBaseValue, @Nullable Function<Float, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled, @Nonnull IModifier<Float>... modifiers)
    {
        super(id, key, blockling, initialBaseValue, displayStringValueFunction, displayStringNameSupplier, isEnabled, modifiers);
    }

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag attributeTag)
    {
        attributeTag.putFloat("base_value", baseValue);

        return super.writeToNBT(attributeTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag attributeTag, @Nonnull Version tagVersion)
    {
        super.readFromNBT(attributeTag, tagVersion);

        baseValue = attributeTag.getFloat("base_value");
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
    public void calculate()
    {
        value = 0.0f;
        float tempBase = baseValue;
        boolean end = false;

        for (IModifier<Float> modifier : getEnabledModifiers())
        {
            if (modifier.getOperation() == Operation.ADD)
            {
                value += modifier.getValue();
            }
            else if (modifier.getOperation() == Operation.MULTIPLY_BASE)
            {
                tempBase *= modifier.getValue();
            }
            else if (modifier.getOperation() == Operation.MULTIPLY_TOTAL)
            {
                if (!end)
                {
                    value += tempBase;
                    end = true;
                }

                value *= modifier.getValue();
            }
        }

        if (!end)
        {
            value += tempBase;
        }

        onValueChanged();
    }

    @Override
    protected void setValue(Float value, boolean sync)
    {
        // Let calculate handle setting the value on client/sever
    }

    @Override
    public void incrementBaseValue(Float amount, boolean sync)
    {
        setBaseValue(baseValue + amount, sync);
    }

    @Override
    public void setBaseValue(Float baseValue, boolean sync)
    {
        this.baseValue = baseValue;

        calculate();

        if (sync)
        {
            new BaseValueMessage(blockling, blockling.getStats().attributes.indexOf(this), baseValue).sync();
        }
    }

    /**
     * The message used to sync the base value of the attribute to the client/server.
     */
    public static class BaseValueMessage extends ModifiableAttribute.BaseValueMessage<Float, BaseValueMessage>
    {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public BaseValueMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param value the base value of the attribute.
         */
        public BaseValueMessage(@Nonnull BlocklingEntity blockling, int index, float value)
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
