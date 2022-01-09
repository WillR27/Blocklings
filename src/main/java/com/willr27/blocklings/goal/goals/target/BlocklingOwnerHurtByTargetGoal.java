package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * Targets the last entity to attack the blockling's owner.
 */
public class BlocklingOwnerHurtByTargetGoal extends BlocklingTargetGoal<BlocklingGoal>
{
    /**
     * The entity attacking the blockling's owner.
     */
    @Nullable
    private LivingEntity ownersAttacker = null;

    /**
     * The mob timestamp.
     */
    private int timestamp = 0;

    /**
     * @param goal the associated goal.
     */
    public BlocklingOwnerHurtByTargetGoal(BlocklingGoal goal)
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

        ownersAttacker = owner.getLastHurtByMob();

        if (ownersAttacker == null)
        {
            return false;
        }

        if (timestamp == owner.getLastHurtByMobTimestamp())
        {
            return false;
        }

        for (GoalWhitelist whitelist : goal.whitelists)
        {
            if (whitelist.isEntryBlacklisted(ownersAttacker))
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
        timestamp = blockling.getOwner().getLastHurtByMobTimestamp();

        blockling.setTarget(ownersAttacker);
    }
}
