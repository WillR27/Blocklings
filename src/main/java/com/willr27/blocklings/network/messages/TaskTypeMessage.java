package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TaskTypeMessage extends BlocklingMessage<TaskTypeMessage>
{
    /**
     * The task id.
     */
    private UUID taskId;

    /**
     * The task type id to set the task's type to.
     */
    private UUID taskTypeId;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public TaskTypeMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId the task id.
     * @param taskTypeId the task type id to set the task's type to.
     */
    public TaskTypeMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, @Nonnull UUID taskTypeId)
    {
        super(blockling);
        this.taskId = taskId;
        this.taskTypeId = taskTypeId;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId);
        buf.writeUUID(taskTypeId);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId = buf.readUUID();
        taskTypeId = buf.readUUID();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().getTask(taskId).setType(BlocklingTasks.getTaskType(taskTypeId), false);
    }
}