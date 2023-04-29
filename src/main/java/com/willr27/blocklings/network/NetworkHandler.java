package com.willr27.blocklings.network;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.client.gui.BlocklingGuiHandler;
import com.willr27.blocklings.entity.blockling.action.Action;
import com.willr27.blocklings.entity.blockling.attribute.Attribute;
import com.willr27.blocklings.entity.blockling.attribute.attributes.EnumAttribute;
import com.willr27.blocklings.entity.blockling.attribute.attributes.numbers.FloatAttribute;
import com.willr27.blocklings.entity.blockling.attribute.attributes.numbers.IntAttribute;
import com.willr27.blocklings.entity.blockling.attribute.attributes.numbers.ModifiableFloatAttribute;
import com.willr27.blocklings.entity.blockling.attribute.attributes.numbers.ModifiableIntAttribute;
import com.willr27.blocklings.entity.blockling.goal.config.iteminfo.OrderedItemInfoSet;
import com.willr27.blocklings.entity.blockling.goal.config.patrol.OrderedPatrolPointList;
import com.willr27.blocklings.entity.blockling.goal.goals.container.BlocklingContainerGoal;
import com.willr27.blocklings.entity.blockling.task.config.Property;
import com.willr27.blocklings.network.messages.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class NetworkHandler
{
    /**
     * The protocol version.
     */
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    /**
     * The simple channel handler.
     */
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Blocklings.MODID, "channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    /**
     * Incremented for each registered message.
     */
    private static int id = 0;

    /**
     * Initialises all message handlers.
     */
    public static void init()
    {
        HANDLER.registerMessage(id++, SetLevelCommandMessage.class, SetLevelCommandMessage::encode, SetLevelCommandMessage::decode, SetLevelCommandMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        HANDLER.registerMessage(id++, SetTypeCommandMessage.class, SetTypeCommandMessage::encode, SetTypeCommandMessage::decode, SetTypeCommandMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        HANDLER.registerMessage(id++, SetXpCommandMessage.class, SetXpCommandMessage::encode, SetXpCommandMessage::decode, SetXpCommandMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        registerMessage(Attribute.IsEnabledMessage.class);
        registerMessage(EnumAttribute.Message.class);
        registerMessage(FloatAttribute.ValueMessage.class);
        registerMessage(ModifiableFloatAttribute.BaseValueMessage.class);
        registerMessage(IntAttribute.ValueMessage.class);
        registerMessage(ModifiableIntAttribute.BaseValueMessage.class);

        registerMessage(Action.CountMessage.class);
        registerMessage(BlocklingAttackTargetMessage.class);
        registerMessage(BlocklingGuiHandler.OpenMessage.class);
        registerMessage(BlocklingNameMessage.class);
        registerMessage(BlocklingScaleMessage.class);
        registerMessage(BlocklingTypeMessage.class);
        registerMessage(EquipmentInventoryMessage.class);
        registerMessage(GoalStateMessage.class);
        registerMessage(SkillStateMessage.class);
        registerMessage(SkillTryBuyMessage.class);

        registerMessage(TaskCreateMessage.class);
        registerMessage(TaskPriorityMessage.class);
        registerMessage(TaskRemoveMessage.class);
        registerMessage(TaskCustomNameMessage.class);
        registerMessage(Property.TaskPropertyMessage.class);
        registerMessage(TaskSwapPriorityMessage.class);
        registerMessage(TaskTypeMessage.class);
        registerMessage(TaskTypeIsUnlockedMessage.class);

        registerMessage(OrderedItemInfoSet.AddItemInfoInfoMessage.class);
        registerMessage(OrderedItemInfoSet.RemoveItemInfoInfoMessage.class);
        registerMessage(OrderedItemInfoSet.MoveItemInfoInfoMessage.class);
        registerMessage(OrderedItemInfoSet.SetItemInfoInfoMessage.class);
        registerMessage(OrderedPatrolPointList.AddPatrolPointMessage.class);
        registerMessage(OrderedPatrolPointList.RemovePatrolPointMessage.class);
        registerMessage(OrderedPatrolPointList.MovePatrolPointMessage.class);
        registerMessage(OrderedPatrolPointList.UpdatePatrolPointMessage.class);
        registerMessage(WhitelistAllMessage.class);
        registerMessage(WhitelistIsUnlockedMessage.class);
        registerMessage(WhitelistSingleMessage.class);
        registerMessage(BlocklingContainerGoal.ContainerGoalContainerAddRemoveMessage.class);
        registerMessage(BlocklingContainerGoal.ContainerGoalContainerMessage.class);
        registerMessage(BlocklingContainerGoal.ContainerGoalContainerMoveMessage.class);
    }

    /**
     * Registers a blockling message.
     *
     * @param messageType the type of the message.
     */
    public static <T extends BlocklingMessage<T>> void registerMessage(@Nonnull Class<T> messageType)
    {
        Function<PacketBuffer, T> decoder = (buf) ->
        {
            try
            {
                T message = messageType.newInstance();
                message.decode(buf);

                return message;
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                Blocklings.LOGGER.warn(e.getLocalizedMessage());

                return null;
            }
        };

        HANDLER.registerMessage(id++, messageType, BlocklingMessage::encode, decoder, BlocklingMessage::handle);
    }

    /**
     * Sends the given message to the server.
     *
     * @param message the message to send.
     */
    public static void sendToServer(Message message)
    {
//        Log.info("Sending to server: " + message.getClass());

        HANDLER.sendToServer(message);
    }

    /**
     * Sends the given message to the given player's client.
     *
     * @param player the player to send the message to.
     * @param message the message to send.
     */
    public static void sendToClient(PlayerEntity player, Message message)
    {
//        Log.info("Sending to client: " + message.getClass());

        HANDLER.sendTo(message, ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Sends the given message to every player's client except the given players.
     *
     * @param world the world the players are in.
     * @param message the message to send.
     * @param playersToIgnore the players to not send the message to.
     */
    public static void sendToAllClients(World world, Message message, List<PlayerEntity> playersToIgnore)
    {
        for (PlayerEntity player : world.players())
        {
            if (!playersToIgnore.contains(player))
            {
                sendToClient(player, message);
            }
        }
    }

    /**
     * Sends the message either to the server or all player's clients.
     *
     * @param world the world.
     * @param message the message to send.
     */
    public static void sync(World world, Message message)
    {
        if (world.isClientSide)
        {
            sendToServer(message);
        }
        else
        {
            sendToAllClients(world, message, new ArrayList<>());
        }
    }
}