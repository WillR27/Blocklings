package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.ModifiableAttribute;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
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
    public void incBaseValue(Integer amount, boolean sync)
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

    public static class BaseValueMessage extends BlocklingMessage<BaseValueMessage>
    {
        /**
         * The index of the attribute.
         */
        private int index;

        /**
         * The base value of the attribute.
         */
        private int baseValue;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public BaseValueMessage()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param baseValue the base value of the attribute.
         */
        public BaseValueMessage(@Nonnull BlocklingEntity blockling, int index, int baseValue)
        {
            super(blockling);
            this.index = index;
            this.baseValue = baseValue;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            buf.writeInt(baseValue);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
            baseValue = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            ((ModifiableIntAttribute) blockling.getStats().attributes.get(index)).setBaseValue(baseValue, false);
        }
    }
}
