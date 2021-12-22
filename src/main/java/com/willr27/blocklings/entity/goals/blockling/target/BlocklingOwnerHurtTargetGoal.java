package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.LivingEntity;

public class BlocklingOwnerHurtTargetGoal extends BlocklingTargetGoal
{
    private LivingEntity ownersTarget = null;
    private int timestamp = 0;

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
        timestamp = blockling.getOwner().getLastHurtMobTimestamp();

        blockling.setTarget(ownersTarget);
    }
}
