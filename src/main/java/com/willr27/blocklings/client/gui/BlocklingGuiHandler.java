package com.willr27.blocklings.client.gui;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui.screen.screens.*;
import com.willr27.blocklings.client.gui.containers.EquipmentContainer;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.BlocklingSkills;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Used to handle opening screens and containers.
 */
public class BlocklingGuiHandler
{
    public static final int STATS_ID = 0;
    public static final int TASKS_ID = 1;
    public static final int EQUIPMENT_ID = 2;
//    public static final int UTILITY_ID = 3;
    public static final int GENERAL_ID = 4;
    public static final int COMBAT_ID = 5;
    public static final int MINING_ID = 6;
    public static final int WOODCUTTING_ID = 7;
    public static final int FARMING_ID = 8;

    /**
     * The blockling.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The gui id of the most recently open gui.
     */
    private int recentGuiId = STATS_ID;

    /**
     * @param blockling the blockling.
     */
    public BlocklingGuiHandler(@Nonnull BlocklingEntity blockling)
    {
        this.blockling = blockling;
    }

    /**
     * Opens the given screen on the client.
     */
    public static void openScreen(@Nonnull Screen screen)
    {
        Minecraft.getInstance().setScreen(screen);

        if (screen instanceof BlocklingsScreen)
        {
            BlocklingsScreen blocklingsScreen = (BlocklingsScreen) screen;

            // If the blockling is dead or dying, properly close the gui in case it was closed without the proper onClose() call.
            // This might happen when configuring a container and the blockling dies.
            if (blocklingsScreen.blockling.isDeadOrDying())
            {
                blocklingsScreen.onClose();
            }
        }
    }

    /**
     * Opens the most recent blockling gui.
     * Syncs to the client/server.
     *
     * @param player the player opening the gui.
     */
    public void openGui(@Nonnull PlayerEntity player)
    {
        openGui(recentGuiId, -1, player);
    }

    /**
     * Opens the blockling gui based on the given gui id.
     * Syncs to the client/server.
     *
     * @param guiId the gui id of the gui to open.
     * @param player the player opening the gui.
     */
    public void openGui(int guiId, @Nonnull PlayerEntity player)
    {
        openGui(guiId, -1, player);
    }

    /**
     * Opens the blockling gui based on the given gui id.
     * Syncs to the client/server if sync is true.
     *
     * @param guiId the gui id of the gui to open.
     * @param windowId the window id for the container if there is one.
     * @param player the player opening the gui.
     */
    private void openGui(int guiId, int windowId, @Nonnull PlayerEntity player)
    {
        if (!blockling.level.isClientSide())
        {
            // Keep track of the gui id for when we just want to open the most recent gui id
            recentGuiId = guiId;

            // Find the next window id
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.nextContainerCounter();
            windowId = serverPlayer.containerCounter;

            // Create the container for this gui id
            Container container = createContainer(guiId, windowId, player);

            // If there is a container set the player container to it
            if (container != null)
            {
                player.containerMenu = container;
            }

            // Tell the client to open the same container and the corresponding screen
            new OpenMessage(blockling, guiId, windowId, player.getUUID()).sendToClient(player);
        }
        else
        {
            // Keep track of the gui id for when we just want to open the most recent gui id
            recentGuiId = guiId;

            // If the window id is -1 then we need to send the request to the server to handle before we do anything on the client
            if (windowId == -1)
            {
                new OpenMessage(blockling, guiId, windowId, player.getUUID()).sendToServer();

                return;
            }

            // If we have a window id then we know the server has sent the request, so we can go ahead and open the container/screen
            Container container = createContainer(guiId, windowId, player);

            // If there is a container set the player container to it
            if (container != null)
            {
                player.containerMenu = container;
            }

            openScreen(guiId, player, container);
        }
    }

