package com.willr27.blocklings.network;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessage
{
    void handle(Supplier<NetworkEvent.Context> ctx);
}
