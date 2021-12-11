package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.gui.GuiHandler;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BlocklingGuiInfo
{
    private final BlocklingEntity blockling;
    private final World world;

    private int currentGuiId = GuiHandler.STATS_ID;

    public BlocklingGuiInfo(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.world = blockling.level;
    }

    public int getCurrentGuiId()
    {
        return currentGuiId;
    }

    public void setCurrentGuiId(int guiId)
    {
        setCurrentGuiId(guiId, true);
    }

    public void setCurrentGuiId(int guiId, boolean sync)
    {
        this.currentGuiId = guiId;

        if (sync)
        {
            NetworkHandler.sync(world, new BlocklingGuiInfoMessage(this, blockling.getId()));
        }
    }

    public static class BlocklingGuiInfoMessage implements IMessage
    {
        BlocklingGuiInfo guiInfo;
        int entityId;

        private BlocklingGuiInfoMessage()
        {
        }

        public BlocklingGuiInfoMessage(BlocklingGuiInfo guiInfo, int entityId)
        {
            this.guiInfo = guiInfo;
            this.entityId = entityId;
        }

        public static void encode(BlocklingGuiInfo.BlocklingGuiInfoMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.guiInfo.currentGuiId);
            buf.writeInt(msg.entityId);
        }

        public static BlocklingGuiInfo.BlocklingGuiInfoMessage decode(PacketBuffer buf)
        {
            BlocklingGuiInfo.BlocklingGuiInfoMessage msg = new BlocklingGuiInfo.BlocklingGuiInfoMessage();
            msg.guiInfo.currentGuiId = buf.readInt();
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
                if (player != null)
                {
                    BlocklingEntity blockling = (BlocklingEntity) player.level.getEntity(entityId);
                    if (blockling != null)
                    {
                        blockling.setGuiInfo(guiInfo, !isClient);
                    }
                }
            });
        }
    }

//    public final int currentlyOpenGuiId;
//    public final int mostRecentTabbedGuiId;
//    public final int currentlySelectedGoalId;
//    public final String abilityGroupId;
//    public final int utility;
//
//    public BlocklingGuiInfo(int currentlyOpenGuiId, int mostRecentTabbedGuiId, int currentlySelectedGoalId, String abilityGroupId, int utility)
//    {
//        this.currentlyOpenGuiId = currentlyOpenGuiId;
//        this.mostRecentTabbedGuiId = mostRecentTabbedGuiId;
//        this.currentlySelectedGoalId = currentlySelectedGoalId;
//        this.abilityGroupId = abilityGroupId;
//        this.utility = utility;
//    }
//
//    public void writeToBuf(PacketBuffer buf)
//    {
//        buf.writeInt(currentlyOpenGuiId);
//        buf.writeInt(mostRecentTabbedGuiId);
//        buf.writeInt(currentlySelectedGoalId);
//        PacketBufferUtils.writeString(buf, abilityGroupId);
//        buf.writeInt(utility);
//    }
//
//    public static BlocklingGuiInfo readFromBuf(PacketBuffer buf)
//    {
//        return new BlocklingGuiInfo(buf.readInt(), buf.readInt(), buf.readInt(), PacketBufferUtils.readString(buf), buf.readInt());
//    }
}
