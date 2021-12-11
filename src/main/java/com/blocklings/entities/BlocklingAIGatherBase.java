package com.blocklings.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class BlocklingAIGatherBase extends EntityAIBase
{
    Random rand = new Random();

    EntityBlockling blockling;
    World world;

    Block targetBlock;
    BlockPos targetPos;
    Vec3d targetVec;
    Path targetPath;
    double targetPathSquareDistance;

    float range = 2.5f;

    BlocklingAIGatherBase(EntityBlockling blockling)
    {
        this.blockling = blockling;
        this.world = blockling.world;
    }

    boolean canExecute()
    {
        return !blockling.isSitting() && blockling.getAttackTarget() == null;
    }

    boolean canContinueExecuting()
    {
        return canExecute() && hasTarget() && world.getBlockState(targetPos).getBlock() != Blocks.AIR;
    }

    @Override
    public boolean shouldExecute()
    {
        return false;
    }

    double getPathSquareDistance(Path path)
    {
        if (path == null)
        {
            return 100000;
        }

        double distance = 0;

        Vec3d prevVec = null;
        for (int i = 0; i < path.getCurrentPathLength(); i++)
        {
            PathPoint nextPoint = path.getPathPointFromIndex(i);
            Vec3d nextVec = new Vec3d(nextPoint.x, nextPoint.y, nextPoint.z);

            if (prevVec != null)
            {
                distance += prevVec.squareDistanceTo(nextVec);
            }

            prevVec = nextVec;
        }

        return distance;
    }

    Block getBlockAt(int x, int y, int z)
    {
        return blockling.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    boolean moveToTarget()
    {
        if (targetPath != null)
        {
            PathPoint finalPoint = targetPath.getFinalPathPoint();
            blockling.getNavigator().tryMoveToXYZ(finalPoint.x + 0.5, finalPoint.y, finalPoint.z + 0.5, blockling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());

            return blockling.getNavigator().getPath() != null;
        }
        else
        {
            return false;
        }
    }

    boolean isPosInRange(BlockPos blockPos, BlockPos blockPosTarget)
    {
        Vec3d blockVec = getVecFromBlockPos(blockPos);
        Vec3d blockVecTarget = getVecFromBlockPos(blockPosTarget);

        return blockVec.distanceTo(blockVecTarget) < range;
    }

    boolean isPathDestInRange(Path path, BlockPos blockPos)
    {
        if (path != null)
        {
            PathPoint finalPoint = path.getFinalPathPoint();
            Vec3d finalVec = getVecFromPathPoint(finalPoint);
            Vec3d blockVec = getVecFromBlockPos(blockPos);

            return blockVec.distanceTo(finalVec) < range;
        }

        return false;
    }

    boolean isBlocklingInRange(BlockPos blockPos)
    {
        Vec3d blocklingVec = getVecFromBlockPos(blockling.getPosition());
        Vec3d blockVec = getVecFromBlockPos(blockPos);

        return blocklingVec.distanceTo(blockVec) < range;
    }

    Path getSafishPathTo(BlockPos blockPos)
    {
        for (int i = -1; i < 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    BlockPos surroundingPos = new BlockPos(blockPos.getX() + i, blockPos.getY() + j, blockPos.getZ() + k);

                    Path testPath = blockling.getNavigator().getPathToPos(surroundingPos);
                    if (testPath != null)
                    {
                        PathPoint finalPoint = testPath.getFinalPathPoint();
                        BlockPos finalPos = getPosFromPathPoint(finalPoint);

                        // If we can't get in range of the block skip to next one
                        if (!isPathDestInRange(testPath, blockPos))
                        {
                            continue;
                        }

                        if (!finalPos.equals(blockPos))
                        {
                            return testPath;
                        }
                    }
                }
            }
        }

        return blockling.getNavigator().getPathToPos(blockPos);
    }

    Path getSafishPathToWithRemovedBlock(BlockPos blockPos, BlockPos removedPos)
    {
//        for (int i = -1; i < 2; i++)
//        {
//            for (int j = -1; j < 2; j++)
//            {
//                for (int k = -1; k < 2; k++)
//                {
//                    BlockPos surroundingPos = new BlockPos(blockPos.getX() + i, blockPos.getY() + j, blockPos.getZ() + k);
//
//                    Path testPath = ((PathNavigateGroundBlockling) blockling.getNavigator()).getPathToPosWithRemovedBlock(surroundingPos, removedPos);
//                    if (testPath != null)
//                    {
//                        PathPoint finalPoint = testPath.getFinalPathPoint();
//                        BlockPos finalPos = getPosFromPathPoint(finalPoint);
//                        Vec3d finalVec = getVecFromPathPoint(finalPoint);
//                        Vec3d blockVec = getVecFromBlockPos(blockPos);
//
//                        // If we can't get in range of the block skip to next one
//                        if (blockVec.distanceTo(finalVec) >= range)
//                        {
//                            continue;
//                        }
//
//                        if (!finalPos.equals(blockPos))
//                        {
//                            return testPath;
//                        }
//                    }
//                }
//            }
//        }

        return ((PathNavigateGroundBlockling) blockling.getNavigator()).getPathToPosWithRemovedBlock(blockPos, removedPos);
    }

    Vec3d getVecFromBlockPos(BlockPos blockPos)
    {
        return new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    Vec3d getVecFromPathPoint(PathPoint pathPoint)
    {
        return new Vec3d(pathPoint.x + 0.5, pathPoint.y + 0.5, pathPoint.z + 0.5);
    }

    BlockPos getPosFromPathPoint(PathPoint pathPoint)
    {
        return new BlockPos(pathPoint.x, pathPoint.y, pathPoint.z);
    }

    Block getBlockFromPos(BlockPos blockPos)
    {
        return world.getBlockState(blockPos).getBlock();
    }

    boolean canSeeBlock(int x, int y, int z)
    {
        return canSeeBlock(new Vec3d(x, y, z));
    }

    boolean canSeeBlock(Vec3d blockVec)
    {
        double height = 0.6F * this.blockling.getBlocklingScale();
        for (int it = 0; it < 2; it++)
        {
            double xStart = this.blockling.posX;
            double yStart;

            if (it == 0)
            {
                yStart = this.blockling.posY + height * 0.2D;
            }
            else
            {
                yStart = this.blockling.posY + height * 0.8D;
            }
            double zStart = this.blockling.posZ;
            Vec3d blocklingVec = new Vec3d(xStart, yStart, zStart);

            for (double i = 0.03D; i <= 0.97D; i += 0.94D)
            {
                for (double j = 0.03D; j <= 0.97D; j += 0.94D)
                {
                    for (double k = 0.03D; k <= 0.97D; k += 0.94D)
                    {
                        Vec3d testVec = new Vec3d(Math.floor(blockVec.x) + i, Math.floor(blockVec.y) + j, Math.floor(blockVec.z) + k);

                        RayTraceResult result = blockling.world.rayTraceBlocks(blocklingVec, testVec, true, true, true);
                        if (result != null)
                        {
                            BlockPos pos = result.getBlockPos();
                            if (pos.equals(new BlockPos(blockVec)))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    void resetTarget()
    {
        this.targetBlock = null;
        this.targetPos = null;
        this.targetVec = null;
        this.targetPath = null;
    }

    void setTarget(BlockPos targetPos)
    {
        this.targetBlock = getBlockFromPos(targetPos);
        this.targetPos = targetPos;
        this.targetVec = getVecFromBlockPos(targetPos);
        this.targetPath = getSafishPathTo(targetPos);
    }

    void setTarget(BlockPos targetPos, Path targetPath)
    {
        this.targetBlock = getBlockFromPos(targetPos);
        this.targetPos = targetPos;
        this.targetVec = getVecFromBlockPos(targetPos);
        this.targetPath = targetPath;
    }

    public boolean hasTarget()
    {
        return targetVec != null;
    }
}
