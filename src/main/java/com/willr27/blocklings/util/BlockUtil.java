package com.willr27.blocklings.util;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.config.BlocklingsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A class containing utility functions for blocks.
 */
public class BlockUtil
{
    /**
     * The most recent world to load (used to then lazy load the list of valid containers).
     */
    @Nullable
    public static World latestWorld;

    /**
     * The list of blocks that are considered containers.
     */
    @Nonnull
    public static Lazy<List<Block>> CONTAINERS = Lazy.of(BlockUtil::createContainersList);

    /**
     * @return the set of blocks that are regarded as containers.
     */
    @Nonnull
    public static List<Block> createContainersList()
    {
        Blocklings.LOGGER.info("Creating valid containers set.");

        if (latestWorld == null)
        {
            Blocklings.LOGGER.error("Tried to initialise valid containers set before a world was loaded!");

            return new ArrayList<>();
        }

        List<Block> containers = new ArrayList<>();
        BlockPos posToReplace = new BlockPos(0, 0, 0);

        for (Block block : Registry.BLOCK)
        {
//            BlockState currentState = latestWorld.getBlockState(posToReplace);
            TileEntity tileEntity = block.defaultBlockState().createTileEntity(latestWorld);

            if (tileEntity == null)
            {
                continue;
            }

            for (Direction direction : Direction.values())
            {
                // If the block has an item handler capability, it is considered a container.
                if (tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).isPresent())
                {
                    containers.add(block);

                    break;
                }
            }
        }

