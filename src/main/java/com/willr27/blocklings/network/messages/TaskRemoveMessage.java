package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TaskRemoveMessage extends BlocklingMessage<TaskRemoveMessage>
{
    /**
     * The task id.
     */
    private UUID taskId;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public TaskRemoveMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId the task id.
     */
    public TaskRemoveMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId)
    {
        super(blockling);
        this.taskId = taskId;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId = buf.readUUID();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().removeTask(taskId, false);
    }
}