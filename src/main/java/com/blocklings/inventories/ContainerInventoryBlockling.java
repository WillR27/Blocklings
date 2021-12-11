package com.blocklings.inventories;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.util.helpers.GuiHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryBlockling extends Container
{
    private EntityBlockling blockling;
    private InventoryBlockling blocklingInv;
    private InventoryPlayer playerInv;

    private int blocklingInventoryX = 36;
    private int blocklingInventoryY = 8 + GuiHelper.YOFFSET;
    private int playerInventoryX = 36;
    private int playerInventoryY = 84 + GuiHelper.YOFFSET;

    public ContainerInventoryBlockling(EntityBlockling blockling, InventoryPlayer playerInv, InventoryBlockling blocklingInv)
    {
        this.blockling = blockling;
        this.blocklingInv = blocklingInv;
        this.playerInv = playerInv;

        bindBlocklingInventory();
        bindPlayerInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    private void bindBlocklingInventory()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new SlotInventory(blockling, blocklingInv, j + (i * 9) + 3, blocklingInventoryX + (j * 18), blocklingInventoryY + (i * 18)));
            }
        }
    }

    private void bindPlayerInventory()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, playerInventoryX + (j * 18), playerInventoryY + (i * 18)));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(playerInv, i, playerInventoryX + (i * 18), playerInventoryY + 58));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemStack = slot.getStack();
            stack = itemStack.copy();

            if (slotIndex >= 36)
            {
                int unlockedSlots = blockling.getUnlockedSlots();
                int u = unlockedSlots / 12;

                if (!this.mergeItemStack(itemStack, 0, 3 * u, false))
                {
                    if (!this.mergeItemStack(itemStack, 9, 12 * u, false))
                    {
                        if (!this.mergeItemStack(itemStack, 18, 21 * u, false))
                        {
                            if (!this.mergeItemStack(itemStack, 27, 30 * u, false))
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
            }
            else
            {
                if (!this.mergeItemStack(itemStack, 36, 72, false))
                {
                    return ItemStack.EMPTY;
                }
            }
        }

        return stack;
    }
}
