package com.willr27.blocklings.gui.containers.slots;

import com.willr27.blocklings.item.ToolUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ToolSlot extends Slot
{
    public ToolSlot(IInventory inventory, int inventoryIndex, int x, int y)
    {
        super(inventory, inventoryIndex, x, y);
    }

    public boolean mayPlace(ItemStack stack)
    {
        return ToolUtil.isTool(stack);
    }
}
