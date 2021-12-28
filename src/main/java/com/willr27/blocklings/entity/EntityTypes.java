package com.willr27.blocklings.entity;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypes
{
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Blocklings.MODID);

    public static final RegistryObject<EntityType<BlocklingEntity>> BLOCKLING_ENTITY =
            ENTITY_TYPES.register("blockling",
                    () -> EntityType.Builder.of(BlocklingEntity::new, EntityClassification.AMBIENT)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(Blocklings.MODID, "blockling").toString()));

    public static void register(IEventBus modEventBus)
    {
        ENTITY_TYPES.register(modEventBus);
    }
}
