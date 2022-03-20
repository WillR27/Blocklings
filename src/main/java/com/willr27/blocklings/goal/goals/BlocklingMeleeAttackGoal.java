package com.willr27.blocklings.goal.goals;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.goal.BlocklingTargetGoal;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.skill.skills.CombatSkills;
import com.willr27.blocklings.skill.skills.GeneralSkills;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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
        EntityUtil.VALID_ATTACK_TARGETS.keySet().forEach(type -> whitelist.put(type, true));
        whitelist.put(BlocklingsEntityTypes.BLOCKLING_ENTITY.getId(), false);
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
        if (isStuck())
        {
            markEntireTargetBad();

            return;
        }

        if (blockling.getSkills().getSkill(GeneralSkills.AUTOSWITCH).isBought())
        {
            blockling.getEquipment().trySwitchToBestTool(BlocklingHand.BOTH, ToolType.WEAPON);
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
    protected boolean recalcPath(boolean force)
    {
        if (isBadPathTargetPos(getTarget().blockPosition()))
        {
            setPathTargetPos(null, null);

            return false;
        }

        Path path = blockling.getNavigation().createPath(getTarget(), 0);

        if (path != null && BlockUtil.distanceSq(getTarget().blockPosition(), path.getTarget()) > getRangeSq())
        {
            path = null;
        }

        setPathTargetPos(getTarget().blockPosition(), path);

        return true;
    }

    /**
     * Performs the necessary actions that occur when a blockling attacks its target.
     *
     * @param target the attack target.
     * @param attackingHand the attacking hand.
     */
    protected void attack(@Nonnull LivingEntity target, @Nonnull BlocklingHand attackingHand)
    {
        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        if (mainStack.hurt(attackingHand == BlocklingHand.MAIN ? blockling.getSkills().getSkill(CombatSkills.WRECKLESS).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
        {
            mainStack.shrink(1);
        }

        if (offStack.hurt(attackingHand == BlocklingHand.OFF ? blockling.getSkills().getSkill(CombatSkills.WRECKLESS).isBought() ? 2 : 1 : 0, blockling.getRandom(), null))
        {
            offStack.shrink(1);
        }

        blockling.incAttacksRecently();
        blockling.doHurtTarget(target);

        recalcPath(true);

        blockling.wasLastAttackHunt = false;
    }

    @Override
    protected void checkForAndRemoveInvalidTargets()
    {
        if (hasTarget() && !isTargetValid())
        {
            markTargetBad();
        }
    }

    @Override
    public void markEntireTargetBad()
    {
        if (hasTarget())
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
    public float getRangeSq()
    {
        return 2.5f * 2.5f;
    }

    /**
     * @param target the target entity.
     * @return true if the blockling is in range of the target entity.
     */
    private boolean isInRange(@Nonnull LivingEntity target)
    {
        return blockling.distanceToSqr(target.getX(), target.getY() + target.getBbHeight() / 2.0f, target.getZ()) < getRangeSq();
    }
}
