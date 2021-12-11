package com.blocklings.entities;

import com.blocklings.util.helpers.EntityHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class BlocklingAIWanderAvoidWater extends BlocklingAIWander
{
    private final float probability;
    private EntityBlockling blockling;

    public BlocklingAIWanderAvoidWater(EntityBlockling blockling, float probability)
    {
        super(blockling);
        this.blockling = blockling;
        this.probability = probability;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getState() != EntityHelper.State.WANDER)
        {
            return false;
        }

        return super.shouldExecute();
    }

    @Nullable
    protected Vec3d getPosition()
    {
        if (this.entity.isInWater())
        {
            Vec3d vec3d = RandomPositionGenerator.getLandPos(this.entity, 15, 7);
            return vec3d == null ? super.getPosition() : vec3d;
        }
        else
        {
            return this.entity.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.entity, 10, 7) : super.getPosition();
        }
    }
}