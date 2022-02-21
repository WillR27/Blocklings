package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.goal.goals.BlocklingMineGoal;
import com.willr27.blocklings.item.ToolType;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to target nearby veins of ore.
 */
public class BlocklingMineTargetGoal extends BlocklingGatherTargetGoal<BlocklingMineGoal>
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
     * The list of block positions in the current vein.
     */
    @Nonnull
    public final List<BlockPos> veinBlockPositions = new ArrayList<>();

    /**
     * @param goal The associated goal instance.
     */
    public BlocklingMineTargetGoal(@Nonnull BlocklingMineGoal goal)
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

        veinBlockPositions.clear();
    }

    @Override
    public void checkForAndRemoveInvalidTargets()
    {
        for (BlockPos blockPos : new ArrayList<>(veinBlockPositions))
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

        if (veinBlockPositions.isEmpty())
        {
            if (!tryFindVein())
            {
                return false;
            }

            Pair<BlockPos, Path> pathToVein = findPathToVein();

            if (pathToVein == null)
            {
                return false;
            }

            goal.setPathTargetPos(pathToVein.getKey(), pathToVein.getValue(), false);
        }

        setTargetPos((BlockPos) veinBlockPositions.toArray()[veinBlockPositions.size() - 1]);

        return true;
    }

    @Override
    public void markTargetBad()
    {
        while (!veinBlockPositions.isEmpty())
        {
            markPosBad(veinBlockPositions.get(0));
        }
    }

    @Override
    public void markPosBad(@Nonnull BlockPos blockPos)
    {
        super.markPosBad(blockPos);

        veinBlockPositions.remove(blockPos);
    }

    @Override
    protected boolean isValidTargetBlock(@Nonnull Block block)
    {
        return goal.oreWhitelist.isEntryWhitelisted(block);
    }

    @Nonnull
    @Override
    protected ToolType getToolType()
    {
        return ToolType.PICKAXE;
    }

    /**
     * Tries to find the nearest vein.
     *
     * @return true if a vein was found.
     */
    private boolean tryFindVein()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        List<BlockPos> veinBlockPositions = new ArrayList<>();
        List<BlockPos> testedBlockPositions = new ArrayList<>();

        double closestVeinDistSq = Float.MAX_VALUE;

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
                        List<BlockPos> veinBlockPositionsToTest = findVeinFrom(testBlockPos);

                        boolean canSeeVein = false;

                        for (BlockPos veinBlockPos : veinBlockPositionsToTest)
                        {
                            if (!testedBlockPositions.contains(veinBlockPos))
                            {
                                testedBlockPositions.add(veinBlockPos);
                            }

                            if (!canSeeVein && EntityUtil.canSee(blockling, veinBlockPos))
                            {
                                canSeeVein = true;
                            }
                        }

                        if (!canSeeVein)
                        {
                            continue;
                        }

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

        if (!veinBlockPositions.isEmpty())
        {
            this.veinBlockPositions.clear();
            this.veinBlockPositions.addAll(veinBlockPositions);

            return true;
        }

        return false;
    }

    /**
     * Returns a vein from the given starting block pos.
     *
     * @param startingBlockPos the starting block pos.
     * @return the list of block positions in the vein.
     */
    @Nonnull
    private List<BlockPos> findVeinFrom(@Nonnull BlockPos startingBlockPos)
    {
        List<BlockPos> veinBlockPositionsToTest = new ArrayList<>();
        List<BlockPos> veinBlockPositions = new ArrayList<>();

        veinBlockPositionsToTest.add(startingBlockPos);
        veinBlockPositions.add(startingBlockPos);

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
                if (isValidTarget(surroundingPos))
                {
                    if (!veinBlockPositions.contains(surroundingPos))
                    {
                        veinBlockPositions.add(surroundingPos);
                        veinBlockPositionsToTest.add(surroundingPos);
                    }
                }
            }

            veinBlockPositionsToTest.remove(testBlockPos);
        }

        return veinBlockPositions;
    }

    /**
     * Finds the first valid path to the vein, not necessarily the most optimal.
     *
     * @return the path target position and the path to the vein, or null if no path could be found.
     */
    @Nullable
    public Pair<BlockPos, Path> findPathToVein()
    {
        for (BlockPos veinBlockPos : veinBlockPositions)
        {
            if (BlockUtil.areAllAdjacentBlocksSolid(world, veinBlockPos))
            {
                continue;
            }

            if (goal.isBadPathTargetPos(veinBlockPos))
            {
                continue;
            }

            Path path = EntityUtil.createPathTo(blockling, veinBlockPos, goal.getRangeSq());

            if (path != null)
            {
                return new Pair<>(veinBlockPos, path);
            }
        }

        return null;
    }

    /**
     * Sets the root vein position to the given block pos.
     * Will then recalculate the vein.
     *
     * @param blockPos the block pos to use as the vein root.
     */
    public void changeVeinRootTo(@Nonnull BlockPos blockPos)
    {
        veinBlockPositions.clear();
        veinBlockPositions.addAll(findVeinFrom(blockPos));
    }
}
