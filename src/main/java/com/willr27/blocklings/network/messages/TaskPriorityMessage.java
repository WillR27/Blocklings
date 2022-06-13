package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TaskPriorityMessage extends BlocklingMessage<TaskPriorityMessage>
{
    /**
     * The task id.
     */
    private UUID taskId;

    /**
     * The task priority.
     */
    private int priority;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public TaskPriorityMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId the task id.
     * @param priority the task priority.
     */
    public TaskPriorityMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, int priority)
    {
        super(blockling);
        this.taskId = taskId;
        this.priority = priority;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId);
        buf.writeInt(priority);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId = buf.readUUID();
        priority = buf.readInt();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().getTask(taskId).setPriority(priority, false);
    }
}