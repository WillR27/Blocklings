package com.blocklings.guis;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.inventories.ContainerEquipmentBlockling;
import com.blocklings.inventories.ContainerInventoryBlockling;
import com.blocklings.util.helpers.GuiHelper.Tab;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int entityID, int unused1, int unused2)
    {
        Entity entity = world.getEntityByID(entityID);

        if (entity != null && entity instanceof EntityBlockling)
        {
            EntityBlockling blockling = (EntityBlockling) entity;

            if (id == Tab.INVENTORY.id)
            {
                return new ContainerInventoryBlockling(blockling, player.inventory, blockling.inv);
            }
            else if (id == Tab.EQUIPMENT.id)
            {
                return new ContainerEquipmentBlockling(player.inventory, blockling.inv);
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int entityID, int unused1, int unused2)
    {
        Entity entity = world.getEntityByID(entityID);

        if (entity != null && entity instanceof EntityBlockling)
        {
            EntityBlockling blockling = (EntityBlockling) entity;

            if (id == Tab.STATS.id)
            {
                return new GuiBlocklingStats(blockling, player);
            }
            else if (id == Tab.INVENTORY.id)
            {
                return new GuiBlocklingInventory(player.inventory, blockling.inv, blockling, player);
            }
            else if (id == Tab.GENERAL.id)
            {
                return new GuiBlocklingGeneral(blockling, player);
            }
            else if (id == Tab.COMBAT.id)
            {
                return new GuiBlocklingCombat(blockling, player);
            }
            else if (id == Tab.MINING.id)
            {
                return new GuiBlocklingMining(blockling, player);
            }
            else if (id == Tab.WOODCUTTING.id)
            {
                return new GuiBlocklingWoodcutting(blockling, player);
            }
            else if (id == Tab.FARMING.id)
            {
                return new GuiBlocklingFarming(blockling, player);
            }
            else if (id == Tab.EQUIPMENT.id)
            {
                return new GuiBlocklingEquipment(player.inventory, blockling.inv, blockling, player);
            }
        }

        return null;
    }
}
