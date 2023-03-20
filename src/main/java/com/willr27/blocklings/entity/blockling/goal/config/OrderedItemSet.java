package com.willr27.blocklings.entity.blockling.goal.config;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.network.messages.GoalMessage;
import com.willr27.blocklings.util.Version;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An ordered set of items that can be associated with a {@link BlocklingGoal}. It syncs to the client/server
 * and saves to NBT.
 */
public class OrderedItemSet implements Iterable<Item>
{
    /**
     * The items in the set.
     */
    @Nonnull
    private List<Item> items = new ArrayList<>();

    /**
     * The associated item set provider.
     */
    @Nonnull
    private IOrderedItemSetProvider itemSetProvider;

    /**
     */
    public OrderedItemSet(@Nonnull IOrderedItemSetProvider itemSetProvider)
    {
        this.itemSetProvider = itemSetProvider;
    }

    /**
     * Writes the goal's data to a NBT tag.
     *
     * @return the tag.
     */
    public CompoundNBT writeToNBT()
    {
        CompoundNBT tag = new CompoundNBT();

        tag.putInt("size", items.size());

        for (int i = 0; i < items.size(); i++)
        {
            tag.putString("item" + i, items.get(i).getRegistryName().toString());
        }

        return tag;
    }

    /**
     * Reads the goal's data from the given tag.
     *
     * @param itemSetTag an item set tag.
     * @param version the version of the tag.
     */
    public void readFromNBT(@Nonnull CompoundNBT itemSetTag, @Nonnull Version version)
    {
        items.clear();

        int size = itemSetTag.getInt("size");

        for (int i = 0; i < size; i++)
        {
            items.add(Registry.ITEM.get(new ResourceLocation(itemSetTag.getString("item" + i))));
        }
    }

