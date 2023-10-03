package com.willr27.blocklings.util;


import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class PacketBufferUtils
{
    public static String readString(ByteBuf buf)
    {
        return buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
    }

    public static void writeString(ByteBuf buf, String string)
    {
        buf.writeInt(string.getBytes(StandardCharsets.UTF_8).length);
        buf.writeCharSequence(string, StandardCharsets.UTF_8);
    }
}
