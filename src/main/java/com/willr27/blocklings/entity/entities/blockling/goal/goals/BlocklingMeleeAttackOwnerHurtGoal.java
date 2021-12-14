package com.willr27.blocklings.entity.entities.blockling.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.entities.blockling.goal.goals.target.BlocklingOwnerHurtTargetGoal;

import java.util.UUID;

public class BlocklingMeleeAttackOwnerHurtGoal extends BlocklingMeleeAttackGoal
{
    private final BlocklingOwnerHurtTargetGoal targetGoal;

    public BlocklingMeleeAttackOwnerHurtGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        targetGoal = new BlocklingOwnerHurtTargetGoal(this);
    }

    @Override
    public BlocklingTargetGoal getTargetGoal()
    {
        return targetGoal;
    }
}
