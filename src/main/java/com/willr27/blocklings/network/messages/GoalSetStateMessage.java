package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class GoalSetStateMessage extends BlocklingMessage<GoalSetStateMessage>
{
    /**
     * The id of the goal.
     */
    @Nullable
    private UUID goalId;

    /**
     * The new state of the goal.
     */
    @Nullable
    private BlocklingGoal.State state;

    /**
     * Constructor used when decoding on the receiving side.
     */
    public GoalSetStateMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param goalId the id of the goal.
     * @param state the new state of the goal.
     */
    public GoalSetStateMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID goalId, @Nonnull BlocklingGoal.State state)
    {
        super(blockling);
        this.goalId = goalId;
        this.state = state;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeUUID(goalId);
        buf.writeEnum(state);

        super.encode(buf);
    }

    @Override
    @Nonnull
    public GoalSetStateMessage decode(@Nonnull PacketBuffer buf)
    {
        goalId = buf.readUUID();
        state = buf.readEnum(BlocklingGoal.State.class);

        return super.decode(buf);
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, boolean isClient)
    {
        Objects.requireNonNull(goalId);
        Objects.requireNonNull(state);

        blockling.getTasks().getTask(goalId).getGoal().setState(state, false);
    }
}