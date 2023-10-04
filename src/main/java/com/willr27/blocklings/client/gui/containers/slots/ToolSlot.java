package com.willr27.blocklings.client.gui.containers.slots;

import com.willr27.blocklings.util.ToolUtil;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * A container slot for a tool.
 */
public class ToolSlot extends Slot
{
    /**
     * @param inventory the corresponding inventory.
     * @param inventoryIndex the inventory index represented by the slot.
     * @param x x location in the gui.
     * @param y y location in the gui.
     */
    public ToolSlot(@Nonnull Container inventory, int inventoryIndex, int x, int y)
    {
        super(inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        return ToolUtil.isTool(stack);
    }
}
