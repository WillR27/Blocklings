package com.willr27.blocklings.util;

import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;

/**
 * Utility class for external mod related things.
 */
public class ModUtil
{
    /**
     * The mod id for Tinkers' Construct.
     */
    public static final String TINKERS_CONSTRUCT_MODID = "tconstruct";

    /**
     * @return true if Tinkers' Construct is loaded.
     */
    public static boolean isTinkersConstructLoaded()
    {
        return isModLoaded(TINKERS_CONSTRUCT_MODID);
    }

    /**
     * @return true if the given mod is loaded.
     */
    public static boolean isModLoaded(@Nonnull String modid)
    {
        return ModList.get().isLoaded(modid);
    }
}
