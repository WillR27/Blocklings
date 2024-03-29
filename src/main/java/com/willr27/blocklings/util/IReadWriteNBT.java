package com.willr27.blocklings.util;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

/**
 * Defines an object that should read/write NBT data.
 */
public interface IReadWriteNBT
{
    /**
     * Writes the object data to a new tag.
     *
     * @return the tag the object data was written to.
     */
    @Nonnull
    default CompoundNBT writeToNBT()
    {
        return writeToNBT(new CompoundNBT());
    }

    /**
     * Writes the object data to the given tag.
     *
     * @param tag the tag to write to (typically the tag to directly write unless otherwise specified).
     * @return the tag the object data was written to (should be the tag that was passed in).
     */
    @Nonnull
    CompoundNBT writeToNBT(@Nonnull CompoundNBT tag);

    /**
     * Reads the object data from the given tag.
     *
     * @param tag the tag to read from.
     * @param tagVersion the tagVersion of the tag (used to perform upgrade operations between versions).
     */
    void readFromNBT(@Nonnull CompoundNBT tag, @Nonnull Version tagVersion);
}
