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
public class BlocklingOwnerHurtTargetGoal extends BlocklingTargetGoal<BlocklingGoal>
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

    @Override
    protected boolean isTargetValid()
    {
        return hasTarget() && isValidTarget(ownersTarget);
    }

    @Override
    protected boolean hasTarget()
    {
        return ownersTarget != null;
    }

    /**
     * @return true if the entity is valid to target.
     */
    private boolean isValidTarget(@Nonnull Entity entity)
    {
        for (GoalWhitelist whitelist : goal.whitelists)
        {
            if (whitelist.isEntryBlacklisted(entity))
            {
                return false;
            }
        }

        return true;
    }
}
