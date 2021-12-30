package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.goals.blockling.BlocklingMineGoal;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlocklingMineTargetGoal extends BlocklingTargetGoal<BlocklingMineGoal>
{
    private static final int SEARCH_RADIUS_X = 8;
    private static final int SEARCH_RADIUS_Y = 8;

    private BlockPos targetPos = null;
    private BlockPos prevTargetPos = null;

    private Set<BlockPos> veinBlockPositions = new HashSet<>();
    private Map<BlockPos, Integer> badBlockPositions = new HashMap<>();

    /**
     * How many recalcs are called before a block is no longer bad.
     */
    private final int recalcBadInterval = 20;

    public BlocklingMineTargetGoal(BlocklingMineGoal goal)
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

        veinBlockPositions = findVein();

        if (veinBlockPositions.isEmpty())
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
        veinBlockPositions.clear();
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    protected boolean isTargetValid()
    {
        if (!isValidOrePos(targetPos))
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
        veinBlockPositions.remove(targetPos);

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
        targetPos = veinBlockPositions.stream().max((o1, o2) -> o1.getY() > o2.getY() ? 1 : -1).orElse(null);
    }

    private Set<BlockPos> findVein()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        Set<BlockPos> veinBlockPositions = new HashSet<>();
        Set<BlockPos> testedBlockPositions = new HashSet<>();

        float closestVeinDistSq = Float.MAX_VALUE;

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

                    if (isValidOrePos(testBlockPos))
                    {
                        Set<BlockPos> veinBlockPositionsToTest = findVeinFrom(testBlockPos);
                        testedBlockPositions.addAll(veinBlockPositionsToTest);

                        for (BlockPos veinBlockPos : veinBlockPositionsToTest)
                        {
                            float distanceSq = (float) blockling.distanceToSqr(veinBlockPos.getX() + 0.5f, veinBlockPos.getY() + 0.5f, veinBlockPos.getZ() + 0.5f);

                            if (distanceSq < closestVeinDistSq)
                            {
                                closestVeinDistSq = distanceSq;
                                veinBlockPositions = veinBlockPositionsToTest;

                                break;
                            }
                        }
                    }
                }
            }
        }

        return veinBlockPositions;
    }

    private Set<BlockPos> findVeinFrom(BlockPos blockPos)
    {
        Set<BlockPos> veinBlockPositionsToTest = new HashSet<>();
        Set<BlockPos> veinBlockPositions = new HashSet<>();

        veinBlockPositionsToTest.add(blockPos);
        veinBlockPositions.add(blockPos);

        while (!veinBlockPositionsToTest.isEmpty())
        {
            BlockPos testBlockPos = veinBlockPositionsToTest.stream().findFirst().get();

            BlockPos[] surroundingBlockPositions = new BlockPos[]
            {
                testBlockPos.offset(-1, 0, 0),
                testBlockPos.offset(1, 0, 0),
                testBlockPos.offset(0, -1, 0),
                testBlockPos.offset(0, 1, 0),
                testBlockPos.offset(0, 0, -1),
                testBlockPos.offset(0, 0, 1),
            };

            for (BlockPos surroundingPos : surroundingBlockPositions)
            {
                if (isValidOrePos(surroundingPos))
                {
                    if (veinBlockPositions.add(surroundingPos))
                    {
                        veinBlockPositionsToTest.add(surroundingPos);
                    }
                }
            }

            veinBlockPositionsToTest.remove(testBlockPos);
        }

        for (BlockPos veinPos : veinBlockPositions)
        {
            if (EntityUtil.canSee(blockling, veinPos))
            {
                return veinBlockPositions;
            }
        }

        return new HashSet<>();
    }

    private boolean isValidOrePos(BlockPos blockPos)
    {
        return isValidOre(world.getBlockState(blockPos).getBlock()) && !badBlockPositions.keySet().contains(blockPos);
    }

    private boolean isValidOre(Block block)
    {
        return goal.oreWhitelist.isEntryWhitelisted(block);
    }

    public void markTargetBad()
    {
        badBlockPositions.put(targetPos, 0);
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
}
