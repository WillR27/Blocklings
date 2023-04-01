package com.willr27.blocklings.entity.blockling.goal.goals.container;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.config.iteminfo.ItemInfo;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.config.ItemConfigurationTypeProperty;
import com.willr27.blocklings.inventory.AbstractInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Finds nearby containers and takes items from them.
 */
public class BlocklingTakeContainerGoal extends BlocklingContainerGoal
{
    /**
     * @param taskId    the taskId associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks     the blockling tasks.
     */
    public BlocklingTakeContainerGoal(@Nonnull UUID taskId, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(taskId, blockling, tasks);
    }

    @Override
    protected boolean tryTransferItems(@Nonnull ContainerInfo containerInfo, boolean simulate)
    {
        AbstractInventory inv = blockling.getEquipment();
        int remainingTakeAmount = getTransferAmount();

        TileEntity tileEntity = containerAsTileEntity(containerInfo);

        if (tileEntity == null)
        {
            return false;
        }

        for (ItemInfo itemInfo : itemInfoSet)
        {
            // If we have taken all the items we can then stop.
            if (remainingTakeAmount <= 0)
            {
                break;
            }

            int startInventoryAmount = itemInfo.getStartInventoryAmount() != null ? itemInfo.getStartInventoryAmount() : Integer.MAX_VALUE;
            int startContainerAmount = itemInfo.getStartContainerAmount() != null ? itemInfo.getStartContainerAmount() : 0;
            int stopInventoryAmount = itemInfo.getStopInventoryAmount() != null ? itemInfo.getStopInventoryAmount() : Integer.MAX_VALUE;
            int stopContainerAmount = itemInfo.getStopContainerAmount() != null ? itemInfo.getStopContainerAmount() : 0;

            Item item = itemInfo.getItem();

            for (Direction direction : containerInfo.getSides())
            {
                IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);

                if (itemHandler == null)
                {
                    return false;
                }

                // Skip any items that are not in the container.
                if (itemConfigurationTypeProperty.getType() == ItemConfigurationTypeProperty.Type.SIMPLE && !hasItemInContainer(itemHandler, item))
                {
                    continue;
                }

                // If we are using the advanced configuration check the item's inventory and container amounts.
                if (itemConfigurationTypeProperty.getType() == ItemConfigurationTypeProperty.Type.ADVANCED)
                {
                    int inventoryAmount = countItemsInInventory(item);
                    int containerAmount = countItemsInContainer(itemHandler, item);

                    // If the task is currently executing then we want to check if the stop amounts have been reached.
                    if (getState() == State.ACTIVE)
                    {
                        if (inventoryAmount >= stopInventoryAmount || containerAmount <= stopContainerAmount)
                        {
                            continue;
                        }
                    }
                    // If the task is not currently executing then we want to check if the start amounts have been reached.
                    else
                    {
                        if (inventoryAmount >= startInventoryAmount || containerAmount <= startContainerAmount)
                        {
                            continue;
                        }
                    }

                    // Make sure we don't deposit more items than the user has configured.
                    remainingTakeAmount = Math.min(remainingTakeAmount, Math.min(stopInventoryAmount - inventoryAmount, containerAmount - stopContainerAmount));
                }

                // If we have taken all the items we can then stop.
                if (remainingTakeAmount <= 0)
                {
                    break;
                }

                int startingStackToTakeCount = Math.min(remainingTakeAmount, countItemsInContainer(itemHandler, item));
                ItemStack stackToTake = new ItemStack(item, startingStackToTakeCount);

                // Loop through all slots or until the stack is empty.
                for (int slot = 0; slot < inv.invSize && !stackToTake.isEmpty(); slot++)
                {
                    // Try insert as many items as possible and update the stack to be the remainder.
                    stackToTake = inv.addItem(stackToTake, slot, simulate);
                }

                int amountTaken = startingStackToTakeCount - stackToTake.getCount();

                // If the count has decreased then at least one item was taken.
                if (amountTaken > 0)
                {
                    remainingTakeAmount -= amountTaken;

                    if (!simulate)
                    {
                        for (int slot = 0; slot < itemHandler.getSlots() && amountTaken > 0; slot++)
                        {
                            ItemStack extractedStack = itemHandler.extractItem(slot, amountTaken, false);
                            amountTaken -= extractedStack.getCount();
                        }
                    }
                }
            }
        }

        return remainingTakeAmount < getTransferAmount();
    }

    @Override
    public boolean hasItemsToTransfer()
    {
        for (ItemInfo itemInfo : itemInfoSet)
        {
            for (ContainerInfo containerInfo : containerInfos)
            {
                TileEntity tileEntity = containerAsTileEntity(containerInfo);

                if (tileEntity == null)
                {
                    return false;
                }

                for (Direction direction : containerInfo.getSides())
                {
                    IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);

                    if (itemHandler == null)
                    {
                        return false;
                    }

                    if (hasItemInContainer(itemHandler, itemInfo.getItem()))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isTakeItems()
    {
        return true;
    }
}
