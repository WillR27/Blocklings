package com.blocklings.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class CropStruct
{
    public Block crop;
    public Item seed;
    public int age;

    public CropStruct(Block crop, Item seed, int age)
    {
        this.crop = crop;
        this.seed = seed;
        this.age = age;
    }
}
