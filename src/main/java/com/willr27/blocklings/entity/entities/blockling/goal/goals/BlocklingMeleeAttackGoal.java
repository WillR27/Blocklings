package com.willr27.blocklings.entity.entities.blockling.goal.goals;

import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingTasks;
import com.willr27.blocklings.entity.entities.blockling.goal.goals.target.IHasTargetGoal;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;

import java.util.EnumSet;
import java.util.UUID;

public abstract class BlocklingMeleeAttackGoal extends BlocklingGoal implements IHasTargetGoal
{
    private Path path = null;
    private int recalcPath = 0;

    public BlocklingMeleeAttackGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

        GoalWhitelist whitelist = new GoalWhitelist("540241cd-085a-4c1f-9e90-8aea973568a8", "targets", Whitelist.Type.ENTITY, this);
        EntityUtil.VALID_ATTACK_TARGETS.keySet().forEach(type -> whitelist.put(type, true));
        whitelists.add(whitelist);
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        LivingEntity target = blockling.getTarget();

        if (target == null || !target.isAlive())
        {
            return false;
        }

        path = blockling.getNavigation().createPath(target, 0);

        if (path == null)
        {
            if (!isInRange(target))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        LivingEntity target = blockling.getTarget();

        if (target == null || !target.isAlive())
        {
            return false;
        }

        if (path == null)
        {
            if (!isInRange(target))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        blockling.setAggressive(true);
        blockling.getNavigation().moveTo(path, 1.0);
    }

    @Override
    public void stop()
    {
        super.stop();

        blockling.setTarget(null);
        blockling.setAggressive(false);
        blockling.getNavigation().stop();
    }

    @Override
    public void tick()
    {
        super.tick();

        LivingEntity target = blockling.getTarget();

        if (isInRange(target))
        {
            BlocklingHand attackingHand = blockling.getEquipment().findAttackingHand();
            attackingHand = attackingHand == BlocklingHand.BOTH ? blockling.getActions().attack.getRecentHand() == BlocklingHand.OFF ? BlocklingHand.MAIN : BlocklingHand.OFF : attackingHand;

            if (blockling.getActions().attack.tryStart(attackingHand))
            {
                blockling.doHurtTarget(target);
            }
        }
        else if (recalcPath >= 20)
        {
            path = blockling.getNavigation().createPath(target, 0);
            blockling.getNavigation().moveTo(path, 1.0);
            recalcPath = 0;
        }

        recalcPath++;
    }

    private boolean isInRange(LivingEntity target)
    {
        return blockling.distanceToSqr(target.getX(), target.getY() + target.getBbHeight() / 2.0f, target.getZ()) < 4.0f;
    }
}
