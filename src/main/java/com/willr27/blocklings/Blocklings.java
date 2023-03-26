package com.willr27.blocklings;

import com.willr27.blocklings.block.BlocklingsBlocks;
import com.willr27.blocklings.capabilities.BlocklingsCapabilities;
import com.willr27.blocklings.client.renderer.entity.BlocklingRenderer;
import com.willr27.blocklings.command.BlocklingsCommands;
import com.willr27.blocklings.config.BlocklingsConfig;
import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.EntityGeneration;
import com.willr27.blocklings.interop.ModProxies;
import com.willr27.blocklings.item.BlocklingItem;
import com.willr27.blocklings.item.BlocklingsItems;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.sound.BlocklingsSounds;
import com.willr27.blocklings.util.ObjectUtil;
import com.willr27.blocklings.util.Version;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Blocklings.MODID)
public class Blocklings
{
    /**
     * The mod's ID, which is also the mod's namespace.
     */
    @Nonnull
    public static final String MODID = "blocklings";

    /**
     * The mod's version.
     */
    @Nonnull
    public static final Version VERSION = new Version(ObjectUtil.coalesce(Blocklings.class.getPackage().getSpecificationVersion(), "99999.0.0.0"));

    /**
     * The mod's logger.
     */
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * The mod's constructor.
     */
    public Blocklings()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlocklingsEntityTypes.register(modEventBus);
        BlocklingsBlocks.register(modEventBus);
        BlocklingsItems.register(modEventBus);
        BlocklingsSounds.register(modEventBus);

        modEventBus.addListener(this::setupCommon);
        modEventBus.addListener(this::setupClient);

        MinecraftForge.EVENT_BUS.register(this);

        BlocklingsConfig.init();
    }

    /**
     * Setup shared between client and server.
     */
    private void setupCommon(final FMLCommonSetupEvent event)
    {
        ModProxies.init();
        NetworkHandler.init();
        BlocklingsCommands.init();
        BlocklingsCapabilities.register();

        event.enqueueWork(EntityGeneration::init);
    }

    /**
     * Setup only on the client.
     */
    private void setupClient(final FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(BlocklingsEntityTypes.BLOCKLING.get(), BlocklingRenderer::new);

        BlocklingItem.registerItemModelsProperties();
    }
}
