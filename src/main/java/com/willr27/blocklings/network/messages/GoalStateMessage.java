package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

public class GoalStateMessage extends BlocklingMessage<GoalStateMessage>
{
    /**
     * The id of the goal.
     */
    private UUID goalId;

    /**
     * The new state of the goal.
     */
    private BlocklingGoal.State state;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public GoalStateMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param goalId the id of the goal.
     * @param state the new state of the goal.
     */
    public GoalStateMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID goalId, @Nonnull BlocklingGoal.State state)
    {
        super(blockling);
        this.goalId = goalId;
        this.state = state;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(goalId);
        buf.writeEnum(state);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        goalId = buf.readUUID();
        state = buf.readEnum(BlocklingGoal.State.class);
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        Objects.requireNonNull(goalId);
        Objects.requireNonNull(state);

        blockling.getTasks().getTask(goalId).getGoal().setState(state, false);
    }
}