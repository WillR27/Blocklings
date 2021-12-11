package com.willr27.blocklings.entity.entities.blockling.goal.goals;

import com.willr27.blocklings.block.BlockUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingTasks;
import com.willr27.blocklings.entity.entities.blockling.goal.goals.target.BlocklingMineTargetGoal;
import com.willr27.blocklings.entity.entities.blockling.goal.goals.target.IHasTargetGoal;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class BlocklingMineGoal extends BlocklingGoal implements IHasTargetGoal
{
    private static final int SEARCH_RADIUS_X = 8;
    private static final int SEARCH_RADIUS_Y = 8;

    private final BlocklingMineTargetGoal targetGoal;

    private Path path = null;
    private int recalc = 0;
    private final int recalcInterval = 20;
    private float prevMoveDist = 0.0f;

    public BlocklingMineGoal(UUID id, BlocklingEntity blockling, BlocklingTasks goals)
    {
        super(id, blockling, goals);

        targetGoal = new BlocklingMineTargetGoal(this);

        GoalWhitelist whitelist = new GoalWhitelist("24d7135e-607b-413b-a2a7-00d19119b9de", "ores", Whitelist.Type.BLOCK, this);
        BlockUtil.ORES.forEach(ore -> whitelist.put(ore.getRegistryName(), true));
        whitelists.add(whitelist);

        setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public BlocklingTargetGoal getTargetGoal()
    {
        return targetGoal;
    }

    @Override
    public boolean canUse()
    {
        if (!super.canUse())
        {
            return false;
        }

        if (!blockling.getEquipment().hasToolEquipped(ToolType.PICKAXE))
        {
            return false;
        }

        if (!targetGoal.hasTarget())
        {
            return false;
        }

        path = blockling.getNavigation().createPath(targetGoal.getTargetPos(), 0);

        if (path == null)
        {
            if (!isInRange(targetGoal.getTargetPos()))
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

        if (!blockling.getEquipment().hasToolEquipped(ToolType.PICKAXE))
        {
            return false;
        }

        if (!targetGoal.hasTarget())
        {
            return false;
        }

        if (path == null)
        {
            if (!isInRange(targetGoal.getTargetPos()))
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

        blockling.getNavigation().moveTo(path, 1.0);
    }

    @Override
    public void stop()
    {
        super.stop();

        if (targetGoal.hasTarget())
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getTargetPos(), 0);
        }

        if (targetGoal.hasPrevTarget())
        {
            world.destroyBlockProgress(blockling.getId(), targetGoal.getPrevTargetPos(), 0);
        }

        blockling.getNavigation().stop();
        blockling.getActions().mining.stop();

        prevMoveDist = 0.0f;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (tickRecalc())
        {
            if ((path == null || path.isDone() || !hasMovedSinceLastRecalc() || blockling.getNavigation().isStuck()) && !isInRange(targetGoal.getTargetPos()))
            {
                targetGoal.markTargetBad();
            }

            tryCalculatePath();

            prevMoveDist = blockling.moveDist;
        }

        if (isInRange(targetGoal.getTargetPos()))
        {
            blockling.getActions().mining.tryStart();

            if (blockling.getActions().mining.isFinished())
            {
                world.destroyBlock(targetGoal.getTargetPos(), false);
                world.destroyBlockProgress(blockling.getId(), targetGoal.getTargetPos(), 0);

                forceRecalc();
            }
            else
            {
                world.destroyBlockProgress(blockling.getId(), targetGoal.getTargetPos(), BlockUtil.calcBlockBreakProgress(blockling.getActions().mining.percentThroughAction()));
            }
        }
        else
        {
            blockling.getActions().mining.stop();
        }
    }

    private void tryCalculatePath()
    {
        path = blockling.getNavigation().createPath(targetGoal.getTargetPos(), 0);
        blockling.getNavigation().moveTo(path, 1.0);
    }

    private boolean hasMovedSinceLastRecalc()
    {
        return blockling.moveDist - prevMoveDist > 0.01f;
    }

    private boolean isInRange(BlockPos blockPos)
    {
        float rangeSq = blockling.getStats().miningRangeSq.getFloat();
        float distanceSq = (float) blockling.distanceToSqr(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);

        return distanceSq < rangeSq;
    }

    private void forceRecalc()
    {
        targetGoal.forceRecalc();
        recalc = recalcInterval;
    }

    private boolean tickRecalc()
    {
        recalc++;

        if (recalc < recalcInterval)
        {
            return false;
        }
        else
        {
            recalc = 0;
        }

        return true;
    }
}
