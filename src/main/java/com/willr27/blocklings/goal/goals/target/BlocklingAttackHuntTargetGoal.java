package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Targets the nearest entity to attack.
 */
public class BlocklingAttackHuntTargetGoal extends BlocklingAttackTargetGoal
{
    /**
     * @param goal the associated goal.
     */
    public BlocklingAttackHuntTargetGoal(BlocklingGoal goal)
    {
        super(goal);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        if (!blockling.isTame())
        {
            return false;
        }

        for (Entity entity : world.getEntities(blockling, new AxisAlignedBB(blockling.position().add(-10.0, -10.0, -10.0), blockling.position().add(10.0, 10.0, 10.0))))
        {
            if (entity instanceof LivingEntity)
            {
                LivingEntity livingEntity = (LivingEntity) entity;

                if (isValidTarget(livingEntity))
                {
                    target = livingEntity;

                    return true;
                }
            }
        }

        return false;
    }
}
