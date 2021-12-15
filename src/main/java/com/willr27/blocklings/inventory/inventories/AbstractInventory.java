package com.willr27.blocklings.inventory.inventories;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

public abstract class AbstractInventory implements IInventory
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

    public void writeToNBT(CompoundNBT c)
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

        c.put("equipment_inv", list);
    }

    public void readFromNBT(CompoundNBT c)
    {
        ListNBT list = (ListNBT) c.get("equipment_inv");

        for (int i = 0; i < list.size(); i++)
        {
            CompoundNBT stackTag = list.getCompound(i);

            int slot = stackTag.getInt("slot");
            ItemStack stack = ItemStack.of(stackTag);

            stacks[slot] = stack;
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
            stacks[i] = buf.readItem();
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

    public ItemStack addItem(ItemStack stack, int slot)
    {
        ItemStack slotStack = getItem(slot);
        if (ItemStack.isSame(stack, slotStack))
        {
            int amountToAdd = stack.getCount();
            amountToAdd = Math.min(amountToAdd, slotStack.getMaxStackSize() - slotStack.getCount());
            stack.shrink(amountToAdd);
            slotStack.grow(amountToAdd);
            setItem(slot, slotStack);
        }
        else
        {
            setItem(slot, stack.copy());
            stack.shrink(stack.getCount());
        }

        return stack;
    }

    public ItemStack addItem(ItemStack stack)
    {
        int maxStackSize = stack.getMaxStackSize();

        for (int i = 0; i < invSize && !stack.isEmpty(); i++)
        {
            ItemStack slotStack = getItem(i);

            if (ItemStack.isSame(stack, slotStack))
            {
                int amountToAdd = stack.getCount();
                amountToAdd = Math.min(amountToAdd, maxStackSize - slotStack.getCount());
                stack.shrink(amountToAdd);
                slotStack.grow(amountToAdd);
                setItem(i, slotStack);
            }
        }

        for (int i = 0; i < invSize && !stack.isEmpty(); i++)
        {
            ItemStack slotStack = getItem(i);

            if (slotStack.isEmpty())
            {
                setItem(i, stack.copy());
                stack.setCount(0);

                break;
            }
        }

        return stack;
    }
}
