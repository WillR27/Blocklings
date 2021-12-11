package com.willr27.blocklings.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockUtil
{
    public static List<Block> ORES = new ArrayList<Block>()
    {{
        add(Blocks.COAL_ORE);
        add(Blocks.IRON_ORE);
        add(Blocks.GOLD_ORE);
        add(Blocks.LAPIS_ORE);
        add(Blocks.REDSTONE_ORE);
        add(Blocks.EMERALD_ORE);
        add(Blocks.DIAMOND_ORE);
        add(Blocks.NETHER_QUARTZ_ORE);
        add(Blocks.ANCIENT_DEBRIS);
    }};

    public static boolean isOre(Block block)
    {
        return ORES.contains(block);
    }

    public static boolean isOre(Item item)
    {
        return getOre(item) != null;
    }

    public static Block getOre(Item item)
    {
        for (Block ore : ORES)
        {
            if (new ItemStack(ore).getItem() == item) // TODO: CACHE THESE ITEMS
            {
                return ore;
            }
        }

        return null;
    }

    public static int calcBlockBreakProgress(float percentage)
    {
        return (int) (10 * percentage);
    }
}
