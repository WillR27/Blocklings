package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.goal.goals.target.BlocklingAttackOwnerHurtTargetGoal;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Attacks the last entity the blockling's owner attacked using melee.
 */
public class BlocklingMeleeAttackOwnerHurtGoal extends BlocklingMeleeAttackGoal<BlocklingAttackOwnerHurtTargetGoal>
{
    /**
     * The associated target goal.
     */
    @Nonnull
    private final BlocklingAttackOwnerHurtTargetGoal targetGoal;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackOwnerHurtGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingAttackOwnerHurtTargetGoal(this);
    }

    @Override
    @Nonnull
    public BlocklingAttackOwnerHurtTargetGoal getTargetGoal()
    {
        return targetGoal;
    }
}
