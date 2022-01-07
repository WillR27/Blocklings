package com.willr27.blocklings.util;

import net.minecraft.network.PacketBuffer;

import java.nio.charset.StandardCharsets;

public class PacketBufferUtils
{
    public static String readString(PacketBuffer buf)
    {
        return buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
    }

    public static void writeString(PacketBuffer buf, String string)
    {
        buf.writeInt(string.getBytes(StandardCharsets.UTF_8).length);
        buf.writeCharSequence(string, StandardCharsets.UTF_8);
    }
}
