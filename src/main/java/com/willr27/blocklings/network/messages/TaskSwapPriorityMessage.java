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

public class TaskSwapPriorityMessage implements IMessage
{
    UUID taskId1;
    UUID taskId2;
    int entityId;

    private TaskSwapPriorityMessage() {}
    public TaskSwapPriorityMessage(UUID taskId1, UUID taskId2, int entityId)
    {
        this.taskId1 = taskId1;
        this.taskId2 = taskId2;
        this.entityId = entityId;
    }

    public static void encode(TaskSwapPriorityMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskId1);
        buf.writeUUID(msg.taskId2);
        buf.writeInt(msg.entityId);
    }

    public static TaskSwapPriorityMessage decode(PacketBuffer buf)
    {
        TaskSwapPriorityMessage msg = new TaskSwapPriorityMessage();
        msg.taskId1 = buf.readUUID();
        msg.taskId2 = buf.readUUID();
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

            Task task1 = blockling.getTasks().getTask(taskId1);
            Task task2 = blockling.getTasks().getTask(taskId2);
            task1.swapPriority(task2, false); // TODO: Sync back to all clients
        });
    }
}