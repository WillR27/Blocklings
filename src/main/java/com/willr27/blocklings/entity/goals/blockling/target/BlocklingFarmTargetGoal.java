package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlocklingFarmTargetGoal extends BlocklingTargetGoal
{
    private static final int SEARCH_RADIUS_X = 8;
    private static final int SEARCH_RADIUS_Y = 8;

    private BlockPos targetPos = null;
    private BlockPos prevTargetPos = null;

    private Map<BlockPos, Integer> badBlockPositions = new HashMap<>();

    /**
     * How many recalcs are called before a block is no longer bad.
     */
    private final int recalcBadInterval = 20;

    public BlocklingFarmTargetGoal(BlocklingGoal goal)
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

        targetPos = findTarget();

        if (!hasTarget())
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

        if (!isValidCropPos(targetPos))
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

    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    protected boolean isTargetValid()
    {
        if (!isValidCropPos(targetPos))
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
        findTarget();
    }

    private void findNextTarget()
    {
        prevTargetPos = targetPos;

        findTarget();
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

    private BlockPos findTarget()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        BlockPos closestPos = null;
        float closestVeinDistSq = Float.MAX_VALUE;

        for (int i = -SEARCH_RADIUS_X; i <= SEARCH_RADIUS_X; i++)
        {
            for (int j = -SEARCH_RADIUS_Y; j <= SEARCH_RADIUS_Y; j++)
            {
                for (int k = -SEARCH_RADIUS_X; k <= SEARCH_RADIUS_X; k++)
                {
                    BlockPos testBlockPos = blocklingBlockPos.offset(i, j, k);

                    if (isValidCropPos(testBlockPos))
                    {
                        float distanceSq = (float) blockling.distanceToSqr(testBlockPos.getX() + 0.5f, testBlockPos.getY() + 0.5f, testBlockPos.getZ() + 0.5f);

                        if (distanceSq < closestVeinDistSq)
                        {
                            closestPos = testBlockPos;
                            closestVeinDistSq = distanceSq;

                            break;
                        }
                    }
                }
            }
        }

        return closestPos;
    }

    private boolean isValidCropPos(BlockPos blockPos)
    {
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (!(isValidCrop(block) && !badBlockPositions.keySet().contains(blockPos)))
        {
            return false;
        }

        if (block instanceof CropsBlock)
        {
            CropsBlock cropsBlock = (CropsBlock) block;

            if (!cropsBlock.isMaxAge(blockState))
            {
                return false;
            }
        }

        return true;
    }

    private boolean isValidCrop(Block block)
    {
        return goal.whitelists.get(0).isEntryWhitelisted(block);
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
