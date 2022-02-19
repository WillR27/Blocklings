package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

/**
 * Targets the last entity to attack the blockling.
 */
public class BlocklingAtackHurtByTargetGoal extends BlocklingAttackTargetGoal
{
    /**
     * The mob timestamp.
     */
    private int timestamp = 0;

    /**
     * @param goal the associated goal.
     */
    public BlocklingAtackHurtByTargetGoal(@Nonnull BlocklingGoal goal)
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

        LivingEntity attacker = blockling.getLastHurtByMob();

        if (attacker == null)
        {
            return false;
        }

        if (timestamp == blockling.getLastHurtByMobTimestamp())
        {
            return false;
        }

        if (!isValidTarget(attacker))
        {
            return false;
        }

        target = attacker;

        return true;
    }
}
