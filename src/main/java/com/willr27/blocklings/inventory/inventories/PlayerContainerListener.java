package com.willr27.blocklings.inventory.inventories;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class PlayerContainerListener implements IContainerListener
{
    private final ServerPlayerEntity player;

    public PlayerContainerListener(ServerPlayerEntity player)
    {
        this.player = player;
    }

//    @Override
//    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
//    {
//
//    }
//
//    @Override
//    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
//    {
//        NetworkHandler.sendTo(player, new PlayerContainerMessage(stack, slotInd));
//    }
//
//    @Override
//    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue)
//    {
//
//    }

    @Override
    public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_)
    {

    }

    @Override
    public void slotChanged(Container p_71111_1_, int slot, ItemStack p_71111_3_)
    {

    }

    @Override
    public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_)
    {

    }
}
