package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.goals.blockling.target.BlocklingHurtByTargetGoal;

import java.util.UUID;

public class BlocklingMeleeAttackHurtByGoal extends BlocklingMeleeAttackGoal
{
    private final BlocklingHurtByTargetGoal targetGoal;

    public BlocklingMeleeAttackHurtByGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        targetGoal = new BlocklingHurtByTargetGoal(this);
    }

    @Override
    public BlocklingTargetGoal getTargetGoal()
    {
        return targetGoal;
    }
}
