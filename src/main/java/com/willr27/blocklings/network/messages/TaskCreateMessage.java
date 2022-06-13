package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TaskCreateMessage extends BlocklingMessage<TaskCreateMessage>
{
    /**
     * The task type id.
     */
    private UUID taskTypeId;

    /**
     * The task id, null to create a new task id on the server.
     */
    private UUID taskId;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public TaskCreateMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskTypeId the task type id.
     * @param taskId the task id, null tp create a new task id on the server.
     */
    public TaskCreateMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskTypeId, @Nullable UUID taskId)
    {
        // Don't automatically sync back to clients and handle it manually in the handle method
        super(blockling, false);
        this.taskTypeId = taskTypeId;
        this.taskId = taskId;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskTypeId);
        buf.writeBoolean(taskId != null);

        if (taskId != null)
        {
            buf.writeUUID(taskId);
        }
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskTypeId = buf.readUUID();

        if (buf.readBoolean())
        {
            taskId = buf.readUUID();
        }
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        // Make sure sync back to all the clients, including the one the original message might have come from
        blockling.getTasks().createTask(BlocklingTasks.getTaskType(taskTypeId), taskId, !blockling.level.isClientSide);
    }
}