package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.goal.goals.target.BlocklingOwnerHurtTargetGoal;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Attacks the last entity the blockling's owner attacked using melee.
 */
public class BlocklingMeleeAttackOwnerHurtGoal extends BlocklingMeleeAttackGoal<BlocklingOwnerHurtTargetGoal>
{
    /**
     * The associated target goal.
     */
    @Nonnull
    private final BlocklingOwnerHurtTargetGoal targetGoal;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackOwnerHurtGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingOwnerHurtTargetGoal(this);
    }

    @Override
    @Nonnull
    public BlocklingOwnerHurtTargetGoal getTargetGoal()
    {
        return targetGoal;
    }
}
