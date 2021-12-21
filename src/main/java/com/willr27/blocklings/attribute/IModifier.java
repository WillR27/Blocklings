package com.willr27.blocklings.attribute;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.Supplier;

public interface IModifier<T>
{
    void writeToNBT(CompoundNBT tag);

    void readFromNBT(CompoundNBT tag);

    void encode(PacketBuffer buf);

    void decode(PacketBuffer buf);

    T getValue();

    String formatValue(String format);

    Operation getOperation();

    Supplier<String> getDisplayStringSupplier();
}
