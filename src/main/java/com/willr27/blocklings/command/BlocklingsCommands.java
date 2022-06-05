package com.willr27.blocklings.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.SetTypeCommandMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Handles the setup of blocklings commands.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlocklingsCommands
{
    /**
     * Registers argument types.
     */
    public static void init()
    {
        ArgumentTypes.register("blocklings:blockling_type", BlocklingTypeArgument.class, new BlocklingTypeArgument.Serialiser());
    }

    /**
     * Registers the custom blocklings commands.
     */
    @SubscribeEvent
    public static void onRegisterCommands(@Nonnull RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("setblocklingtype")
                  .requires(commandSource -> commandSource.hasPermission(2))
                  .then(Commands.argument("type", new BlocklingTypeArgument())
                  .executes(context -> executeTypeCommand(context, false)))
        );

        dispatcher.register(Commands.literal("setnaturalblocklingtype")
                  .requires(commandSource -> commandSource.hasPermission(2))
                  .then(Commands.argument("type", new BlocklingTypeArgument())
                  .executes(context -> executeTypeCommand(context, true)))
        );
    }

    /**
     * Executes the set blockling type commands.
     *
     * @param natural whether the type being set is the natural type or not.
     */
    private static int executeTypeCommand(@Nonnull CommandContext<CommandSource> context, boolean natural)
    {
        CommandSource source = context.getSource();
        PlayerEntity player = (PlayerEntity) source.getEntity();

        if (player == null)
        {
            return 1;
        }

        NetworkHandler.sendToClient(player, new SetTypeCommandMessage(context.getArgument("type", BlocklingType.class).key, natural));

        return 0;
    }

    /**
     * Represents a command argument of a blockling type.
     */
    public static class BlocklingTypeArgument implements ArgumentType<BlocklingType>
    {
        @Override
        public BlocklingType parse(StringReader reader) throws CommandSyntaxException
        {
            String typeString = reader.readString();

            return BlocklingType.find(typeString);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
        {
            return ISuggestionProvider.suggest(BlocklingType.TYPES.stream().map(blocklingType -> blocklingType.key).collect(Collectors.toList()), builder);
        }

        /**
         * Serialiser for the blockling type argument.
         */
        public static class Serialiser implements IArgumentSerializer<BlocklingTypeArgument>
        {
            @Override
            public void serializeToNetwork(BlocklingTypeArgument argument, PacketBuffer buf)
            {

            }

            @Override
            public BlocklingTypeArgument deserializeFromNetwork(PacketBuffer buf)
            {
                return new BlocklingTypeArgument();
            }

            @Override
            public void serializeToJson(BlocklingTypeArgument argument, JsonObject json)
            {

            }
        }
    }
}
