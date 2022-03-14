package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.task.BlocklingTasks;
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
    public boolean tryRecalcTarget()
    {
        if (!blockling.isTame())
        {
            return false;
        }

        for (Entity entity : world.getEntities(blockling, new AxisAlignedBB(blockling.position().add(-10.0, -10.0, -10.0), blockling.position().add(10.0, 10.0, 10.0))))
        {
            if (entity instanceof LivingEntity)
            {
                LivingEntity livingEntity = (LivingEntity) entity;

                if (isValidTarget(livingEntity))
                {
                    setTarget(livingEntity);

                    if (super.tryRecalcTarget())
                    {
                        return true;
                    }
                    else
                    {
                        setTarget(null);
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void attack(@Nonnull LivingEntity target, @Nonnull BlocklingHand attackingHand)
    {
        super.attack(target, attackingHand);

        blockling.wasLastAttackHunt = true;
    }
}
