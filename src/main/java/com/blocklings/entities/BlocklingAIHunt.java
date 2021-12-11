package com.blocklings.entities;

import com.blocklings.util.helpers.EntityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class BlocklingAIHunt extends EntityAIBase
{
    World world;
    private EntityBlockling blockling;
    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
    private int attackTick;
    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
    boolean longMemory;
    /** The PathEntity of our entity. */
    Path path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public BlocklingAIHunt(EntityBlockling blockling)
    {
        this.blockling = blockling;
        this.world = blockling.world;
        this.longMemory = true;
        this.setMutexBits(3);
    }

    private <T extends Entity> T findNearestEntityWithinAABB(Class <? extends T > entityType, AxisAlignedBB aabb, T closestTo)
    {
        List<T> list = blockling.world.<T>getEntitiesWithinAABB(entityType, aabb);
        T t = null;
        double d0 = Double.MAX_VALUE;

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            T t1 = list.get(j2);

            if (t1 != closestTo && EntitySelectors.NOT_SPECTATING.apply(t1))
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)t1;

                if (entitylivingbase instanceof EntityChicken ||
                entitylivingbase instanceof EntityCow ||
                entitylivingbase instanceof EntityGiantZombie ||
                entitylivingbase instanceof EntityPig ||
                entitylivingbase instanceof EntitySheep ||
                entitylivingbase instanceof EntityEndermite ||
                entitylivingbase instanceof EntityEvoker ||
                entitylivingbase instanceof EntityHusk ||
                entitylivingbase instanceof EntityIllusionIllager ||
                entitylivingbase instanceof EntityMagmaCube ||
                entitylivingbase instanceof EntitySlime ||
                entitylivingbase instanceof EntityStray ||
                entitylivingbase instanceof EntityWitch ||
                entitylivingbase instanceof EntityWitherSkeleton ||
                entitylivingbase instanceof EntityZombie ||
                entitylivingbase instanceof EntityCaveSpider ||
                entitylivingbase instanceof EntityEnderman ||
                entitylivingbase instanceof EntitySpider)
                {
                    double d1 = closestTo.getDistanceSq(t1);

                    if (d1 <= d0)
                    {
                        t = t1;
                        d0 = d1;
                    }
                }
            }
        }

        return t;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (blockling.isSitting())
        {
            return false;
        }

        if (blockling.getTask() != EntityHelper.Task.HUNT)
        {
            return false;
        }

        Vec3d blocklingVec = blockling.getPositionVector();
        Vec3d corner1 = blocklingVec.subtract(20, 10, 20);
        Vec3d corner2 = blocklingVec.addVector(20, 10, 20);
        EntityLivingBase entitylivingbase = findNearestEntityWithinAABB(EntityLivingBase.class, new AxisAlignedBB(corner1.x, corner1.y, corner1.z, corner2.x, corner2.y, corner2.z), blockling);
        blockling.setAttackTarget(entitylivingbase);

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else
        {
            if (canPenalize)
            {
                if (--this.delayCounter <= 0)
                {
                    this.path = this.blockling.getNavigator().getPathToEntityLiving(entitylivingbase);
                    this.delayCounter = 4 + this.blockling.getRNG().nextInt(7);
                    return this.path != null;
                }
                else
                {
                    return true;
                }
            }
            this.path = this.blockling.getNavigator().getPathToEntityLiving(entitylivingbase);

            if (this.path != null)
            {
                return true;
            }
            else
            {
                return this.getAttackReachSqr(entitylivingbase) >= this.blockling.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        EntityLivingBase entitylivingbase = this.blockling.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else if (!this.longMemory)
        {
            return !this.blockling.getNavigator().noPath();
        }
        else if (!this.blockling.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)))
        {
            return false;
        }
        else
        {
            return entitylivingbase instanceof EntityChicken ||
                entitylivingbase instanceof EntityCow ||
                entitylivingbase instanceof EntityGiantZombie ||
                entitylivingbase instanceof EntityPig ||
                entitylivingbase instanceof EntitySheep ||
                entitylivingbase instanceof EntityEndermite ||
                entitylivingbase instanceof EntityEvoker ||
                entitylivingbase instanceof EntityHusk ||
                entitylivingbase instanceof EntityIllusionIllager ||
                entitylivingbase instanceof EntityMagmaCube ||
                entitylivingbase instanceof EntitySlime ||
                entitylivingbase instanceof EntityStray ||
                entitylivingbase instanceof EntityWitch ||
                entitylivingbase instanceof EntityWitherSkeleton ||
                entitylivingbase instanceof EntityZombie ||
                entitylivingbase instanceof EntityCaveSpider ||
                entitylivingbase instanceof EntityEnderman ||
                entitylivingbase instanceof EntitySpider;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.blockling.getNavigator().setPath(this.path, blockling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
        this.delayCounter = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        EntityLivingBase entitylivingbase = this.blockling.getAttackTarget();

        if (!(entitylivingbase instanceof EntityChicken ||
            entitylivingbase instanceof EntityCow ||
            entitylivingbase instanceof EntityGiantZombie ||
            entitylivingbase instanceof EntityPig ||
            entitylivingbase instanceof EntitySheep ||
            entitylivingbase instanceof EntityEndermite ||
            entitylivingbase instanceof EntityEvoker ||
            entitylivingbase instanceof EntityHusk ||
            entitylivingbase instanceof EntityIllusionIllager ||
            entitylivingbase instanceof EntityMagmaCube ||
            entitylivingbase instanceof EntitySlime ||
            entitylivingbase instanceof EntityStray ||
            entitylivingbase instanceof EntityWitch ||
            entitylivingbase instanceof EntityWitherSkeleton ||
            entitylivingbase instanceof EntityZombie ||
            entitylivingbase instanceof EntityCaveSpider ||
            entitylivingbase instanceof EntityEnderman ||
            entitylivingbase instanceof EntitySpider))
        {
            this.blockling.setAttackTarget((EntityLivingBase)null);
        }

        this.blockling.getNavigator().clearPath();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        EntityLivingBase entitylivingbase = this.blockling.getAttackTarget();
        this.blockling.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.blockling.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
        --this.delayCounter;

        if ((this.longMemory || this.blockling.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.blockling.getRNG().nextFloat() < 0.05F))
        {
            this.targetX = entitylivingbase.posX;
            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = 4 + this.blockling.getRNG().nextInt(7);

            if (this.canPenalize)
            {
                this.delayCounter += failedPathFindingPenalty;
                if (this.blockling.getNavigator().getPath() != null)
                {
                    net.minecraft.pathfinding.PathPoint finalPathPoint = this.blockling.getNavigator().getPath().getFinalPathPoint();
                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                        failedPathFindingPenalty = 0;
                    else
                        failedPathFindingPenalty += 10;
                }
                else
                {
                    failedPathFindingPenalty += 10;
                }
            }

            if (d0 > 1024.0D)
            {
                this.delayCounter += 10;
            }
            else if (d0 > 256.0D)
            {
                this.delayCounter += 5;
            }

            if (!this.blockling.getNavigator().tryMoveToEntityLiving(entitylivingbase, blockling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()))
            {
                this.delayCounter += 15;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        this.checkAndPerformAttack(entitylivingbase, d0);
    }

    protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_)
    {
        double d0 = this.getAttackReachSqr(p_190102_1_);

        if (p_190102_2_ <= d0 && this.attackTick <= 0)
        {
            this.attackTick = 20;
            this.blockling.swingArm(EnumHand.MAIN_HAND);
            this.blockling.attackEntityAsMob(p_190102_1_);
        }
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return (double)(this.blockling.width * 2.0F * this.blockling.width * 2.0F + attackTarget.width);
    }
}