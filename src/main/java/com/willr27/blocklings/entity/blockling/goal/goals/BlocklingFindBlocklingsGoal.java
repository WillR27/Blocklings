package com.willr27.blocklings.entity.blockling.goal.goals;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Finds nearby blocklings of a certain type and leads the owner to them.
 */
public class BlocklingFindBlocklingsGoal extends BlocklingTargetGoal<BlocklingEntity>
{
    /**
     * @param id the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingFindBlocklingsGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        LivingEntity owner = blockling.getOwner();

        if (owner == null)
        {
            return false;
        }
        else if (owner.distanceToSqr(blockling) > 16 * 16)
        {
            return false;
        }

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
    }

    @Override
    public void stop()
    {
        super.stop();
    }

    @Override
    public boolean tryRecalcTarget()
    {
        int chunkX = blockling.blockPosition().getX() >> 4;
        int chunkZ = blockling.blockPosition().getZ() >> 4;
        int worldHeight = world.getHeight();
        AxisAlignedBB baseBB = new AxisAlignedBB(0, 0, 0, 16, worldHeight, 16);

        final int chunkRange = 6;

        BlocklingEntity closestBlockling = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (int i = chunkX - chunkRange; i < chunkX + chunkRange; i++)
        {
            for (int j = chunkZ - chunkRange; j < chunkZ + chunkRange; j++)
            {
                Chunk chunk = world.getChunk(i, j);
                List<BlocklingEntity> blocklingsInChunk = new ArrayList<>();
                chunk.getEntitiesOfClass(BlocklingEntity.class, baseBB.move(i * 16, 0, j * 16), blocklingsInChunk, this::isValidTarget);

                for (BlocklingEntity chunkBlockling : blocklingsInChunk)
                {
                    double distanceSq = blockling.distanceToSqr(chunkBlockling);

                    if (distanceSq < closestDistanceSq)
                    {
                        closestBlockling = chunkBlockling;
                        closestDistanceSq = distanceSq;
                    }
                }
            }
        }

        setTarget(closestBlockling);

        return closestBlockling != null;
    }

    @Override
    protected void checkForAndRemoveInvalidTargets()
    {
        if (hasTarget() && getTarget().isDeadOrDying())
        {
            markTargetBad();
        }
    }

    @Override
    public void markEntireTargetBad()
    {
        markTargetBad();
    }

    @Override
    public boolean isValidTarget(@Nullable BlocklingEntity target)
    {
        if (target != null)
        {
            if (target.getNaturalBlocklingType() != blockling.getBlocklingType())
            {
                return false;
            }
            else if (target.getOwner() != null || target.getOwner() == blockling.getOwner())
            {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void tickGoal()
    {

    }

    @Override
    protected boolean recalcPath(boolean force)
    {
        setPathTargetPos(getTarget().blockPosition(), null);

        return true;
    }

    @Override
    protected boolean isValidPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return true;
    }

    @Override
    public int getRecalcInterval()
    {
        return 100;
    }

    @Override
    public int getPathRecalcInterval()
    {
        return 40;
    }

    @Override
    public float getRangeSq()
    {
        return 10;
    }
}
