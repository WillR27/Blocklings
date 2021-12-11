package com.blocklings.inventories;

import com.blocklings.entities.EntityBlockling;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInventory extends Slot
{
    EntityBlockling blockling;
    int index;

    public SlotInventory(EntityBlockling blockling, IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
        this.blockling = blockling;
        this.index = index;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        int unlockedSlots = blockling.getUnlockedSlots();
        int u = unlockedSlots / 12;
        if (unlockedSlots < 36)
        {
            if (index < 3 || (index >= 6 + (3 * (u - 1)) && index < 12) || (index >= 15 + (3 * (u - 1)) && index < 21) || (index >= 24 + (3 * (u - 1)) && index < 30) || (index >= 33 + (3 * (u - 1)) && index < 39))
            {
                return false;
            }
        }

        return true;
    }
}
