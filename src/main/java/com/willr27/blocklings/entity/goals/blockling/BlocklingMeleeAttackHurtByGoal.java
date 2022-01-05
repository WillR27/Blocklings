package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingHurtByTargetGoal;

import javax.annotation.Nonnull;
import java.util.UUID;

public class BlocklingMeleeAttackHurtByGoal extends BlocklingMeleeAttackGoal<BlocklingHurtByTargetGoal>
{
    /**
     * The associated target goal.
     */
    @Nonnull
    private final BlocklingHurtByTargetGoal targetGoal;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackHurtByGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        targetGoal = new BlocklingHurtByTargetGoal(this);
    }

    @Override
    @Nonnull
    public BlocklingHurtByTargetGoal getTargetGoal()
    {
        return targetGoal;
    }
}
