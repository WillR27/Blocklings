package com.willr27.blocklings.entity.blockling.goal.goals.combat;

import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import com.willr27.blocklings.entity.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.blockling.skill.skills.CombatSkills;
import com.willr27.blocklings.entity.blockling.skill.skills.GeneralSkills;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.GoalWhitelist;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.Whitelist;
import com.willr27.blocklings.util.BlockUtil;
import com.willr27.blocklings.util.EntityUtil;
import com.willr27.blocklings.util.ToolContext;
import com.willr27.blocklings.util.ToolType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Contains common behaviour shared between melee attack goals.
 */
public abstract class BlocklingMeleeAttackGoal extends BlocklingTargetGoal<LivingEntity>
{
    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingMeleeAttackGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        GoalWhitelist whitelist = new GoalWhitelist("540241cd-085a-4c1f-9e90-8aea973568a8", "targets", Whitelist.Type.ENTITY, this);
        whitelist.setIsUnlocked(blockling.getSkills().getSkill(CombatSkills.WHITELIST).isBought(), false);
        EntityUtil.VALID_ATTACK_TARGETS.get().keySet().forEach(type -> whitelist.put(type, true));
        whitelist.put(BlocklingsEntityTypes.BLOCKLING.getId(), false);
        whitelists.add(whitelist);
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
        if (!super.canContinueToUse())
        {
            return false;
        }

        return true;
    }

    @Override
    public void start()
    {
        super.start();

        blockling.setAggressive(true);
        blockling.setTarget(getTarget());
    }

    @Override
    public void stop()
    {
        super.stop();

        blockling.setAggressive(false);
        blockling.setTarget(null);
    }

    @Override
    public void tickGoal()
    {
        if (isStuck(false))
        {
            markEntireTargetBad();

            return;
        }

        if (blockling.getSkills().getSkill(GeneralSkills.AUTOSWITCH).isBought())
        {
            blockling.getEquipment().trySwitchToBestTool(BlocklingHand.BOTH, new ToolContext(ToolType.WEAPON, getTarget()));
        }

        LivingEntity target = getTarget();

        if (isInRange(target))
        {
            BlocklingHand attackingHand = blockling.getEquipment().findAttackingHand();
            attackingHand = attackingHand == BlocklingHand.BOTH ? blockling.getActions().attack.getRecentHand() == BlocklingHand.OFF ? BlocklingHand.MAIN : BlocklingHand.OFF : attackingHand;

            if (blockling.getActions().attack.tryStart(attackingHand))
            {
                attack(target, attackingHand);
            }
        }
    }

    @Override
    protected void recalcPathTargetPosAndPath(boolean force)
    {
        if (isBadPathTargetPos(getTarget().blockPosition()))
        {
            trySetPathTarget(null, null);

            return;
        }

        Path path = blockling.getNavigation().createPath(getTarget(), 0);

        if (path != null && BlockUtil.distanceSq(getTarget().blockPosition(), path.getTarget()) > getPathTargetRangeSq())
        {
            path = null;
        }

        trySetPathTarget(getTarget().blockPosition(), path);
    }

    /**
     * Performs the necessary actions that occur when a blockling attacks its target.
     *
     * @param target the attack target.
     * @param attackingHand the attacking hand.
     */
    protected void attack(@Nonnull LivingEntity target, @Nonnull BlocklingHand attackingHand)
    {
        blockling.doHurtTarget(target);

        recalcPathTargetPosAndPath(true);

        blockling.wasLastAttackHunt = false;
    }

    @Override
    protected void checkForAndHandleInvalidTargets()
    {
        if (getTarget() != null && !isTargetValid())
        {
            markTargetBad();
        }
    }

    @Override
    public void markEntireTargetBad()
    {
        if (getTarget() != null)
        {
            markTargetBad();
        }
    }

    @Override
    protected boolean isValidPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return true;
    }

    @Override
    public boolean isValidTarget(@Nullable LivingEntity entity)
    {
        if (entity == null)
        {
            return false;
        }

        if (entity == blockling)
        {
            return false;
        }

        if (entity == blockling.getOwner())
        {
            return false;
        }

        if (entity.isDeadOrDying())
        {
            return false;
        }

        if (badTargets.contains(entity))
        {
            return false;
        }

        for (GoalWhitelist whitelist : whitelists)
        {
            if (whitelist.isEntryBlacklisted(entity))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void setTarget(@Nullable LivingEntity target)
    {
        super.setTarget(target);

        blockling.setTarget(target, false);
    }

    /**
     * @return the attack range squared.
     */
    @Override
    public float getPathTargetRangeSq()
    {
        return 2.5f * 2.5f;
    }

    /**
     * @param target the target entity.
     * @return true if the blockling is in range of the target entity.
     */
    private boolean isInRange(@Nonnull LivingEntity target)
    {
        return blockling.distanceToSqr(target.getX(), target.getY() + target.getBbHeight() / 2.0f, target.getZ()) < getPathTargetRangeSq();
    }
}
