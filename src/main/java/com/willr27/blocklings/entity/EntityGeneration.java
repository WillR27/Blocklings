package com.willr27.blocklings.entity;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
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
    public static final MobCategory BLOCKLING = MobCategory.create("BLOCKLING", "blockling", 64, true, false, 192);

    /**
     * Registers entities to be spawned in the world.
     */
    public static void init()
    {
        SpawnPlacements.register(BlocklingsEntityTypes.BLOCKLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlocklingEntity::checkBlocklingSpawnRules);
    }

    @SubscribeEvent
    public static void onBiomeLoad(final BiomeLoadingEvent event)
    {
        addEntityToAllBiomes(event.getSpawns(), BlocklingsEntityTypes.BLOCKLING.get(), 1, 1, 1);
    }

//    private static void addEntityToAllBiomesExceptThese(BiomeLoadingEvent event, EntityType<?> type, int weight, int minCount, int maxCount, RegistryKey<Biome>... biomes)
//    {
//        // Goes through each entry in the biomes and sees if it matches the current biome we are loading
//        boolean isBiomeSelected = Arrays.stream(biomes).map(RegistryKey::location).map(Object::toString).anyMatch(s -> s.equals(event.getName().toString()));
//
//        if(!isBiomeSelected)
//        {
//            addEntityToAllBiomes(event.getSpawns(), type, weight, minCount, maxCount);
//        }
//    }
//
//    private static void addEntityToSpecificBiomes(BiomeLoadingEvent event, EntityType<?> type, int weight, int minCount, int maxCount, RegistryKey<Biome>... biomes)
//    {
//        // Goes through each entry in the biomes and sees if it matches the current biome we are loading
//        boolean isBiomeSelected = Arrays.stream(biomes).map(RegistryKey::location).map(Object::toString).anyMatch(s -> s.equals(event.getName().toString()));
//
//        if(isBiomeSelected)
//        {
//            addEntityToAllBiomes(event.getSpawns(), type, weight, minCount, maxCount);
//        }
//    }

    private static void addEntityToAllBiomes(MobSpawnSettingsBuilder spawns, EntityType<?> type, int weight, int minCount, int maxCount)
    {
        List<MobSpawnSettings.SpawnerData> base = spawns.getSpawner(type.getCategory());
        base.add(new MobSpawnSettings.SpawnerData(type, weight, minCount, maxCount));
    }
}
