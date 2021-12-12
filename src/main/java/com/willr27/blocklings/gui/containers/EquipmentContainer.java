package com.willr27.blocklings.gui.containers;

import com.willr27.blocklings.gui.containers.slots.ToolSlot;
import com.willr27.blocklings.gui.containers.slots.UtilitySlot;
import com.willr27.blocklings.inventory.inventories.EquipmentInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class EquipmentContainer extends Container
{
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 74;

    public EquipmentContainer(int id, PlayerEntity player, EquipmentInventory blocklingInv)
    {
        super(null, id);

        if (!player.level.isClientSide)
        {
            addSlotListener((IContainerListener) player);
        }

        addSlot(new UtilitySlot(blocklingInv, EquipmentInventory.UTILITY_1, 12, -2));
        addSlot(new UtilitySlot(blocklingInv, EquipmentInventory.UTILITY_2, 32, -2));
        addSlot(new ToolSlot(blocklingInv, EquipmentInventory.TOOL_MAIN_HAND, 12, 52));
        addSlot(new ToolSlot(blocklingInv, EquipmentInventory.TOOL_OFF_HAND, 32, 52));

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 6; j++)
            {
                addSlot(new Slot(blocklingInv, j + i * 6 + 4, PLAYER_INV_X + (j * 18) + 50, PLAYER_INV_Y + (i * 18) - 66));
            }
        }

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(player.inventory, j + i * 9 + 9, PLAYER_INV_X + (j * 18), PLAYER_INV_Y + (i * 18)));
            }
        }
        for (int i = 0; i < 9; i++)
        {
            addSlot(new Slot(player.inventory, i, PLAYER_INV_X + (i * 18), PLAYER_INV_Y + 58));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int clickedSlotIndex)
    {
        Slot clickedSlot = this.slots.get(clickedSlotIndex);

        if (!clickedSlot.hasItem())
        {
            return ItemStack.EMPTY;
        }

        ItemStack clickedSlotStack = clickedSlot.getItem();

        if (clickedSlotIndex >= 22 && clickedSlotIndex <= 58)
        {
            if (!this.moveItemStackTo(clickedSlotStack, 0, 22, false))
            {
                return ItemStack.EMPTY;
            }
        }
        else
        {
            if (!this.moveItemStackTo(clickedSlotStack, 22, 58, true))
            {
                return ItemStack.EMPTY;
            }
        }

        return ItemStack.EMPTY;
    }
}
