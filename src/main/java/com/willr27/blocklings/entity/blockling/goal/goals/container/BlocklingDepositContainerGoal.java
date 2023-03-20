package com.willr27.blocklings.entity.blockling.goal.goals.container;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.controls.config.ItemsSelectionControl;
import com.willr27.blocklings.client.gui.control.controls.panels.TabbedPanel;
import com.willr27.blocklings.client.gui.control.event.events.ItemAddedEvent;
import com.willr27.blocklings.client.gui.control.event.events.ItemMovedEvent;
import com.willr27.blocklings.client.gui.control.event.events.ItemRemovedEvent;
import com.willr27.blocklings.client.gui.control.event.events.ReorderEvent;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.config.OrderedItemSet;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.inventory.AbstractInventory;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.Version;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
public class BlocklingDepositContainerGoal extends BlocklingContainerGoal implements OrderedItemSet.IOrderedItemSetProvider
{
    /**
     * The list of items to use as a whitelist/blacklist.
     */
    @Nonnull
    public final OrderedItemSet itemsSet;

    /**
     * @param taskId the taskId associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks the blockling tasks.
     */
    public BlocklingDepositContainerGoal(@Nonnull UUID taskId, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(taskId, blockling, tasks);

        itemsSet = new OrderedItemSet(this);
    }

    @Override
    public void writeToNBT(@Nonnull CompoundNBT taskTag)
    {
        super.writeToNBT(taskTag);

        taskTag.put("item_set", itemsSet.writeToNBT());
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT taskTag, @Nonnull Version tagVersion)
    {
        super.readFromNBT(taskTag, tagVersion);

        CompoundNBT itemSetTag = taskTag.getCompound("item_set");

        if (itemSetTag != null)
        {
            itemsSet.readFromNBT(itemSetTag, tagVersion);
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

        itemsSet.encode(buf);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        itemsSet.decode(buf);
    }

    @Override
    protected void tickGoal()
    {
        if (isInRangeOfPathTargetPos())
        {
            AbstractInventory inv = blockling.getEquipment();
            boolean depositedAnItem = false;

            for (Direction direction : Direction.values())
//            Direction direction = Direction.SOUTH;
            {
                IItemHandler itemHandler = getTarget().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);

//                for (Item item : itemsSet)
                Item item = itemsSet.getItems().get(0);
                {
                    // Skip any items that are not in the blockling's inventory.
                    if (!hasItemToDeposit(item))
                    {
                        continue;
                    }

                    int startingCount = inv.count(new ItemStack(item));
                    ItemStack stackToDeposit = new ItemStack(item, startingCount);

                    if (stackToDeposit.isEmpty())
                    {
                        continue;
                    }

                    // Loop through all slots or until the stack is empty.
                    for (int slot = 0; slot < itemHandler.getSlots() && !stackToDeposit.isEmpty(); slot++)
                    {
                        // Try insert as many items as possible and update the stack to be the remainder.
                        stackToDeposit = itemHandler.insertItem(slot, stackToDeposit, false);
                    }

                    // If the count has decreased then at least one item was deposited.
                    if (stackToDeposit.getCount() < startingCount)
                    {
                        inv.take(new ItemStack(item, startingCount - stackToDeposit.getCount()));

                        depositedAnItem = true;
                    }
                }
            }

            // If no items were deposited then try other targets before this one again.
            if (!depositedAnItem)
            {
                markTargetBad();
            }
        }
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

        for (BlockPos testPos : BlockPos.betweenClosed(blockling.blockPosition().offset(-range, -range, -range), blockling.blockPosition().offset(range, range, range)))
        {
            TileEntity tileEntity = world.getBlockEntity(testPos);

            if (isValidTarget(tileEntity))
            {
                setTarget(tileEntity);
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
    public boolean isValidTarget(@Nullable TileEntity tileEntity)
    {
        if (tileEntity == null)
        {
            return false;
        }

        if (!(tileEntity instanceof IInventory))
        {
            return false;
        }

        if (badTargets.contains(tileEntity))
        {
            return false;
        }

        return true;
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
        for (Item item : itemsSet)
        {
            if (hasItemToDeposit(item))
            {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    @Override
    public OrderedItemSet getItemSet()
    {
        return itemsSet;
    }

    @Nonnull
    @Override
    public void addConfigTabControls(@Nonnull TabbedPanel tabbedPanel)
    {
        super.addConfigTabControls(tabbedPanel);

        BaseControl itemsContainer = tabbedPanel.addTab(new BlocklingsTranslationTextComponent("config.items"));
        itemsContainer.setCanScrollVertically(true);

        ItemsSelectionControl itemsSelectionControl = new ItemsSelectionControl();
        itemsSelectionControl.setParent(itemsContainer);
        itemsSelectionControl.setMargins(5.0, 9.0, 5.0, 5.0);
        itemsSelectionControl.setItems(itemsSet.getItems());
        itemsSelectionControl.eventBus.subscribe((BaseControl c, ItemAddedEvent e) ->
        {
            itemsSet.add(e.item);
        });
        itemsSelectionControl.eventBus.subscribe((BaseControl c, ItemRemovedEvent e) ->
        {
            itemsSet.remove(e.item);
        });
        itemsSelectionControl.eventBus.subscribe((BaseControl c, ItemMovedEvent e) ->
        {
            if (e.insertBefore)
            {
                itemsSet.moveBefore(e.movedItem, e.closestItem);
            }
            else
            {
                itemsSet.moveAfter(e.movedItem, e.closestItem);
            }
        });
    }
}
