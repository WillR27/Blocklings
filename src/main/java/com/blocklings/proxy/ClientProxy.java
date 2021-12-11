package com.blocklings.proxy;

import com.blocklings.main.Blocklings;
import com.blocklings.util.helpers.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent e)
    {
        EntityHelper.registerRenderers();
    }

    @Override
    public void init(FMLInitializationEvent e)
    {
//        List<IResourcePack> defaultResourcePacks = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks");
//        defaultResourcePacks.add(new BlocklingsResourcePack());
//        Minecraft.getMinecraft().refreshResources();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e)
    {

    }

    @Override
    public EntityPlayer getPlayer(MessageContext ctx)
    {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : ctx.getServerHandler().player);
    }
}