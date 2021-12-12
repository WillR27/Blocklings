package com.willr27.blocklings.network;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.entities.blockling.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.attribute.AttributeModifier;
import com.willr27.blocklings.entity.entities.blockling.BlocklingGuiInfo;
import com.willr27.blocklings.network.messages.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jline.utils.Log;

public class NetworkHandler
{
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Blocklings.MODID, "channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void init()
    {
        int id = 0;

        HANDLER.registerMessage(id++, Attribute.BlocklingAttributeBaseValueMessage.class, Attribute.BlocklingAttributeBaseValueMessage::encode, Attribute.BlocklingAttributeBaseValueMessage::decode, Attribute.BlocklingAttributeBaseValueMessage::handle);
        HANDLER.registerMessage(id++, AttributeModifier.BlocklingAttributeModifierValueMessage.class, AttributeModifier.BlocklingAttributeModifierValueMessage::encode, AttributeModifier.BlocklingAttributeModifierValueMessage::decode, AttributeModifier.BlocklingAttributeModifierValueMessage::handle);
        HANDLER.registerMessage(id++, BlocklingGuiInfo.BlocklingGuiInfoMessage.class, BlocklingGuiInfo.BlocklingGuiInfoMessage::encode, BlocklingGuiInfo.BlocklingGuiInfoMessage::decode, BlocklingGuiInfo.BlocklingGuiInfoMessage::handle);
        HANDLER.registerMessage(id++, BlocklingTargetMessage.class, BlocklingTargetMessage::encode, BlocklingTargetMessage::decode, BlocklingTargetMessage::handle);
        HANDLER.registerMessage(id++, BlocklingTypeMessage.class, BlocklingTypeMessage::encode, BlocklingTypeMessage::decode, BlocklingTypeMessage::handle);
        HANDLER.registerMessage(id++, EquipmentInventoryMessage.class, EquipmentInventoryMessage::encode, EquipmentInventoryMessage::decode, EquipmentInventoryMessage::handle);
        HANDLER.registerMessage(id++, GoalSetStateMessage.class, GoalSetStateMessage::encode, GoalSetStateMessage::decode, GoalSetStateMessage::handle);
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
        HANDLER.registerMessage(id++, WhitelistSingleMessage.class, WhitelistSingleMessage::encode, WhitelistSingleMessage::decode, WhitelistSingleMessage::handle);
    }

    public static void sendToServer(IMessage message)
    {
        Log.info("Sending to server: " + message.getClass());

        HANDLER.sendToServer(message);
    }

    public static void sendTo(PlayerEntity player, IMessage message)
    {
        Log.info("Sending to client: " + message.getClass());

        HANDLER.sendTo(message, ((ServerPlayerEntity)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAll(World world, IMessage message)
    {
        for (PlayerEntity player : world.players())
        {
            sendTo(player, message);
        }
    }

    public static void sync(World world, IMessage message)
    {
        if (world.isClientSide) sendToServer(message);
        else sendToAll(world, message);
    }
}