package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EquipmentInventoryMessage implements IMessage
{
    int index;
    ItemStack stack;
    int entityId;

    private EquipmentInventoryMessage() {}
    public EquipmentInventoryMessage(int index, ItemStack stack, int entityId)
    {
        this.index = index;
        this.stack = stack;
        this.entityId = entityId;
    }

    public static void encode(EquipmentInventoryMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.index);
        buf.writeItem(msg.stack);
        buf.writeInt(msg.entityId);
    }

    public static EquipmentInventoryMessage decode(PacketBuffer buf)
    {
        EquipmentInventoryMessage msg = new EquipmentInventoryMessage();
        msg.index = buf.readInt();
        msg.stack = buf.readItem();
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

            blockling.getEquipment().setItem(index, stack);
        });
    }
}