package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.goal.goals.BlocklingWoodcutGoal;
import com.willr27.blocklings.item.ToolType;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Used to target nearby tree.
 */
public class BlocklingWoodcutTargetGoal extends BlocklingGatherTargetGoal<BlocklingWoodcutGoal>
{
    /**
     * The x and z search radius.
     */
    private static final int SEARCH_RADIUS_X = 8;

    /**
     * The y search radius.
     */
    private static final int SEARCH_RADIUS_Y = 8;

    /**
     * The current target tree.
     */
    private Tree tree = new Tree();

    /**
     * @param goal The associated goal instance.
     */
    public BlocklingWoodcutTargetGoal(@Nonnull BlocklingWoodcutGoal goal)
    {
        super(goal);
    }

    @Override
    public boolean canUse()
    {
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse()
    {
        return super.canContinueToUse();
    }

    @Override
    public void stop()
    {
        super.stop();

        tree.logs.clear();
        tree.leaves.clear();
    }

    @Override
    public void checkForAndRemoveInvalidTargets()
    {
        for (BlockPos blockPos : new ArrayList<>(tree.logs))
        {
            if (!isValidTarget(blockPos))
            {
                markPosBad(blockPos);
            }
        }
    }

    @Override
    public boolean tryRecalcTargetPos()
    {
        if (isTargetValid())
        {
            return true;
        }
        else
        {
            markTargetPosBad();
        }

        if (tree.logs.isEmpty())
        {
            if (!tryFindTree())
            {
                return false;
            }

            Pair<BlockPos, Path> pathToTree = findPathToTree();

            if (pathToTree == null)
            {
                return false;
            }

            goal.setPathTargetPos(pathToTree.getKey(), pathToTree.getValue());
        }

        setTargetPos((BlockPos) tree.logs.toArray()[tree.logs.size() - 1]);

        return true;
    }

    @Override
    public void markTargetBad()
    {
        while (!tree.logs.isEmpty())
        {
            markPosBad(tree.logs.get(0));
        }

        while (!tree.leaves.isEmpty())
        {
            markPosBad(tree.leaves.get(0));
        }
    }

    @Override
    public void markPosBad(@Nonnull BlockPos blockPos)
    {
        super.markPosBad(blockPos);

        tree.logs.remove(blockPos);
        tree.leaves.remove(blockPos);
    }

    @Override
    protected boolean isValidTargetBlock(@Nonnull Block block)
    {
        return goal.logWhitelist.isEntryWhitelisted(block);
    }

    @Nonnull
    @Override
    protected ToolType getToolType()
    {
        return ToolType.AXE;
    }

    /**
     * Tries to find the nearest tree.
     *
     * @return true if a tree was found.
     */
    private boolean tryFindTree()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        Tree tree = new Tree();
        List<BlockPos> testedBlockPositions = new ArrayList<>();

        double closestTreeDistSq = Float.MAX_VALUE;

        for (int i = -SEARCH_RADIUS_X; i <= SEARCH_RADIUS_X; i++)
        {
            for (int j = -SEARCH_RADIUS_Y; j <= SEARCH_RADIUS_Y; j++)
            {
                for (int k = -SEARCH_RADIUS_X; k <= SEARCH_RADIUS_X; k++)
                {
                    BlockPos testBlockPos = blocklingBlockPos.offset(i, j, k);

                    if (testedBlockPositions.contains(testBlockPos))
                    {
                        continue;
                    }

                    if (isValidTarget(testBlockPos))
                    {
                        Tree treeToTest = findTreeFrom(testBlockPos);

                        // How many leaves we need per log for it to be a valid tree.
                        final float leafToLogRatio = 0.8f;

                        if (treeToTest.logs.size() / (float) treeToTest.leaves.size() > leafToLogRatio)
                        {
                            continue;
                        }

                        boolean canSeeTree = false;

                        for (BlockPos logBlockPos : treeToTest.logs)
                        {
                            if (!testedBlockPositions.contains(logBlockPos))
                            {
                                testedBlockPositions.add(logBlockPos);
                            }

                            if (!canSeeTree && EntityUtil.canSee(blockling, logBlockPos))
                            {
                                canSeeTree = true;
                            }
                        }

                        for (BlockPos leafBlockPos : treeToTest.leaves)
                        {
                            if (!testedBlockPositions.contains(leafBlockPos))
                            {
                                testedBlockPositions.add(leafBlockPos);
                            }

                            if (!canSeeTree && EntityUtil.canSee(blockling, leafBlockPos))
                            {
                                canSeeTree = true;
                            }
                        }

                        if (!canSeeTree)
                        {
                            continue;
                        }

                        for (BlockPos logBlockPos : treeToTest.logs)
                        {
                            float distanceSq = (float) blockling.distanceToSqr(logBlockPos.getX() + 0.5f, logBlockPos.getY() + 0.5f, logBlockPos.getZ() + 0.5f);

                            if (distanceSq < closestTreeDistSq)
                            {
                                closestTreeDistSq = distanceSq;
                                tree = treeToTest;

                                break;
                            }
                        }
                    }
                }
            }
        }

        if (!tree.logs.isEmpty())
        {
            this.tree.logs.clear();
            this.tree.leaves.clear();
            this.tree.logs.addAll(tree.logs);
            this.tree.logs.addAll(tree.leaves);

            return true;
        }

        return false;
    }

