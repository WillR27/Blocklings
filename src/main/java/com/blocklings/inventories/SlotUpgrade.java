package com.blocklings.inventories;

import com.blocklings.util.helpers.ItemHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot
{
    public SlotUpgrade(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return ItemHelper.isUpgradeMaterial(stack);
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}
