package com.willr27.blocklings.block;

import com.willr27.blocklings.Blocklings;
import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

/**
 * Handles the registration of blocks.
 */
public class BlocklingsBlocks
{
    /**
     * The deferred block registry.
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Blocklings.MODID);

    public static final RegistryObject<Block> LIGHT = BLOCKS.register("light", LightBlock::new);

    /**
     * Registers the blocks.
     */
    public static void register(@Nonnull IEventBus modEventBus)
    {
        BLOCKS.register(modEventBus);
    }
}
