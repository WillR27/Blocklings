package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import net.minecraft.entity.LivingEntity;

/**
 * Targets the last entity to attack the blockling's owner.
 */
public class BlocklingAttackOwnerHurtByTargetGoal extends BlocklingAttackTargetGoal
{
    /**
     * The mob timestamp.
     */
    private int timestamp = 0;

    /**
     * @param goal the associated goal.
     */
    public BlocklingAttackOwnerHurtByTargetGoal(BlocklingGoal goal)
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

        LivingEntity ownersAttacker = owner.getLastHurtByMob();

        if (ownersAttacker == null)
        {
            return false;
        }

        if (timestamp == owner.getLastHurtByMobTimestamp())
        {
            return false;
        }

        if (!isValidTarget(ownersAttacker))
        {
            return false;
        }

        target = ownersAttacker;

        return true;
    }
}
