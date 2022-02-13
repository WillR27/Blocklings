package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoalOLD;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * Targets the last entity the blockling's owner attacked.
 */
public class BlocklingOwnerHurtTargetGoal extends BlocklingTargetGoalOLD<BlocklingGoal>
{
    /**
     * The entity the blockling's owner last attacked.
     */
    @Nullable
    private LivingEntity ownersTarget = null;

    /**
     * @param goal the associated goal.
     */
    public BlocklingOwnerHurtTargetGoal(BlocklingGoal goal)
    {
        super(goal);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        if (!blockling.isTame())
        {
            return false;
        }

        LivingEntity owner = blockling.getOwner();

        if (owner == null)
        {
            return false;
        }

        ownersTarget = owner.getLastHurtMob();

        if (ownersTarget == null)
        {
            return false;
        }

        if (ownersTarget == blockling)
        {
            return false;
        }

        for (GoalWhitelist whitelist : goal.whitelists)
        {
            if (whitelist.isEntryBlacklisted(ownersTarget))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        return false;
    }

    @Override
    public void start()
    {
        blockling.setTarget(ownersTarget);
    }
}
