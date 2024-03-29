package com.willr27.blocklings.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class Message
{
    public abstract void handle(Supplier<NetworkEvent.Context> ctx);

    /**
     * @return the uuid of the client player.
     */
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    protected UUID getClientPlayerId()
    {
        return Minecraft.getInstance().player.getUUID();
    }

    /**
     * @return the client player.
     */
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    protected PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }
}
