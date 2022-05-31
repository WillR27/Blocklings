package com.willr27.blocklings;

import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.renderers.blockling.BlocklingRenderer;
import com.willr27.blocklings.item.items.BlocklingItem;
import com.willr27.blocklings.item.items.BlocklingsItems;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.sound.BlocklingsSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Blocklings.MODID)
public class Blocklings
{
    public static final String MODID = "blocklings";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Blocklings()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlocklingsEntityTypes.register(modEventBus);
        BlocklingsItems.register(modEventBus);
        BlocklingsSounds.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModConfig config = new ModConfig(ModConfig.Type.COMMON, BlocklingsConfig.COMMON_SPEC, ModLoadingContext.get().getActiveContainer());
        ModLoadingContext.get().getActiveContainer().addConfig(config);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(BlocklingsEntityTypes.BLOCKLING_ENTITY.get(), BlocklingRenderer::new);

        BlocklingItem.registerItemModelsProperties();
    }
}
