package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.ModifiableAttribute;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ModifiableIntAttribute extends ModifiableAttribute<Integer>
{
    public ModifiableIntAttribute(String id, String key, BlocklingEntity blockling, int baseValue, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, baseValue, displayStringValueSupplier, displayStringNameSupplier);
    }

    @Override
    public void writeToNBT(CompoundNBT attributeTag)
    {
        super.writeToNBT(attributeTag);

        attributeTag.putInt("base_value", baseValue);
    }

    @Override
    public void readFromNBT(CompoundNBT attributeTag)
    {
        super.readFromNBT(attributeTag);

        baseValue = attributeTag.getInt("base_value");
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(value);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        super.decode(buf);

        value = buf.readInt();
    }

    @Override
    public void calculate()
    {
        value = 0;
        int tempBase = baseValue;
        boolean end = false;

        for (IModifier<Integer> modifier : getEnabledModifiers())
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

        callUpdateCallbacks();
    }

    @Override
    protected void setValue(Integer value, boolean sync)
    {
        // Let calculate handle setting the value on client/sever
    }

    @Override
    public void incrementBaseValue(Integer amount, boolean sync)
    {
        setBaseValue(baseValue + amount, sync);
    }

    @Override
    public void setBaseValue(Integer baseValue)
    {
        setBaseValue(baseValue, true);
    }

    @Override
    public void setBaseValue(Integer baseValue, boolean sync)
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
    public static class BaseValueMessage extends ModifiableAttribute.BaseValueMessage<Integer, ModifiableIntAttribute.BaseValueMessage>
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
        public BaseValueMessage(@Nonnull BlocklingEntity blockling, int index, int value)
        {
            super(blockling, index, value);
        }

        @Override
        protected void encodeValue(@Nonnull PacketBuffer buf)
        {
            buf.writeInt(value);
        }

        @Override
        protected void decodeValue(@Nonnull PacketBuffer buf)
        {
            value = buf.readInt();
        }
    }
}
