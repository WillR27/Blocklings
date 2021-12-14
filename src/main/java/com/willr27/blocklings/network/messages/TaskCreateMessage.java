package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TaskCreateMessage implements IMessage
{
    UUID taskTypeId;
    UUID taskId = null;
    int entityId;

    private TaskCreateMessage() {}
    public TaskCreateMessage(UUID taskTypeId, UUID taskId, int entityId)
    {
        this.taskTypeId = taskTypeId;
        this.taskId = taskId;
        this.entityId = entityId;
    }

    public static void encode(TaskCreateMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskTypeId);
        buf.writeBoolean(msg.taskId != null);
        if (msg.taskId != null) buf.writeUUID(msg.taskId);
        buf.writeInt(msg.entityId);
    }

    public static TaskCreateMessage decode(PacketBuffer buf)
    {
        TaskCreateMessage msg = new TaskCreateMessage();
        msg.taskTypeId = buf.readUUID();
        if (buf.readBoolean()) msg.taskId = buf.readUUID();
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

            blockling.getTasks().createTask(BlocklingTasks.getTaskType(taskTypeId), taskId, !isClient);
        });
    }
}