    /**
     * Writes the item set's data to the given buffer.
     *
     * @param buf the buffer to write to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeInt(items.size());

        for (Item item : items)
        {
            buf.writeResourceLocation(item.getRegistryName());
        }
    }

    /**
     * Reads the item set's data from the given buffer.
     *
     * @param buf the buffer to read from.
     */
    public void decode(@Nonnull PacketBuffer buf)
    {
        items.clear();

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            items.add(Registry.ITEM.get(buf.readResourceLocation()));
        }
    }

    /**
     * Adds an item to the set. Also syncs the item set to the client/server.
     *
     * @param item the item to add.
     */
    public void add(@Nonnull Item item)
    {
        add(item, true);
    }

    /**
     * Adds an item to the set.
     *
     * @param item the item to add.
     * @param sync whether to sync the item set to the client/server.
     */
    public void add(@Nonnull Item item, boolean sync)
    {
        if (items.contains(item))
        {
            return;
        }

        items.add(item);

        if (sync)
        {
            new AddItemMessage(itemSetProvider, item).sync();
        }
    }

    /**
     * Removes an item from the set. Also syncs the item set to the client/server.
     *
     * @param item the item to remove.
     */
    public void remove(@Nonnull Item item)
    {
        remove(item, true);
    }

    /**
     * Removes an item from the set.
     *
     * @param item the item to remove.
     * @param sync whether to sync the item set to the client/server.
     */
    public void remove(@Nonnull Item item, boolean sync)
    {
        items.remove(item);

        if (sync)
        {
            new RemoveItemMessage(itemSetProvider, item).sync();
        }
    }

    /**
     * Moves an item to the front of the set. Also syncs the item set to the client/server.
     *
     * @param item the item to move.
     * @param before the item to move the item before.
     */
    public void moveBefore(@Nonnull Item item, @Nonnull Item before)
    {
        moveBefore(item, before, true);
    }

    /**
     * Moves an item to the front of the set.
     *
     * @param item the item to move.
     * @param before the item to move the item before.
     * @param sync whether to sync the item set to the client/server.
     */
    public void moveBefore(@Nonnull Item item, @Nonnull Item before, boolean sync)
    {
        int itemIndex = items.indexOf(item);
        int beforeIndex = items.indexOf(before);

        if (itemIndex != -1 && beforeIndex != -1 && itemIndex != beforeIndex)
        {
            items.remove(itemIndex);
            items.add(beforeIndex - (itemIndex > beforeIndex ? 0 : 1), item);

            if (sync)
            {
                new MoveItemMessage(itemSetProvider, item, before, true).sync();
            }
        }
    }

    /**
     * Moves an item to the back of the set. Also syncs the item set to the client/server.
     *
     * @param item the item to move.
     * @param after the item to move the item after.
     */
    public void moveAfter(@Nonnull Item item, @Nonnull Item after)
    {
        moveAfter(item, after, true);
    }

    /**
     * Moves an item to the back of the set.
     *
     * @param item the item to move.
     * @param after the item to move the item after.
     * @param sync whether to sync the item set to the client/server.
     */
    public void moveAfter(@Nonnull Item item, @Nonnull Item after, boolean sync)
    {
        int itemIndex = items.indexOf(item);
        int afterIndex = items.indexOf(after);

        if (itemIndex != -1 && afterIndex != -1 && itemIndex != afterIndex)
        {
            items.remove(itemIndex);
            items.add(afterIndex + (itemIndex > afterIndex ? 1 : 0), item);

            if (sync)
            {
                new MoveItemMessage(itemSetProvider, item, after, false).sync();
            }
        }
    }

    /**
     * @return a copy of the item set as a list.
     */
    @Nonnull
    public List<Item> getItems()
    {
        return new ArrayList<>(items);
    }

    @Override
    public Iterator<Item> iterator()
    {
        return items.iterator();
    }

    /**
     * An interface used by goals to provide an item set.
     */
    public interface IOrderedItemSetProvider
    {
        /**
         * @return the associated task.
         */
        @Nonnull
        Task getTask();

        /**
         * @return the item set.
         */
        @Nonnull
        OrderedItemSet getItemSet();
    }

    /**
     * A base class for item set messages.
     */
    private static abstract class ItemMessage<T extends BlocklingMessage<T>> extends GoalMessage<T>
    {
        /**
         * The item.
         */
        protected Item item;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ItemMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param itemSetProvider the item set provider.
         */
        public ItemMessage(@Nonnull BlocklingEntity blockling, @Nonnull IOrderedItemSetProvider itemSetProvider, @Nonnull Item item)
        {
            super(blockling, itemSetProvider.getTask().id);
            this.item = item;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeItem(new ItemStack(item));
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            item = buf.readItem().getItem();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingGoal goal)
        {
            if (goal instanceof IOrderedItemSetProvider)
            {
                handle(player, blockling, ((IOrderedItemSetProvider) goal).getItemSet());
            }
            else
            {
                Blocklings.LOGGER.warn("Received item set message for goal that does not provide an item set: " + goal);
            }
        }

        /**
         * Handles the message.
         *
         * @param player the player.
         * @param blockling the blockling.
         * @param itemSet the item set.
         */
        protected abstract void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemSet itemSet);
    }

    /**
     * A message used to sync an item set when an item is added.
     */
    public static class AddItemMessage extends ItemMessage<AddItemMessage>
    {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public AddItemMessage()
        {
            super();
        }

        /**
         * @param itemSetProvider the item set provider.
         * @param item the item to add.
         */
        public AddItemMessage(@Nonnull IOrderedItemSetProvider itemSetProvider, @Nonnull Item item)
        {
            super(itemSetProvider.getTask().blockling, itemSetProvider, item);
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemSet itemSet)
        {
            itemSet.add(item, false);
        }
    }

    /**
     * A message used to sync an item set when an item is removed.
     */
    public static class RemoveItemMessage extends ItemMessage<RemoveItemMessage>
    {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public RemoveItemMessage()
        {
            super();
        }

        /**
         * @param itemSetProvider the item set provider.
         * @param item the item to remove.
         */
        public RemoveItemMessage(@Nonnull IOrderedItemSetProvider itemSetProvider, @Nonnull Item item)
        {
            super(itemSetProvider.getTask().blockling, itemSetProvider, item);
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemSet itemSet)
        {
            itemSet.remove(item, false);
        }
    }

    /**
     * A message used to sync an item set when an item is moved.
     */
    public static class MoveItemMessage extends ItemMessage<MoveItemMessage>
    {
        /**
         * The item to move the item before/after.
         */
        private Item closestItem;

        /**
         * Whether to move the item before or after the closest item.
         */
        private boolean before;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public MoveItemMessage()
        {
            super();
        }

        /**
         * @param itemSetProvider the item set provider.
         * @param item the item to add.
         * @param closestItem the item to move the item before/after.
         * @param before whether to move the item before or after the closest item.
         */
        public MoveItemMessage(@Nonnull IOrderedItemSetProvider itemSetProvider, @Nonnull Item item, @Nonnull Item closestItem, boolean before)
        {
            super(itemSetProvider.getTask().blockling, itemSetProvider, item);
            this.closestItem = closestItem;
            this.before = before;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeItem(new ItemStack(closestItem));
            buf.writeBoolean(before);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            closestItem = buf.readItem().getItem();
            before = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemSet itemSet)
        {
            if (before)
            {
                itemSet.moveBefore(item, closestItem, false);
            }
            else
            {
                itemSet.moveAfter(item, closestItem, false);
            }
        }
    }
}
