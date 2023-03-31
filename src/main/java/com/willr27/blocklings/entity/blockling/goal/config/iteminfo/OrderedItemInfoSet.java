package com.willr27.blocklings.entity.blockling.goal.config.iteminfo;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.network.messages.GoalMessage;
import com.willr27.blocklings.util.Version;
import com.willr27.blocklings.util.event.EventBus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An ordered set of items that can be associated with a {@link BlocklingGoal}. It syncs to the client/server
 * and saves to NBT.
 */
public class OrderedItemInfoSet implements Iterable<ItemInfo>
{
    /**
     * The event bus for this item set.
     */
    @Nonnull
    public final EventBus<OrderedItemInfoSet> eventBus = new EventBus<>();

    /**
     * The items in the set.
     */
    @Nonnull
    private List<ItemInfo> itemInfos = new ArrayList<>();

    /**
     * The associated item set provider.
     */
    @Nonnull
    private IOrderedItemInfoSetProvider itemSetProvider;

    /**
     */
    public OrderedItemInfoSet(@Nonnull IOrderedItemInfoSetProvider itemSetProvider)
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

        tag.putInt("size", itemInfos.size());

        for (int i = 0; i < itemInfos.size(); i++)
        {
            tag.put("item_info" + i, itemInfos.get(i).writeToNBT());
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
        itemInfos.clear();

        int size = itemSetTag.getInt("size");

        for (int i = 0; i < size; i++)
        {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.readFromNBT(itemSetTag.getCompound("item_info" + i), version);
            itemInfos.add(itemInfo);
        }
    }