    /**
     * Finds the tree stemming from the given pos.
     *
     * @param blockPos the starting pos.
     * @return the tree.
     */
    @Nonnull
    private Tree findTreeFrom(@Nonnull BlockPos blockPos)
    {
        Tree tree = new Tree();
        Set<BlockPos> logBlockPositionsToTest = new HashSet<>();

        logBlockPositionsToTest.add(blockPos);
        tree.logs.add(blockPos);

        while (!logBlockPositionsToTest.isEmpty())
        {
            BlockPos testBlockPos = logBlockPositionsToTest.stream().findFirst().get();

            for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(testBlockPos))
            {
                if (isValidTarget(surroundingPos))
                {
                    if (!tree.logs.contains(surroundingPos))
                    {
                        tree.logs.add(surroundingPos);
                        logBlockPositionsToTest.add(surroundingPos);
                    }
                }
                else if (isValidLeafPos(surroundingPos))
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
     * Finds the first valid path to the tree, not necessarily the most optimal.
     *
     * @return the path target position and the path to the tree, or null if no path could be found.
     */
    @Nullable
    public Pair<BlockPos, Path> findPathToTree()
    {
        for (BlockPos logBlockPos : tree.logs)
        {
            if (BlockUtil.areAllAdjacentBlocksSolid(world, logBlockPos))
            {
                continue;
            }

            if (goal.isBadPathTargetPos(logBlockPos))
            {
                continue;
            }

            Path path = EntityUtil.createPathTo(blockling, logBlockPos, goal.getRangeSq());

            if (path != null)
            {
                return new Pair<>(logBlockPos, path);
            }
        }

        return null;
    }

    /**
     * Sets the tree's root position to the given block pos.
     * Will then recalculate the tree.
     *
     * @param blockPos the block pos to use as the tree's root.
     */
    public void changeTreeRootTo(@Nonnull BlockPos blockPos)
    {
        tree.logs.clear();
        tree.leaves.clear();

        Tree newTree = findTreeFrom(blockPos);

        tree.logs.addAll(newTree.logs);
        tree.leaves.addAll(newTree.leaves);
    }

    /**
     * @param blockPos the pos to check.
     * @return true if the block at the pos is a leaf.
     */
    private boolean isValidLeafPos(BlockPos blockPos)
    {
        return isValidLeaf(world.getBlockState(blockPos).getBlock());
    }

    /**
     * @param block the block to check.
     * @return true if the block is a leaf.
     */
    private boolean isValidLeaf(Block block)
    {
        return BlockUtil.isLeaf(block);
    }

    /**
     * @return the current tree.
     */
    @Nonnull
    public Tree getTree()
    {
        return tree;
    }

    /**
     * Class to represent a tree.
     */
    public static class Tree
    {
        public final List<BlockPos> logs = new ArrayList<>();
        public final List<BlockPos> leaves = new ArrayList<>();
    }
}
