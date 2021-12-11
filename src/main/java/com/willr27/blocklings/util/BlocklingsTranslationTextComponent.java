package com.willr27.blocklings.util;

import net.minecraft.util.text.TranslationTextComponent;

public class BlocklingsTranslationTextComponent extends TranslationTextComponent
{
    public BlocklingsTranslationTextComponent(String key)
    {
        super("blocklings." + key);
    }

    public BlocklingsTranslationTextComponent(String key, Object... objects)
    {
        super("blocklings." + key, objects);
    }
}
