package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.ModifiableAttribute;
import com.willr27.blocklings.attribute.modifier.AttributeModifier;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ModifiableIntAttribute extends ModifiableAttribute<Integer>
{
    private int baseValue;
    private int value;

    public ModifiableIntAttribute(String id, String key, BlocklingEntity blockling, int baseValue)
    {
        super(id, key, blockling);
        this.baseValue = baseValue;
        this.value = baseValue;
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = new CompoundNBT();

        attributeTag.putInt("base_value", baseValue);

        tag.put(id.toString(), attributeTag);
    }

    @Override
    public void readFromNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = (CompoundNBT) tag.get(id.toString());

        if (attributeTag != null)
        {
            baseValue = attributeTag.getInt("base_value");
        }
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeInt(value);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        value = buf.readInt();
    }

    @Override
    public void calculate()
    {
        value = 0;
        int tempBase = baseValue;
        boolean end = false;

        for (AttributeModifier<Integer> modifier : modifiers)
        {
            if (modifier.operation == AttributeModifier.Operation.ADD)
            {
                value += modifier.value;
            }
            else if (modifier.operation == AttributeModifier.Operation.MULTIPLY_BASE)
            {
                tempBase *= modifier.value;
            }
            else if (modifier.operation == AttributeModifier.Operation.MULTIPLY_TOTAL)
            {
                if (!end)
                {
                    value += tempBase;
                    end = true;
                }

                value *= modifier.value;
            }
        }

        if (!end)
        {
            value += tempBase;
        }

        updateCallbacks.forEach(floatConsumer -> floatConsumer.accept(value));
    }

    @Override
    public Integer getBaseValue()
    {
        return baseValue;
    }

    @Override
    public void incBaseValue(Integer amount)
    {
        incBaseValue(amount, true);
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
            NetworkHandler.sync(world, new BaseValueMessage(blockling.getStats().attributes.indexOf(this), baseValue, blockling.getId()));
        }
    }

    @Override
    public Integer getValue()
    {
        return value;
    }

    public static class BaseValueMessage implements IMessage
    {
        public int index;
        public int baseValue;
        public int entityId;

        private BaseValueMessage() {}
        public BaseValueMessage(int index, int baseValue, int entityId)
        {
            this.index = index;
            this.baseValue = baseValue;
            this.entityId = entityId;
        }

        public static void encode(BaseValueMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeInt(msg.baseValue);
            buf.writeInt(msg.entityId);
        }

        public static BaseValueMessage decode(PacketBuffer buf)
        {
            BaseValueMessage msg = new BaseValueMessage();
            msg.index = buf.readInt();
            msg.baseValue = buf.readInt();
            msg.entityId = buf.readInt();

            return msg;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                NetworkEvent.Context context = ctx.get();
                boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

                PlayerEntity player = isClient ? Minecraft.getInstance().player : ctx.get().getSender();
                BlocklingEntity blockling = (BlocklingEntity) player.level.getEntity(entityId);

                ModifiableIntAttribute attribute = (ModifiableIntAttribute) blockling.getStats().attributes.get(index);
                attribute.setBaseValue(baseValue, !isClient);
            });
        }
    }
}
