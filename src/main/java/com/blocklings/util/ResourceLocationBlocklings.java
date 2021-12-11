package com.blocklings.util;

import com.blocklings.main.Blocklings;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationBlocklings extends ResourceLocation
{
    public ResourceLocationBlocklings(String name)
    {
        super(Blocklings.MODID + ":" + name);
    }
}
