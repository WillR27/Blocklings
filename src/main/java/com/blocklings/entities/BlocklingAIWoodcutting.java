package com.blocklings.entities;

import com.blocklings.abilities.AbilityHelper;
import com.blocklings.util.helpers.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class BlocklingAIWoodcutting extends BlocklingAIGatherBase
{
    private static final int X_RADIUS = 10, Y_RADIUS = 10;

    private int treeSearchCount = 0;
    private int leafCount = 0;
    private List<BlockPos> tree = new ArrayList<>();
    private List<BlockPos> logsToCheck = new ArrayList<>();

    private int badLogResetCount = 0;
    private List<BlockPos> logsThatAreNotTrees = new ArrayList<>();

    public BlocklingAIWoodcutting(EntityBlockling blockling)
    {
        super(blockling);
    }

    @Override
    public void resetTask()
    {
        blockling.stopMining();

        if (tree != null && !tree.isEmpty())
        {
            world.sendBlockBreakProgress(blockling.getEntityId(), tree.get(tree.size() - 1), -1);
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

        badLogResetCount++;

        if (badLogResetCount > 100)
        {
            logsThatAreNotTrees.clear();
            badLogResetCount = 0;
        }

        boolean foundLog = false;

        resetTarget();
        targetPathSquareDistance = 10000;

        for (int x = (int) blockling.posX - X_RADIUS; x < blockling.posX + X_RADIUS; x++)
        {
            for (int y = (int) blockling.posY - Y_RADIUS; y < blockling.posY + Y_RADIUS; y++)
            {
                for (int z = (int) blockling.posZ - X_RADIUS; z < blockling.posZ + X_RADIUS; z++)
                {
                    Block block = getBlockAt(x, y, z);
                    if (BlockHelper.isLog(block))
                    {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        Vec3d blockVec = getVecFromBlockPos(blockPos);

                        Block belowBlock = getBlockAt(x, y - 1, z);
                        if (!BlockHelper.isDirt(belowBlock))
                        {
                            continue;
                        }

                        // We have already decided before that this is not a tree;
                        if (logsThatAreNotTrees.contains(blockPos))
                        {
                            continue;
                        }

                        // If we are already in range to mine the block then set it as target
                        if (blockling.getPositionVector().distanceTo(blockVec) < range)
                        {
                            targetPathSquareDistance = 1;
                            setTarget(blockPos);
                            foundLog = true;
                        }

                        Path pathToBlock = getSafishPathTo(blockPos);
                        if (pathToBlock != null)
                        {
                            if (isPathDestInRange(pathToBlock, blockPos))
                            {
                                // Find the closest block (using path distance)
                                double pathSquareDistance = getPathSquareDistance(pathToBlock);
                                if (pathSquareDistance < targetPathSquareDistance)
                                {
                                    targetPathSquareDistance = pathSquareDistance;
                                    setTarget(blockPos, pathToBlock);
                                    foundLog = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return foundLog;
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
        else if (blockling.getTask() != EntityHelper.Task.CHOP)
        {
            return false;
        }
        else if (!blockling.hasAxe())
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
            if (findTree())
            {
                if (isTree())
                {
                    if (isBlocklingInRange(targetPos))
                    {
                        if (tryChopTarget())
                        {
                            if (tree.isEmpty())
                            {
                                resetTarget();
                            }
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
                else
                {
                    logsThatAreNotTrees.addAll(tree);
                    resetTarget();
                }
            }
        }
    }

    private boolean tryChopTarget()
    {
        blockling.getLookHelper().setLookPosition(targetVec.x, targetVec.y, targetVec.z, 1000, 100);

        if (!blockling.isMining())
        {
            blockling.startMining();
        }

        BlockPos logPos = tree.get(tree.size() - 1);
        if (blockling.getMiningTimer() >= blockling.getChoppingInterval())
        {
            if (blockling.woodcuttingAbilities.isAbilityAcquired(AbilityHelper.sawmill))
            {
                if (rand.nextFloat() < 0.1f && tree.size() > 1)
                {
                    chopLog(tree.size() - 2);
                }
            }

            chopLog();
            blockling.stopMining();
            world.sendBlockBreakProgress(blockling.getEntityId(), logPos, -1);
            return true;
        }
        else
        {
            int progress = (int)(((float)(blockling.getMiningTimer()) / (float)blockling.getChoppingInterval()) * 9.0f);
            world.sendBlockBreakProgress(blockling.getEntityId(), logPos, progress);
            return false;
        }
    }

    private void chopLog()
    {
        chopLog(tree.size() - 1);
    }

    private void chopLog(int pos)
    {
        BlockPos logPos = tree.get(pos);

        if (blockling.woodcuttingAbilities.isAbilityAcquired(AbilityHelper.leafBlower))
        {
            for (int i = -1; i < 2; i++)
            {
                for (int j = -1; j < 2; j++)
                {
                    for (int k = -1; k < 2; k++)
                    {
                        if (i == 0 && j == 0 && k == 0)
                        {
                            continue;
                        }

                        BlockPos surroundingPos = new BlockPos(logPos.getX() + i, logPos.getY() + j, logPos.getZ() + k);
                        Block surroundingBlock = getBlockFromPos(surroundingPos);

                        if (BlockHelper.isLeaf(surroundingBlock))
                        {
                            if (blockling.woodcuttingAbilities.isAbilityAcquired(AbilityHelper.treeSurgeon))
                            {
                                NonNullList<ItemStack> dropStacks = DropHelper.getDops(blockling, world, surroundingPos);
                                for (ItemStack dropStack : dropStacks)
                                {
                                    ItemStack leftoverStack = blockling.inv.addItem(dropStack);
                                    if (!leftoverStack.isEmpty())
                                    {
                                        blockling.entityDropItem(leftoverStack, 0);
                                    }
                                }
                            }

                            world.setBlockToAir(surroundingPos);
                        }
                    }
                }
            }
        }

        NonNullList<ItemStack> dropStacks = DropHelper.getDops(blockling, world, logPos);
        for (ItemStack dropStack : dropStacks)
        {
            if (blockling.woodcuttingAbilities.isAbilityAcquired(AbilityHelper.forestFire))
            {
                dropStack = new ItemStack(Items.COAL, dropStack.getCount(), 1);
            }

            ItemStack leftoverStack = blockling.inv.addItem(dropStack);
            if (!leftoverStack.isEmpty())
            {
                blockling.entityDropItem(leftoverStack, 0);
            }
        }

        if (blockling.isUsingAxeRight())
        {
            blockling.damageItem(EnumHand.MAIN_HAND);
        }
        if (blockling.isUsingAxeLeft())
        {
            blockling.damageItem(EnumHand.OFF_HAND);
        }

        ItemStack sapling = ItemHelper.getSaplingForLog(world.getBlockState(targetPos));
        if (sapling != null && pos == 0 && blockling.woodcuttingAbilities.isAbilityAcquired(AbilityHelper.treeHugger) && blockling.inv.takeStackFromInventory(sapling))
        {
            world.setBlockState(targetPos, Blocks.SAPLING.getStateFromMeta(sapling.getMetadata()));
            if (blockling.woodcuttingAbilities.isAbilityAcquired(AbilityHelper.fertilisationWoodcutting) && blockling.inv.takeStackFromInventory(new ItemStack(Items.DYE, 1, 15)))
            {
                BlockSapling saplingBlock = ((BlockSapling)getBlockFromPos(targetPos));
                saplingBlock.grow(world, targetPos, world.getBlockState(targetPos), rand);
                world.playEvent(2005, targetPos, 0);
            }
        }
        else
        {
            world.setBlockToAir(logPos);
        }

        blockling.incrementWoodcuttingXp(rand.nextInt(5) + 3);
        tree.remove(logPos);
    }

    private boolean isTree()
    {
        return leafCount > 1;
    }

    private boolean findTree()
    {
        if (logsToCheck.isEmpty())
        {
            if (tree.isEmpty())
            {
                treeSearchCount = 0;
                leafCount = 0;
                logsToCheck.add(targetPos);
                tree.add(targetPos);
            }
            else
            {
                return true;
            }
        }
        else
        {
            if (treeSearchCount > 150)
            {
                return true;
            }

            int localCount = 0;
            while (localCount < 2)
            {
                BlockPos testLog = logsToCheck.get(0);

                for (int i = -1; i < 2; i++)
                {
                    for (int j = -1; j < 2; j++)
                    {
                        for (int k = -1; k < 2; k++)
                        {
                            if (i == 0 && j == 0 && k == 0)
                            {
                                continue;
                            }

                            BlockPos surroundingPos = new BlockPos(testLog.getX() + i, testLog.getY() + j, testLog.getZ() + k);
                            Block surroundingBlock = getBlockFromPos(surroundingPos);

                            if (BlockHelper.isLog(surroundingBlock))
                            {
                                if (!tree.contains(surroundingPos))
                                {
                                    tree.add(surroundingPos);
                                    logsToCheck.add(surroundingPos);
                                }
                            }
                            else if (BlockHelper.isLeaf(surroundingBlock))
                            {
                                leafCount++;
                            }

                            localCount++;
                        }
                    }
                }

                logsToCheck.remove(0);
            }
        }

        treeSearchCount++;

        return false;
    }

    @Override
    void resetTarget()
    {
        super.resetTarget();
        tree.clear();
        logsToCheck.clear();
    }

    private void setTargetToRandom()
    {
        if (getBlockFromPos(targetPos) != Blocks.LOG)
        {
            world.setBlockState(targetPos, Blocks.LOG.getDefaultState());
        }
        else
        {
            world.setBlockState(targetPos, Blocks.LOG2.getDefaultState());
        }
    }
}
