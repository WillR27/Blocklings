package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlocklingWoodcutTargetGoal extends BlocklingTargetGoal
{
    private static final int SEARCH_RADIUS_X = 8;
    private static final int SEARCH_RADIUS_Y = 8;

    private BlockPos targetPos = null;
    private BlockPos prevTargetPos = null;

    private Tree tree = new Tree();
    private Map<BlockPos, Integer> badBlockPositions = new HashMap<>();

    /**
     * How many recalcs are called before a block is no longer bad.
     */
    private final int recalcBadInterval = 20;

    /**
     * How many leaves we need per log for it to be a valid tree.
     */
    private final float leafToLogRatio = 1.0f;

    public BlocklingWoodcutTargetGoal(BlocklingGoal goal)
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
    public void start()
    {
        findNextTarget();
    }

    @Override
    public void stop()
    {
        tree.logs.clear();
        tree.leaves.clear();
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    protected boolean isTargetValid()
    {
        if (!isValidLogPos(targetPos))
        {
            return false;
        }

        return true;
    }

    @Override
    protected void recalc()
    {
        updateBadPositions();
    }

    @Override
    protected void recalcTarget()
    {
        tree.logs.remove(targetPos);

        findNextTarget();
    }

    private void updateBadPositions()
    {
        Set<BlockPos> freshBlockPositions = new HashSet<>();

        badBlockPositions.forEach((blockPos, time) ->
        {
            if (time >= recalcBadInterval || time >= badBlockPositions.size())
            {
                freshBlockPositions.add(blockPos);
            }

            badBlockPositions.put(blockPos, time + 1);
        });

        freshBlockPositions.forEach(blockPos ->
        {
            badBlockPositions.remove(blockPos);
        });
    }

    private void findNextTarget()
    {
        prevTargetPos = targetPos;
        targetPos = tree.logs.stream().max((o1, o2) -> o1.getY() > o2.getY() ? 1 : -1).orElse(null);
    }

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

                    if (isValidLogPos(testBlockPos))
                    {
                        Tree treeBlockPositionsToTest = findTreeFrom(testBlockPos);
                        testedBlockPositions.addAll(treeBlockPositionsToTest.logs);
                        testedBlockPositions.addAll(treeBlockPositionsToTest.leaves);

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
                if (isValidLogPos(surroundingPos))
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

    private boolean isValidLogPos(BlockPos blockPos)
    {
        return isValidLog(world.getBlockState(blockPos).getBlock()) && !badBlockPositions.keySet().contains(blockPos);
    }

    private boolean isValidLeafPos(BlockPos blockPos)
    {
        return isValidLeaf(world.getBlockState(blockPos).getBlock());
    }

    private boolean isValidLog(Block block)
    {
        return goal.whitelists.get(0).isEntryWhitelisted(block);
    }

    private boolean isValidLeaf(Block block)
    {
        return BlockUtil.isLeaf(block);
    }

    public void markTreeBad()
    {
        tree.logs.forEach(blockPos -> badBlockPositions.put(blockPos, 0));
        tree.logs.clear();
        tree.leaves.clear();
    }

    public boolean hasTarget()
    {
        return targetPos != null;
    }

    public BlockPos getTargetPos()
    {
        return targetPos;
    }

    public boolean hasPrevTarget()
    {
        return prevTargetPos != null;
    }

    public BlockPos getPrevTargetPos()
    {
        return prevTargetPos;
    }

    public Tree getTree()
    {
        return tree;
    }

    public static class Tree
    {
        public final Set<BlockPos> logs = new HashSet<>();
        public final Set<BlockPos> leaves = new HashSet<>();
    }
}
