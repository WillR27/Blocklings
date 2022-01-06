package com.willr27.blocklings.network;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.attribute.attributes.EnumAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.FloatAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.IntAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.ModifiableFloatAttribute;
import com.willr27.blocklings.attribute.attributes.numbers.ModifiableIntAttribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingGuiInfo;
import com.willr27.blocklings.network.messages.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jline.utils.Log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
        HANDLER.registerMessage(id++, Attribute.IsEnabledMessage.class, Attribute.IsEnabledMessage::encode, Attribute.IsEnabledMessage::decode, Attribute.IsEnabledMessage::handle);
        HANDLER.registerMessage(id++, BlocklingGuiInfo.Message.class, BlocklingGuiInfo.Message::encode, BlocklingGuiInfo.Message::decode, BlocklingGuiInfo.Message::handle);
        HANDLER.registerMessage(id++, BlocklingScaleMessage.class, BlocklingScaleMessage::encode, BlocklingScaleMessage::decode, BlocklingScaleMessage::handle);
        HANDLER.registerMessage(id++, BlocklingTargetMessage.class, BlocklingTargetMessage::encode, BlocklingTargetMessage::decode, BlocklingTargetMessage::handle);
        HANDLER.registerMessage(id++, BlocklingTypeMessage.class, BlocklingTypeMessage::encode, BlocklingTypeMessage::decode, BlocklingTypeMessage::handle);
        HANDLER.registerMessage(id++, EquipmentInventoryMessage.class, EquipmentInventoryMessage::encode, EquipmentInventoryMessage::decode, EquipmentInventoryMessage::handle);
        registerMessage(GoalSetStateMessage.class);
        HANDLER.registerMessage(id++, EnumAttribute.Message.class, EnumAttribute.Message::encode, EnumAttribute.Message::decode, EnumAttribute.Message::handle);
        HANDLER.registerMessage(id++, FloatAttribute.ValueMessage.class, FloatAttribute.ValueMessage::encode, FloatAttribute.ValueMessage::decode, FloatAttribute.ValueMessage::handle);
        HANDLER.registerMessage(id++, ModifiableFloatAttribute.BaseValueMessage.class, ModifiableFloatAttribute.BaseValueMessage::encode, ModifiableFloatAttribute.BaseValueMessage::decode, ModifiableFloatAttribute.BaseValueMessage::handle);
        HANDLER.registerMessage(id++, IntAttribute.ValueMessage.class, IntAttribute.ValueMessage::encode, IntAttribute.ValueMessage::decode, IntAttribute.ValueMessage::handle);
        HANDLER.registerMessage(id++, ModifiableIntAttribute.BaseValueMessage.class, ModifiableIntAttribute.BaseValueMessage::encode, ModifiableIntAttribute.BaseValueMessage::decode, ModifiableIntAttribute.BaseValueMessage::handle);
        HANDLER.registerMessage(id++, OpenGuiMessage.class, OpenGuiMessage::encode, OpenGuiMessage::decode, OpenGuiMessage::handle);
        HANDLER.registerMessage(id++, SkillStateMessage.class, SkillStateMessage::encode, SkillStateMessage::decode, SkillStateMessage::handle);
        HANDLER.registerMessage(id++, SkillTryBuyMessage.class, SkillTryBuyMessage::encode, SkillTryBuyMessage::decode, SkillTryBuyMessage::handle);
        HANDLER.registerMessage(id++, TaskCreateMessage.class, TaskCreateMessage::encode, TaskCreateMessage::decode, TaskCreateMessage::handle);
        HANDLER.registerMessage(id++, TaskPriorityMessage.class, TaskPriorityMessage::encode, TaskPriorityMessage::decode, TaskPriorityMessage::handle);
        HANDLER.registerMessage(id++, TaskRemoveMessage.class, TaskRemoveMessage::encode, TaskRemoveMessage::decode, TaskRemoveMessage::handle);
        HANDLER.registerMessage(id++, TaskSetCustomNameMessage.class, TaskSetCustomNameMessage::encode, TaskSetCustomNameMessage::decode, TaskSetCustomNameMessage::handle);
        HANDLER.registerMessage(id++, TaskSetTypeMessage.class, TaskSetTypeMessage::encode, TaskSetTypeMessage::decode, TaskSetTypeMessage::handle);
        HANDLER.registerMessage(id++, TaskSwapPriorityMessage.class, TaskSwapPriorityMessage::encode, TaskSwapPriorityMessage::decode, TaskSwapPriorityMessage::handle);
        HANDLER.registerMessage(id++, TaskTypeIsUnlockedMessage.class, TaskTypeIsUnlockedMessage::encode, TaskTypeIsUnlockedMessage::decode, TaskTypeIsUnlockedMessage::handle);
        HANDLER.registerMessage(id++, WhitelistAllMessage.class, WhitelistAllMessage::encode, WhitelistAllMessage::decode, WhitelistAllMessage::handle);
        HANDLER.registerMessage(id++, WhitelistIsUnlockedMessage.class, WhitelistIsUnlockedMessage::encode, WhitelistIsUnlockedMessage::decode, WhitelistIsUnlockedMessage::handle);
        HANDLER.registerMessage(id++, WhitelistSingleMessage.class, WhitelistSingleMessage::encode, WhitelistSingleMessage::decode, WhitelistSingleMessage::handle);
    }

    /**
     * Registers a blockling message.
     *
     * @param messageType the type of the message.
     */
    private static <T extends BlocklingMessage<T>> void registerMessage(@Nonnull Class<T> messageType)
    {
        Function<PacketBuffer, T> decoder = (buf) ->
        {
            try
            {
                return messageType.newInstance().decode(buf);
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                Blocklings.LOGGER.warn(e.getStackTrace());

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
    public static void sendToServer(IMessage message)
    {
        Log.info("Sending to server: " + message.getClass());

        HANDLER.sendToServer(message);
    }

    /**
     * Sends the given message to the given player's client.
     *
     * @param player the player to send the message to.
     * @param message the message to send.
     */
    public static void sendToClient(PlayerEntity player, IMessage message)
    {
        Log.info("Sending to client: " + message.getClass());

        HANDLER.sendTo(message, ((ServerPlayerEntity)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Sends the given message to every player's client except the given players.
     *
     * @param world the world the players are in.
     * @param message the message to send.
     * @param playersToIgnore the players to not send the message to.
     */
    public static void sendToAllClients(World world, IMessage message, List<PlayerEntity> playersToIgnore)
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
    public static void sync(World world, IMessage message)
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