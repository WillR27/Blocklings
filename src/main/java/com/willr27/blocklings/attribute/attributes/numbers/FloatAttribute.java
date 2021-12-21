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

public class FloatAttribute extends Attribute<Float>
{
    protected float value;

    public FloatAttribute(String id, String key, BlocklingEntity blockling, float value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, displayStringValueSupplier, displayStringNameSupplier);
        this.value = value;
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = new CompoundNBT();

        attributeTag.putFloat("value", value);

        tag.put(id.toString(), attributeTag);
    }

    @Override
    public void readFromNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = (CompoundNBT) tag.get(id.toString());

        if (attributeTag != null)
        {
            value = attributeTag.getFloat("value");
        }
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeFloat(value);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
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
            NetworkHandler.sync(blockling.level, new ValueMessage(blockling.getStats().attributes.indexOf(this), value, blockling.getId()));
        }
    }

    public static class ValueMessage implements IMessage
    {
        public int index;
        public float value;
        public int entityId;

        private ValueMessage() {}
        public ValueMessage(int index, float value, int entityId)
        {
            this.index = index;
            this.value = value;
            this.entityId = entityId;
        }

        public static void encode(ValueMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeFloat(msg.value);
            buf.writeInt(msg.entityId);
        }

        public static ValueMessage decode(PacketBuffer buf)
        {
            ValueMessage msg = new ValueMessage();
            msg.index = buf.readInt();
            msg.value = buf.readFloat();
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

                FloatAttribute attribute = (FloatAttribute) blockling.getStats().attributes.get(index);
                attribute.setValue(value, !isClient);
            });
        }
    }
}
