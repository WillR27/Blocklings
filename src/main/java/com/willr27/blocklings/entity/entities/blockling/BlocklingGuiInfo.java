package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.gui.GuiHandler;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
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
            new Message(blockling, this).sync();
        }
    }

    public static class Message extends BlocklingMessage<Message>
    {
        /**
         * The current gui id.
         */
        private int currentGuiId;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public Message()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param guiInfo the current gui info.
         */
        public Message(@Nonnull BlocklingEntity blockling, @Nonnull BlocklingGuiInfo guiInfo)
        {
            super(blockling);
            this.currentGuiId = guiInfo.currentGuiId;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(currentGuiId);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            currentGuiId = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            blockling.getGuiInfo().currentGuiId = currentGuiId;
            blockling.setGuiInfo(blockling.getGuiInfo(), false);
        }
    }
}
