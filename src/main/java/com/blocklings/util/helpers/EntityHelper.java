package com.blocklings.util.helpers;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.main.Blocklings;
import com.blocklings.render.RenderBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHelper
{
    public static final float BASE_SCALE = 0.75f;
    public static final float BASE_SCALE_FOR_HITBOX = BASE_SCALE * 0.95f;

    public static final int SKILL_POINT_INTERVAL = 5;

    public static final float SWING_SPEED_COEF = 0.75f;

    public static void registerEntities()
    {
        int id = 1;
        EntityRegistry.registerModEntity(new ResourceLocationBlocklings("entity_blockling"), EntityBlockling.class, "blockling", id++, Blocklings.instance, 64, 3, true, 7951674, 7319108);
        EntityRegistry.addSpawn(EntityBlockling.class, 10, 1, 2, EnumCreatureType.CREATURE,
            Biomes.PLAINS,
            Biomes.FOREST,
            Biomes.FOREST_HILLS,
            Biomes.REDWOOD_TAIGA,
            Biomes.ROOFED_FOREST,
            Biomes.TAIGA,
            Biomes.TAIGA_HILLS
        );
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityBlockling.class, RenderBlockling.FACTORY);
    }

    public static int getXpUntilNextLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    public enum Task
    {
        IDLE("Idle", 1),
        HUNT("Hunt", 2),
        MINE("Mine", 3),
        CHOP("Chop", 4),
        FARM("Farm", 5);

        public String name;
        public int id;

        Task(String name, int id)
        {
            this.name = name;
            this.id = id;
        }

        public static Task getFromID(int id)
        {
            for (Task task : Task.values())
            {
                if (task.id == id)
                {
                    return task;
                }
            }

            return Task.IDLE;
        }
    }

    public enum Guard
    {
        NOGUARD("Ignore", 1),
        GUARD("Guard", 2);

        public String name;
        public int id;

        Guard(String name, int id)
        {
            this.name = name;
            this.id = id;
        }

        public static Guard getFromID(int id)
        {
            for (Guard guard : Guard.values())
            {
                if (guard.id == id)
                {
                    return guard;
                }
            }

            return Guard.NOGUARD;
        }
    }

    public enum State
    {
        STAY("Stay", 1),
        FOLLOW("Follow", 2),
        WANDER("Wander", 3);

        public String name;
        public int id;

        State(String name, int id)
        {
            this.name = name;
            this.id = id;
        }

        public static State getFromID(int id)
        {
            for (State state : State.values())
            {
                if (state.id == id)
                {
                    return state;
                }
            }

            return State.WANDER;
        }
    }
}
