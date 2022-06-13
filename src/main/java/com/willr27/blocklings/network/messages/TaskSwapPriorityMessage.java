package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TaskSwapPriorityMessage extends BlocklingMessage<TaskSwapPriorityMessage>
{
    /**
     * The first task id.
     */
    private UUID taskId1;

    /**
     * The second task id.
     */
    private UUID taskId2;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public TaskSwapPriorityMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId1 the first task id.
     * @param taskId2 the second task id.
     */
    public TaskSwapPriorityMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId1, @Nonnull UUID taskId2)
    {
        super(blockling);
        this.taskId1 = taskId1;
        this.taskId2 = taskId2;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId1);
        buf.writeUUID(taskId2);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId1 = buf.readUUID();
        taskId2 = buf.readUUID();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        Task task1 = blockling.getTasks().getTask(taskId1);
        Task task2 = blockling.getTasks().getTask(taskId2);
        task1.swapPriority(task2, false);
    }
}