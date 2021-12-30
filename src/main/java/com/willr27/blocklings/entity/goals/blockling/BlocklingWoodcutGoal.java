package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingWoodcutTargetGoal;
import com.willr27.blocklings.entity.goals.blockling.target.IHasTargetGoal;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.item.DropUtil;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;
import java.util.stream.Collectors;

public class BlocklingWoodcutGoal extends BlocklingGoal implements IHasTargetGoal
{
    public final GoalWhitelist logWhitelist;

    private final BlocklingWoodcutTargetGoal targetGoal;

    private BlockPos trunkPos;
    private Set<BlockPos> badTrunkBlockPositions = new HashSet<>();
    private Path path = null;
    private int recalc = 0;
    private final int recalcInterval = 20;
    private float prevMoveDist = 0.0f;

    /**
     * How many recalcs are called before a block is no longer bad.
     */
    private final int recalcBadInterval = 20;

    public BlocklingWoodcutGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        targetGoal = new BlocklingWoodcutTargetGoal(this);

        logWhitelist = new GoalWhitelist("fbfbfd44-c1b0-4420-824a-270b34c866f7", "logs", Whitelist.Type.BLOCK, this);
        BlockUtil.LOGS.forEach(log -> logWhitelist.put(log.getRegistryName(), true));
        whitelists.add(logWhitelist);

        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public BlocklingTargetGoal getTargetGoal()
    {
        return targetGoal;
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        if (!blockling.getEquipment().hasToolEquipped(ToolType.AXE))
        {
            return false;
        }

        if (targetGoal.getTree().logs.isEmpty())
        {
            return false;
        }

        if (!blockling.getEquipment().canHarvestBlockWithEquippedTools(world.getBlockState(targetGoal.getTargetPos())))
        {
            return false;
        }

        calculateTrunkPosAndPath();

        if (path == null)
        {
            if (trunkPos == null || !isInRange(trunkPos))
            {
                targetGoal.markTreeBad();
                badTrunkBlockPositions.clear();

                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        if (!blockling.getEquipment().hasToolEquipped(ToolType.AXE))
        {
            return false;
        }

        if (targetGoal.getTree().logs.isEmpty())
        {
            return false;
        }

        if (!blockling.getEquipment().canHarvestBlockWithEquippedTools(world.getBlockState(targetGoal.getTargetPos())))
        {
            return false;
        }

        if (path == null)
        {
            if (trunkPos == null || !isInRange(trunkPos))
            {
                targetGoal.markTreeBad();

                return false;
            }
        }

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        blockling.getNavigation().moveTo(path, 1.0);
    }

    @Override
    public void stop()
    {
        super.stop();

        if (targetGoal.hasTarget())
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getTargetPos(), -1);
        }

        if (targetGoal.hasPrevTarget())
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getPrevTargetPos(), -1);
        }

        blockling.getNavigation().stop();
        blockling.getActions().mine.stop();

        badTrunkBlockPositions.clear();

        trunkPos = null;
        prevMoveDist = 0.0f;
    }

    @Override
    public void tick()
    {
        super.tick();

        // Tick to make sure isFinished is only true for a single tick
        blockling.getActions().mine.tick(0.0f);

        if (isInRange(trunkPos))
        {
            ItemStack mainStack = blockling.getMainHandItem();
            ItemStack offStack = blockling.getOffhandItem();

            BlockPos targetBlockPos = targetGoal.getTargetPos();
            BlockState targetBlockState = world.getBlockState(targetBlockPos);

            boolean mainCanHarvest = ToolUtil.canToolHarvestBlock(mainStack, targetBlockState);
            boolean offCanHarvest = ToolUtil.canToolHarvestBlock(offStack, targetBlockState);

            blockling.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(trunkPos.getX() + 0.5, trunkPos.getY() + 0.5, trunkPos.getZ() + 0.5));

            if (mainCanHarvest || offCanHarvest)
            {
                blockling.getActions().mine.tryStart();

                if (blockling.getActions().mine.isRunning())
                {
                    float blocklingDestroySpeed = blockling.getStats().woodcuttingSpeed.getValue();
                    float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolWoodcuttingSpeedWithEnchantments(mainStack) : 0.0f;
                    float offDestroySpeed = offCanHarvest ? ToolUtil.getToolWoodcuttingSpeedWithEnchantments(offStack) : 0.0f;

                    float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                    float blockStrength = targetBlockState.getDestroySpeed(world, targetGoal.getTargetPos());

                    blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                    float progress = destroySpeed / blockStrength / 100.0f;
                    blockling.getActions().mine.tick(progress);

                    if (blockling.getActions().mine.isFinished())
                    {
                        blockling.getActions().mine.stop();
                        blockling.getStats().woodcuttingXp.incValue((int) (blockStrength * 3.0f));

                        for (ItemStack stack : DropUtil.getDrops(blockling, targetBlockPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                        {
                            stack = blockling.getEquipment().addItem(stack);
                            blockling.dropItemStack(stack);
                        }

                        if (mainStack.hurt(mainCanHarvest ? 1 : 0, blockling.getRandom(), null))
                        {
                            mainStack.shrink(1);
                        }

                        if (offStack.hurt(offCanHarvest ? 1 : 0, blockling.getRandom(), null))
                        {
                            offStack.shrink(1);
                        }

                        world.destroyBlock(targetBlockPos, false);
                        world.destroyBlockProgress(blockling.getId(), targetBlockPos, 0);

                        forceRecalc();
                    }
                    else
                    {
                        world.destroyBlockProgress(blockling.getId(), targetBlockPos, BlockUtil.calcBlockBreakProgress(blockling.getActions().mine.count()));
                    }
                }
            }
            else
            {
                world.destroyBlockProgress(blockling.getId(), targetBlockPos, -1);
                blockling.getActions().mine.stop();
            }
        }
        else
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getTargetPos(), -1);
            blockling.getActions().mine.stop();
        }

        if (tickRecalc())
        {
            if ((path == null || path.isDone() || !hasMovedSinceLastRecalc() || blockling.getNavigation().isStuck()) && !isInRange(trunkPos))
            {
                badTrunkBlockPositions.add(trunkPos);
            }

            tryCalculatePath();

            prevMoveDist = blockling.moveDist;
        }
    }

    private void calculateTrunkPosAndPath()
    {
        List<BlockPos> sortedLogBlockPositions = targetGoal.getTree().logs.stream().sorted((o1, o2) -> o1.getY() > o2.getY() ? 1 : -1).collect(Collectors.toList());

        for (BlockPos testTrunkPos : sortedLogBlockPositions)
        {
            if (badTrunkBlockPositions.contains(testTrunkPos))
            {
                continue;
            }

            path = createPath(testTrunkPos);

            if (path != null || isInRange(testTrunkPos))
            {
                trunkPos = testTrunkPos;

                return;
            }
            else
            {
                badTrunkBlockPositions.add(testTrunkPos);
            }
        }

        path = null;
        trunkPos = null;
    }

    private Path createPath(BlockPos blockPos)
    {
        Path closestPath = null;
        double closestDistanceSq = Double.MAX_VALUE;

        Path path = blockling.getNavigation().createPath(blockPos, 0);

        if (path != null)
        {
            closestPath = path;
            closestDistanceSq = blockPos.distSqr(path.getTarget());
        }

        for (BlockPos adjacentPos : BlockUtil.getSurroundingBlockPositions(blockPos))
        {
            path = blockling.getNavigation().createPath(adjacentPos, 0);

            if (path != null)
            {
                double distanceSq = adjacentPos.distSqr(path.getTarget());

                if (distanceSq < closestDistanceSq)
                {
                    closestPath = path;
                    closestDistanceSq = distanceSq;
                }
            }
        }

        return closestPath;
    }

    private void tryCalculatePath()
    {
        calculateTrunkPosAndPath();
        blockling.getNavigation().moveTo(path, 1.0);
    }

    private boolean hasMovedSinceLastRecalc()
    {
        return blockling.moveDist - prevMoveDist > 0.01f;
    }

    private boolean isInRange(BlockPos blockPos)
    {
        float rangeSq = blockling.getStats().woodcuttingRangeSq.getValue();
        float distanceSq = (float) blockling.distanceToSqr(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);

        return distanceSq < rangeSq;
    }

    private void forceRecalc()
    {
        targetGoal.forceRecalc();
        recalc = recalcInterval;
    }

    private boolean tickRecalc()
    {
        recalc++;

        if (recalc < recalcInterval)
        {
            return false;
        }
        else
        {
            recalc = 0;
        }

        return true;
    }
}
