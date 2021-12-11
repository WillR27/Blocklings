package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class WhitelistSingleMessage implements IMessage
{
    UUID taskId;
    int whitelistId;
    ResourceLocation entry;
    boolean value;
    int entityId;

    private WhitelistSingleMessage() {}
    public WhitelistSingleMessage(UUID taskId, int whitelistId, ResourceLocation entry, boolean value, int entityId)
    {
        this.taskId = taskId;
        this.whitelistId = whitelistId;
        this.entry = entry;
        this.value = value;
        this.entityId = entityId;
    }

    public static void encode(WhitelistSingleMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskId);
        buf.writeInt(msg.whitelistId);
        PacketBufferUtils.writeString(buf, msg.entry.toString());
        buf.writeBoolean(msg.value);
        buf.writeInt(msg.entityId);
    }

    public static WhitelistSingleMessage decode(PacketBuffer buf)
    {
        WhitelistSingleMessage msg = new WhitelistSingleMessage();
        msg.taskId = buf.readUUID();
        msg.whitelistId = buf.readInt();
        msg.entry = new ResourceLocation(PacketBufferUtils.readString(buf));
        msg.value = buf.readBoolean();
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

            blockling.getTasks().getTask(taskId).getGoal().whitelists.get(whitelistId).setEntry(entry, value, !isClient);
        });
    }
}