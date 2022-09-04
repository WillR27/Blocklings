package com.willr27.blocklings.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Contains utility functions for things found in the world.
 */
public class WorldUtil
{
    /**
     * The default minimum number of leaves blocks for each log block to classify a tree as valid.
     */
    public static final float DEFAULT_MIN_LEAVES_TO_LOGS_RATIO = 1.0f;

    /**
     * @param world the world to search in.
     * @param blockPos the block position to start from (must be a log).
     * @param maxTreeLogsSize the max number of blocks that can make up a tree's logs.
     * @return a tree containing all the blocks that make up the tree.
     */
    @Nonnull
    public static Tree findTreeFromPos(@Nonnull World world, @Nonnull BlockPos blockPos, int maxTreeLogsSize, @Nonnull Predicate<BlockPos> isValidLogPos, @Nonnull Predicate<BlockPos> isValidLeavesPos)
    {
        Tree tree = new Tree();
        Block logBlock = world.getBlockState(blockPos).getBlock();

        if (!BlockUtil.isLog(logBlock))
        {
            return tree;
        }

        Block leavesBlock = BlockUtil.getLeaves(logBlock);
        Set<BlockPos> logBlockPositionsToTest = new HashSet<>();

        logBlockPositionsToTest.add(blockPos);
        tree.logs.add(blockPos);

        while (!logBlockPositionsToTest.isEmpty() && tree.logs.size() < maxTreeLogsSize)
        {
            BlockPos testBlockPos = logBlockPositionsToTest.stream().findFirst().get();

            for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(testBlockPos))
            {
                Block surroundingLogBlock = world.getBlockState(surroundingPos).getBlock();

                if (surroundingLogBlock == logBlock && isValidLogPos.test(surroundingPos))
                {
                    if (!tree.logs.contains(surroundingPos))
                    {
                        tree.logs.add(surroundingPos);
                        logBlockPositionsToTest.add(surroundingPos);
                    }
                }
                else if (surroundingLogBlock == leavesBlock && isValidLeavesPos.test(surroundingPos))
                {
                    if (!tree.leaves.contains(surroundingPos))
                    {
                        tree.leaves.add(surroundingPos);
                    }
                }
            }

            logBlockPositionsToTest.remove(testBlockPos);
        }

        return tree;
    }

    /**
     * Represents a tree.
     */
    public static class Tree
    {
        /**
         * The list of log block positions in the tree.
         */
        public final List<BlockPos> logs = new ArrayList<>();

        /**
         * The list of leaves block positions in the tree.
         */
        public final List<BlockPos> leaves = new ArrayList<>();

        /**
         * @return true if the tree is a valid tree (is the leaves to logs ratio valid).
         */
        public boolean isValid(float minLeavesToLogsRatio)
        {
            return logs.size() != 0 && ((float) leaves.size() / logs.size()) >= minLeavesToLogsRatio;
        }
    }
}
