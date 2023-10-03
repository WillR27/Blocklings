package com.willr27.blocklings.util;

import com.willr27.blocklings.Blocklings;
import net.minecraft.resources.ResourceLocation;

public class BlocklingsResourceLocation extends ResourceLocation
{
    public BlocklingsResourceLocation(String path)
    {
        super(Blocklings.MODID, path);
    }
}
