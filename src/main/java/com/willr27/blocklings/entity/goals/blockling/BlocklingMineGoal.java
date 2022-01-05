package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingMineTargetGoal;
import com.willr27.blocklings.entity.goals.blockling.target.IHasTargetGoal;
import com.willr27.blocklings.item.DropUtil;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.skills.BlocklingSkills;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.UUID;

public class BlocklingMineGoal extends BlocklingGatherGoal<BlocklingMineTargetGoal> implements IHasTargetGoal<BlocklingMineTargetGoal>
{
    /**
     * The ore whitelist.
     */
    @Nonnull
    public final GoalWhitelist oreWhitelist;

    /**
     * The associated target goal.
     */
    @Nonnull
    private final BlocklingMineTargetGoal targetGoal;

    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingMineGoal(UUID id, BlocklingEntity blockling, BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingMineTargetGoal(this);

        oreWhitelist = new GoalWhitelist("24d7135e-607b-413b-a2a7-00d19119b9de", "ores", Whitelist.Type.BLOCK, this);
        oreWhitelist.setIsUnlocked(blockling.getSkills().getSkill(BlocklingSkills.Mining.WHITELIST).isBought(), false);
        BlockUtil.ORES.forEach(ore -> oreWhitelist.put(ore.getRegistryName(), true));
        whitelists.add(oreWhitelist);

        setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    @Nonnull
    public BlocklingMineTargetGoal getTargetGoal()
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

        setPathTargetPos(targetGoal.getTargetPos(), null);

        if (isStuck())
        {
            return false;
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

        if (isStuck())
        {
            getTargetGoal().markTargetBad();

            return false;
        }

        return true;
    }

    @Override
    protected void tickGather()
    {
        super.tickGather();

        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        BlockPos targetBlockPos = targetGoal.getTargetPos();
        BlockState targetBlockState = world.getBlockState(targetBlockPos);

        boolean mainCanHarvest = ToolUtil.canToolHarvestBlock(mainStack, targetBlockState);
        boolean offCanHarvest = ToolUtil.canToolHarvestBlock(offStack, targetBlockState);

        if (mainCanHarvest || offCanHarvest)
        {
            blockling.getActions().gather.tryStart();

            if (blockling.getActions().gather.isRunning())
            {
                float blocklingDestroySpeed = blockling.getStats().miningSpeed.getValue();
                float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolMiningSpeedWithEnchantments(mainStack) : 0.0f;
                float offDestroySpeed = offCanHarvest ? ToolUtil.getToolMiningSpeedWithEnchantments(offStack) : 0.0f;

                float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                float blockStrength = targetBlockState.getDestroySpeed(world, targetGoal.getTargetPos());

                blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                float progress = destroySpeed / blockStrength / 100.0f;
                blockling.getActions().gather.tick(progress);

                if (blockling.getActions().gather.isFinished())
                {
                    blockling.getActions().gather.stop();
                    blockling.getStats().miningXp.incValue((int) (blockStrength * 2.0f));

                    for (ItemStack stack : DropUtil.getDrops(blockling, targetBlockPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                    {
                        stack = blockling.getEquipment().addItem(stack);
                        blockling.dropItemStack(stack);
                    }

                    if (mainStack.hurt(mainCanHarvest ? blockling.getSkills().getSkill(BlocklingSkills.Mining.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        mainStack.shrink(1);
                    }

                    if (offStack.hurt(offCanHarvest ? blockling.getSkills().getSkill(BlocklingSkills.Mining.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        offStack.shrink(1);
                    }

                    blockling.incOresMinedRecently();

                    world.destroyBlock(targetBlockPos, false);
                    world.destroyBlockProgress(blockling.getId(), targetBlockPos, 0);

                    recalc();
                }
                else
                {
                    world.destroyBlockProgress(blockling.getId(), targetBlockPos, BlockUtil.calcBlockBreakProgress(blockling.getActions().gather.count()));
                }
            }
        }
        else
        {
            world.destroyBlockProgress(blockling.getId(), targetBlockPos, -1);
            blockling.getActions().gather.stop();
        }
    }

    @Override
    protected void recalc()
    {
        if (isStuck())
        {
            targetGoal.markTargetBad();
        }

        if (!getTargetGoal().isTargetValid())
        {
            getTargetGoal().recalcTarget();
        }

        if (targetGoal.hasTarget())
        {
            setPathTargetPos(targetGoal.getTargetPos(), null);
        }
    }

    @Override
    public float getRangeSq()
    {
        return blockling.getStats().miningRangeSq.getValue();
    }
}
