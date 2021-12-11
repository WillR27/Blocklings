package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.entities.blockling.goal.Task;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GoalSetStateMessage implements IMessage
{
    UUID goalId;
    BlocklingGoal.State state;
    int entityId;

    private GoalSetStateMessage() {}
    public GoalSetStateMessage(UUID goalId, BlocklingGoal.State state, int entityId)
    {
        this.goalId = goalId;
        this.state = state;
        this.entityId = entityId;
    }

    public static void encode(GoalSetStateMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.goalId);
        buf.writeEnum(msg.state);
        buf.writeInt(msg.entityId);
    }

    public static GoalSetStateMessage decode(PacketBuffer buf)
    {
        GoalSetStateMessage msg = new GoalSetStateMessage();
        msg.goalId = buf.readUUID();
        msg.state = buf.readEnum(BlocklingGoal.State.class);
        msg.entityId = buf.readInt();

        return msg;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            NetworkEvent.Context context = ctx.get();
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            PlayerEntity player = isClient ? Minecraft.getInstance().player : ctx.get().getSender();
            BlocklingEntity blockling = (BlocklingEntity) player.level.getEntity(entityId);

            Task task = blockling.getTasks().getTask(goalId);
            task.getGoal().setState(state, !isClient);
        });
    }
}