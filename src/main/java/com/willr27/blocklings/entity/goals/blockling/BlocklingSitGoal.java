package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.goal.BlocklingGoal;
import net.minecraft.entity.ai.goal.Goal;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.UUID;

public class BlocklingSitGoal extends BlocklingGoal
{
    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingSitGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

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
