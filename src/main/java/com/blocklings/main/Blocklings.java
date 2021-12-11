package com.blocklings.main;

import com.blocklings.events.AttackEventHandler;
import com.blocklings.guis.GuiHandler;
import com.blocklings.items.BlocklingsItems;
import com.blocklings.proxy.IProxy;
import com.blocklings.util.helpers.EntityHelper;
import com.blocklings.util.helpers.NetworkHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Blocklings.MODID, name = Blocklings.MODNAME)
public class Blocklings
{
    public static final String MODID = "blocklings";
    public static final String MODNAME = "Blocklings";

    @SidedProxy(clientSide = "com.blocklings.proxy.ClientProxy", serverSide = "com.blocklings.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance
    public static Blocklings instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        BlocklingsItems.init();
        EntityHelper.registerEntities();
        NetworkHelper.registerMessages();
        NetworkRegistry.INSTANCE.registerGuiHandler(Blocklings.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new AttackEventHandler());

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.postInit(e);
    }
}