package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Attacks the last entity the blockling's owner attacked using melee.
 */
public class BlocklingMeleeAttackOwnerHurtGoal extends BlocklingMeleeAttackGoal
{
    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackOwnerHurtGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);
    }

    @Override
    public boolean tryRecalcTarget()
    {
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

        setTarget(ownersTarget);

        return super.tryRecalcTarget();
    }
}
