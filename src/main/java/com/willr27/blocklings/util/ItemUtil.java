package com.willr27.blocklings.util;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil
{
    public static final List<Item> FLOWERS = new ArrayList<>();
    static
    {
        FLOWERS.add(Items.ORANGE_TULIP);
        FLOWERS.add(Items.PINK_TULIP);
        FLOWERS.add(Items.RED_TULIP);
        FLOWERS.add(Items.WHITE_TULIP);
        FLOWERS.add(Items.DANDELION);
        FLOWERS.add(Items.POPPY);
        FLOWERS.add(Items.BLUE_ORCHID);
        FLOWERS.add(Items.ALLIUM);
        FLOWERS.add(Items.AZURE_BLUET);
        FLOWERS.add(Items.OXEYE_DAISY);
        FLOWERS.add(Items.CORNFLOWER);
        FLOWERS.add(Items.LILY_OF_THE_VALLEY);
        FLOWERS.add(Items.LILAC);
        FLOWERS.add(Items.ROSE_BUSH);
        FLOWERS.add(Items.PEONY);
    }
    public static boolean isFlower(Item item)
    {
        return FLOWERS.contains(item);
    }
}
