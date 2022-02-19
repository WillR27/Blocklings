package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Targets the last entity the blockling's owner attacked.
 */
public class BlocklingAttackOwnerHurtTargetGoal extends BlocklingAttackTargetGoal
{
    /**
     * @param goal the associated goal.
     */
    public BlocklingAttackOwnerHurtTargetGoal(BlocklingGoal goal)
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

        LivingEntity ownersTarget = owner.getLastHurtMob();

        if (ownersTarget == null)
        {
            return false;
        }

        if (!isValidTarget(ownersTarget))
        {
            return false;
        }

        target = ownersTarget;

        return true;
    }
}
