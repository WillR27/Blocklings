package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingMineTargetGoal;
import com.willr27.blocklings.entity.goals.blockling.target.IHasTargetGoal;
import com.willr27.blocklings.item.DropUtil;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.skills.BlocklingSkills;
import com.willr27.blocklings.skills.Skill;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;

public class BlocklingMineGoal extends BlocklingGoal implements IHasTargetGoal
{
    public final GoalWhitelist oreWhitelist;

    private final BlocklingMineTargetGoal targetGoal;

    private Path path = null;
    private int recalc = 0;
    private final int recalcInterval = 20;
    private float prevMoveDist = 0.0f;

    public BlocklingMineGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        targetGoal = new BlocklingMineTargetGoal(this);

        oreWhitelist = new GoalWhitelist("24d7135e-607b-413b-a2a7-00d19119b9de", "ores", Whitelist.Type.BLOCK, this);
        oreWhitelist.setIsUnlocked(blockling.getSkills().getSkill(BlocklingSkills.Mining.WHITELIST).isBought(), false);
        BlockUtil.ORES.forEach(ore -> oreWhitelist.put(ore.getRegistryName(), true));
        whitelists.add(oreWhitelist);

        setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
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

        if (!blockling.getEquipment().hasToolEquipped(ToolType.PICKAXE))
        {
            return false;
        }

        if (!targetGoal.hasTarget())
        {
            return false;
        }

        if (!blockling.getEquipment().canHarvestBlockWithEquippedTools(world.getBlockState(targetGoal.getTargetPos())))
        {
            return false;
        }

        path = blockling.getNavigation().createPath(targetGoal.getTargetPos(), 0);

        if (path == null)
        {
            if (!isInRange(targetGoal.getTargetPos()))
            {
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

        if (!blockling.getEquipment().hasToolEquipped(ToolType.PICKAXE))
        {
            return false;
        }

        if (!targetGoal.hasTarget())
        {
            return false;
        }

        if (!blockling.getEquipment().canHarvestBlockWithEquippedTools(world.getBlockState(targetGoal.getTargetPos())))
        {
            return false;
        }

        if (path == null)
        {
            if (!isInRange(targetGoal.getTargetPos()))
            {
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

        prevMoveDist = 0.0f;
    }

    @Override
    public void tick()
    {
        super.tick();

        // Tick to make sure isFinished is only true for a single tick
        blockling.getActions().mine.tick(0.0f);

        if (isInRange(targetGoal.getTargetPos()))
        {
            ItemStack mainStack = blockling.getMainHandItem();
            ItemStack offStack = blockling.getOffhandItem();

            BlockPos targetBlockPos = targetGoal.getTargetPos();
            BlockState targetBlockState = world.getBlockState(targetBlockPos);

            boolean mainCanHarvest = ToolUtil.canToolHarvestBlock(mainStack, targetBlockState);
            boolean offCanHarvest = ToolUtil.canToolHarvestBlock(offStack, targetBlockState);

            blockling.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(targetBlockPos.getX() + 0.5, targetBlockPos.getY() + 0.5, targetBlockPos.getZ() + 0.5));

            if (mainCanHarvest || offCanHarvest)
            {
                blockling.getActions().mine.tryStart();

                if (blockling.getActions().mine.isRunning())
                {
                    float blocklingDestroySpeed = blockling.getStats().miningSpeed.getValue();
                    float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolMiningSpeedWithEnchantments(mainStack) : 0.0f;
                    float offDestroySpeed = offCanHarvest ? ToolUtil.getToolMiningSpeedWithEnchantments(offStack) : 0.0f;

                    float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                    float blockStrength = targetBlockState.getDestroySpeed(world, targetGoal.getTargetPos());

                    blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                    float progress = destroySpeed / blockStrength / 100.0f;
                    blockling.getActions().mine.tick(progress);

                    if (blockling.getActions().mine.isFinished())
                    {
                        blockling.getActions().mine.stop();
                        blockling.getStats().miningXp.incValue((int) (blockStrength * 2.0f));

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
            if ((path == null || path.isDone() || !hasMovedSinceLastRecalc() || blockling.getNavigation().isStuck()) && !isInRange(targetGoal.getTargetPos()))
            {
                targetGoal.markTargetBad();
            }

            tryCalculatePath();

            prevMoveDist = blockling.moveDist;
        }
    }

    private void tryCalculatePath()
    {
        path = blockling.getNavigation().createPath(targetGoal.getTargetPos(), -1);
        blockling.getNavigation().moveTo(path, 1.0);
    }

    private boolean hasMovedSinceLastRecalc()
    {
        return blockling.moveDist - prevMoveDist > 0.01f;
    }

    private boolean isInRange(BlockPos blockPos)
    {
        float rangeSq = blockling.getStats().miningRangeSq.getValue();
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
