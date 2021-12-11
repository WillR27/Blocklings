package com.willr27.blocklings.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

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
}
