package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class WhitelistAllMessage implements IMessage
{
    UUID taskId;
    int whitelistId;
    Whitelist<ResourceLocation> whitelist;
    int entityId;

    private WhitelistAllMessage() {}
    public WhitelistAllMessage(UUID taskId, int whitelistId, GoalWhitelist whitelist, int entityId)
    {
        this.taskId = taskId;
        this.whitelistId = whitelistId;
        this.whitelist = whitelist;
        this.entityId = entityId;
    }

    public static void encode(WhitelistAllMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskId);
        buf.writeInt(msg.whitelistId);
        buf.writeInt(msg.whitelist.size());
        for (ResourceLocation entry : msg.whitelist.keySet())
        {
            PacketBufferUtils.writeString(buf, entry.toString());
            buf.writeBoolean(msg.whitelist.get(entry));
        }
        buf.writeInt(msg.entityId);
    }

    public static WhitelistAllMessage decode(PacketBuffer buf)
    {
        WhitelistAllMessage msg = new WhitelistAllMessage();
        msg.taskId = buf.readUUID();
        msg.whitelistId = buf.readInt();
        msg.whitelist = new Whitelist<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            msg.whitelist.put(new ResourceLocation(PacketBufferUtils.readString(buf)), buf.readBoolean());
        }
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

            blockling.getTasks().getTask(taskId).getGoal().whitelists.get(whitelistId).setWhitelist(whitelist, !isClient);
        });
    }
}