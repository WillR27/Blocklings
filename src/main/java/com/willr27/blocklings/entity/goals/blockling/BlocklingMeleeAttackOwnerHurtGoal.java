package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingOwnerHurtTargetGoal;

import javax.annotation.Nonnull;
import java.util.UUID;

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
