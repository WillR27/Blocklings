package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.goals.blockling.BlocklingMineGoal;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
     * The set of block positions in the current vein.
     */
    @Nonnull
    private final Set<BlockPos> veinBlockPositions = new HashSet<>();

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
        if (!super.canUse())
        {
            return false;
        }

        veinBlockPositions.addAll(findVein());

        if (veinBlockPositions.isEmpty())
        {
            return false;
        }

        return true;
    }

    @Override
    public void stop()
    {
        super.stop();

        veinBlockPositions.clear();
    }

    @Override
    public void recalcTarget()
    {
        veinBlockPositions.remove(getTargetPos());

        super.recalcTarget();
    }

    @Override
    protected BlockPos findNextTargetPos()
    {
        return veinBlockPositions.stream().max(Comparator.comparingInt(Vector3i::getY)).orElse(null);
    }

    @Override
    protected boolean isValidTargetBlock(@Nullable Block block)
    {
        return block != null && goal.oreWhitelist.isEntryWhitelisted(block);
    }

    @Override
    public void markBad()
    {
        veinBlockPositions.forEach(blockPos -> markPosBad(blockPos));
    }

    /**
     * Attempts to find the nearest vein.
     *
     * @return a set of block positions for the nearest vein.
     */
    @Nonnull
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

                    if (isValidTarget(testBlockPos))
                    {
                        Set<BlockPos> veinBlockPositionsToTest = findVeinFrom(testBlockPos);
                        testedBlockPositions.addAll(veinBlockPositionsToTest);

                        boolean canSeeVein = false;

                        for (BlockPos veinBlockPos : veinBlockPositionsToTest)
                        {
                            if (EntityUtil.canSee(blockling, veinBlockPos))
                            {
                                canSeeVein = true;

                                break;
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

        return veinBlockPositions;
    }

    /**
     * Returns a vein from the given starting block pos.
     *
     * @param startingBlockPos the starting block pos.
     * @return the set of block positions in the vein.
     */
    @Nonnull
    private Set<BlockPos> findVeinFrom(@Nonnull BlockPos startingBlockPos)
    {
        Set<BlockPos> veinBlockPositionsToTest = new HashSet<>();
        Set<BlockPos> veinBlockPositions = new HashSet<>();

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
                    if (veinBlockPositions.add(surroundingPos))
                    {
                        veinBlockPositionsToTest.add(surroundingPos);
                    }
                }
            }

            veinBlockPositionsToTest.remove(testBlockPos);
        }

        return veinBlockPositions;
    }
}
