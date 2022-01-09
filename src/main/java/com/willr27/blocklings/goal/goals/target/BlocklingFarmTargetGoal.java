package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.goals.BlocklingFarmGoal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Targets the nearest fully grown crop to harvest.
 */
public class BlocklingFarmTargetGoal extends BlocklingGatherTargetGoal<BlocklingFarmGoal>
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
     * @param goal The associated goal instance.
     */
    public BlocklingFarmTargetGoal(@Nonnull BlocklingFarmGoal goal)
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

        recalcTarget();

        if (!hasTarget())
        {
            return false;
        }

        return true;
    }

    @Override
    public void stop()
    {
        setTargetPos(null);
    }

    @Override
    protected BlockPos findNextTargetPos()
    {
        return findTarget();
    }

    @Override
    protected boolean isValidTargetBlock(@Nullable Block block)
    {
        return block != null && goal.cropWhitelist.isEntryWhitelisted(block);
    }

    @Override
    public void markBad()
    {
        markTargetBad();
    }

    /**
     * Finds the closest crop pos.
     *
     * @return the closest crop pos.
     */
    @Nullable
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

                    if (isValidTarget(testBlockPos))
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

    @Override
    public boolean isValidTarget(@Nullable BlockPos blockPos)
    {
        if (!super.isValidTarget(blockPos))
        {
            return false;
        }

        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

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
}
