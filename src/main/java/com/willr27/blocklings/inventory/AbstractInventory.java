package com.willr27.blocklings.inventory;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.Version;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class AbstractInventory implements IInventory, IReadWriteNBT
{
    public final int invSize;

    protected BlocklingEntity blockling;
    protected World world;

    protected ItemStack[] stacks;
    protected ItemStack[] stacksCopy;

    private boolean dirty = false;

    public AbstractInventory(BlocklingEntity blockling, int invSize)
    {
        this.blockling = blockling;
        this.world = blockling.level;
        this.invSize = invSize;

        stacks = new ItemStack[invSize];
        stacksCopy = new ItemStack[invSize];

        clearContent();

        for (int i = 0; i < getContainerSize(); i++)
        {
            stacksCopy[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT equipmentInvTag)
    {
        ListNBT list = new ListNBT();

        for (int i = 0; i < getContainerSize(); i++)
        {
            ItemStack stack = stacks[i];

            if (stack.isEmpty())
            {
                continue;
            }

            CompoundNBT stackTag = new CompoundNBT();

            stackTag.putInt("slot", i);
            stack.save(stackTag);

            list.add(stackTag);
        }

        equipmentInvTag.put("slots", list);

        return equipmentInvTag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT invTag, @Nonnull Version tagVersion)
    {
        ListNBT list = (ListNBT) invTag.get("slots");

        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                CompoundNBT stackTag = list.getCompound(i);

                int slot = stackTag.getInt("slot");
                ItemStack stack = ItemStack.of(stackTag);

                if (slot < stacks.length)
                {
                    setItem(slot, stack);
                }
            }
        }
    }

    public void encode(PacketBuffer buf)
    {
        for (int i = 0; i < getContainerSize(); i++)
        {
            buf.writeItemStack(stacks[i], false);
        }
    }

    public void decode(PacketBuffer buf)
    {
        for (int i = 0; i < getContainerSize(); i++)
        {
            setItem(i, buf.readItem());
        }
    }

    @Override
    public void setChanged()
    {

    }

    @Override
    public int getContainerSize()
    {
        return invSize;
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < getContainerSize(); i++)
        {
            if (!getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index)
    {
        return stacks[index];
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        ItemStack stack = getItem(index);
        ItemStack copy = stack.copy();
        stack.shrink(count);
        setItem(index, stack);
        copy.setCount(count);
        return copy;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        ItemStack stack = getItem(index);
        stacks[index] = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        stacks[index] = stack;
    }

    public void swapItems(int slot1, int slot2)
    {
        ItemStack stack1 = getItem(slot1);
        setItem(slot1, getItem(slot2));
        setItem(slot2, stack1);
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        for (int i = 0; i < getContainerSize(); i++)
        {
            removeItemNoUpdate(i);
        }
    }

    public int find(Item item)
    {
        return find(item, 0, getContainerSize() - 1);
    }

    public int find(Item item, int startIndex, int endIndex)
    {
        for (int i = startIndex; i < endIndex + 1; i++)
        {
            if (getItem(i).getItem() == item)
            {
                return i;
            }
        }

        return -1;
    }

    public boolean has(ItemStack stack)
    {
        return has(stack, 0, getContainerSize() - 1);
    }

    public boolean has(ItemStack stack, int startIndex, int endIndex)
    {
        int count = 0;

        for (int i = startIndex; i < endIndex + 1; i++)
        {
            ItemStack slotStack = getItem(i);

            if (ItemStack.isSame(slotStack, stack))
            {
                count += slotStack.getCount();

                if (count >= stack.getCount())
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Deprecated
    public boolean take(ItemStack stack)
    {
        return take(stack, 0, getContainerSize() - 1);
    }

    @Deprecated
    public boolean take(ItemStack stack, int startIndex, int endIndex)
    {
        if (!has(stack))
        {
            return false;
        }

        int remainder = stack.getCount();

        for (int i = startIndex; i < endIndex + 1; i++)
        {
            ItemStack slotStack = getItem(i);

            if (ItemStack.isSame(slotStack, stack))
            {
                int slotCount = slotStack.getCount();

                if (slotCount >= remainder)
                {
                    slotStack.shrink(remainder);

                    break;
                }
                else
                {
                    remainder -= slotCount;
                    slotStack.shrink(slotCount);
                }
            }
        }

        return true;
    }

    /**
     * Tries to take the given item stack from the given slot. Doesn't modify the given item stack.
     *
     * @param stack the item stack to take.
     * @param slot the slot to take from.
     * @param simulate whether to simulate the take.
     * @return the item stack that was taken.
     */
    @Nonnull
    public ItemStack takeItem(@Nonnull ItemStack stack, int slot, boolean simulate)
    {
        ItemStack slotStack = getItem(slot);

        if (ItemStack.isSame(slotStack, stack))
        {
            int count = Math.min(stack.getCount(), slotStack.getCount());

            if (!simulate)
            {
                slotStack.shrink(count);
            }

            ItemStack taken = stack.copy();
            taken.setCount(count);

            return taken;
        }

        return ItemStack.EMPTY;
    }

    /**
     * Tries to take the given item stack from the inventory. Doesn't modify the given item stack.
     *
     * @param stack the item stack to take.
     * @param simulate whether to simulate the take.
     * @return the item stack that was taken.
     */
    @Nonnull
    public ItemStack takeItem(@Nonnull ItemStack stack, boolean simulate)
    {
        ItemStack stackCopy = stack.copy();

        for (int i = getContainerSize() - 1; i >= 0 && !stackCopy.isEmpty(); i--)
        {
            stackCopy.shrink(takeItem(stackCopy, i, simulate).getCount());
        }

        stackCopy.setCount(stack.getCount() - stackCopy.getCount());

        return stackCopy;
    }

    /**
     * Counts the number of items in the inventory of the given item stack.
     *
     * @param stack      the item stack to count.
     * @return           the number of items in the inventory of the given item stack.
     */
    public int count(@Nonnull ItemStack stack)
    {
        return count(stack, 0, getContainerSize() - 1);
    }

    /**
     * Counts the number of items in the inventory of the given item stack.
     *
     * @param stack      the item stack to count.
     * @param startIndex the inclusive slot index to start looking from.
     * @param endIndex   the exclusive slot index to end looking at.
     * @return           the number of items in the inventory of the given item stack.
     */
    public int count(@Nonnull ItemStack stack, int startIndex, int endIndex)
    {
        int count = 0;

        for (int i = startIndex; i < endIndex + 1; i++)
        {
            ItemStack slotStack = getItem(i);

            if (ItemStack.isSame(slotStack, stack))
            {
                count += slotStack.getCount();
            }
        }

        return count;
    }

    public boolean couldAddItem(ItemStack stack, int slot)
    {
        boolean couldAdd = true;

        ItemStack slotStack = getItem(slot);
        if (ItemStack.isSame(stack, slotStack))
        {
            couldAdd = slotStack.getCount() + stack.getCount() <= slotStack.getMaxStackSize();
        }

        return couldAdd;
    }

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @param slot the slot to add the item stack to.
     * @return the remainder of the item stack that could not be added.
     */
    @Nonnull
    public ItemStack addItem(@Nonnull ItemStack stackToAdd, int slot)
    {
        return addItem(stackToAdd, slot, false);
    }

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @param slot the slot to add the item stack to.
     * @param simulate whether to simulate the addition.
     * @return the remainder of the item stack that could not be added.
     */
    @Nonnull
    public ItemStack addItem(@Nonnull ItemStack stackToAdd, int slot, boolean simulate)
    {
        ItemStack slotStack = getItem(slot);
        ItemStack stackToAddCopy = stackToAdd.copy();

        if (ItemStack.isSame(stackToAddCopy, slotStack))
        {
            int amountToAdd = stackToAddCopy.getCount();
            amountToAdd = Math.min(amountToAdd, slotStack.getMaxStackSize() - slotStack.getCount());

            if (!simulate)
            {
                slotStack.grow(amountToAdd);
            }

            stackToAddCopy.shrink(amountToAdd);
        }
        else if (slotStack.isEmpty())
        {
            if (!simulate)
            {
                setItem(slot, stackToAddCopy.copy());
            }

            stackToAddCopy.shrink(stackToAddCopy.getCount());
        }

        return stackToAddCopy;
    }

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @return the remainder of the item stack that could not be added.
     */
    @Nonnull
    public ItemStack addItem(@Nonnull ItemStack stackToAdd)
    {
        return addItem(stackToAdd, false);
    }

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @param simulate whether to simulate the addition.
     * @return the remainder of the item stack that could not be added.
     */
    @Nonnull
    public ItemStack addItem(@Nonnull ItemStack stackToAdd, boolean simulate)
    {
        ItemStack stackToAddCopy = stackToAdd.copy();

        for (int i = 0; i < invSize && !stackToAddCopy.isEmpty(); i++)
        {
            if (ItemStack.isSame(stackToAddCopy, getItem(i)))
            {
                stackToAddCopy = addItem(stackToAddCopy, i, simulate);
            }
        }

        for (int i = 0; i < invSize && !stackToAddCopy.isEmpty(); i++)
        {
            if (getItem(i).isEmpty())
            {
                stackToAddCopy = addItem(stackToAddCopy, i, simulate);
            }
        }

        return stackToAddCopy;
    }
}
