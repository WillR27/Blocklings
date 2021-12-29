package com.willr27.blocklings.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

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

    public static List<Block> LOGS = new ArrayList<Block>()
    {{
        add(Blocks.OAK_LOG);
        add(Blocks.BIRCH_LOG);
        add(Blocks.SPRUCE_LOG);
        add(Blocks.JUNGLE_LOG);
        add(Blocks.ACACIA_LOG);
        add(Blocks.DARK_OAK_LOG);
    }};

    public static boolean isLog(Block block)
    {
        return LOGS.contains(block);
    }

    public static boolean isLog(Item item)
    {
        return getLog(item) != null;
    }

    public static Block getLog(Item item)
    {
        for (Block log : LOGS)
        {
            if (new ItemStack(log).getItem() == item) // TODO: CACHE THESE ITEMS
            {
                return log;
            }
        }

        return null;
    }

    public static List<Block> LEAVES = new ArrayList<Block>()
    {{
        add(Blocks.OAK_LEAVES);
        add(Blocks.BIRCH_LEAVES);
        add(Blocks.SPRUCE_LEAVES);
        add(Blocks.JUNGLE_LEAVES);
        add(Blocks.ACACIA_LEAVES);
        add(Blocks.DARK_OAK_LEAVES);
    }};

    public static boolean isLeaf(Block block)
    {
        return LEAVES.contains(block);
    }

    public static boolean isLeaf(Item item)
    {
        return getLeaf(item) != null;
    }

    public static Block getLeaf(Item item)
    {
        for (Block leaf : LEAVES)
        {
            if (new ItemStack(leaf).getItem() == item) // TODO: CACHE THESE ITEMS
            {
                return leaf;
            }
        }

        return null;
    }

    public static int calcBlockBreakProgress(float percentage)
    {
        return (int) (10 * percentage);
    }

    public static BlockPos[] getAdjacentBlockPositions(BlockPos blockPos)
    {
        return new BlockPos[]
        {
            blockPos.offset(-1, 0, 0),
            blockPos.offset(1, 0, 0),
            blockPos.offset(0, -1, 0),
            blockPos.offset(0, 1, 0),
            blockPos.offset(0, 0, -1),
            blockPos.offset(0, 0, 1),
        };
    }

    public static BlockPos[] getSurroundingBlockPositions(BlockPos blockPos)
    {
        return new BlockPos[]
        {
            blockPos.offset(-1, -1, -1),
            blockPos.offset(-1, -1, 0),
            blockPos.offset(-1, -1, 1),
            blockPos.offset(-1, 0, -1),
            blockPos.offset(-1, 0, 0),
            blockPos.offset(-1, 0, 1),
            blockPos.offset(-1, 1, -1),
            blockPos.offset(-1, 1, 0),
            blockPos.offset(-1, 1, 1),
            blockPos.offset(0, -1, -1),
            blockPos.offset(0, -1, 0),
            blockPos.offset(0, -1, 1),
            blockPos.offset(0, 0, -1),
            blockPos.offset(0, 0, 1),
            blockPos.offset(0, 1, -1),
            blockPos.offset(0, 1, 0),
            blockPos.offset(0, 1, 1),
            blockPos.offset(1, -1, -1),
            blockPos.offset(1, -1, 0),
            blockPos.offset(1, -1, 1),
            blockPos.offset(1, 0, -1),
            blockPos.offset(1, 0, 0),
            blockPos.offset(1, 0, 1),
            blockPos.offset(1, 1, -1),
            blockPos.offset(1, 1, 0),
            blockPos.offset(1, 1, 1),
        };
    }
}
