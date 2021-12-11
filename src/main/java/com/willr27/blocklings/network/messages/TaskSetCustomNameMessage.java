package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.Task;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TaskSetCustomNameMessage implements IMessage
{
    UUID taskId;
    String customName;
    int entityId;

    private TaskSetCustomNameMessage() {}
    public TaskSetCustomNameMessage(UUID taskId, String customName, int entityId)
    {
        this.taskId = taskId;
        this.customName = customName;
        this.entityId = entityId;
    }

    public static void encode(TaskSetCustomNameMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskId);
        PacketBufferUtils.writeString(buf, msg.customName);
        buf.writeInt(msg.entityId);
    }

    public static TaskSetCustomNameMessage decode(PacketBuffer buf)
    {
        TaskSetCustomNameMessage msg = new TaskSetCustomNameMessage();
        msg.taskId = buf.readUUID();
        msg.customName = PacketBufferUtils.readString(buf);
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
            task.setCustomName(customName, !isClient);
        });
    }
}