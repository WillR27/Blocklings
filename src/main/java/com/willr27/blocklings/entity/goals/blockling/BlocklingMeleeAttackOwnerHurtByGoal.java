package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.entity.goals.blockling.target.BlocklingOwnerHurtByTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;

import java.util.UUID;

public class BlocklingMeleeAttackOwnerHurtByGoal extends BlocklingMeleeAttackGoal
{
    private final BlocklingOwnerHurtByTargetGoal targetGoal;

    public BlocklingMeleeAttackOwnerHurtByGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        targetGoal = new BlocklingOwnerHurtByTargetGoal(this);
    }

    @Override
    public BlocklingTargetGoal getTargetGoal()
    {
        return targetGoal;
    }
}
