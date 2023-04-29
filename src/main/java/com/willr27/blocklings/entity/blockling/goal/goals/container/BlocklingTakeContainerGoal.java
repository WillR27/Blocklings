package com.willr27.blocklings.entity.blockling.goal.goals.container;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.config.ContainerInfo;
import com.willr27.blocklings.entity.blockling.goal.config.iteminfo.ItemInfo;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.config.ItemConfigurationTypeProperty;
import com.willr27.blocklings.inventory.AbstractInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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
        TileEntity tileEntity = containerAsTileEntity(containerInfo);

        if (tileEntity == null)
        {
            return false;
        }

        AbstractInventory inv = blockling.getEquipment();
        int remainingTakeAmount = getTransferAmount();

        // Loop through each item and try to take it from the container.
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

            ItemStack remainingStack = new ItemStack(item, remainingTakeAmount);
            int amountOfSpaceInInventoryForItem = remainingStack.getCount() - inv.addItem(remainingStack, true).getCount();

            // If there is no space in the inventory for the item then skip it.
            if (amountOfSpaceInInventoryForItem == 0)
            {
                continue;
            }

            // Loop through each selected side of the container (in priority order) and try to take the item from it.
            for (Direction direction : containerInfo.getSides())
            {
                // If there is no space in the inventory for the item then skip it. We need to check again here as we
                // may have taken some items from the container in the previous loop.
                if (amountOfSpaceInInventoryForItem == 0)
                {
                    continue;
                }

                IItemHandler itemHandler = getItemHandler(tileEntity, direction);

                // Something has probably gone wrong if the item handler is null, but we have to check.
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

                    // We always want to check if the stop amounts have been reached.
                    if (inventoryAmount >= stopInventoryAmount || containerAmount <= stopContainerAmount)
                    {
                        continue;
                    }

                    // If the task is not currently executing then we want to check if the start amounts have been reached.
                    if (getState() != State.ACTIVE)
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

                // Only try to take what is needed and the blockling has room for.
                int amountToTake = Math.min(remainingTakeAmount, amountOfSpaceInInventoryForItem);
                ItemStack stackLeftToTake = new ItemStack(item, amountToTake);

                // Try extract as many items as possible and update the stack to be the remaining stack. Extract first
                // in case the item handler has special rules that prevent an item being extracted, e.g. a machine that
                // prevents items being extracted through the top.
                for (int slot = itemHandler.getSlots() - 1; slot >= 0 && !stackLeftToTake.isEmpty(); slot--)
                {
                    // Shrink the stack left to take by the amount extracted.
                    stackLeftToTake.shrink(itemHandler.extractItem(slot, stackLeftToTake.getCount(), simulate).getCount());
                }

                // Calculate the amount of items we have taken.
                int amountTaken = amountToTake - stackLeftToTake.getCount();

                // If we have not taken any items then continue to the next direction.
                if (amountTaken == 0)
                {
                    continue;
                }

                // If we are not simulating then add the items to the blockling's inventory.
                if (!simulate)
                {
                    inv.addItem(new ItemStack(item, amountToTake), false);
                }
                else
                {
                    // If we are here then we are simulating and have taken an item so can return true.
                    return true;
                }

                // Update the amount of items we have taken.
                remainingTakeAmount -= amountTaken;
                amountOfSpaceInInventoryForItem -= amountTaken;
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
                    IItemHandler itemHandler = getItemHandler(tileEntity, direction);

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
