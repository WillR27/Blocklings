package com.willr27.blocklings.entity.blockling.goal.goals.combat;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
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
    public void recalcTarget()
    {
        if (!blockling.isTame())
        {
            setTarget(null);

            return;
        }

        LivingEntity owner = blockling.getOwner();

        if (owner == null)
        {
            setTarget(null);

            return;
        }

        LivingEntity ownersTarget = owner.getLastHurtMob();

        if (ownersTarget == null)
        {
            setTarget(null);

            return;
        }

        if (!isValidTarget(ownersTarget))
        {
            setTarget(null);

            return;
        }

        setTarget(ownersTarget);
    }
}
