package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.goal.goals.target.BlocklingHuntTargetGoal;
import com.willr27.blocklings.task.BlocklingTasks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Attacks the nearest entity to the blockling using melee.
 */
public class BlocklingMeleeAttackHuntGoal extends BlocklingMeleeAttackGoal<BlocklingHuntTargetGoal>
{
    /**
     * The associated target goal.
     */
    @Nonnull
    private final BlocklingHuntTargetGoal targetGoal;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackHuntGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingHuntTargetGoal(this);

        whitelists.get(0).setEntry(Registry.ENTITY_TYPE.getKey(EntityType.VILLAGER), false, false);
    }

    @Override
    @Nonnull
    public BlocklingHuntTargetGoal getTargetGoal()
    {
        return targetGoal;
    }

    @Override
    protected void attack(@Nonnull LivingEntity target, @Nonnull BlocklingHand attackingHand)
    {
        super.attack(target, attackingHand);

        blockling.wasLastAttackHunt = true;
    }
}
