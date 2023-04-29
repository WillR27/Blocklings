package com.willr27.blocklings.util;

import com.willr27.blocklings.Blocklings;
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
import net.minecraftforge.common.util.Lazy;

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
     * The most recent world to load (used to then lazy load the list of valid attack targets).
     */
    @Nullable
    public static World latestWorld;

    /**
     * A map of entities that should be deemed attackable by a blockling in game.
     */
    @Nonnull
    public static final Lazy<Map<ResourceLocation, Entity>> VALID_ATTACK_TARGETS = Lazy.of(EntityUtil::createValidAttackTargetsMap);

    /**
     * @return the map of valid attack targets.
     */
    @Nonnull
    public static Map<ResourceLocation, Entity> createValidAttackTargetsMap()
    {
        Blocklings.LOGGER.info("Creating valid attack targets map.");

        if (latestWorld == null)
        {
            Blocklings.LOGGER.error("Tried to initialise valid attack targets list before a world was loaded!");

            return new TreeMap<>();
        }

        Map<ResourceLocation, Entity> validAttackTargets = new TreeMap<>();

        for (ResourceLocation entry : Registry.ENTITY_TYPE.keySet())
        {
            Entity entity = Registry.ENTITY_TYPE.get(entry).create(latestWorld);

            if (entity != null)
            {
                if (isValidAttackTarget(entity))
                {
                    validAttackTargets.put(entry, entity);
                }
            }
            else
            {
                Blocklings.LOGGER.warn("Failed to create entity: " + entry);
            }
        }

        return validAttackTargets;
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
}
