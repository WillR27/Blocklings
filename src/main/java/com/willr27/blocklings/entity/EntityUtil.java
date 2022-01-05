package com.willr27.blocklings.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.TreeMap;

public class EntityUtil
{
    public static final Map<ResourceLocation, Entity> VALID_ATTACK_TARGETS = new TreeMap();

    public static void init(World world)
    {
        for (ResourceLocation entry : Registry.ENTITY_TYPE.keySet())
        {
            Entity entity = Registry.ENTITY_TYPE.get(entry).create(world);
            if (isValidAttackTarget(entity))
            {
                VALID_ATTACK_TARGETS.put(entry, entity);
            }
        }
    }

    public static Entity create(ResourceLocation type, World world)
    {
        return Registry.ENTITY_TYPE.get(type).create(world);
    }

    public static boolean isValidAttackTarget(Entity entity)
    {
        if (!(entity instanceof MobEntity))
        {
           return false;
        }
        else if (entity instanceof FlyingEntity)
        {
            return false;
        }
        else if (entity instanceof WaterMobEntity)
        {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the given entity can see the given block pos.
     */
    public static boolean canSee(LivingEntity entity, BlockPos blockPos)
    {
        Vector3d entityPos = new Vector3d(entity.getX(), entity.getEyeY(), entity.getZ());

        for (double x = 0.05; x < 1.0; x += 0.9)
        {
            for (double y = 0.05; y < 1.0; y += 0.9)
            {
                for (double z = 0.05; z < 1.0; z += 0.9)
                {
                    Vector3d targetPos = new Vector3d(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
                    BlockRayTraceResult result = entity.level.clip(new RayTraceContext(entityPos, targetPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, entity));

                    if (result.getType() != RayTraceResult.Type.MISS)
                    {
                        if (result.getBlockPos().equals(blockPos))
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns true if the given entity is within range of the center of the given block pos.
     */
    public static boolean isInRange(@Nonnull LivingEntity entity, @Nonnull BlockPos blockPos, float rangeSq)
    {
        return (float) entity.distanceToSqr(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f) < rangeSq;
    }
}
