package com.willr27.blocklings.util;

import net.minecraft.network.chat.TranslatableComponent;

public class BlocklingsTranslatableComponent extends TranslatableComponent
{
    public BlocklingsTranslatableComponent(String key)
    {
        super("blocklings." + key);
    }

    public BlocklingsTranslatableComponent(String key, Object... objects)
    {
        super("blocklings." + key, objects);
    }
}
