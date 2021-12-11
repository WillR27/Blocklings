package com.willr27.blocklings.util;

import com.willr27.blocklings.Blocklings;
import net.minecraft.util.ResourceLocation;

public class BlocklingsResourceLocation extends ResourceLocation
{
    public BlocklingsResourceLocation(String path)
    {
        super(Blocklings.MODID, path);
    }
}
