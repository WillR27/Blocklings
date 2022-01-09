package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.goal.goals.target.BlocklingFarmTargetGoal;
import com.willr27.blocklings.goal.IHasTargetGoal;
import com.willr27.blocklings.item.DropUtil;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.skills.BlocklingSkills;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Harvests/replants the targeted crop.
 */
public class BlocklingFarmGoal extends BlocklingGatherGoal<BlocklingFarmTargetGoal> implements IHasTargetGoal<BlocklingFarmTargetGoal>
{
    /**
     * The crop whitelist.
     */
    public final GoalWhitelist cropWhitelist;

    /**
     * The seed whitelist.
     */
    public final GoalWhitelist seedWhitelist;

    /**
     * The associated target goal.
     */
    private final BlocklingFarmTargetGoal targetGoal;


    /**
     * @param id the id associated with the owning task of this goal.
     * @param blockling the blockling the goal is assigned to.
     * @param tasks the associated tasks.
     */
    public BlocklingFarmGoal(UUID id, BlocklingEntity blockling, BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingFarmTargetGoal(this);

        cropWhitelist = new GoalWhitelist("25140edf-f60e-459e-b1f0-9ff82108ec0b", "crops", Whitelist.Type.BLOCK, this);
        cropWhitelist.setIsUnlocked(blockling.getSkills().getSkill(BlocklingSkills.Farming.CROP_WHITELIST).isBought(), false);
        BlockUtil.CROPS.forEach(crop -> cropWhitelist.put(crop.getRegistryName(), true));
        whitelists.add(cropWhitelist);

        seedWhitelist = new GoalWhitelist("d77bf1c1-7718-4733-b763-298b03340eea", "seeds", Whitelist.Type.ITEM, this);
        seedWhitelist.setIsUnlocked(blockling.getSkills().getSkill(BlocklingSkills.Farming.SEED_WHITELIST).isBought(), false);
        BlockUtil.CROPS.forEach(crop ->
        {
            if (crop instanceof CropsBlock)
            {
                seedWhitelist.put(crop.getCloneItemStack(world, null, crop.defaultBlockState()).getItem().getRegistryName(), true);
            }
        });
        whitelists.add(seedWhitelist);

        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    @Nonnull
    public BlocklingFarmTargetGoal getTargetGoal()
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
            targetGoal.markTargetBad();

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
        Block targetBlock = targetBlockState.getBlock();

        boolean mainCanHarvest = ToolUtil.isHoe(mainStack);
        boolean offCanHarvest = ToolUtil.isHoe(offStack);

        if (mainCanHarvest || offCanHarvest)
        {
            blockling.getActions().gather.tryStart();

            if (blockling.getActions().gather.isRunning())
            {
                float blocklingDestroySpeed = blockling.getStats().farmingSpeed.getValue();
                float mainDestroySpeed = mainCanHarvest ? ToolUtil.getToolFarmingSpeedWithEnchantments(mainStack) : 0.0f;
                float offDestroySpeed = offCanHarvest ? ToolUtil.getToolFarmingSpeedWithEnchantments(offStack) : 0.0f;

                float destroySpeed = blocklingDestroySpeed + mainDestroySpeed + offDestroySpeed;
                float blockStrength = targetBlockState.getDestroySpeed(world, targetGoal.getTargetPos());

                blockling.getStats().hand.setValue(BlocklingHand.fromBooleans(mainCanHarvest, offCanHarvest));

                float progress = destroySpeed / (blockStrength + 2.5f) / 100.0f;
                blockling.getActions().gather.tick(progress);

                if (blockling.getActions().gather.isFinished())
                {
                    blockling.getActions().gather.stop();
                    blockling.getStats().farmingXp.incrementValue((int) ((blockStrength + 1.0f) * 3.0f));

                    for (ItemStack stack : DropUtil.getDrops(blockling, targetBlockPos, mainCanHarvest ? mainStack : ItemStack.EMPTY, offCanHarvest ? offStack : ItemStack.EMPTY))
                    {
                        stack = blockling.getEquipment().addItem(stack);
                        blockling.dropItemStack(stack);
                    }

                    if (mainStack.hurt(mainCanHarvest ? blockling.getSkills().getSkill(BlocklingSkills.Farming.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        mainStack.shrink(1);
                    }

                    if (offStack.hurt(offCanHarvest ? blockling.getSkills().getSkill(BlocklingSkills.Farming.HASTY).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
                    {
                        offStack.shrink(1);
                    }

                    blockling.incCropsHarvestedRecently();

                    ItemStack seedStack = ItemStack.EMPTY;

                    if (blockling.getSkills().getSkill(BlocklingSkills.Farming.REPLANTER).isBought() && targetBlock instanceof CropsBlock)
                    {
                        CropsBlock cropsBlock = (CropsBlock) targetBlock;
                        seedStack = cropsBlock.getCloneItemStack(world, targetBlockPos, targetBlockState);
                    }

                    world.destroyBlock(targetBlockPos, false);
                    world.destroyBlockProgress(blockling.getId(), targetBlockPos, 0);

                    if (!seedStack.isEmpty() && blockling.getEquipment().take(seedStack) && seedWhitelist.isEntryWhitelisted(seedStack.getItem()))
                    {
                        world.setBlock(targetBlockPos, Block.byItem(seedStack.getItem()).defaultBlockState(), 3);
                    }

                    recalc();
                }
                else if (targetBlockState.getMaterial().isSolid())
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
    public boolean canHarvestTargetPos()
    {
        return blockling.getEquipment().hasToolEquipped(ToolType.HOE);
    }

    @Override
    public float getRangeSq()
    {
        return blockling.getStats().farmingRangeSq.getValue();
    }
}
