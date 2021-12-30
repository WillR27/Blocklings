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

public class WhitelistIsUnlockedMessage implements IMessage
{
    UUID taskId;
    int whitelistId;
    boolean isUnlocked;
    int entityId;

    private WhitelistIsUnlockedMessage() {}
    public WhitelistIsUnlockedMessage(UUID taskId, int whitelistId, boolean isUnlocked, int entityId)
    {
        this.taskId = taskId;
        this.whitelistId = whitelistId;
        this.isUnlocked = isUnlocked;
        this.entityId = entityId;
    }

    public static void encode(WhitelistIsUnlockedMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.taskId);
        buf.writeInt(msg.whitelistId);
        buf.writeBoolean(msg.isUnlocked);
        buf.writeInt(msg.entityId);
    }

    public static WhitelistIsUnlockedMessage decode(PacketBuffer buf)
    {
        WhitelistIsUnlockedMessage msg = new WhitelistIsUnlockedMessage();
        msg.taskId = buf.readUUID();
        msg.whitelistId = buf.readInt();
        msg.isUnlocked = buf.readBoolean();
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

            blockling.getTasks().getTask(taskId).getGoal().whitelists.get(whitelistId).setIsUnlocked(isUnlocked, !isClient);
        });
    }
}