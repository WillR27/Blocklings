package com.willr27.blocklings.entity.entities.blockling.attribute;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AttributeModifier
{
    public final Attribute attribute;
    public final String key;
    public float value;
    public final Operation operation;

    public AttributeModifier(Attribute attribute, String key, float value, Operation operation)
    {
        this.attribute = attribute;
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public float getValue()
    {
        return value;
    }

    public void setValue(float value)
    {
        setValue(value, true);
    }

    public void setValue(float value, boolean sync)
    {
        this.value = value;

        attribute.calculateValue();

        if (sync)
        {
            NetworkHandler.sync(attribute.world, new BlocklingAttributeModifierValueMessage(attribute.blockling.getStats().attributes.indexOf(attribute), attribute.indexOf(this), value, attribute.blockling.getId()));
        }
    }

    public enum Operation
    {
        ADD,
        MULTIPLY_BASE,
        MULTIPLY_TOTAL
    }

    public static class BlocklingAttributeModifierValueMessage implements IMessage
    {
        int attributeIndex;
        int attributeModifierIndex;
        float value;
        int entityId;

        private BlocklingAttributeModifierValueMessage() {}
        public BlocklingAttributeModifierValueMessage(int attributeIndex, int attributeModifierIndex, float value, int entityId)
        {
            this.attributeIndex = attributeIndex;
            this.attributeModifierIndex = attributeModifierIndex;
            this.value = value;
            this.entityId = entityId;
        }

        public static void encode(AttributeModifier.BlocklingAttributeModifierValueMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.attributeIndex);
            buf.writeInt(msg.attributeModifierIndex);
            buf.writeFloat(msg.value);
            buf.writeInt(msg.entityId);
        }

        public static AttributeModifier.BlocklingAttributeModifierValueMessage decode(PacketBuffer buf)
        {
            AttributeModifier.BlocklingAttributeModifierValueMessage msg = new AttributeModifier.BlocklingAttributeModifierValueMessage();
            msg.attributeIndex = buf.readInt();
            msg.attributeModifierIndex = buf.readInt();
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
                if (player != null)
                {
                    BlocklingEntity blockling = (BlocklingEntity) player.level.getEntity(entityId);
                    if (blockling != null)
                    {
                        AttributeModifier modifier = blockling.getStats().attributes.get(attributeIndex).getModifier(attributeModifierIndex);
                        modifier.setValue(value, !isClient);
                    }
                }
            });
        }
    }
}
