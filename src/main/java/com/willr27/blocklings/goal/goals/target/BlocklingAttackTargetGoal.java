package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Targets an entity to attack.
 */
public class BlocklingAttackTargetGoal extends BlocklingTargetGoal<BlocklingGoal>
{
    /**
     * The entity to target.
     */
    @Nullable
    protected LivingEntity target = null;

    /**
     * @param goal the associated goal.
     */
    public BlocklingAttackTargetGoal(@Nonnull BlocklingGoal goal)
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

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (blockling.getTarget() != target)
        {
            return false;
        }

        if (!isTargetValid())
        {
            return false;
        }

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        blockling.setTarget(target);
    }

    @Override
    public void stop()
    {
        super.stop();

        target = null;

        blockling.setTarget(target);
    }

    @Override
    public boolean isTargetValid()
    {
        return hasTarget() && isValidTarget(target);
    }

    @Override
    public boolean hasTarget()
    {
        return target != null;
    }

    /**
     * @return the current target entity.
     */
    @Nullable
    public LivingEntity getTarget()
    {
        return target;
    }

    /**
     * @return true if the entity is valid to target.
     */
    public boolean isValidTarget(@Nonnull LivingEntity entity)
    {
        if (entity == blockling)
        {
            return false;
        }

        if (entity.isDeadOrDying())
        {
            return false;
        }

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
