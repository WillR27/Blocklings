package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.Task;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TaskPriorityMessage implements IMessage
{
    UUID taskId;
    int priority;
    int entityId;

    private TaskPriorityMessage() {}
    public TaskPriorityMessage(UUID taskId, int priority, int entityId)
    {
        this.taskId = taskId;
        this.priority = priority;
        this.entityId = entityId;
    }

    public static void encode(TaskPriorityMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskId);
        buf.writeInt(msg.priority);
        buf.writeInt(msg.entityId);
    }

    public static TaskPriorityMessage decode(PacketBuffer buf)
    {
        TaskPriorityMessage msg = new TaskPriorityMessage();
        msg.taskId = buf.readUUID();
        msg.priority = buf.readInt();
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
            task.setPriority(priority, !isClient);
        });
    }
}