package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.goals.blockling.BlocklingWoodcutGoal;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
        if (!super.canUse())
        {
            return false;
        }

        tree = findTree();

        if (tree.logs.isEmpty())
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        if (!hasTarget())
        {
            return false;
        }

        return true;
    }

    @Override
    public void stop()
    {
        tree.logs.clear();
        tree.leaves.clear();
    }

    @Override
    public void recalcTarget()
    {
        tree.logs.remove(getTargetPos());

        super.recalcTarget();
    }

    @Override
    protected BlockPos findNextTargetPos()
    {
        return tree.logs.stream().max(Comparator.comparingInt(Vector3i::getY)).orElse(null);
    }

    @Override
    protected boolean isValidTargetBlock(@Nullable Block block)
    {
        return block != null && goal.logWhitelist.isEntryWhitelisted(block);
    }

    @Override
    public void markBad()
    {
        tree.logs.forEach(blockPos -> markPosBad(blockPos));
    }

    /**
     * Finds the nearest tree.
     *
     * @return the tree.
     */
    @Nonnull
    private Tree findTree()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        Tree tree = new Tree();
        Set<BlockPos> testedBlockPositions = new HashSet<>();

        float closestTreeDistSq = Float.MAX_VALUE;

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
                        Tree treeBlockPositionsToTest = findTreeFrom(testBlockPos);
                        testedBlockPositions.addAll(treeBlockPositionsToTest.logs);
                        testedBlockPositions.addAll(treeBlockPositionsToTest.leaves);

                        if (treeBlockPositionsToTest.logs.stream().anyMatch(blockPos -> !isValidTargetPos(blockPos)))
                        {
                            continue;
                        }

                        // How many leaves we need per log for it to be a valid tree.
                        float leafToLogRatio = 1.0f;

                        if (treeBlockPositionsToTest.logs.size() / (float) treeBlockPositionsToTest.leaves.size() > leafToLogRatio)
                        {
                            continue;
                        }

                        for (BlockPos logBlockPos : treeBlockPositionsToTest.logs)
                        {
                            float distanceSq = (float) blockling.distanceToSqr(logBlockPos.getX() + 0.5f, logBlockPos.getY() + 0.5f, logBlockPos.getZ() + 0.5f);

                            if (distanceSq < closestTreeDistSq)
                            {
                                closestTreeDistSq = distanceSq;
                                tree = treeBlockPositionsToTest;

                                break;
                            }
                        }
                    }
                }
            }
        }

        return tree;
    }

    /**
     * Finds the tree stemming from the given pos.
     *
     * @param blockPos the starting pos.
     * @return the tree.
     */
    @Nonnull
    private Tree findTreeFrom(BlockPos blockPos)
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
                    if (tree.logs.add(surroundingPos))
                    {
                        logBlockPositionsToTest.add(surroundingPos);
                    }
                }
                else if (isValidLeafPos(surroundingPos))
                {
                    tree.leaves.add(surroundingPos);
                }
            }

            logBlockPositionsToTest.remove(testBlockPos);
        }

        return tree;
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
        public final Set<BlockPos> logs = new HashSet<>();
        public final Set<BlockPos> leaves = new HashSet<>();
    }
}
