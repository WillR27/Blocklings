package com.willr27.blocklings.entity;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityGeneration
{
    /**
     * Custom entity classification for blocklings.
     */
    public static final EntityClassification BLOCKLING = EntityClassification.create("BLOCKLING", "blockling", 64, true, false, 192);

    /**
     * Registers entities to be spawned in the world.
     */
    public static void init()
    {
        EntitySpawnPlacementRegistry.register(BlocklingsEntityTypes.BLOCKLING.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlocklingEntity::checkBlocklingSpawnRules);
    }

    @SubscribeEvent
    public static void onBiomeLoad(final BiomeLoadingEvent event)
    {
        addEntityToAllBiomes(event.getSpawns(), BlocklingsEntityTypes.BLOCKLING.get(), 1, 1, 1);
    }

    private static void addEntityToAllBiomesExceptThese(BiomeLoadingEvent event, EntityType<?> type, int weight, int minCount, int maxCount, RegistryKey<Biome>... biomes)
    {
        // Goes through each entry in the biomes and sees if it matches the current biome we are loading
        boolean isBiomeSelected = Arrays.stream(biomes).map(RegistryKey::location).map(Object::toString).anyMatch(s -> s.equals(event.getName().toString()));

        if(!isBiomeSelected)
        {
            addEntityToAllBiomes(event.getSpawns(), type, weight, minCount, maxCount);
        }
    }

    private static void addEntityToSpecificBiomes(BiomeLoadingEvent event, EntityType<?> type, int weight, int minCount, int maxCount, RegistryKey<Biome>... biomes)
    {
        // Goes through each entry in the biomes and sees if it matches the current biome we are loading
        boolean isBiomeSelected = Arrays.stream(biomes).map(RegistryKey::location).map(Object::toString).anyMatch(s -> s.equals(event.getName().toString()));

        if(isBiomeSelected)
        {
            addEntityToAllBiomes(event.getSpawns(), type, weight, minCount, maxCount);
        }
    }

    private static void addEntityToAllBiomes(MobSpawnInfoBuilder spawns, EntityType<?> type, int weight, int minCount, int maxCount)
    {
        List<MobSpawnInfo.Spawners> base = spawns.getSpawner(type.getCategory());
        base.add(new MobSpawnInfo.Spawners(type, weight, minCount, maxCount));
    }
}
