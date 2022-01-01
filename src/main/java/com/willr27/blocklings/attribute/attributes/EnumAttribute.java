package com.willr27.blocklings.attribute.attributes;

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

import java.util.function.Function;
import java.util.function.Supplier;

public class EnumAttribute<T extends Enum<?>> extends Attribute<T>
{
    private T value;
    private final Function<Integer, T> ordinalConverter;

    public EnumAttribute(String id, String key, BlocklingEntity blockling, T value, Function<Integer, T> ordinalConverter, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, displayStringValueSupplier, displayStringNameSupplier);
        this.value = value;
        this.ordinalConverter = ordinalConverter;
    }

    @Override
    public void writeToNBT(CompoundNBT attributeTag)
    {
        super.writeToNBT(attributeTag);

        attributeTag.putInt("value", value.ordinal());
    }

    @Override
    public void readFromNBT(CompoundNBT attributeTag)
    {
        super.readFromNBT(attributeTag);

        setValue(ordinalConverter.apply(attributeTag.getInt("value")), false);
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(value.ordinal());
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        super.decode(buf);

        setValue(ordinalConverter.apply(buf.readInt()), false);
    }

    @Override
    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        setValue(value, true);
    }

    public void setValue(T value, boolean sync)
    {
        this.value = value;

        callUpdateCallbacks();

        if (sync)
        {
            NetworkHandler.sync(blockling.level, new Message<T>(blockling.getStats().attributes.indexOf(this), value, blockling.getId()));
        }
    }

    public static class Message<T extends Enum<?>> implements IMessage
    {
        public int index;
        public int value;
        public int entityId;

        private Message() {}
        public Message(int index, T value, int entityId)
        {
            this.index = index;
            this.value = value.ordinal();
            this.entityId = entityId;
        }

        public static void encode(Message<Enum<?>> msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeInt(msg.value);
            buf.writeInt(msg.entityId);
        }

        public static Message<Enum<?>> decode(PacketBuffer buf)
        {
            Message<Enum<?>> msg = new Message<Enum<?>>();
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

                EnumAttribute<T> attribute = (EnumAttribute<T>) blockling.getStats().attributes.get(index);
                attribute.setValue(attribute.ordinalConverter.apply(value), !isClient);
            });
        }
    }
}
