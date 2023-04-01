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
        AbstractInventory inv = blockling.getEquipment();
        int remainingDepositAmount = getTransferAmount();

        TileEntity tileEntity = containerAsTileEntity(containerInfo);

        if (tileEntity == null)
        {
            return false;
        }

        for (ItemInfo itemInfo : itemInfoSet)
        {
            // If we have deposited all the items we can then stop.
            if (remainingDepositAmount <= 0)
            {
                break;
            }

            int startInventoryAmount = itemInfo.getStartInventoryAmount() != null ? itemInfo.getStartInventoryAmount() : 0;
            int startContainerAmount = itemInfo.getStartContainerAmount() != null ? itemInfo.getStartContainerAmount() : Integer.MAX_VALUE;
            int stopInventoryAmount = itemInfo.getStopInventoryAmount() != null ? itemInfo.getStopInventoryAmount() : 0;
            int stopContainerAmount = itemInfo.getStopContainerAmount() != null ? itemInfo.getStopContainerAmount() : Integer.MAX_VALUE;

            Item item = itemInfo.getItem();

            // Skip any items that are not in the blockling's inventory.
            if (itemConfigurationTypeProperty.getType() == ItemConfigurationTypeProperty.Type.SIMPLE && !hasItemInInventory(item))
            {
                continue;
            }

            for (Direction direction : containerInfo.getSides())
            {
                IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);

                if (itemHandler == null)
                {
                    return false;
                }

                // If we are using the advanced configuration check the item's inventory and container amounts.
                if (itemConfigurationTypeProperty.getType() == ItemConfigurationTypeProperty.Type.ADVANCED)
                {
                    int inventoryAmount = countItemsInInventory(item);
                    int containerAmount = countItemsInContainer(itemHandler, item);

                    // If the task is currently executing then we want to check if the stop amounts have been reached.
                    if (getState() == State.ACTIVE)
                    {
                        if (inventoryAmount <= stopInventoryAmount || containerAmount >= stopContainerAmount)
                        {
                            continue;
                        }
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

                int startingStackToDepositCount = Math.min(remainingDepositAmount, inv.count(new ItemStack(item)));
                ItemStack stackToDeposit = new ItemStack(item, startingStackToDepositCount);

                // Loop through all slots or until the stack is empty.
                for (int slot = 0; slot < itemHandler.getSlots() && !stackToDeposit.isEmpty(); slot++)
                {
                    // Try insert as many items as possible and update the stack to be the remainder.
                    stackToDeposit = itemHandler.insertItem(slot, stackToDeposit, simulate);
                }

                int amountDeposited = startingStackToDepositCount - stackToDeposit.getCount();

                // If the count has decreased then at least one item was deposited.
                if (amountDeposited > 0)
                {
                    if (!simulate)
                    {
                        inv.take(new ItemStack(item, amountDeposited));
                    }

                    remainingDepositAmount -= amountDeposited;
                }
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
