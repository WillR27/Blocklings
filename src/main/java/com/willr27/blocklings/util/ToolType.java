package com.willr27.blocklings.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ToolType
{
    WEAPON,
    PICKAXE,
    AXE,
    HOE;

    public boolean is(ItemStack stack)
    {
        return isTooltype(this, stack.getItem());
    }

    public boolean is(Item item)
    {
        return isTooltype(this, item);
    }

    public static boolean isTooltype(ToolType type, Item item)
    {
        switch (type)
        {
            case WEAPON: return ToolUtil.isWeapon(item);
            case PICKAXE: return ToolUtil.isPickaxe(item);
            case AXE: return ToolUtil.isAxe(item);
            case HOE: return ToolUtil.isHoe(item);
        }

        return false;
    }

    public static ToolType getToolType(ItemStack stack)
    {
        return getToolType(stack.getItem());
    }

    public static ToolType getToolType(Item item)
    {
        for (ToolType type : values())
        {
            if (isTooltype(type, item)) return type;
        }

        return null;
    }
}
