package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.entities.blockling.goal.Task;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TaskSetTypeMessage implements IMessage
{
    UUID taskId;
    UUID taskTypeId;
    int entityId;

    private TaskSetTypeMessage() {}
    public TaskSetTypeMessage(UUID taskId, UUID taskTypeId, int entityId)
    {
        this.taskId = taskId;
        this.taskTypeId = taskTypeId;
        this.entityId = entityId;
    }

    public static void encode(TaskSetTypeMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskId);
        buf.writeUUID(msg.taskTypeId);
        buf.writeInt(msg.entityId);
    }

    public static TaskSetTypeMessage decode(PacketBuffer buf)
    {
        TaskSetTypeMessage msg = new TaskSetTypeMessage();
        msg.taskId = buf.readUUID();
        msg.taskTypeId = buf.readUUID();
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

            Task task = blockling.getTasks().getTask(taskId);
            task.setType(BlocklingTasks.getTaskType(taskTypeId), !isClient);
        });
    }
}