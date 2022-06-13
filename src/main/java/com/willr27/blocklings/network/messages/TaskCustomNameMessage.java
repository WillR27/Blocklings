package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TaskCustomNameMessage extends BlocklingMessage<TaskCustomNameMessage>
{
    /**
     * The task id.
     */
    private UUID taskId;

    /**
     * The custom task name.
     */
    private String customName;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public TaskCustomNameMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId the task id.
     * @param customName the custom task name.
     */
    public TaskCustomNameMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, @Nonnull String customName)
    {
        super(blockling);
        this.taskId = taskId;
        this.customName = customName;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId);
        PacketBufferUtils.writeString(buf, customName);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId = buf.readUUID();
        customName = PacketBufferUtils.readString(buf);
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().getTask(taskId).setCustomName(customName, false);
    }
}