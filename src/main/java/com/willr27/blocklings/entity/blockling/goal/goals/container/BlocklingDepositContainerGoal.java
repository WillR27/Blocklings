package com.willr27.blocklings.entity.blockling.goal.goals.container;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.controls.config.ItemsConfigurationControl;
import com.willr27.blocklings.client.gui.control.controls.panels.TabbedPanel;
import com.willr27.blocklings.client.gui.control.event.events.*;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.config.ItemInfo;
import com.willr27.blocklings.entity.blockling.goal.config.OrderedItemInfoSet;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.config.ItemConfigurationTypeProperty;
import com.willr27.blocklings.inventory.AbstractInventory;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.Version;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Finds nearby containers and deposits items into them.
 */
public class BlocklingDepositContainerGoal extends BlocklingContainerGoal
{
    /**
     * The amount of items that can be deposited per second.
     */
    private int depositAmount = 1;

    /**
     * The timer used to determine when to deposit items.
     */
    private int depositTimer = 0;

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
    public void writeToNBT(@Nonnull CompoundNBT taskTag)
    {
        super.writeToNBT(taskTag);

        taskTag.put("item_set", itemInfoSet.writeToNBT());
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT taskTag, @Nonnull Version tagVersion)
    {
        super.readFromNBT(taskTag, tagVersion);

        CompoundNBT itemSetTag = taskTag.getCompound("item_set");

        if (taskTag.contains("item_set"))
        {
            itemInfoSet.readFromNBT(itemSetTag, tagVersion);
        }
        else
        {
            Blocklings.LOGGER.warn("Could not find item set for deposit container goal!");
        }
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        itemInfoSet.encode(buf);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        itemInfoSet.decode(buf);
    }

    @Override
    protected void tickGoal()
    {
        if (depositTimer < 20)
        {
            depositTimer++;

            return;
        }

        if (isInRangeOfPathTargetPos())
        {
            boolean depositedAnItem = tryDepositItemsToContainer(getTarget(), false);

            // If no items were deposited then try other targets before this one again.
            if (!depositedAnItem)
            {
                markTargetBad();
            }
        }

        depositTimer = 0;
    }

    /**
     * Tries to add items from the blockling's inventory to the given container.
     *
     * @param containerInfo the container to add the item to.
     * @param simulate      whether to simulate the action.
     * @return true if an item was added, false otherwise.
     */
    private boolean tryDepositItemsToContainer(@Nonnull ContainerInfo containerInfo, boolean simulate)
    {
        AbstractInventory inv = blockling.getEquipment();
        int remainingDepositAmount = getDepositAmount();

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
            if (itemConfigurationTypeProperty.getType() == ItemConfigurationTypeProperty.Type.SIMPLE && !hasItemToDeposit(item))
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
                    int inventoryAmount = countItemsToDeposit(item);
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

        return remainingDepositAmount < getDepositAmount();
    }

    @Override
    public boolean tryRecalcTarget()
    {
        if (!hasItemsToDeposit())
        {
            setTarget(null);
            setPathTargetPos(null, null);

            return false;
        }

        final int range = 8;

//        for (BlockPos testPos : BlockPos.betweenClosed(blockling.blockPosition().offset(-range, -range, -range), blockling.blockPosition().offset(range, range, range)))
//        {
//            TileEntity tileEntity = world.getBlockEntity(testPos);
//
//            if (isValidTarget(tileEntity))
//            {
//                setTarget(tileEntity);
//                setPathTargetPos(null, null);
//
//                return true;
//            }
//        }

        for (ContainerInfo containerInfo : containerInfos)
        {
            if (!isInRange(containerInfo.getBlockPos(), range * range))
            {
                continue;
            }

            if (isValidTarget(containerInfo))
            {
                setTarget(containerInfo);
                setPathTargetPos(null, null);

                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean recalcPath(boolean force)
    {
        setPathTargetPos(getTarget().getBlockPos(), path);

        return true;
    }

    @Override
    protected boolean isValidPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return true;
    }

    @Override
    public float getRangeSq()
    {
        return 3.5f;
    }

    @Override
    protected void checkForAndHandleInvalidTargets()
    {
        if (!isTargetValid())
        {
            markTargetBad();
        }
    }

    @Override
    public void markEntireTargetBad()
    {
        if (hasTarget())
        {
            markTargetBad();
        }
    }

    @Override
    public boolean isValidTarget(@Nullable ContainerInfo containerInfo)
    {
        if (containerInfo == null)
        {
            return false;
        }

        if (!containerInfo.isConfigured())
        {
            return false;
        }

        if (badTargets.contains(containerInfo))
        {
            return false;
        }

        if (!tryDepositItemsToContainer(containerInfo, true))
        {
            return false;
        }

        return true;
    }

    /**
     * Counts the number of items in the container.
     *
     * @param containerItemHandler the container to count the items in.
     * @param item the item to count.
     * @return the number of items in the container.
     */
    public int countItemsInContainer(@Nonnull IItemHandler containerItemHandler, @Nonnull Item item)
    {
        int count = 0;

        for (int slot = 0; slot < containerItemHandler.getSlots(); slot++)
        {
            ItemStack stack = containerItemHandler.getStackInSlot(slot);

            if (stack.getItem() == item)
            {
                count += stack.getCount();
            }
        }

        return count;
    }

    /**
     * Counts the number of items in the blockling's inventory.
     *
     * @param item the item to count.
     * @return the number of items in the blockling's inventory.
     */
    public int countItemsToDeposit(@Nonnull Item item)
    {
        return blockling.getEquipment().count(new ItemStack(item));
    }

    /**
     * @return true if the blockling has the given item in their inventory.
     */
    public boolean hasItemToDeposit(@Nonnull Item item)
    {
        return blockling.getEquipment().has(new ItemStack(item));
    }

    /**
     * @return true if the blockling has items in their inventory to deposit.
     */
    public boolean hasItemsToDeposit()
    {
        for (ItemInfo itemInfo : itemInfoSet)
        {
            if (hasItemToDeposit(itemInfo.getItem()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the number of items to deposit every second.
     */
    public int getDepositAmount()
    {
        return depositAmount;
    }

    /**
     * @param depositAmount the number of items to deposit every second.
     */
    public void setDepositAmount(int depositAmount)
    {
        this.depositAmount = depositAmount;
    }
}
