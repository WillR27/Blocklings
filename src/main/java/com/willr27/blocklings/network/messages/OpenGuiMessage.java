package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.GuiHandler;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;

public class OpenGuiMessage extends BlocklingMessage<OpenGuiMessage>
{
    /**
     * The gui id.
     */
    private int guiId;

    /**
     * The window id.
     */
    private int windowId;

    /**
     * Whether to open the container on the client.
     */
    private boolean openContainerFromClient = false;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public OpenGuiMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param guiId the guid id.
     * @param windowId the window id.
     */
    public OpenGuiMessage(@Nonnull BlocklingEntity blockling, int guiId, int windowId)
    {
        super(blockling);
        this.guiId = guiId;
        this.windowId = windowId;
    }

    /**
     * @param blockling the blockling.
     * @param guiId the guid id.
     * @param windowId the window id.
     * @param openContainerFromClient whether to open the container on the client.
     */
    public OpenGuiMessage(@Nonnull BlocklingEntity blockling, int guiId, int windowId, boolean openContainerFromClient)
    {
        this(blockling, guiId, windowId);
        this.openContainerFromClient = openContainerFromClient;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(guiId);
        buf.writeInt(windowId);
        buf.writeBoolean(openContainerFromClient);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        guiId = buf.readInt();
        windowId = buf.readInt();
        openContainerFromClient = buf.readBoolean();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        if (openContainerFromClient) GuiHandler.openGui(guiId, blockling, player);
        else GuiHandler.openGui(guiId, windowId, blockling, player, false);
    }
}