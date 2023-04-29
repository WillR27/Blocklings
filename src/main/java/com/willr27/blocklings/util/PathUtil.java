package com.willr27.blocklings.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for pathfinding.
 */
public class PathUtil
{
    /**
     * Creates the closest path to the given block.
     *
     * @param entity   the entity to create a path for.
     * @param blockPos the pos to create a path to.
     * @return the path or null if no path was found.
     */
    @Nullable
    public static Path createPathTo(@Nonnull MobEntity entity, @Nonnull BlockPos blockPos)
    {
        return createPathTo(entity, blockPos, 0, false);
    }

    /**
     * Creates a path to the given block.
     *
     * @param entity         the entity to create a path for.
     * @param blockPos       the pos to create a path to.
     * @param stopDistanceSq if the distance between the path's target and target block is within this range we can return (0 to just find the closest path).
     * @param preferTopBlock if true we will prefer to path to the top of the block.
     * @return the path or null if no path was found.
     */
    @Nullable
    public static Path createPathTo(@Nonnull MobEntity entity, @Nonnull BlockPos blockPos, float stopDistanceSq, boolean preferTopBlock)
    {
        Path closestPath = null;
        double closestDistanceSq = Double.MAX_VALUE;

        Path path = entity.getNavigation().createPath(blockPos, 0);

        if (path != null)
        {
            closestPath = path;
            closestDistanceSq = pathTargetDistanceToSq(path, blockPos);

            // Usually the pathfinding will create a path to the top of the block, so check for that here.
            if ((path.getTarget().equals(blockPos) || !preferTopBlock) && closestDistanceSq < stopDistanceSq)
            {
                return closestPath;
            }
            else if (stopDistanceSq != 0)
            {
                closestDistanceSq = stopDistanceSq;
            }
        }

        for (BlockPos adjacentPos : BlockUtil.getSurroundingBlockPositions(blockPos))
        {
            path = entity.getNavigation().createPath(adjacentPos, 0);

            if (path != null)
            {
                double distanceSq = pathTargetDistanceToSq(path, blockPos);

                if (distanceSq < closestDistanceSq)
                {
                    closestPath = path;
                    closestDistanceSq = distanceSq;

                    if (closestDistanceSq < stopDistanceSq)
                    {
                        return closestPath;
                    }
                }
            }
        }

        // If we get here with a stop distance > 0 then return null as we didn't find a path within range
        return stopDistanceSq > 0 ? null : closestPath;
    }

    /**
     * Gets the distance between the end node of the path and the given block pos. If the path is empty then the path target is used.
     *
     * @param path the path to check.
     * @param blockPos the block pos to check.
     * @return the distance between the end node of the path and the given block pos.
     */
    public static double pathTargetDistanceToSq(@Nonnull Path path, @Nonnull BlockPos blockPos)
    {
        BlockPos targetPos = path.getEndNode() != null ? path.getEndNode().asBlockPos() : path.getTarget();

        return BlockUtil.distanceSq(targetPos, blockPos);
    }
}
