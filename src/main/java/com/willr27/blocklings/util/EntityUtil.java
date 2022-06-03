package com.willr27.blocklings.util;

import com.willr27.blocklings.util.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Contains utility methods pertinent to entities.
 */
public class EntityUtil
{
    /**
     * A map of entities that should be deemed attackable by a blockling in game.
     */
    @Nonnull
    public static final Map<ResourceLocation, Entity> VALID_ATTACK_TARGETS = new TreeMap<>();

    /**
     * Initialises the valid attack targets when the world loads.
     *
     * @param world the loading world.
     */
    public static void init(@Nonnull World world)
    {
        // TODO: https://forums.minecraftforge.net/topic/105550-1165-forge-config/

        for (ResourceLocation entry : Registry.ENTITY_TYPE.keySet())
        {
            Entity entity = Registry.ENTITY_TYPE.get(entry).create(world);

            if (isValidAttackTarget(entity))
            {
                VALID_ATTACK_TARGETS.put(entry, entity);
            }
        }
    }

    /**
     * @param type the entity type to create an instance of.
     * @param world the world to create the entity in.
     * @return an instance of the given entity type.
     */
    @Nonnull
    public static Entity create(@Nonnull ResourceLocation type, @Nonnull World world)
    {
        return Objects.requireNonNull(Registry.ENTITY_TYPE.get(type).create(world));
    }

    /**
     * @param entity the entity to check.
     * @return true if the entity is deemed attackable by a blockling in game.
     */
    public static boolean isValidAttackTarget(@Nonnull Entity entity)
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
     * @return true if the given entity can see the given block pos.
     */
    public static boolean canSee(@Nonnull LivingEntity entity, @Nonnull BlockPos blockPos)
    {
        Vector3d entityPos = new Vector3d(entity.getX(), entity.getEyeY(), entity.getZ());

        // Check each corner of the block for a more robust result.
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
     * @return true if the given entity is within range of the center of the given block pos.
     */
    public static boolean isInRange(@Nonnull LivingEntity entity, @Nonnull BlockPos blockPos, float rangeSq)
    {
        return (float) BlockUtil.distanceSq(entity.blockPosition(), blockPos) < rangeSq;
    }

    /**
     * Creates the closest path to the given block.
     *
     * @param entity the entity to create a path for.
     * @param blockPos the pos to create a path to.
     * @return the path or null if no path was found.
     */
    @Nullable
    public static Path createPathTo(@Nonnull MobEntity entity, @Nonnull BlockPos blockPos)
    {
        return createPathTo(entity, blockPos, 0);
    }

    /**
     * Creates a path to the given block.
     *
     * @param entity the entity to create a path for.
     * @param blockPos the pos to create a path to.
     * @param stopDistanceSq if the distance between the path's target and target block is within this range we can return (0 to just find the closest path).
     * @return the path or null if no path was found.
     */
    @Nullable
    public static Path createPathTo(@Nonnull MobEntity entity, @Nonnull BlockPos blockPos, float stopDistanceSq)
    {
        Path closestPath = null;
        double closestDistanceSq = Double.MAX_VALUE;

        Path path = entity.getNavigation().createPath(blockPos, 0);

        if (path != null)
        {
            closestPath = path;
            closestDistanceSq = BlockUtil.distanceSq(blockPos, path.getTarget());

            if (closestDistanceSq < stopDistanceSq)
            {
                return closestPath;
            }
        }

        for (BlockPos adjacentPos : BlockUtil.getSurroundingBlockPositions(blockPos))
        {
            path = entity.getNavigation().createPath(adjacentPos, 0);

            if (path != null)
            {
                double distanceSq = BlockUtil.distanceSq(blockPos, path.getTarget());

                if (distanceSq < closestDistanceSq)
                {
                    closestPath = path;
                    closestDistanceSq = distanceSq;

                    if (closestDistanceSq < stopDistanceSq)
                    {
                        return closestPath;
                    }
                }
            }
        }

        // If we get here with a stop distance > 0 then return null as we didn't find a path within range
        return stopDistanceSq > 0 ? null : closestPath;
    }
}