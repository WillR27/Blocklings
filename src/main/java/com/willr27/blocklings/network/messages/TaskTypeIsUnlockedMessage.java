package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.TaskType;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TaskTypeIsUnlockedMessage extends BlocklingMessage<TaskTypeIsUnlockedMessage>
{
    /**
     * The task info id.
     */
    private UUID taskInfoId;

    /**
     * Whether the task type is unlocked.
     */
    private boolean isUnlocked;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public TaskTypeIsUnlockedMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling,
     * @param taskInfo the task info.
     * @param isUnlocked whether the task type is unlocked.
     */
    public TaskTypeIsUnlockedMessage(@Nonnull BlocklingEntity blockling, @Nonnull TaskType taskInfo, boolean isUnlocked)
    {
        super(blockling);
        this.taskInfoId = taskInfo.id;
        this.isUnlocked = isUnlocked;
    }

    @Override
    public void encode(@Nonnull FriendlyByteBuf buf)
    {
        super.encode(buf);

        buf.writeUUID(taskInfoId);
        buf.writeBoolean(isUnlocked);
    }

    @Override
    public void decode(@Nonnull FriendlyByteBuf buf)
    {
        super.decode(buf);

        taskInfoId = buf.readUUID();
        isUnlocked = buf.readBoolean();
    }

    @Override
    protected void handle(@Nonnull Player player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().setIsUnlocked(BlocklingTasks.getTaskType(taskInfoId), isUnlocked, false);
    }
}