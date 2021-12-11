package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BlocklingTypeMessage implements IMessage
{
    BlocklingType type;
    int entityId;

    private BlocklingTypeMessage() {}
    public BlocklingTypeMessage(BlocklingType type, int entityId)
    {
        this.type = type;
        this.entityId = entityId;
    }

    public static void encode(BlocklingTypeMessage msg, PacketBuffer buf)
    {
        buf.writeInt(BlocklingType.TYPES.indexOf(msg.type));
        buf.writeInt(msg.entityId);
    }

    public static BlocklingTypeMessage decode(PacketBuffer buf)
    {
        BlocklingTypeMessage msg = new BlocklingTypeMessage();
        msg.type = BlocklingType.TYPES.get(buf.readInt());
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

            blockling.setBlocklingType(type, !isClient);
        });
    }
}