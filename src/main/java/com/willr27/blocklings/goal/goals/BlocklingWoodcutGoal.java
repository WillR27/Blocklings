package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.skill.skills.WoodcuttingSkills;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.util.*;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Chops the targeted tree.
 */
public class BlocklingWoodcutGoal extends BlocklingGatherGoal
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
     * The current target tree.
     */
    @Nonnull
    private final Tree tree = new Tree();

    /**
     * The log whitelist.
     */
    @Nonnull
    public final GoalWhitelist logWhitelist;

    /**
     * The set of positions we have attempted to use as path targets so far.
     */
    @Nonnull
    private final Set<BlockPos> pathTargetPositionsTested = new HashSet<>();

    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingWoodcutGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        logWhitelist = new GoalWhitelist("fbfbfd44-c1b0-4420-824a-270b34c866f7", "logs", Whitelist.Type.BLOCK, this);
        logWhitelist.setIsUnlocked(blockling.getSkills().getSkill(WoodcuttingSkills.WHITELIST).isBought(), false);
        BlockUtil.TREES.get().forEach(tree -> logWhitelist.put(tree.log.getRegistryName(), true));
        whitelists.add(logWhitelist);

        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
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

        tree.logs.clear();
        tree.leaves.clear();
    }

    @Override
    protected void tickGather()
    {
        super.tickGather();

        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        BlockPos targetPos = getTarget();
        BlockState targetBlockState = world.getBlockState(targetPos);
        Block targetBlock = targetBlockState.getBlock();

        boolean mainCanHarvest = ToolUtil.canToolHarvestBlock(mainStack, targetBlockState);
        boolean offCanHarvest = ToolUtil.canToolHarvestBlock(offStack, targetBlockState);

        if (mainCanHarvest || offCanHarvest)
        {
            blockling.getActions().gather.tryStart();

            if (blockling.getActions().gather.isRunning())
            {
                float blocklingDestroySpeed = blockling.getStats().woodcuttingSpeed.getValue();
                float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolWoodcuttingSpeedWithEnchantments(mainStack) : 0.0f;
                float offDestroySpeed = offCanHarvest ? ToolUtil.getToolWoodcuttingSpeedWithEnchantments(offStack) : 0.0f;

                float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                float blockStrength = targetBlockState.getDestroySpeed(world, targetPos) + 1.5f;

                blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                float progress = destroySpeed / blockStrength / 100.0f;
                blockling.getActions().gather.tick(progress);

                if (blockling.getActions().gather.isFinished())
                {
                    blockling.getActions().gather.stop();
                    blockling.getStats().woodcuttingXp.incrementValue((int) blockStrength);

                    for (ItemStack stack : DropUtil.getDrops(DropUtil.Context.WOODCUTTING, blockling, targetPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                    {
                        stack = blockling.getEquipment().addItem(stack);
                        blockling.dropItemStack(stack);
                    }

                    if (mainStack.hurt(mainCanHarvest ? blockling.getSkills().getSkill(WoodcuttingSkills.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        mainStack.shrink(1);
                    }

                    if (offStack.hurt(offCanHarvest ? blockling.getSkills().getSkill(WoodcuttingSkills.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        offStack.shrink(1);
                    }

                    blockling.incLogsChoppedRecently();

                    world.destroyBlock(targetPos, false);
                    world.destroyBlockProgress(blockling.getId(), targetPos, -1);

                    if (blockling.getSkills().getSkill(WoodcuttingSkills.LEAF_BLOWER).isBought())
                    {
                        for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(targetPos))
                        {
                            if (isValidLeavesPos(surroundingPos))
                            {
                                if (blockling.getSkills().getSkill(WoodcuttingSkills.TREE_SURGEON).isBought())
                                {
                                    for (ItemStack stack : DropUtil.getDrops(DropUtil.Context.WOODCUTTING, blockling, surroundingPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                                    {
                                        stack = blockling.getEquipment().addItem(stack);
                                        blockling.dropItemStack(stack);
                                    }
                                }

                                world.destroyBlock(surroundingPos, false);
                            }
                        }
                    }

                    if (blockling.getSkills().getSkill(WoodcuttingSkills.LUMBER_AXE).isBought())
                    {
                        for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(targetPos))
                        {
                            Block surroundingBlock = world.getBlockState(surroundingPos).getBlock();

                            if (isValidTarget(surroundingPos))
                            {
                                for (ItemStack stack : DropUtil.getDrops(DropUtil.Context.WOODCUTTING, blockling, surroundingPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                                {
                                    stack = blockling.getEquipment().addItem(stack);
                                    blockling.dropItemStack(stack);
                                }

                                world.destroyBlock(surroundingPos, false);

                                if (blockling.getSkills().getSkill(WoodcuttingSkills.REPLANTER).isBought())
                                {
                                    Block saplingBlock = BlockUtil.getSaplingFromLog(surroundingBlock);

                                    if (saplingBlock != null)
                                    {
                                        if (BlockUtil.canPlaceAt(world, saplingBlock, surroundingPos))
                                        {
                                            ItemStack itemStack = new ItemStack(saplingBlock);

                                            if (blockling.getEquipment().has(itemStack))
                                            {
                                                blockling.getEquipment().take(itemStack);

                                                world.setBlock(surroundingPos, saplingBlock.defaultBlockState(), 3);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (blockling.getSkills().getSkill(WoodcuttingSkills.REPLANTER).isBought())
                    {
                        Block saplingBlock = BlockUtil.getSaplingFromLog(targetBlock);

                        if (saplingBlock != null)
                        {
                            if (BlockUtil.canPlaceAt(world, saplingBlock, targetPos))
                            {
                                ItemStack itemStack = new ItemStack(saplingBlock);

                                if (blockling.getEquipment().has(itemStack))
                                {
                                    blockling.getEquipment().take(itemStack);

                                    world.setBlock(targetPos, saplingBlock.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
                else
                {
                    world.destroyBlockProgress(blockling.getId(), targetPos, BlockUtil.calcBlockBreakProgress(blockling.getActions().gather.count()));
                }
            }
        }
        else
        {
            world.destroyBlockProgress(blockling.getId(), targetPos, -1);
            blockling.getActions().gather.stop();
        }
    }
    @Override
    public void checkForAndRemoveInvalidTargets()
    {
        for (BlockPos blockPos : new ArrayList<>(tree.logs))
        {
            if (!isValidTarget(blockPos))
            {
                markBad(blockPos);
            }
        }
    }

    @Override
    public boolean tryRecalcTarget()
    {
        if (super.tryRecalcTarget())
        {
            return true;
        }

        if (tree.logs.isEmpty())
        {
            if (!tryFindTree())
            {
                return false;
            }

                Pair<BlockPos, Path> pathToTree = findPathToTree();

            if (pathToTree == null)
            {
                return false;
            }

            setPathTargetPos(pathToTree.getKey(), pathToTree.getValue());
        }

        setTarget((BlockPos) tree.logs.toArray()[tree.logs.size() - 1]);

        return true;
    }

    @Override
    public void markEntireTargetBad()
    {
        while (!tree.logs.isEmpty())
        {
            markBad(tree.logs.get(0));
        }

        while (!tree.leaves.isEmpty())
        {
            markBad(tree.leaves.get(0));
        }
    }

    @Override
    public void markBad(@Nonnull BlockPos blockPos)
    {
        super.markBad(blockPos);

        tree.logs.remove(blockPos);
        tree.leaves.remove(blockPos);
    }

    @Override
    protected boolean isValidTargetBlock(@Nonnull Block block)
    {
        return logWhitelist.isEntryWhitelisted(block);
    }

    @Nonnull
    @Override
    protected ToolType getToolType()
    {
        return ToolType.AXE;
    }

    /**
     * Tries to find the nearest tree.
     *
     * @return true if a tree was found.
     */
    private boolean tryFindTree()
    {
        BlockPos blocklingBlockPos = blockling.blockPosition();

        Tree tree = new Tree();
        List<BlockPos> testedBlockPositions = new ArrayList<>();

        double closestTreeDistSq = Float.MAX_VALUE;

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
                        Tree treeToTest = findTreeFrom(testBlockPos);

                        // How many leaves we need per log for it to be a valid tree.
                        final float leafToLogRatio = 0.8f;

                        if (treeToTest.logs.size() / (float) treeToTest.leaves.size() > leafToLogRatio)
                        {
                            continue;
                        }

                        boolean canSeeTree = false;

                        for (BlockPos logBlockPos : treeToTest.logs)
                        {
                            if (!testedBlockPositions.contains(logBlockPos))
                            {
                                testedBlockPositions.add(logBlockPos);
                            }

                            if (!canSeeTree && EntityUtil.canSee(blockling, logBlockPos))
                            {
                                canSeeTree = true;
                            }
                        }

                        for (BlockPos leafBlockPos : treeToTest.leaves)
                        {
                            if (!testedBlockPositions.contains(leafBlockPos))
                            {
                                testedBlockPositions.add(leafBlockPos);
                            }

                            if (!canSeeTree && EntityUtil.canSee(blockling, leafBlockPos))
                            {
                                canSeeTree = true;
                            }
                        }

                        if (!canSeeTree)
                        {
                            continue;
                        }

                        for (BlockPos logBlockPos : treeToTest.logs)
                        {
                            float distanceSq = (float) blockling.distanceToSqr(logBlockPos.getX() + 0.5f, logBlockPos.getY() + 0.5f, logBlockPos.getZ() + 0.5f);

                            if (distanceSq < closestTreeDistSq)
                            {
                                closestTreeDistSq = distanceSq;
                                tree = treeToTest;

                                break;
                            }
                        }
                    }
                }
            }
        }

        if (!tree.logs.isEmpty())
        {
            this.tree.logs.clear();
            this.tree.leaves.clear();
            this.tree.logs.addAll(tree.logs);
            this.tree.leaves.addAll(tree.leaves);

            return true;
        }

        return false;
    }

    /**
     * Finds the tree stemming from the given pos.
     *
     * @param blockPos the starting pos.
     * @return the tree.
     */
    @Nonnull
    private Tree findTreeFrom(@Nonnull BlockPos blockPos)
    {
        Tree tree = new Tree();
        Set<BlockPos> logBlockPositionsToTest = new HashSet<>();

        logBlockPositionsToTest.add(blockPos);
        tree.logs.add(blockPos);

        while (!logBlockPositionsToTest.isEmpty())
        {
            BlockPos testBlockPos = logBlockPositionsToTest.stream().findFirst().get();

            for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(testBlockPos))
            {
                if (isValidTarget(surroundingPos))
                {
                    if (!tree.logs.contains(surroundingPos))
                    {
                        tree.logs.add(surroundingPos);
                        logBlockPositionsToTest.add(surroundingPos);
                    }
                }
                else if (isValidLeavesPos(surroundingPos))
                {
                    if (!tree.leaves.contains(surroundingPos))
                    {
                        tree.leaves.add(surroundingPos);
                    }
                }
            }

            logBlockPositionsToTest.remove(testBlockPos);
        }

        return tree;
    }

    /**
     * Finds the first valid path to the tree, not necessarily the most optimal.
     *
     * @return the path target position and the path to the tree, or null if no path could be found.
     */
    @Nullable
    public Pair<BlockPos, Path> findPathToTree()
    {
        for (BlockPos logBlockPos : tree.logs)
        {
            if (BlockUtil.areAllAdjacentBlocksSolid(world, logBlockPos))
            {
                continue;
            }

//            if (isBadPathTargetPos(logBlockPos))
//            {
//                continue;
//            }

            Path path = EntityUtil.createPathTo(blockling, logBlockPos, getRangeSq());

            if (path != null)
            {
                return new MutablePair<>(logBlockPos, path);
            }
        }

        return null;
    }

    /**
     * Sets the tree's root position to the given block pos.
     * Will then     recalculate the tree.
     *
     * @param blockPos the block pos to use as the tree's root.
     */
    public void changeTreeRootTo(@Nonnull BlockPos blockPos)
    {
        tree.logs.clear();
        tree.leaves.clear();

        Tree newTree = findTreeFrom(blockPos);

        tree.logs.addAll(newTree.logs);
        tree.leaves.addAll(newTree.leaves);
    }

    /**
     * @param blockPos the pos to check.
     * @return true if the block at the pos is leaves and has a persistent property set to false.
     */
    private boolean isValidLeavesPos(@Nonnull BlockPos blockPos)
    {
        return isValidLeaves(world.getBlockState(blockPos));
    }

    /**
     * @param blockState the blockState to check.
     * @return true if the block is a leaves block and has a persistent property set to false.
     */
    private boolean isValidLeaves(@Nonnull BlockState blockState)
    {
        return isValidLeaves(blockState.getBlock()) && (!(blockState.getBlock() instanceof LeavesBlock) || !blockState.getValue(LeavesBlock.PERSISTENT));
    }

    /**
     * @param block the block to check.
     * @return true if the block is leaves.
     */
    private boolean isValidLeaves(@Nonnull Block block)
    {
        return BlockUtil.isLeaves(block);
    }

    @Override
    protected boolean recalcPath(boolean force)
    {
        if (force)
        {
            Pair<BlockPos, Path> result = findPathToTree();

            if (result != null)
            {
                setPathTargetPos(result.getKey(), result.getValue());
            }
            else
            {
                setPathTargetPos(null, null);

                return false;
            }

            return true;
        }

        // Try to improve our path each recalc by testing different logs in the tree
        for (BlockPos logBlockPos : tree.logs)
        {
            if (pathTargetPositionsTested.contains(logBlockPos))
            {
                continue;
            }

            pathTargetPositionsTested.add(logBlockPos);

            if (BlockUtil.areAllAdjacentBlocksSolid(world, logBlockPos))
            {
                continue;
            }

            Path path = EntityUtil.createPathTo(blockling, logBlockPos, getRangeSq());

            if (path != null)
            {
                if (path.getDistToTarget() < this.path.getDistToTarget())
                {
                    setPathTargetPos(logBlockPos, path);

                    return true;
                }
            }

            return hasPath();
        }

        pathTargetPositionsTested.clear();

        return false;
    }

    @Override
    protected boolean isValidPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return tree.logs.contains(blockPos);
    }

    @Override
    public void setPathTargetPos(@Nullable BlockPos blockPos, @Nullable Path pathToPos)
    {
        super.setPathTargetPos(blockPos, pathToPos);

        if (hasPathTargetPos())
        {
            changeTreeRootTo(getPathTargetPos());
        }
    }

    @Override
    public float getRangeSq()
    {
        return blockling.getStats().woodcuttingRangeSq.getValue();
    }

    /**
     * Class to represent a tree.
     */
    public static class Tree
    {
        public final List<BlockPos> logs = new ArrayList<>();
        public final List<BlockPos> leaves = new ArrayList<>();
    }
}
