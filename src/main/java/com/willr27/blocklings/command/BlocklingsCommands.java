package com.willr27.blocklings.command;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Handles the setup of blocklings commands.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlocklingsCommands
{
    /**
     * Registers the custom blocklings commands.
     */
    @SubscribeEvent
    public static void onRegisterCommands(@Nonnull RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("setblocklingtype")
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .then(Commands.argument("types", new BlocklingTypeArgument())
                                .executes(context -> executeTypeCommand(context)))
        );
    }

    /**
     * Executes the setblocklingtype command.
     */
    private static int executeTypeCommand(@Nonnull CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        PlayerEntity player = (PlayerEntity) source.getEntity();

        if (player == null)
        {
            return 1;
        }

        NetworkHandler.sendToClient(player, new SetTypeCommandMessage(context.getArgument("types", BlocklingType.class).key));

        return 0;
    }

    /**
     * Represents a command argument of a blockling type.
     */
    private static class BlocklingTypeArgument implements ArgumentType<BlocklingType>
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
    }
}
