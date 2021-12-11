package com.blocklings.entities;

import com.blocklings.abilities.AbilityHelper;
import com.blocklings.util.helpers.BlockHelper;
import com.blocklings.util.helpers.DropHelper;
import com.blocklings.util.helpers.EntityHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlocklingAIMining extends BlocklingAIGatherBase
{
    private int xRadius = 10, yRadius = 10;
    private int targetYValue;

    public BlocklingAIMining(EntityBlockling blockling)
    {
        super(blockling);
    }

    @Override
    public void resetTask()
    {
        blockling.stopMining();

        if (hasTarget())
        {
            world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, -1);
        }

        resetTarget();

        super.resetTask();
    }

    @Override
    public boolean shouldExecute()
    {
        if (!canExecute())
        {
            return false;
        }

        if (blockling.miningAbilities.isAbilityAcquired(AbilityHelper.dwarvenSenses1))
        {
            xRadius = 20;
            yRadius = 20;
        }
        else
        {
            xRadius = 10;
            yRadius = 10;
        }

        boolean foundOre = false;

        resetTarget();
        targetPathSquareDistance = 10000;
        targetYValue = -1000;

        for (int x = (int) blockling.posX - xRadius; x < blockling.posX + xRadius; x++)
        {
            for (int y = (int) blockling.posY + yRadius; y > blockling.posY - yRadius; y--)
            {
                for (int z = (int) blockling.posZ - xRadius; z < blockling.posZ + xRadius; z++)
                {
                    Block block = getBlockAt(x, y, z);
                    if (BlockHelper.isOre(block))
                    {
                        if (blockling.miningAbilities.isAbilityAcquired(AbilityHelper.dwarvenSenses2) || canSeeBlock(x, y, z))
                        {
                            double xx = x + 0.5f;
                            double yy = y + 0.5f;
                            double zz = z + 0.5f;
                            BlockPos blockPos = new BlockPos(x, y, z);
                            Vec3d blockVec = getVecFromBlockPos(blockPos);

                            // If we are already in range to mine the block then set it as target
                            if (y >= targetYValue && blockling.getPositionVector().distanceTo(blockVec) < range)
                            {
                                targetPathSquareDistance = 1;
                                targetYValue = y;
                                setTarget(blockPos);
                                foundOre = true;
                            }

                            Path pathToBlock = getSafishPathTo(blockPos);
                            if (pathToBlock != null)
                            {
                                if (isPathDestInRange(pathToBlock, blockPos))
                                {
                                    // Find the closest block (using path distance)
                                    double pathSquareDistance = getPathSquareDistance(pathToBlock);
                                    if (y > targetYValue)
                                    {
                                        targetPathSquareDistance = pathSquareDistance;
                                        targetYValue = y;
                                        setTarget(blockPos, pathToBlock);
                                        foundOre = true;
                                    }
                                    else if (pathSquareDistance < targetPathSquareDistance)
                                    {
                                        targetPathSquareDistance = pathSquareDistance;
                                        targetYValue = y;
                                        setTarget(blockPos, pathToBlock);
                                        foundOre = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return foundOre;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return canContinueExecuting();
    }

    @Override
    boolean canExecute()
    {
        if (!super.canExecute())
        {
            return false;
        }
        else if (blockling.getTask() != EntityHelper.Task.MINE)
        {
            return false;
        }
        else if (!blockling.hasPickaxe())
        {
            return false;
        }

        return true;
    }

    @Override
    boolean canContinueExecuting()
    {
        if (!super.canContinueExecuting())
        {
            return false;
        }

        return true;
    }

    @Override
    public void updateTask()
    {
        if (hasTarget())
        {
            if (isBlocklingInRange(targetPos))
            {
                if (tryMineTarget())
                {
                    resetTarget();
                }
            }
            else
            {
                if (!moveToTarget())
                {
                    resetTarget();
                }
            }
        }
    }

    private boolean tryMineTarget()
    {
        blockling.getLookHelper().setLookPosition(targetVec.x, targetVec.y, targetVec.z, 1000, 100);

        if (!blockling.isMining())
        {
            blockling.startMining();
        }

        if (blockling.getMiningTimer() == 0 && blockling.miningAbilities.isAbilityAcquired(AbilityHelper.brittleBlock))
        {
            if (rand.nextFloat() < 0.1f)
            {
                mineTarget();
                blockling.stopMining();
                world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, -1);
                return true;
            }
        }

        if (blockling.getMiningTimer() >= blockling.getMiningInterval())
        {
            mineTarget();
            blockling.stopMining();
            world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, -1);
            return true;
        }
        else
        {
            int progress = (int)(((float)(blockling.getMiningTimer()) / (float)blockling.getMiningInterval()) * 9.0f);
            world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, progress);
            return false;
        }
    }

    private void mineTarget()
    {
        NonNullList<ItemStack> dropStacks = DropHelper.getDops(blockling, world, targetPos);
        for (ItemStack dropStack : dropStacks)
        {
            if (blockling.miningAbilities.isAbilityAcquired(AbilityHelper.blocksmith))
            {
                ItemStack smeltResult = DropHelper.getFurnaceResult(blockling, dropStack);
                dropStack = smeltResult != ItemStack.EMPTY ? smeltResult : dropStack;
            }

            ItemStack leftoverStack = blockling.inv.addItem(dropStack);
            if (!leftoverStack.isEmpty())
            {
                blockling.entityDropItem(leftoverStack, 0);
            }
        }

        if (blockling.isUsingPickaxeRight())
        {
            blockling.damageItem(EnumHand.MAIN_HAND);
        }
        if (blockling.isUsingPickaxeLeft())
        {
            blockling.damageItem(EnumHand.OFF_HAND);
        }

        blockling.incrementMiningXp(rand.nextInt(5) + 3);
        world.setBlockToAir(targetPos);
    }

//    private void setTargetToRandom()
//    {
//        if (getBlockFromPos(targetPos) != Blocks.DIAMOND_ORE)
//        {
//            world.setBlockState(targetPos, Blocks.DIAMOND_ORE.getDefaultState());
//        }
//        else
//        {
//            world.setBlockState(targetPos, Blocks.GOLD_ORE.getDefaultState());
//        }
//    }
}
