package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * A message used to sync something between a goal on the client/server.
 */
public abstract class GoalMessage<T extends BlocklingMessage<T>> extends BlocklingMessage<T>
{
    /**
     * The task id associated with the goal.
     */
    private UUID taskId;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public GoalMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling associated with the goal.
     * @param taskId the task id associated with the goal.
     */
    public GoalMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId)
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
        BlocklingGoal goal = blockling.getTasks().getTask(taskId).getGoal();

        handle(player, blockling, goal);
    }

    /**
     * Handles the message.
     *
     * @param player the player.
     * @param blockling the blockling.
     * @param goal the goal.
     */
    protected abstract void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingGoal goal);
}
