package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TaskRemoveMessage implements IMessage
{
    UUID goalId;
    int entityId;

    private TaskRemoveMessage() {}
    public TaskRemoveMessage(UUID goalId, int entityId)
    {
        this.goalId = goalId;
        this.entityId = entityId;
    }

    public static void encode(TaskRemoveMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.goalId);
        buf.writeInt(msg.entityId);
    }

    public static TaskRemoveMessage decode(PacketBuffer buf)
    {
        TaskRemoveMessage msg = new TaskRemoveMessage();
        msg.goalId = buf.readUUID();
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

            blockling.getTasks().removeTask(goalId, !isClient);
        });
    }
}