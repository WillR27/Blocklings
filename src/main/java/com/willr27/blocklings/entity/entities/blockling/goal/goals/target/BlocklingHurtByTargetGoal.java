package com.willr27.blocklings.entity.entities.blockling.goal.goals.target;

import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.LivingEntity;

public class BlocklingHurtByTargetGoal extends BlocklingTargetGoal
{
    private LivingEntity attacker = null;
    private int timestamp = 0;

    public BlocklingHurtByTargetGoal(BlocklingGoal goal)
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

        attacker = blockling.getLastHurtByMob();

        if (attacker == null)
        {
            return false;
        }

        if (timestamp == blockling.getLastHurtByMobTimestamp())
        {
            return false;
        }

        for (GoalWhitelist whitelist : goal.whitelists)
        {
            if (whitelist.isEntryBlacklisted(attacker))
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
        timestamp = blockling.getLastHurtByMobTimestamp();

        blockling.setTarget(attacker);
    }
}
