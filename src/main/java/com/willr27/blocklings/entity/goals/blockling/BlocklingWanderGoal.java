package com.willr27.blocklings.entity.goals.blockling;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;

import java.util.EnumSet;
import java.util.UUID;

public class BlocklingWanderGoal extends BlocklingGoal
{
    private WaterAvoidingRandomWalkingGoal vanillaGoal;

    public BlocklingWanderGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        this.vanillaGoal = new WaterAvoidingRandomWalkingGoal(blockling, 1.0);

        setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        return vanillaGoal.canUse();
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        return vanillaGoal.canContinueToUse();
    }

    @Override
    public void start()
    {
        super.start();

        vanillaGoal.start();
    }

    @Override
    public void stop()
    {
        super.stop();

        vanillaGoal.stop();
    }

    @Override
    public void tick()
    {
        super.tick();

        vanillaGoal.tick();
    }
}