    /**
     * Writes the item set's data to the given buffer.
     *
     * @param buf the buffer to write to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeInt(itemInfos.size());

        for (ItemInfo itemInfo : itemInfos)
        {
            itemInfo.encode(buf);
        }
    }

    /**
     * Reads the item set's data from the given buffer.
     *
     * @param buf the buffer to read from.
     */
    public void decode(@Nonnull PacketBuffer buf)
    {
        itemInfos.clear();

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.decode(buf);
            itemInfos.add(itemInfo);
        }
    }

    /**
     * Adds an item info to the set. Also syncs the item info to the client/server.
     *
     * @param itemInfo the item info to add.
     * @param addFirst whether to add the item info to the beginning of the list.
     */
    public void add(@Nonnull ItemInfo itemInfo, boolean addFirst)
    {
        add(itemInfo, addFirst, true);
    }

    /**
     * Adds an item info to the set.
     *
     * @param itemInfo the item info to add.
     * @param addFirst whether to add the item info to the beginning of the list.
     * @param sync whether to sync the item info to the client/server.
     */
    public void add(@Nonnull ItemInfo itemInfo, boolean addFirst, boolean sync)
    {
        if (itemInfos.contains(itemInfo))
        {
            return;
        }

        itemInfos.add(addFirst ? 0 : itemInfos.size(), itemInfo);

        if (sync)
        {
            new AddItemInfoInfoMessage(itemSetProvider, itemInfo, addFirst).sync();
        }

        eventBus.post(this, new ItemInfoAddedEvent(itemInfo, addFirst));
    }

    /**
     * Removes an item info from the set. Also syncs the item to the client/server.
     *
     * @param itemInfo the item info to remove.
     */
    public void remove(@Nonnull ItemInfo itemInfo)
    {
        remove(itemInfo, true);
    }

    /**
     * Removes an item info from the set.
     *
     * @param itemInfo the item info to remove.
     * @param sync whether to sync the item to the client/server.
     */
    public void remove(@Nonnull ItemInfo itemInfo, boolean sync)
    {
        itemInfos.remove(itemInfo);

        if (sync)
        {
            new RemoveItemInfoInfoMessage(itemSetProvider, itemInfo).sync();
        }

        eventBus.post(this, new ItemInfoRemovedEvent(itemInfo));
    }

    /**
     * Moves an item info to the front of the set. Also syncs the item info set to the client/server.
     *
     * @param itemInfo the item info to move.
     * @param before the item info to move the item info before.
     */
    public void moveBefore(@Nonnull ItemInfo itemInfo, @Nonnull ItemInfo before)
    {
        moveBefore(itemInfo, before, true);
    }

    /**
     * Moves an item info to the front of the set.
     *
     * @param itemInfo the item info to move.
     * @param before the item info to move the item info before.
     * @param sync whether to sync the item info set to the client/server.
     */
    public void moveBefore(@Nonnull ItemInfo itemInfo, @Nonnull ItemInfo before, boolean sync)
    {
        int itemIndex = itemInfos.indexOf(itemInfo);
        int beforeIndex = itemInfos.indexOf(before);

        if (itemIndex != -1 && beforeIndex != -1 && itemIndex != beforeIndex)
        {
            itemInfos.remove(itemIndex);
            itemInfos.add(beforeIndex - (itemIndex > beforeIndex ? 0 : 1), itemInfo);

            if (sync)
            {
                new MoveItemInfoInfoMessage(itemSetProvider, itemInfo, before, true).sync();
            }

            eventBus.post(this, new ItemInfoMovedEvent(itemInfo, before, true));
        }
    }

    /**
     * Moves an item info to the back of the set. Also syncs the item info set to the client/server.
     *
     * @param itemInfo the item info to move.
     * @param after the item info to move the item info after.
     */
    public void moveAfter(@Nonnull ItemInfo itemInfo, @Nonnull ItemInfo after)
    {
        moveAfter(itemInfo, after, true);
    }

    /**
     * Moves an item info to the back of the set.
     *
     * @param itemInfo the item info to move.
     * @param after the item info to move the item info after.
     * @param sync whether to sync the item info set to the client/server.
     */
    public void moveAfter(@Nonnull ItemInfo itemInfo, @Nonnull ItemInfo after, boolean sync)
    {
        int itemIndex = itemInfos.indexOf(itemInfo);
        int afterIndex = itemInfos.indexOf(after);

        if (itemIndex != -1 && afterIndex != -1 && itemIndex != afterIndex)
        {
            itemInfos.remove(itemIndex);
            itemInfos.add(afterIndex + (itemIndex > afterIndex ? 1 : 0), itemInfo);

            if (sync)
            {
                new MoveItemInfoInfoMessage(itemSetProvider, itemInfo, after, false).sync();
            }

            eventBus.post(this, new ItemInfoMovedEvent(itemInfo, after, false));
        }
    }

    /**
     * Sets an item info at the given index. Also syncs the item info set to the client/server.
     *
     * @param index the index to set the item info at.
     * @param itemInfo the item info to set.
     */
    public void set(int index, @Nonnull ItemInfo itemInfo)
    {
        set(index, itemInfo, true);
    }

    /**
     * Sets an item info at the given index.
     *
     * @param index the index to set the item info at.
     * @param itemInfo the item info to set.
     * @param sync whether to sync the item info set to the client/server.
     */
    public void set(int index, @Nonnull ItemInfo itemInfo, boolean sync)
    {
        if (!itemInfos.contains(itemInfo))
        {
            return;
        }

        ItemInfo oldItemInfo = itemInfos.get(index);

        itemInfos.set(index, itemInfo);

        if (sync)
        {
            new SetItemInfoInfoMessage(itemSetProvider, itemInfo, index).sync();
        }

        eventBus.post(this, new ItemInfoSetEvent(itemInfo, oldItemInfo, index));
    }

    /**
     * @return a copy of the item info set as a list.
     */
    @Nonnull
    public List<ItemInfo> getItemInfos()
    {
        return new ArrayList<>(itemInfos);
    }

    @Override
    public Iterator<ItemInfo> iterator()
    {
        return itemInfos.iterator();
    }

    /**
     * An interface used by goals to provide an item info set.
     */
    public interface IOrderedItemInfoSetProvider
    {
        /**
         * @return the associated task.
         */
        @Nonnull
        Task getTask();

        /**
         * @return the item info set.
         */
        @Nonnull
        OrderedItemInfoSet getItemSet();
    }

    /**
     * A base class for item info set messages.
     */
    private static abstract class ItemInfoMessage<T extends BlocklingMessage<T>> extends GoalMessage<T, IOrderedItemInfoSetProvider>
    {
        /**
         * The item info.
         */
        protected ItemInfo itemInfo;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ItemInfoMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param itemSetProvider the item set provider.
         * @param itemInfo the item info.
         */
        public ItemInfoMessage(@Nonnull BlocklingEntity blockling, @Nonnull IOrderedItemInfoSetProvider itemSetProvider, @Nonnull ItemInfo itemInfo)
        {
            super(blockling, itemSetProvider.getTask().id);
            this.itemInfo = itemInfo;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            itemInfo.encode(buf);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            itemInfo = new ItemInfo();
            itemInfo.decode(buf);
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull IOrderedItemInfoSetProvider goal)
        {
            handle(player, blockling, ((IOrderedItemInfoSetProvider) goal).getItemSet());
        }

        /**
         * Handles the message.
         *
         * @param player the player.
         * @param blockling the blockling.
         * @param itemInfoSet the item info set.
         */
        protected abstract void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemInfoSet itemInfoSet);
    }

    /**
     * A message used to sync an item info set when an item is added.
     */
    public static class AddItemInfoInfoMessage extends ItemInfoMessage<AddItemInfoInfoMessage>
    {
        /**
         * Whether to add the item info to the front of the set.
         */
        boolean addFirst;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public AddItemInfoInfoMessage()
        {
            super();
        }

        /**
         * @param itemInfoSetProvider the item info set provider.
         * @param itemInfo the item info to add.
         * @param addFirst whether to add the item info to the front of the set.
         */
        public AddItemInfoInfoMessage(@Nonnull IOrderedItemInfoSetProvider itemInfoSetProvider, @Nonnull ItemInfo itemInfo, boolean addFirst)
        {
            super(itemInfoSetProvider.getTask().blockling, itemInfoSetProvider, itemInfo);
            this.addFirst = addFirst;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeBoolean(addFirst);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            addFirst = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemInfoSet itemInfoSet)
        {
            itemInfoSet.add(itemInfo, addFirst, false);
        }
    }

    /**
     * A message used to sync an item info set when an item is removed.
     */
    public static class RemoveItemInfoInfoMessage extends ItemInfoMessage<RemoveItemInfoInfoMessage>
    {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public RemoveItemInfoInfoMessage()
        {
            super();
        }

        /**
         * @param itemInfoSetProvider the item info set provider.
         * @param itemInfo the item info to remove.
         */
        public RemoveItemInfoInfoMessage(@Nonnull IOrderedItemInfoSetProvider itemInfoSetProvider, @Nonnull ItemInfo itemInfo)
        {
            super(itemInfoSetProvider.getTask().blockling, itemInfoSetProvider, itemInfo);
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemInfoSet itemInfoSet)
        {
            itemInfoSet.remove(itemInfo, false);
        }
    }

    /**
     * A message used to sync an item info set when an item is moved.
     */
    public static class MoveItemInfoInfoMessage extends ItemInfoMessage<MoveItemInfoInfoMessage>
    {
        /**
         * The item info to move the item info before/after.
         */
        private ItemInfo closestItemInfo;

        /**
         * Whether to move the item info before or after the closest item info.
         */
        private boolean before;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public MoveItemInfoInfoMessage()
        {
            super();
        }

        /**
         * @param itemInfoSetProvider the item info set provider.
         * @param itemInfo the item info to move.
         * @param closestItemInfo the item info to move the item info before/after.
         * @param before whether to move the item info before or after the closest item info.
         */
        public MoveItemInfoInfoMessage(@Nonnull IOrderedItemInfoSetProvider itemInfoSetProvider, @Nonnull ItemInfo itemInfo, @Nonnull ItemInfo closestItemInfo, boolean before)
        {
            super(itemInfoSetProvider.getTask().blockling, itemInfoSetProvider, itemInfo);
            this.closestItemInfo = closestItemInfo;
            this.before = before;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            closestItemInfo.encode(buf);
            buf.writeBoolean(before);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            closestItemInfo = new ItemInfo();
            closestItemInfo.decode(buf);
            before = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemInfoSet itemInfoSet)
        {
            if (before)
            {
                itemInfoSet.moveBefore(itemInfo, closestItemInfo, false);
            }
            else
            {
                itemInfoSet.moveAfter(itemInfo, closestItemInfo, false);
            }
        }
    }
    /**
     * A message used to sync an item info set when an item info is set.
     */
    public static class SetItemInfoInfoMessage extends ItemInfoMessage<SetItemInfoInfoMessage>
    {
        /**
         * The index of the item info to set.
         */
        private int index;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public SetItemInfoInfoMessage()
        {
            super();
        }

        /**
         * @param itemInfoSetProvider the item info set provider.
         * @param itemInfo the item info to move.
         * @param index the index of the item info to set.
         */
        public SetItemInfoInfoMessage(@Nonnull IOrderedItemInfoSetProvider itemInfoSetProvider, @Nonnull ItemInfo itemInfo, int index)
        {
            super(itemInfoSetProvider.getTask().blockling, itemInfoSetProvider, itemInfo);
            this.index = index;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull OrderedItemInfoSet itemInfoSet)
        {
            itemInfoSet.set(index, itemInfo, false);
        }
    }
}
