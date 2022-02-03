package com.willr27.blocklings.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockUtil
{
    /**
     * The list of blocks that are considered ores.
     */
    @Nonnull
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

    /**
     * @param block the block to check.
     * @return true if the block is an ore.
     */
    public static boolean isOre(@Nonnull Block block)
    {
        return ORES.contains(block);
    }

    /**
     * @param blockItem a block in item form.
     * @return true if the block item is an ore.
     */
    public static boolean isOre(@Nonnull Item blockItem)
    {
        return getOre(blockItem) != null;
    }

    /**
     * Gets the block of the given block item.
     *
     * @param blockItem a block in item form.
     * @return the block if it is an ore else null.
     */
    @Nullable
    public static Block getOre(@Nonnull Item blockItem)
    {
        for (Block ore : ORES)
        {
            if (new ItemStack(ore).getItem() == blockItem)
            {
                return ore;
            }
        }

        return null;
    }

    /**
     * The list of blocks that are considered logs.
     */
    @Nonnull
    public static List<Block> LOGS = new ArrayList<Block>()
    {{
        add(Blocks.OAK_LOG);
        add(Blocks.BIRCH_LOG);
        add(Blocks.SPRUCE_LOG);
        add(Blocks.JUNGLE_LOG);
        add(Blocks.ACACIA_LOG);
        add(Blocks.DARK_OAK_LOG);
    }};

    /**
     * @param block the block to check.
     * @return true if the block is a log.
     */
    public static boolean isLog(@Nonnull Block block)
    {
        return LOGS.contains(block);
    }

    /**
     * @param blockItem a block in item form.
     * @return true if the block item is a log.
     */
    public static boolean isLog(@Nonnull Item blockItem)
    {
        return getLog(blockItem) != null;
    }

    /**
     * Gets the block of the given block item.
     *
     * @param blockItem a block in item form.
     * @return the block if it is a log else null.
     */
    @Nullable
    public static Block getLog(@Nonnull Item blockItem)
    {
        for (Block log : LOGS)
        {
            if (new ItemStack(log).getItem() == blockItem)
            {
                return log;
            }
        }

        return null;
    }

    /**
     * The list of blocks that are considered leaves.
     */
    @Nonnull
    public static List<Block> LEAVES = new ArrayList<Block>()
    {{
        add(Blocks.OAK_LEAVES);
        add(Blocks.BIRCH_LEAVES);
        add(Blocks.SPRUCE_LEAVES);
        add(Blocks.JUNGLE_LEAVES);
        add(Blocks.ACACIA_LEAVES);
        add(Blocks.DARK_OAK_LEAVES);
    }};

    /**
     * @param block the block to check.
     * @return true if the block is a leaf.
     */
    public static boolean isLeaf(@Nonnull Block block)
    {
        return LEAVES.contains(block);
    }

    /**
     * @param blockItem a block in item form.
     * @return true if the block item is a leaf.
     */
    public static boolean isLeaf(@Nonnull Item blockItem)
    {
        return getLeaf(blockItem) != null;
    }

    /**
     * Gets the block of the given block item.
     *
     * @param blockItem a block in item form.
     * @return the block if it is a leaf else null.
     */
    @Nullable
    public static Block getLeaf(@Nonnull Item blockItem)
    {
        for (Block leaf : LEAVES)
        {
            if (new ItemStack(leaf).getItem() == blockItem)
            {
                return leaf;
            }
        }

        return null;
    }

    /**
     * The list of blocks that are considered saplings.
     */
    @Nonnull
    public static List<Block> SAPLINGS = new ArrayList<Block>()
    {{
        add(Blocks.OAK_SAPLING);
        add(Blocks.BIRCH_SAPLING);
        add(Blocks.SPRUCE_SAPLING);
        add(Blocks.JUNGLE_SAPLING);
        add(Blocks.ACACIA_SAPLING);
        add(Blocks.DARK_OAK_SAPLING);
    }};

    /**
     * The map of logs to saplings.
     */
    @Nonnull
    public static Map<Block, Block> LOGS_TO_SAPLINGS = new HashMap<Block, Block>()
    {{
        put(Blocks.OAK_LOG, Blocks.OAK_SAPLING);
        put(Blocks.BIRCH_LOG, Blocks.BIRCH_SAPLING);
        put(Blocks.SPRUCE_LOG, Blocks.SPRUCE_SAPLING);
        put(Blocks.JUNGLE_LOG, Blocks.JUNGLE_SAPLING);
        put(Blocks.ACACIA_LOG, Blocks.ACACIA_SAPLING);
        put(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_SAPLING);
    }};

    /**
     * @param block the block to check.
     * @return true if the block is a sapling.
     */
    public static boolean isSapling(@Nonnull Block block)
    {
        return SAPLINGS.contains(block);
    }

    /**
     * @param blockItem a block in item form.
     * @return true if the block item is a sapling.
     */
    public static boolean isSapling(@Nonnull Item blockItem)
    {
        return getSapling(blockItem) != null;
    }

    /**
     * Gets the block of the given block item.
     *
     * @param blockItem a block in item form.
     * @return the block if it is a sapling else null.
     */
    @Nullable
    public static Block getSapling(@Nonnull Item blockItem)
    {
        for (Block sapling : SAPLINGS)
        {
            if (new ItemStack(sapling).getItem() == blockItem)
            {
                return sapling;
            }
        }

        return null;
    }

    /**
     * Gets the sapling block for the given log.
     *
     * @param logBlock the log block.
     * @return the sapling if the log block is recognised, else null.
     */
    @Nullable
    public static Block getSaplingFromLog(@Nonnull Block logBlock)
    {
        return LOGS_TO_SAPLINGS.get(logBlock);
    }

    /**
     * The list of blocks that are considered crops.
     */
    @Nonnull
    public static List<Block> CROPS = new ArrayList<Block>()
    {{
        add(Blocks.WHEAT);
        add(Blocks.CARROTS);
        add(Blocks.POTATOES);
        add(Blocks.BEETROOTS);
        add(Blocks.PUMPKIN);
        add(Blocks.MELON);
    }};

    /**
     * @param block the block to check.
     * @return true if the block is a crop.
     */
    public static boolean isCrop(@Nonnull Block block)
    {
        return CROPS.contains(block);
    }

    /**
     * @param blockItem a block in item form.
     * @return true if the block item is a crop.
     */
    public static boolean isCrop(@Nonnull Item blockItem)
    {
        return getLeaf(blockItem) != null;
    }

    /**
     * Gets the block of the given block item.
     *
     * @param blockItem a block in item form.
     * @return the block if it is a crop else null.
     */
    @Nullable
    public static Block getCrop(@Nonnull Item blockItem)
    {
        for (Block crop : CROPS)
        {
            if (new ItemStack(crop).getItem() == blockItem)
            {
                return crop;
            }
        }

        return null;
    }

    /**
     * The list of blocks that are considered dirt.
     */
    @Nonnull
    public static List<Block> DIRTS = new ArrayList<Block>()
    {{
        add(Blocks.DIRT);
        add(Blocks.GRASS_BLOCK);
    }};

    /**
     * @param percentage the percentage to convert to block break progress.
     * @return the block break progress.
     */
    public static int calcBlockBreakProgress(float percentage)
    {
        return (int) (10 * percentage);
    }

    /**
     * Gets the positions adjacent to the given block pos.
     * Does not include diagonals.
     *
     * @param blockPos the position to get the adjacent positions of.
     * @return an array of the adjacent block positions.
     */
    @Nonnull
    public static BlockPos[] getAdjacentBlockPositions(@Nonnull BlockPos blockPos)
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

    /**
     * Gets the positions surrounding the given block pos.
     * Includes diagonals.
     *
     * @param blockPos the position to get the surrounding positions of.
     * @return an array of the surrounding block positions.
     */
    @Nonnull
    public static BlockPos[] getSurroundingBlockPositions(@Nonnull BlockPos blockPos)
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
