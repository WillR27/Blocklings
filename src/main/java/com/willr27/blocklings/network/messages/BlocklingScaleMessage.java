package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BlocklingScaleMessage implements IMessage
{
    float scale;
    int entityId;

    private BlocklingScaleMessage() {}
    public BlocklingScaleMessage(float scale, int entityId)
    {
        this.scale = scale;
        this.entityId = entityId;
    }

    public static void encode(BlocklingScaleMessage msg, PacketBuffer buf)
    {
        buf.writeFloat(msg.scale);
        buf.writeInt(msg.entityId);
    }

    public static BlocklingScaleMessage decode(PacketBuffer buf)
    {
        BlocklingScaleMessage msg = new BlocklingScaleMessage();
        msg.scale = buf.readFloat();
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

            blockling.setScale(scale, !isClient);
        });
    }
}