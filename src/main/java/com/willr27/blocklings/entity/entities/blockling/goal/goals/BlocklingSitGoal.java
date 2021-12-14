package com.willr27.blocklings.entity.entities.blockling.goal.goals;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.UUID;

public class BlocklingSitGoal extends BlocklingGoal
{
    public BlocklingSitGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        return false;
    }

    @Override
    public void start()
    {
        blockling.getNavigation().stop();
        blockling.setInSittingPose(true);
        blockling.setOrderedToSit(true);
    }

    @Override
    public void stop()
    {
        blockling.setInSittingPose(false);
        blockling.setOrderedToSit(false);
    }
}
