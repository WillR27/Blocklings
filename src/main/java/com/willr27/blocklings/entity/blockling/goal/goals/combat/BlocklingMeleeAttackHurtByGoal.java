package com.willr27.blocklings.entity.blockling.goal.goals.combat;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Attacks the last entity to attack the blockling using melee.
 */
public class BlocklingMeleeAttackHurtByGoal extends BlocklingMeleeAttackGoal
{
    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackHurtByGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
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

        LivingEntity attacker = blockling.getLastHurtByMob();

        if (attacker == null)
        {
            setTarget(null);

            return;
        }

        if (!isValidTarget(attacker))
        {
            setTarget(null);

            return;
        }

        setTarget(attacker);
    }
}
