package com.willr27.blocklings.entity.entities.blockling.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.entities.blockling.goal.goals.target.BlocklingHurtByTargetGoal;

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
