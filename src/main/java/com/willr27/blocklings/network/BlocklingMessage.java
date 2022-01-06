package com.willr27.blocklings.network;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.client.Minecraft;
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

public abstract class BlocklingMessage<T extends BlocklingMessage<T>> implements IMessage
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
    protected boolean syncBackToClients = true;

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
                clientPlayerId = Minecraft.getInstance().player.getUUID();
            }
        }
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
     * @return the instance of this message.
     */
    @Nonnull
    public T decode(@Nonnull PacketBuffer buf)
    {
        blocklingId = buf.readInt();
        clientPlayerId = buf.readUUID();
        syncBackToClients = buf.readBoolean();

        return (T) this;
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

            PlayerEntity player = isClient ? Minecraft.getInstance().player : context.getSender();
            Objects.requireNonNull(player, "No player entity found when handling message.");

            blockling = (BlocklingEntity) player.level.getEntity(blocklingId);
            Objects.requireNonNull(blockling, String.format("No blockling entity found when handling message with id: %d.", blocklingId));

            handle(player, blockling, isClient);

            if (!isClient && syncBackToClients)
            {
                sendToAllClients(blockling.level.players().stream().filter(serverPlayer -> serverPlayer.getUUID().equals(clientPlayerId)).collect(Collectors.toList()));
            }
        });
    }

    /**
     * Handles the message when received on the client/server/
     *
     * @param player the player.
     * @param blockling the blockling.
     * @param isClient true if on a client.
     */
    protected abstract void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, boolean isClient);

    /**
     * Sends the message either to the server or all player's clients.
     */
    public void sync()
    {
        sync(syncBackToClients);
    }

    /**
     * Sends the message either to the server or all player's clients.
     *
     * @param syncBackToClients true if the message should be synced back to clients when received on the server.
     */
    public void sync(boolean syncBackToClients)
    {
        this.syncBackToClients = syncBackToClients;

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
