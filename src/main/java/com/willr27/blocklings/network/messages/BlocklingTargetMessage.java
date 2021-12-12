package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BlocklingTargetMessage implements IMessage
{
    int targetId;
    int entityId;

    private BlocklingTargetMessage() {}
    public BlocklingTargetMessage(LivingEntity target, int entityId)
    {
        this.targetId = target != null ? target.getId() : -1;
        this.entityId = entityId;
    }

    public static void encode(BlocklingTargetMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.targetId);
        buf.writeInt(msg.entityId);
    }

    public static BlocklingTargetMessage decode(PacketBuffer buf)
    {
        BlocklingTargetMessage msg = new BlocklingTargetMessage();
        msg.targetId = buf.readInt();
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

            blockling.setTarget(targetId == -1 ? null : (LivingEntity) player.level.getEntity(targetId), !isClient);
        });
    }
}