    /**
     * Opens the screen on the client with the given gui id.
     *
     * @param guiId the gui id of the screen to open.
     * @param player the player opening the screen.
     * @param container the container associated with the screen.
     */
    @OnlyIn(Dist.CLIENT)
    private void openScreen(int guiId, @Nonnull PlayerEntity player, @Nullable Container container)
    {
        // Create the screen for this gui id
        Screen screen = createScreen(guiId, container, player);

        // If there is no screen something has gone wrong
        if (screen != null)
        {
            Minecraft.getInstance().setScreen(screen);
        }
        else
        {
            Blocklings.LOGGER.warn("No screen exists for gui id: " + guiId);
        }
    }

    /**
     * Creates a new instance of the container with the given id.
     *
     * @param guiId the id of the container to create.
     * @param windowId the window id to pass to the container.
     * @param player the player to pass to the container.
     * @return the container or null if the gui does not have a container or the gui id is not recognised.
     */
    @Nullable
    private Container createContainer(int guiId, int windowId, @Nonnull PlayerEntity player)
    {
        switch (guiId)
        {
            case EQUIPMENT_ID: return new EquipmentContainer(windowId, player, blockling);
//            case UTILITY_ID: return new UtilityContainer(windowId, player, blockling);
        }

        return null;
    }

    /**
     * Creates a new instance of the screen with the given id.
     *
     * @param guiId the id of the screen to create.
     * @param container the container to pass to the screen if it needs one.
     * @param player the player to pass to the screen.
     * @return the screen or null if gui id is not recognised.
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    private Screen createScreen(int guiId, @Nullable Container container, @Nonnull PlayerEntity player)
    {
        switch (guiId)
        {
            case STATS_ID: return new StatsScreen(blockling);
            case TASKS_ID: return new TasksScreen(blockling);
            case EQUIPMENT_ID: return new EquipmentScreen(blockling, (EquipmentContainer) container);
            case GENERAL_ID: return new SkillsScreen(blockling, BlocklingSkills.Groups.GENERAL, TabbedUIControl.Tab.GENERAL);
            case COMBAT_ID: return new SkillsScreen(blockling, BlocklingSkills.Groups.COMBAT, TabbedUIControl.Tab.COMBAT);
            case MINING_ID: return new SkillsScreen(blockling, BlocklingSkills.Groups.MINING, TabbedUIControl.Tab.MINING);
            case WOODCUTTING_ID: return new SkillsScreen(blockling, BlocklingSkills.Groups.WOODCUTTING, TabbedUIControl.Tab.WOODCUTTING);
            case FARMING_ID: return new SkillsScreen(blockling, BlocklingSkills.Groups.FARMING, TabbedUIControl.Tab.FARMING);
        }

        return null;
    }

    /**
     * @return the most recently opened gui id.
     */
    public int getRecentGuiId()
    {
        return recentGuiId;
    }

    /**
     * The message used to sync opening a gui across the client/server.
     */
    public static class OpenMessage extends BlocklingMessage<OpenMessage>
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
         * The id of the player opening the gui.
         */
        private UUID playerId;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public OpenMessage()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param guiId the gui id.
         * @param windowId the window id.
         */
        public OpenMessage(@Nonnull BlocklingEntity blockling, int guiId, int windowId, @Nonnull UUID playerId)
        {
            super(blockling, false);
            this.guiId = guiId;
            this.windowId = windowId;
            this.playerId = playerId;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(guiId);
            buf.writeInt(windowId);
            buf.writeUUID(playerId);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            guiId = buf.readInt();
            windowId = buf.readInt();
            playerId = buf.readUUID();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            if (blockling.level.isClientSide())
            {
                blockling.guiHandler.openGui(guiId, windowId, player);
            }
            else
            {
                PlayerEntity targetedPlayer = blockling.level.players().stream().filter(serverPlayer -> serverPlayer.getUUID().equals(playerId)).findFirst().orElse(null);

                if (targetedPlayer != null)
                {
                    blockling.guiHandler.openGui(guiId, windowId, targetedPlayer);
                }
                else
                {
                    Blocklings.LOGGER.warn("Tried opening a gui for a player that does not exist on the server with id: " + playerId);
                }
            }
        }
    }
}
