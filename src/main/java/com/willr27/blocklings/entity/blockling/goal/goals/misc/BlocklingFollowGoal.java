package com.willr27.blocklings.entity.blockling.goal.goals.misc;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.config.range.IntRangeProperty;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Follows the blockling's owner when out of range.
 */
public class BlocklingFollowGoal extends BlocklingGoal
{
    /**
     * The speed modifier.
     */
    private final double speedModifier = 1.0;

    /**
     * The distance to stop following at.
     */
    @Nonnull
    private final IntRangeProperty stopDistance;

    /**
     * The distance to start following at.
     */
    @Nonnull
    private final IntRangeProperty startDistance;

    /**
     * The navigator used for pathing.
     */
    @Nonnull
    private final PathNavigator navigation;

    /**
     * The owner of the blockling.
     */
    private LivingEntity owner;

    /**
     * The counter used to work out when to recalc the path.
     */
    private int timeToRecalcPath;

    /**
     * The malus from water.
     */
    private float oldWaterCost;

    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingFollowGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        this.navigation = blockling.getNavigation();

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

        properties.add(startDistance = new IntRangeProperty(
                "590fb919-6ac7-4af7-98ec-6e01919782c1", this,
                new BlocklingsTranslationTextComponent("task.property.follow_start_range.name"),
                new BlocklingsTranslationTextComponent("task.property.follow_start_range.desc"),
                1, 20, 4));
        properties.add(stopDistance = new IntRangeProperty(
                "99d39a22-3abe-4109-b493-dcb922f0c08a", this,
                new BlocklingsTranslationTextComponent("task.property.follow_stop_range.name"),
                new BlocklingsTranslationTextComponent("task.property.follow_stop_range.desc"),
                1, 20, 2));
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        LivingEntity owner = blockling.getOwner();

        if (owner == null)
        {
            return false;
        }
        else if (owner.isSpectator())
        {
            return false;
        }
        else if (blockling.distanceToSqr(owner) < (double) (startDistance.getValue() * startDistance.getValue()))
        {
            return false;
        }
        else
        {
            this.owner = owner;

            return true;
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!super.canContinueToUse())
        {
            return false;
        }

        if (navigation.isDone())
        {
            return false;
        }
        else
        {
            return !(blockling.distanceToSqr(owner) <= (double) (stopDistance.getValue() * stopDistance.getValue()));
        }
    }

    @Override
    public void start()
    {
        super.start();

        timeToRecalcPath = 0;
        oldWaterCost = blockling.getPathfindingMalus(PathNodeType.WATER);
        blockling.setPathfindingMalus(PathNodeType.WATER, 0.0f);
    }

    @Override
    public void stop()
    {
        super.stop();

        owner = null;
        navigation.stop();
        blockling.setPathfindingMalus(PathNodeType.WATER, oldWaterCost);
    }

    @Override
    public void tick()
    {
        super.tick();

        blockling.getLookControl().setLookAt(owner, 10.0f, (float) blockling.getMaxHeadXRot());

        if (--timeToRecalcPath <= 0)
        {
            timeToRecalcPath = 10;

            if (!blockling.isLeashed() && !blockling.isPassenger())
            {
                navigation.stop();
                navigation.moveTo(owner, speedModifier);
            }
        }
    }
}
