package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.goal.goals.BlocklingFarmGoal;
import com.willr27.blocklings.item.ToolType;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Used to target nearby crops.
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
    }

    @Override
    public void checkForAndRemoveInvalidTargets()
    {
        if (!isTargetValid())
        {
            markTargetBad();
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

        if (!hasTarget())
        {
            if (!tryFindCrop())
            {
                return false;
            }

            Pair<BlockPos, Path> pathToCrop = findPathToCrop();

            if (pathToCrop == null)
            {
                return false;
            }

            goal.setPathTargetPos(pathToCrop.getKey(), pathToCrop.getValue());
        }

        return true;
    }

    @Override
    public void markTargetBad()
    {
        if (hasTarget())
        {
            markPosBad(getTargetPos());
        }
    }
    @Override
    protected boolean isValidTargetBlock(@Nonnull Block block)
    {
        return goal.cropWhitelist.isEntryWhitelisted(block);
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

    @Nonnull
    @Override
    protected ToolType getToolType()
    {
        return ToolType.HOE;
    }

    /**
     * Tries to find the nearest tree.
     *
     * @return true if a tree was found.
     */
    private boolean tryFindCrop()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        BlockPos closestPos = null;
        double closestCropDistSq = Float.MAX_VALUE;

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

                        if (distanceSq < closestCropDistSq)
                        {
                            closestPos = testBlockPos;
                            closestCropDistSq = distanceSq;

                            break;
                        }
                    }
                }
            }
        }

        if (closestPos != null)
        {
            setTargetPos(closestPos);

            return true;
        }

        return false;
    }

    /**
     * Finds the first valid path to the crop, not necessarily the most optimal.
     *
     * @return the path target position and the path to the crop, or null if no path could be found.
     */
    @Nullable
    public Pair<BlockPos, Path> findPathToCrop()
    {
        if (BlockUtil.areAllAdjacentBlocksSolid(world, getTargetPos()))
        {
            return null;
        }

        if (goal.isBadPathTargetPos(getTargetPos()))
        {
            return null;
        }

        Path path = EntityUtil.createPathTo(blockling, getTargetPos(), goal.getRangeSq());

        if (path != null)
        {
            return new Pair<>(getTargetPos(), path);
        }

        return null;
    }
}
