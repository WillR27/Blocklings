package com.willr27.blocklings.gui.containers;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.containers.slots.ToolSlot;
import com.willr27.blocklings.inventory.inventories.EquipmentInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * The container for the blockling's equipment.
 */
public class EquipmentContainer extends Container
{
    /**
     * The starting x location of the player's slots.
     */
    private static final int PLAYER_INV_X = 8;

    /**
     * The starting y location of the player's slots.
     */
    private static final int PLAYER_INV_Y = 74;

    /**
     * @param windowId the window id.
     * @param player the player opening the container.
     * @param blockling the blockling.
     */
    public EquipmentContainer(int windowId, @Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        super(null, windowId);

        if (!player.level.isClientSide)
        {
            addSlotListener((IContainerListener) player);
        }

        EquipmentInventory blocklingInv = blockling.getEquipment();

        addSlot(new ToolSlot(blocklingInv, EquipmentInventory.TOOL_MAIN_HAND, 12, 44));
        addSlot(new ToolSlot(blocklingInv, EquipmentInventory.TOOL_OFF_HAND, 32, 44));

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 6; j++)
            {
                addSlot(new Slot(blocklingInv, j + i * 6 + 2, PLAYER_INV_X + (j * 18) + 50, PLAYER_INV_Y + (i * 18) - 66));
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
    public boolean stillValid(@Nonnull PlayerEntity player)
    {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int clickedSlotIndex)
    {
        Slot clickedSlot = this.slots.get(clickedSlotIndex);

        if (!clickedSlot.hasItem())
        {
            return ItemStack.EMPTY;
        }

        ItemStack clickedSlotStack = clickedSlot.getItem();

        if (clickedSlotIndex >= 20 && clickedSlotIndex <= 56)
        {
            if (!this.moveItemStackTo(clickedSlotStack, 0, 20, false))
            {
                return ItemStack.EMPTY;
            }
        }
        else
        {
            if (!this.moveItemStackTo(clickedSlotStack, 20, 56, true))
            {
                return ItemStack.EMPTY;
            }
        }

        return ItemStack.EMPTY;
    }
}
