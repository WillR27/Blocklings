package com.willr27.blocklings.entity.blockling.goal.goals.combat;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Attacks the nearest entity to the blockling using melee.
 */
public class BlocklingMeleeAttackHuntGoal extends BlocklingMeleeAttackGoal
{
    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackHuntGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        whitelists.get(0).setEntry(Registry.ENTITY_TYPE.getKey(EntityType.VILLAGER), false, false);
    }

    @Override
    public void recalcTarget()
    {
        if (!blockling.isTame())
        {
            setTarget(null);

            return;
        }

        if (isTargetValid())
        {
            return;
        }

        for (Entity entity : world.getEntities(blockling, new AxisAlignedBB(blockling.position().add(-10.0, -10.0, -10.0), blockling.position().add(10.0, 10.0, 10.0))))
        {
            if (entity instanceof LivingEntity)
            {
                LivingEntity livingEntity = (LivingEntity) entity;

                if (isValidTarget(livingEntity))
                {
                    setTarget(livingEntity);

                    return;
                }
            }
        }

        setTarget(null);
    }

    @Override
    protected void attack(@Nonnull LivingEntity target, @Nonnull BlocklingHand attackingHand)
    {
        blockling.wasLastAttackHunt = true;

        super.attack(target, attackingHand);
    }
}
