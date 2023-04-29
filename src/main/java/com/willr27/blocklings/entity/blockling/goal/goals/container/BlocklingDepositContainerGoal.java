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
 * Finds nearby containers and deposits items into them.
 */
public class BlocklingDepositContainerGoal extends BlocklingContainerGoal
{
    /**
     * @param taskId    the taskId associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks     the blockling tasks.
     */
    public BlocklingDepositContainerGoal(@Nonnull UUID taskId, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
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
        int remainingDepositAmount = getTransferAmount();

        // Loop through each item and try to take it from the blockling's inventory.
        for (ItemInfo itemInfo : itemInfoSet)
        {
            // If we have deposited all the items we can then stop.
            if (remainingDepositAmount <= 0)
            {
                break;
            }

            Item item = itemInfo.getItem();

            // Skip any items that are not in the container's inventory.
            if (itemConfigurationTypeProperty.getType() == ItemConfigurationTypeProperty.Type.SIMPLE && !hasItemInInventory(item))
            {
                continue;
            }

            int startInventoryAmount = itemInfo.getStartInventoryAmount() != null ? itemInfo.getStartInventoryAmount() : 0;
            int startContainerAmount = itemInfo.getStartContainerAmount() != null ? itemInfo.getStartContainerAmount() : Integer.MAX_VALUE;
            int stopInventoryAmount = itemInfo.getStopInventoryAmount() != null ? itemInfo.getStopInventoryAmount() : 0;
            int stopContainerAmount = itemInfo.getStopContainerAmount() != null ? itemInfo.getStopContainerAmount() : Integer.MAX_VALUE;

            // Loop through each selected side of the container (in priority order) and try to deposit the item in it.
            for (Direction direction : containerInfo.getSides())
            {
                IItemHandler itemHandler = getItemHandler(tileEntity, direction);

                // Something has probably gone wrong if the item handler is null, but we have to check.
                if (itemHandler == null)
                {
                    return false;
                }

                ItemStack remainingStack = new ItemStack(item, remainingDepositAmount);
                int amountOfSpaceInContainerForItem = 0;

                for (int i = 0; i < itemHandler.getSlots() && !remainingStack.isEmpty(); i++)
                {
                    ItemStack remainderStack = itemHandler.insertItem(i, remainingStack, true);
                    amountOfSpaceInContainerForItem += remainingStack.getCount() - remainderStack.getCount();
                    remainingStack = remainderStack;
                }

                // If there is no space in the container for the item then continue to the next direction.
                if (amountOfSpaceInContainerForItem == 0)
                {
                    continue;
                }

                // If we are using the advanced configuration check the item's inventory and container amounts.
                if (itemConfigurationTypeProperty.getType() == ItemConfigurationTypeProperty.Type.ADVANCED)
                {
                    int inventoryAmount = countItemsInInventory(item);
                    int containerAmount = countItemsInContainer(itemHandler, item);

                    // We always want to check if the stop amounts have been reached.
                    if (inventoryAmount <= stopInventoryAmount || containerAmount >= stopContainerAmount)
                    {
                        continue;
                    }

                    // If the task is not currently executing then we want to check if the start amounts have been reached.
                    else
                    {
                        if (inventoryAmount <= startInventoryAmount || containerAmount >= startContainerAmount)
                        {
                            continue;
                        }
                    }

                    // Make sure we don't deposit more items than the user has configured.
                    remainingDepositAmount = Math.min(remainingDepositAmount, Math.min(inventoryAmount - stopInventoryAmount, stopContainerAmount - containerAmount));
                }

                // If we have deposited all the items we can then stop.
                if (remainingDepositAmount <= 0)
                {
                    break;
                }

                // Only try to deposit what is needed and the container has room for.
                int amountToDeposit = Math.min(remainingDepositAmount, amountOfSpaceInContainerForItem);
                ItemStack stackLeftToDeposit = new ItemStack(item, amountToDeposit);

                // Try take as many items as possible from the blockling's inventory.
                ItemStack stackDeposited = inv.takeItem(stackLeftToDeposit, simulate);

                // Calculate the amount of items we have taken.
                int amountTaken = stackDeposited.getCount();

                // If we have not taken any items then continue to the next direction.
                if (amountTaken == 0)
                {
                    continue;
                }

                // If we are not simulating then add the items to the container's inventory.
                if (!simulate)
                {
                    for (int i = 0; i < itemHandler.getSlots() && !stackDeposited.isEmpty(); i++)
                    {
                        stackDeposited = itemHandler.insertItem(i, stackDeposited, false);
                    }
                }
                else
                {
                    // If we are here then we are simulating and have deposited an item so can return true.
                    return true;
                }

                // Update the amount of items we have taken.
                remainingDepositAmount -= amountTaken;
            }
        }

        return remainingDepositAmount < getTransferAmount();
    }

    @Override
    public boolean hasItemsToTransfer()
    {
        for (ItemInfo itemInfo : itemInfoSet)
        {
            if (hasItemInInventory(itemInfo.getItem()))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isTakeItems()
    {
        return false;
    }
}
