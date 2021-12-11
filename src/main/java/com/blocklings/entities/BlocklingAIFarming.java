package com.blocklings.entities;

import com.blocklings.abilities.AbilityHelper;
import com.blocklings.util.helpers.BlockHelper;
import com.blocklings.util.helpers.DropHelper;
import com.blocklings.util.helpers.EntityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlocklingAIFarming extends BlocklingAIGatherBase
{
    private static final int X_RADIUS = 10, Y_RADIUS = 10;

    private int targetYValue;

    public BlocklingAIFarming(EntityBlockling blockling)
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

        boolean foundCrop = false;

        resetTarget();
        targetPathSquareDistance = 10000;
        targetYValue = -1000;

        for (int x = (int) blockling.posX - X_RADIUS; x < blockling.posX + X_RADIUS; x++)
        {
            for (int y = (int) blockling.posY + Y_RADIUS; y > blockling.posY - Y_RADIUS; y--)
            {
                for (int z = (int) blockling.posZ - X_RADIUS; z < blockling.posZ + X_RADIUS; z++)
                {
                    Block block = getBlockAt(x, y, z);
                    if (BlockHelper.isCrop(block))
                    {
                        // Check block is grown
                        if (!BlockHelper.isGrown(world.getBlockState(new BlockPos(x, y, z))))
                        {
                            continue;
                        }

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
                            foundCrop = true;
                        }

                        Path pathToBlock = getSafishPathTo(blockPos);
                        if (pathToBlock != null)
                        {
                            if (isPathDestInRange(pathToBlock, blockPos))
                            {
                                // Find the closest block (using path distance)
                                double pathSquareDistance = getPathSquareDistance(pathToBlock);
                                if (y >= targetYValue && (pathSquareDistance - 10) < targetPathSquareDistance)
                                {
                                    targetPathSquareDistance = pathSquareDistance;
                                    targetYValue = y;
                                    setTarget(blockPos, pathToBlock);
                                    foundCrop = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return foundCrop;
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
        else if (blockling.getTask() != EntityHelper.Task.FARM)
        {
            return false;
        }
        else if (!blockling.hasHoe())
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
                if (tryHarvestTarget())
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

    private boolean tryHarvestTarget()
    {
        blockling.getLookHelper().setLookPosition(targetVec.x, targetVec.y, targetVec.z, 1000, 100);

        if (!blockling.isMining())
        {
            blockling.startMining();
        }

        if (blockling.getMiningTimer() >= blockling.getFarmingInterval())
        {
            harvestBlock();
            blockling.stopMining();
            world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, -1);
            return true;
        }
        else
        {
            int progress = (int)(((float)(blockling.getMiningTimer()) / (float)blockling.getFarmingInterval()) * 9.0f);
            world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, progress);
            return false;
        }
    }

    private void harvestBlock()
    {
        if (blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.scythe))
        {
            if (rand.nextFloat() < 0.1f)
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int z = -1; z < 2; z++)
                    {
                        BlockPos surroundingPos = new BlockPos(targetPos.getX() + x, targetPos.getY(), targetPos.getZ() + z);
                        Block surroundingBlock = getBlockFromPos(surroundingPos);
                        if (BlockHelper.isCrop(surroundingBlock))
                        {
                            IBlockState surroundingState = world.getBlockState(surroundingPos);
                            if (BlockHelper.isGrown(surroundingState))
                            {
                                NonNullList<ItemStack> dropStacks = DropHelper.getDops(blockling, world, surroundingPos);
                                for (ItemStack dropStack : dropStacks)
                                {
                                    if (blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.plentifulHarvest) && rand.nextFloat() <= 0.5f)
                                    {
                                        if (!(dropStack.getItem() instanceof ItemBlock))
                                        {
                                            dropStack.grow(dropStack.getCount());
                                        }
                                    }
                                    ItemStack leftoverStack = blockling.inv.addItem(dropStack);
                                    if (!leftoverStack.isEmpty())
                                    {
                                        blockling.entityDropItem(leftoverStack, 0);
                                    }
                                }

                                Item seed = BlockHelper.getSeed(getBlockFromPos(surroundingPos));
                                if (seed != Items.AIR)
                                {
                                    int slot = blockling.inv.find(seed);
                                    world.setBlockToAir(surroundingPos);
                                    world.setBlockState(surroundingPos, surroundingBlock.getDefaultState());
                                    if (slot != -1)
                                    {
                                        if (blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.clinicalDibber) && rand.nextFloat() < 0.5)
                                        {
                                            blockling.inv.getStackInSlot(slot).shrink(1);
                                        }
                                        if (blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.fertilisationFarming) && blockling.inv.takeStackFromInventory(new ItemStack(Items.DYE, 1, 15)))
                                        {
                                            BlockCrops cropBlock = ((BlockCrops)getBlockFromPos(surroundingPos));
                                            cropBlock.grow(world, rand, surroundingPos, world.getBlockState(surroundingPos));
                                            world.playEvent(2005, surroundingPos, 0);
                                        }
                                    }
                                }
                                else
                                {
                                    world.setBlockToAir(surroundingPos);
                                }
                            }
                        }
                    }
                }
            }
        }

        NonNullList<ItemStack> dropStacks = DropHelper.getDops(blockling, world, targetPos);
        for (ItemStack dropStack : dropStacks)
        {
            if (blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.plentifulHarvest) && rand.nextFloat() <= 0.5f)
            {
                if (!(dropStack.getItem() instanceof ItemBlock))
                {
                    dropStack.grow(dropStack.getCount());
                }
            }
            ItemStack leftoverStack = blockling.inv.addItem(dropStack);
            if (!leftoverStack.isEmpty())
            {
                blockling.entityDropItem(leftoverStack, 0);
            }
        }

        if (blockling.isUsingHoeRight())
        {
            blockling.damageItem(EnumHand.MAIN_HAND);
        }
        if (blockling.isUsingHoeLeft())
        {
            blockling.damageItem(EnumHand.OFF_HAND);
        }

        blockling.incrementFarmingXp(rand.nextInt(5) + 3);
        Item seed = blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.replanter) ? BlockHelper.getSeed(getBlockFromPos(targetPos)) : Items.AIR;
        if (seed != Items.AIR)
        {
            int slot = blockling.inv.find(seed);
            world.setBlockToAir(targetPos);
            world.setBlockState(targetPos, targetBlock.getDefaultState());
            if (slot != -1)
            {
                if (blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.clinicalDibber) && rand.nextFloat() < 0.5)
                {
                    blockling.inv.getStackInSlot(slot).shrink(1);
                }
                if (blockling.farmingAbilities.isAbilityAcquired(AbilityHelper.fertilisationFarming) && blockling.inv.takeStackFromInventory(new ItemStack(Items.DYE, 1, 15)))
                {
                    BlockCrops cropBlock = ((BlockCrops)getBlockFromPos(targetPos));
                    cropBlock.grow(world, rand, targetPos, world.getBlockState(targetPos));
                    world.playEvent(2005, targetPos, 0);
                }
            }
        }
        else
        {
            world.setBlockToAir(targetPos);
        }
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
