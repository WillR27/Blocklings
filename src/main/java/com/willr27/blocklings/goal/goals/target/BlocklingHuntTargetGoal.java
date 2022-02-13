package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoalOLD;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Targets the nearest entity to attack.
 */
public class BlocklingHuntTargetGoal extends BlocklingTargetGoalOLD<BlocklingGoal>
{
    /**
     * The entity the blockling is hunting.
     */
    @Nullable
    private LivingEntity target = null;

    /**
     * @param goal the associated goal.
     */
    public BlocklingHuntTargetGoal(BlocklingGoal goal)
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

        for (Entity entity : world.getEntities(blockling, new AxisAlignedBB(blockling.position().add(-10.0, -10.0, -10.0), blockling.position().add(10.0, 10.0, 10.0))))
        {
            if (entity instanceof LivingEntity)
            {
                if (isValidTarget(entity))
                {
                    target = (LivingEntity) entity;

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse()
    {
        return false;
    }

    @Override
    public void start()
    {
        blockling.setTarget(target);
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
