package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.Attribute;
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

public class IntAttribute extends Attribute<Integer>
{
    private int value;

    public IntAttribute(String id, String key, BlocklingEntity blockling, int value)
    {
        super(id, key, blockling);
        this.value = value;
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = new CompoundNBT();

        attributeTag.putInt("value", value);

        tag.put(id.toString(), attributeTag);
    }

    @Override
    public void readFromNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = (CompoundNBT) tag.get(id.toString());

        if (attributeTag != null)
        {
            value = attributeTag.getInt("value");
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
    public Integer getValue()
    {
        return value;
    }

    public void incValue(int amount)
    {
        incValue(amount, true);
    }

    public void incValue(int amount, boolean sync)
    {
        setValue(value + amount, sync);
    }

    public void setValue(int value)
    {
        setValue(value, true);
    }

    public void setValue(int value, boolean sync)
    {
        this.value = value;

        updateCallbacks.forEach(consumer -> consumer.accept(value));

        if (sync)
        {
            NetworkHandler.sync(blockling.level, new IntAttribute.ValueMessage(blockling.getStats().attributes.indexOf(this), value, blockling.getId()));
        }
    }

    public static class ValueMessage implements IMessage
    {
        public int index;
        public int value;
        public int entityId;

        private ValueMessage() {}
        public ValueMessage(int index, int value, int entityId)
        {
            this.index = index;
            this.value = value;
            this.entityId = entityId;
        }

        public static void encode(ValueMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeInt(msg.value);
            buf.writeInt(msg.entityId);
        }

        public static ValueMessage decode(PacketBuffer buf)
        {
            ValueMessage msg = new ValueMessage();
            msg.index = buf.readInt();
            msg.value = buf.readInt();
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

                IntAttribute attribute = (IntAttribute) blockling.getStats().attributes.get(index);
                attribute.setValue(value, !isClient);
            });
        }
    }
}
