package com.willr27.blocklings.capabilities;

import com.willr27.blocklings.Blocklings;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
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
    public static Capability<BlockSelectCapability> CAPABILITY = null;

    /**
     * Whether the player is currently selecting a block.
     */
    public boolean isSelecting = false;

    @SubscribeEvent
    public void registerCaps(@Nonnull RegisterCapabilitiesEvent event)
    {
        event.register(BlockSelectCapability.class);
    }

    @SubscribeEvent
    public static void attachCapabilities(@Nonnull AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
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
}