        return containers;
    }

    /**
     * Checks if the given block is a container.
     *
     * @param block the block to check.
     * @return true if the block is a container, false otherwise.
     */
    public static boolean isContainer(@Nonnull Block block)
    {
        return CONTAINERS.get().contains(block);
    }

    /**
     * The list of blocks that are considered ores.
     */
    @Nonnull
    public static Lazy<Set<Block>> ORES = Lazy.of(BlockUtil::createOresList);

    /**
     * @return the list of blocks that are regarded as ores.
     */
    @Nonnull
    public static Set<Block> createOresList()
    {
        Blocklings.LOGGER.info("Creating ores list.");

        Set<Block> ores = new HashSet<>();

        List<? extends String> additionalBlocks = BlocklingsConfig.COMMON.additionalOres.get();
        List<? extends String> excludedBlocks = BlocklingsConfig.COMMON.excludedOres.get();

        ores.clear();

        for (Block block : Tags.Blocks.ORES.getValues())
        {
            if (excludedBlocks.contains(block.getRegistryName().toString()))
            {
                continue;
            }

            ores.add(block);
        }

        for (String entry : additionalBlocks)
        {
            Runnable warn = () -> Blocklings.LOGGER.warn("Skipping additional ore \"" + entry + "\".");

            Block block = Registry.BLOCK.get(new ResourceLocation(entry));

            if (block.is(Blocks.AIR))
            {
                warn.run();

                continue;
            }

            if (excludedBlocks.contains(entry))
            {
                warn.run();

                continue;
            }

            ores.add(block);
        }

        return ores;
    }

    /**
     * @param block the block to check.
     * @return true if the block is an ore.
     */
    public static boolean isOre(@Nonnull Block block)
    {
        return ORES.get().contains(block);
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
        for (Block ore : ORES.get())
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
    public static class TreeTuple
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
        public TreeTuple(@Nonnull Block log, @Nonnull Block leaves, @Nonnull Block sapling)
        {
            this.log = log;
            this.leaves = leaves;
            this.sapling = sapling;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof TreeTuple)
            {
                TreeTuple tree = (TreeTuple) obj;

                return tree.log == log && tree.leaves == leaves && tree.sapling == sapling;
            }

            return super.equals(obj);
        }
    }

    /**
     * The list of trees.
     */
    @Nonnull
    public static Lazy<List<TreeTuple>> TREES = Lazy.of(BlockUtil::createTreesList);

    /**
     * @return the list of trees.
     */
    @Nonnull
    public static List<TreeTuple> createTreesList()
    {
        Blocklings.LOGGER.info("Creating trees list.");

        List<TreeTuple> trees = new ArrayList<>();

        List<? extends String> customTrees = BlocklingsConfig.COMMON.customTrees.get();

        trees.clear();

        trees.add(new TreeTuple(Blocks.ACACIA_LOG, Blocks.ACACIA_LEAVES, Blocks.ACACIA_SAPLING));
        trees.add(new TreeTuple(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, Blocks.BIRCH_SAPLING));
        trees.add(new TreeTuple(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_LEAVES, Blocks.DARK_OAK_SAPLING));
        trees.add(new TreeTuple(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, Blocks.JUNGLE_SAPLING));
        trees.add(new TreeTuple(Blocks.OAK_LOG, Blocks.OAK_LEAVES, Blocks.OAK_SAPLING));
        trees.add(new TreeTuple(Blocks.SPRUCE_LOG, Blocks.SPRUCE_LEAVES, Blocks.SPRUCE_SAPLING));

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

            TreeTuple tree = new TreeTuple(log, leaves, sapling);

            if (!trees.contains(tree))
            {
                trees.add(tree);
            }
        }

        return trees;
    }

    /**
     * @param block the block to check.
     * @return true if the block is a log.
     */
    public static boolean isLog(@Nonnull Block block)
    {
        return TREES.get().stream().anyMatch(tree -> tree.log == block);
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
        for (TreeTuple tree : TREES.get())
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
        return TREES.get().stream().anyMatch(tree -> tree.leaves == block);
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
     * Gets the leaves block of the given log block.
     *
     * @return the block if it is leaves else null.
     */
    @Nullable
    public static Block getLeaves(@Nonnull Block logBlock)
    {
        for (TreeTuple tree : TREES.get())
        {
            if (tree.log == logBlock)
            {
                return tree.leaves;
            }
        }

        return null;
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
        for (TreeTuple tree : TREES.get())
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
        return TREES.get().stream().anyMatch(tree -> tree.sapling == block);
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
        for (TreeTuple tree : TREES.get())
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
        for (TreeTuple tree : TREES.get())
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
    public static Lazy<Set<Block>> CROPS = Lazy.of(BlockUtil::createCropsList);

    /**
     * @return the list of blocks that are regarded as crops.
     */
    @Nonnull
    public static Set<Block> createCropsList()
    {
        Blocklings.LOGGER.info("Creating crops list.");

        Set<Block> crops = new HashSet<>();

        crops.clear();

        crops.add(Blocks.WHEAT);
        crops.add(Blocks.BEETROOTS);
        crops.add(Blocks.CARROTS);
        crops.add(Blocks.POTATOES);
        crops.add(Blocks.PUMPKIN);
        crops.add(Blocks.MELON);

        for (String additionalString : BlocklingsConfig.COMMON.additionalCrops.get())
        {
            Runnable warn = () -> Blocklings.LOGGER.warn("Skipping additional crop \"" + additionalString + "\".");

            Block block = Registry.BLOCK.get(new ResourceLocation(additionalString));

            if (block.is(Blocks.AIR))
            {
                warn.run();

                continue;
            }

            crops.add(block);
        }

        for (String excludedString : BlocklingsConfig.COMMON.excludedCrops.get())
        {
            crops.remove(Registry.BLOCK.get(new ResourceLocation(excludedString)));
        }

        return crops;
    }

    /**
     * @param block the block to check.
     * @return true if the block is a crop.
     */
    public static boolean isCrop(@Nonnull Block block)
    {
        return CROPS.get().contains(block);
    }

    /**
     * @param blockItem a block in item form.
     * @return true if the block item is a crop.
     */
    public static boolean isCrop(@Nonnull Item blockItem)
    {
        return getCrop(blockItem) != null;
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
        for (Block crop : CROPS.get())
        {
            if (new ItemStack(crop).getItem() == blockItem)
            {
                return crop;
            }
        }

        return null;
    }

    /**
     * @param world the world the block is being checked in.
     * @param block the block to check.
     * @param pos the position in the world at which to check.
     * @return true if the block can be placed at the given location.
     */
    public static boolean canPlaceAt(@Nonnull World world, @Nonnull Block block, @Nonnull BlockPos pos)
    {
        return block.canSurvive(block.defaultBlockState(), world, pos);
    }

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
            // Blocks at the same level first.
            blockPos.offset(-1, 0, -1),
            blockPos.offset(-1, 0, 0),
            blockPos.offset(-1, 0, 1),
            blockPos.offset(0, 0, -1),
            blockPos.offset(0, 0, 1),
            blockPos.offset(1, 0, -1),
            blockPos.offset(1, 0, 0),
            blockPos.offset(1, 0, 1),
            // Then blocks below.
            blockPos.offset(-1, -1, -1),
            blockPos.offset(-1, -1, 0),
            blockPos.offset(-1, -1, 1),
            blockPos.offset(0, -1, -1),
            blockPos.offset(0, -1, 0),
            blockPos.offset(0, -1, 1),
            blockPos.offset(1, -1, -1),
            blockPos.offset(1, -1, 0),
            blockPos.offset(1, -1, 1),
            // Then blocks above.
            blockPos.offset(0, 1, -1),
            blockPos.offset(0, 1, 0),
            blockPos.offset(0, 1, 1),
            blockPos.offset(-1, 1, -1),
            blockPos.offset(-1, 1, 0),
            blockPos.offset(-1, 1, 1),
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
