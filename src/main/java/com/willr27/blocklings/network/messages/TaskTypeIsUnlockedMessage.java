package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.TaskType;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TaskTypeIsUnlockedMessage implements IMessage
{
    UUID goalInfoId;
    boolean isUnlocked;
    int entityId;

    private TaskTypeIsUnlockedMessage() {}
    public TaskTypeIsUnlockedMessage(TaskType goalInfo, boolean isUnlocked, int entityId)
    {
        this.goalInfoId = goalInfo.id;
        this.isUnlocked = isUnlocked;
        this.entityId = entityId;
    }

    public static void encode(TaskTypeIsUnlockedMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.goalInfoId);
        buf.writeBoolean(msg.isUnlocked);
        buf.writeInt(msg.entityId);
    }

    public static TaskTypeIsUnlockedMessage decode(PacketBuffer buf)
    {
        TaskTypeIsUnlockedMessage msg = new TaskTypeIsUnlockedMessage();
        msg.goalInfoId = buf.readUUID();
        msg.isUnlocked = buf.readBoolean();
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

            TaskType goalInfo = BlocklingTasks.getTaskType(goalInfoId);
            blockling.getTasks().setIsUnlocked(goalInfo, isUnlocked, !isClient);
        });
    }
}