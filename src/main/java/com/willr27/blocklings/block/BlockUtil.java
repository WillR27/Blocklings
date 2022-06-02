package com.willr27.blocklings.block;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.BlocklingsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A class containing utility functions for blocks.
 */
public class BlockUtil
{
    /**
     * Initialises any fields used by the class.
     * E.g. the list of ores based on the user's config and the ores tag.
     */
    public static void init()
    {
        initOres(BlocklingsConfig.COMMON.additionalOres.get(), BlocklingsConfig.COMMON.excludedOres.get(), "forge:ores");
        initTrees(BlocklingsConfig.COMMON.customTrees.get());
    }

    /**
     * The list of blocks that are considered ores.
     */
    @Nonnull
    public static Set<Block> ORES = new HashSet<>();

    /**
     * Initialises the given set using the given config lists and tags.
     *
     * @param additionalBlocks the list of additional blocks to add.
     * @param excludedBlocks the list of blocks to ensure are excluded.
     * @param tags the tags to use to find blocks to add to the set.
     */
    public static void initOres(@Nonnull List<? extends String> additionalBlocks, @Nonnull List<? extends String> excludedBlocks, @Nonnull String... tags)
    {
        ORES.clear();

        for (ResourceLocation entry : Registry.BLOCK.keySet())
        {
            Block block = Registry.BLOCK.get(entry);

            if (!block.getTags().stream().anyMatch(r ->
            {
                for (String tag : tags)
                {
                    if (r.toString().equals(tag))
                    {
                        return true;
                    }
                }

                return false;
            }))
            {
                continue;
            }

            if (excludedBlocks.contains(entry.toString()))
            {
                continue;
            }

            ORES.add(block);
        }

        for (String entry : additionalBlocks)
        {
            Block block = Registry.BLOCK.get(new ResourceLocation(entry));

            if (block.is(Blocks.AIR))
            {
                continue;
            }

            if (excludedBlocks.contains(entry))
            {
                continue;
            }

            ORES.add(block);
        }
    }

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
     * Represents the 3 blocks that make up a tree.
     */
    public static class Tree
    {
        /**
         * The block that makes up the trunk of the tree.
         */
        @Nonnull
        public final Block log;

        /**
         * The block that makes up the leaves of the tree.
         */
        @Nonnull
        public final Block leaves;

        /**
         * The sapling block for the tree.
         */
        @Nonnull
        public final Block sapling;

        /**
         * @param log the log block.
         * @param leaves the leaves block.
         * @param sapling the sapling block.
         */
        public Tree(@Nonnull Block log, @Nonnull Block leaves, @Nonnull Block sapling)
        {
            this.log = log;
            this.leaves = leaves;
            this.sapling = sapling;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Tree)
            {
                Tree tree = (Tree) obj;

                return tree.log == log && tree.leaves == leaves && tree.sapling == sapling;
            }

            return super.equals(obj);
        }
    }

    /**
     * The list of trees.
     */
    @Nonnull
    public static List<Tree> TREES = new ArrayList<>();

    /**
     * Initialises the given set using the given config lists and tags.
     *
     * @param customTrees the custom trees to add.
     */
    public static void initTrees(@Nonnull List<? extends String> customTrees)
    {
        TREES.clear();

        TREES.add(new Tree(Blocks.ACACIA_LOG, Blocks.ACACIA_LEAVES, Blocks.ACACIA_SAPLING));
        TREES.add(new Tree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, Blocks.BIRCH_SAPLING));
        TREES.add(new Tree(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_LEAVES, Blocks.DARK_OAK_SAPLING));
        TREES.add(new Tree(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, Blocks.JUNGLE_SAPLING));
        TREES.add(new Tree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, Blocks.OAK_SAPLING));
        TREES.add(new Tree(Blocks.SPRUCE_LOG, Blocks.SPRUCE_LEAVES, Blocks.SPRUCE_SAPLING));

        for (String treeString : customTrees)
        {
            Runnable warn = () -> Blocklings.LOGGER.warn("The custom tree \"" + treeString + "\" is invalid and won't be added. Should look like \"[minecraft:oak_log; minecraft:oak_leaf; minecraft:oak_sapling]\".");

            if (!treeString.startsWith("[") || !treeString.endsWith("]") || treeString.length() < 10)
            {
                warn.run();

                continue;
            }

            String[] splitTreeString = treeString.substring(1, treeString.length() - 1).split("; ");

            if (splitTreeString.length != 3)
            {
                warn.run();

                continue;
            }

            Block log = Registry.BLOCK.get(new ResourceLocation(splitTreeString[0]));
            Block leaves = Registry.BLOCK.get(new ResourceLocation(splitTreeString[1]));
            Block sapling = Registry.BLOCK.get(new ResourceLocation(splitTreeString[2]));

            if (log.is(Blocks.AIR) || leaves.is(Blocks.AIR) || sapling.is(Blocks.AIR))
            {
                warn.run();

                continue;
            }

            Tree tree = new Tree(log, leaves, sapling);

            if (!TREES.contains(tree))
            {
                TREES.add(tree);
            }
        }
    }

    /**
     * @param block the block to check.
     * @return true if the block is a log.
     */
    public static boolean isLog(@Nonnull Block block)
    {
        return TREES.stream().anyMatch(tree -> tree.log == block);
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
        for (Tree tree : TREES)
        {
            if (new ItemStack(tree.log).getItem() == blockItem)
            {
                return tree.log;
            }
        }

        return null;
    }

    /**
     * @param block the block to check.
     * @return true if the block is leaves.
     */
    public static boolean isLeaves(@Nonnull Block block)
    {
        return TREES.stream().anyMatch(tree -> tree.leaves == block);
    }

    /**
     * @param blockItem a block in item form.
     * @return true if the block item is a leaf.
     */
    public static boolean isLeaves(@Nonnull Item blockItem)
    {
        return getLeaves(blockItem) != null;
    }

    /**
     * Gets the block of the given block item.
     *
     * @param blockItem a block in item form.
     * @return the block if it is leaves else null.
     */
    @Nullable
    public static Block getLeaves(@Nonnull Item blockItem)
    {
        for (Tree tree : TREES)
        {
            if (new ItemStack(tree.leaves).getItem() == blockItem)
            {
                return tree.leaves;
            }
        }

        return null;
    }

    /**
     * @param block the block to check.
     * @return true if the block is a sapling.
     */
    public static boolean isSapling(@Nonnull Block block)
    {
        return TREES.stream().anyMatch(tree -> tree.sapling == block);
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
        for (Tree tree : TREES)
        {
            if (new ItemStack(tree.sapling).getItem() == blockItem)
            {
                return tree.sapling;
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
        for (Tree tree : TREES)
        {
            if (tree.log == logBlock)
            {
                return tree.sapling;
            }
        }

        return null;
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
        return getLeaves(blockItem) != null;
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
     * Checks whether all adjacent blocks are solid.
     *
     * @param world the world the block is in.
     * @param blockPos the block position to test.
     * @return true if all adjacent blocks are solid.
     */
    public static boolean areAllAdjacentBlocksSolid(@Nonnull World world, @Nonnull BlockPos blockPos)
    {
        return !Arrays.stream(getAdjacentBlockPositions(blockPos)).anyMatch(blockPos1 -> !world.getBlockState(blockPos1).getMaterial().isSolid());
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

    /**
     * @return the distance squared between two blocks from center to center.
     */
    public static double distanceSq(@Nonnull BlockPos blockPos1, @Nonnull BlockPos blockPos2)
    {
        return blockPos1.distSqr(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), false);
    }
}
