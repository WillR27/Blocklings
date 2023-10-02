package com.willr27.blocklings.capabilities;

import com.willr27.blocklings.Blocklings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A capability used to select a block from the world.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockSelectCapability
{
    @CapabilityInject(BlockSelectCapability.class)
    public static Capability<BlockSelectCapability> CAPABILITY = null;

    /**
     * Whether the player is currently selecting a block.
     */
    public boolean isSelecting = false;

    /**
     * Registers this capability.
     */
    public static void register()
    {
        CapabilityManager.INSTANCE.register(BlockSelectCapability.class, new Storage(), BlockSelectCapability::new);
    }

    @SubscribeEvent
    public static void attachCapabilities(@Nonnull AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof PlayerEntity)
        {
            BlockSelectCapability.Provider provider = new BlockSelectCapability.Provider();
            event.addCapability(new ResourceLocation(Blocklings.MODID, "block_select_capability"), provider);
            event.addListener(provider::invalidate);
        }
    }

    /**
     * A provider for this capability.
     */
    public static class Provider implements ICapabilityProvider
    {
        /**
         * The default instance of this capability.
         */
        @Nonnull
        private final BlockSelectCapability defaultInstance = new BlockSelectCapability();

        /**
         * The lazy optional of this capability.
         */
        @Nonnull
        private final LazyOptional<BlockSelectCapability> lazyOptional = LazyOptional.of(() -> defaultInstance);

        /**
         * Invalidates the lazy optional.
         */
        public void invalidate()
        {
            lazyOptional.invalidate();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return lazyOptional.cast();
        }
    }

    /**
     * Not needed.
     */
    public static class Storage implements Capability.IStorage<BlockSelectCapability>
    {
        @Nullable
        @Override
        public INBT writeNBT(Capability<BlockSelectCapability> capability, BlockSelectCapability instance, Direction side)
        {
            return null;
        }

        @Override
        public void readNBT(Capability<BlockSelectCapability> capability, BlockSelectCapability instance, Direction side, INBT nbt)
        {

        }
    }
}
