package com.willr27.blocklings.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;

/**
 * Defines an object that should sync to/from the client/server.
 */
public interface ISyncable
{
    /**
     * Writes the object's data to the given packet buffer.
     *
     * @param buf the buffer to write to.
     */
    void encode(@Nonnull FriendlyByteBuf buf);

    /**
     * Reads the object's data from the given packet buffer.
     *
     * @param buf the buffer to read from.
     */
    void decode(@Nonnull FriendlyByteBuf buf);
}
