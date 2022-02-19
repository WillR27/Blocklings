package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.goal.goals.target.BlocklingAttackOwnerHurtByTargetGoal;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Attacks the last entity to attack the blockling's owner using melee.
 */
public class BlocklingMeleeAttackOwnerHurtByGoal extends BlocklingMeleeAttackGoal<BlocklingAttackOwnerHurtByTargetGoal>
{
    /**
     * The associated target goal.
     */
    @Nonnull
    private final BlocklingAttackOwnerHurtByTargetGoal targetGoal;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackOwnerHurtByGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingAttackOwnerHurtByTargetGoal(this);
    }

    @Override
    @Nonnull
    public BlocklingAttackOwnerHurtByTargetGoal getTargetGoal()
    {
        return targetGoal;
    }
}
