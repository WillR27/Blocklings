package com.willr27.blocklings.goal.goals.target;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Targets the last entity to attack the blockling.
 */
public class BlocklingHurtByTargetGoal extends BlocklingTargetGoal<BlocklingGoal>
{
    /**
     * The entity attacking the blockling.
     */
    @Nullable
    private LivingEntity attacker = null;

    /**
     * The mob timestamp.
     */
    private int timestamp = 0;

    /**
     * @param goal the associated goal.
     */
    public BlocklingHurtByTargetGoal(@Nonnull BlocklingGoal goal)
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

        attacker = blockling.getLastHurtByMob();

        if (attacker == null)
        {
            return false;
        }

        if (timestamp == blockling.getLastHurtByMobTimestamp())
        {
            return false;
        }

        for (GoalWhitelist whitelist : goal.whitelists)
        {
            if (whitelist.isEntryBlacklisted(attacker))
            {
                return false;
            }
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
        timestamp = blockling.getLastHurtByMobTimestamp();

        blockling.setTarget(attacker);
    }
}
