package com.willr27.blocklings.gui;

import com.willr27.blocklings.skills.BlocklingSkillGroups;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.containers.EquipmentContainer;
import com.willr27.blocklings.gui.screens.SkillsScreen;
import com.willr27.blocklings.gui.screens.EquipmentScreen;
import com.willr27.blocklings.gui.screens.StatsScreen;
import com.willr27.blocklings.gui.screens.TasksScreen;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.OpenGuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import org.jline.utils.Log;

public class GuiHandler
{
    public static final int STATS_ID = 0;
    public static final int TASKS_ID = 1;
    public static final int EQUIPMENT_ID = 2;
    public static final int UTILITY_ID = 3;
    public static final int GENERAL_ID = 4;
    public static final int COMBAT_ID = 5;
    public static final int MINING_ID = 6;
    public static final int WOODCUTTING_ID = 7;
    public static final int FARMING_ID = 8;

    public static final int WHITELIST_ID = 9;


    public static void openGui(int guiId, BlocklingEntity blockling, PlayerEntity player)
    {
        openGui(guiId, blockling, player, true);
    }

    public static void openGui(int guiId, BlocklingEntity blockling, PlayerEntity player, boolean sync)
    {
//        if (isClientOnly(guiId))
//        {
//            openGui(guiId, -1, blockling, player, sync);
//        }
//        else
//        {
            if (!blockling.level.isClientSide)
            {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                serverPlayer.nextContainerCounter();
                openGui(guiId, serverPlayer.containerCounter, blockling, player, sync);
            }
            else
            {
                NetworkHandler.sendToServer(new OpenGuiMessage(guiId, 0, blockling.getId(), true));
            }
//        }
    }

    public static void openGui(int guiId, int windowId, BlocklingEntity blockling, PlayerEntity player, boolean sync)
    {
        if (!blockling.level.isClientSide)
        {
            ((ServerPlayerEntity) player).nextContainerCounter();
            windowId = ((ServerPlayerEntity) player).containerCounter;

            Container container = getContainer(guiId, windowId, blockling, player);
            if (container != null) player.containerMenu = container;
            if (sync) NetworkHandler.sendTo(player, new OpenGuiMessage(guiId, windowId, blockling.getId()));

            // Sync back to the client
            blockling.getGuiInfo().setCurrentGuiId(guiId, true);
        }
        else
        {
            if (windowId == 0)
            {
                Log.warn("The window id should never be 0 on the client as this is for the player inventory container!");
            }
            else
            {
                Container container = getContainer(guiId, windowId, blockling, player);
                if (container != null) player.containerMenu = container;
                Screen screen = getScreen(guiId, container, blockling, player);
                if (screen != null) Minecraft.getInstance().setScreen(screen);
                if (sync) NetworkHandler.sendToServer(new OpenGuiMessage(guiId, windowId, blockling.getId()));

                // Don't sync back to the server
                blockling.getGuiInfo().setCurrentGuiId(guiId, false);
            }
        }
    }

    private static boolean isClientOnly(int guiId)
    {
        switch (guiId)
        {
            case EQUIPMENT_ID:
            case UTILITY_ID:
                return false;

            case STATS_ID:
            case TASKS_ID:
            case GENERAL_ID:
            case COMBAT_ID:
            case MINING_ID:
            case WOODCUTTING_ID:
            case FARMING_ID:
            case WHITELIST_ID:
                return true;
        }

        return true;
    }

    private static Container getContainer(int guiId, int windowId, BlocklingEntity blockling, PlayerEntity player)
    {
        switch (guiId)
        {
            case EQUIPMENT_ID: return new EquipmentContainer(windowId, player, blockling.getEquipment());
//            case UTILITY_ID: return new UtilityContainer(windowId, player, blockling);
        }

        return null;
    }

    private static Screen getScreen(int guiId, Container container, BlocklingEntity blockling, PlayerEntity player)
    {
        switch (guiId)
        {
            case STATS_ID: return new StatsScreen(blockling, player);
            case TASKS_ID: return new TasksScreen(blockling, player);
            case EQUIPMENT_ID: return new EquipmentScreen((EquipmentContainer) container, blockling, player);
//            case UTILITY_ID: return new UtilityScreen(container, blockling, player);
            case GENERAL_ID: return new SkillsScreen(blockling, player, BlocklingSkillGroups.GENERAL);
            case COMBAT_ID: return new SkillsScreen(blockling, player, BlocklingSkillGroups.COMBAT);
            case MINING_ID: return new SkillsScreen(blockling, player, BlocklingSkillGroups.MINING);
            case WOODCUTTING_ID: return new SkillsScreen(blockling, player, BlocklingSkillGroups.WOODCUTTING);
            case FARMING_ID: return new SkillsScreen(blockling, player, BlocklingSkillGroups.FARMING);
//            case WHITELIST_ID: return new WhitelistScreen(blockling, player);
        }

        return null;
    }
}