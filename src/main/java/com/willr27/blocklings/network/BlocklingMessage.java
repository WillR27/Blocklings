package com.willr27.blocklings.network;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BlocklingMessage<T extends BlocklingMessage<T>> extends Message
{
    /**
     * The blockling.
     */
    @Nullable
    protected BlocklingEntity blockling;

    /**
     * The blockling's entity id.
     */
    protected int blocklingId;

    /**
     * The player's id if we are on the client.
     * Used so we can avoid syncing the same information back to a sending client.
     */
    @Nonnull
    private UUID clientPlayerId = new UUID(0L, 0L);

    /**
     * Determines whether messages received on the server should be synced back to all other clients.
     */
    private boolean syncBackToClients = true;

    /**
     * @param blockling the blockling.
     */
    protected BlocklingMessage(@Nullable BlocklingEntity blockling)
    {
        this.blockling = blockling;

        if (blockling != null)
        {
            blocklingId = blockling.getId();

            if (blockling.level.isClientSide())
            {
                clientPlayerId = getClientPlayerId();
            }
        }
    }

    /**
     * @param blockling the blockling.
     * @param syncBackToClients determines whether messages received on the server should be synced back to all other clients.
     */
    protected BlocklingMessage(@Nullable BlocklingEntity blockling, boolean syncBackToClients)
    {
        this(blockling);
        this.syncBackToClients = syncBackToClients;
    }

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeInt(blocklingId);
        buf.writeUUID(clientPlayerId);
        buf.writeBoolean(syncBackToClients);
    }

    /**
     * Decodes the message.
     *
     * @param buf the buffer to decode from.
     */
    public void decode(@Nonnull PacketBuffer buf)
    {
        blocklingId = buf.readInt();
        clientPlayerId = buf.readUUID();
        syncBackToClients = buf.readBoolean();
    }

    /**
     * Handles the message when received on the client/server.
     *
     * @param ctx the network context.
     */
    public void handle(@Nonnull Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();

        context.enqueueWork(() ->
        {
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            PlayerEntity player = isClient ? getClientPlayer() : context.getSender();
            Objects.requireNonNull(player, "No player entity found when handling message.");

            blockling = (BlocklingEntity) player.level.getEntity(blocklingId);

            // The client may unload the blockling before the server.
            if (blockling == null)
            {
                return;
            }

            handle(player, blockling);

            if (!isClient && syncBackToClients)
            {
                sendToAllClients(blockling.level.players().stream().filter(serverPlayer -> serverPlayer.getUUID().equals(clientPlayerId)).collect(Collectors.toList()));
            }

            ctx.get().setPacketHandled(true);
        });

        context.setPacketHandled(true);
    }

    /**
     * Handles the message when received on the client/server/
     *
     * @param player the player.
     * @param blockling the blockling.
     */
    protected abstract void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling);

    /**
     * Sends the message either to the server or all player's clients.
     */
    public void sync()
    {
        NetworkHandler.sync(blockling.level, this);
    }

    /**
     * Sends the message to the server.
     */
    public void sendToServer()
    {
        NetworkHandler.sendToServer(this);
    }

    /**
     * Sends the message to the player's client.
     *
     * @param player the player to send the message to.
     */
    public void sendToClient(PlayerEntity player)
    {
        NetworkHandler.sendToClient(player, this);
    }

    /**
     * Sends the given message to every player's client except the given players.
     *
     * @param playersToIgnore the players to not send the message to.
     */
    public void sendToAllClients(List<PlayerEntity> playersToIgnore)
    {
        NetworkHandler.sendToAllClients(blockling.level, this, playersToIgnore);
    }
}
