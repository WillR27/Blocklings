package com.willr27.blocklings.entity;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

/**
 * Handles all the added entity types.
 */
public class BlocklingsEntityTypes
{
    /**
     * The deferred register to register the entity type.
     */
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Blocklings.MODID);

    /**
     * The blockling entity type.
     */
    public static final RegistryObject<EntityType<BlocklingEntity>> BLOCKLING = ENTITY_TYPES.register("blockling",
        () -> EntityType.Builder.of(BlocklingEntity::new, EntityGeneration.BLOCKLING)
                                .sized(1.0f, 1.0f)
                                .build(new ResourceLocation(Blocklings.MODID, "blockling").toString()));

    /**
     * Registers the entity types.
     *
     * @param modEventBus the mod event bus.
     */
    public static void register(@Nonnull IEventBus modEventBus)
    {
        ENTITY_TYPES.register(modEventBus);
    }
}
