package com.blocklings.proxy;

import com.blocklings.main.Blocklings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerProxy implements IProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent e)
    {

    }

    @Override
    public void init(FMLInitializationEvent e)
    {

    }

    @Override
    public void postInit(FMLPostInitializationEvent e)
    {

    }

    @Override
    public EntityPlayer getPlayer(MessageContext ctx)
    {
        return ctx.getServerHandler().player;
    }
}