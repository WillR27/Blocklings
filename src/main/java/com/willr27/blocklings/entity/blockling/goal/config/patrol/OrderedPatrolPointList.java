package com.willr27.blocklings.entity.blockling.goal.config.patrol;

import com.willr27.blocklings.capabilities.BlockSelectCapability;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.network.messages.GoalMessage;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.ISyncable;
import com.willr27.blocklings.util.Version;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * A class used to store a list of patrol points in a specific order.
 */
public class OrderedPatrolPointList implements Iterable<PatrolPoint>, IReadWriteNBT, ISyncable
{
    /**
     * The event handler for when a {@link PatrolPoint} is removed from the list.
     */
    @Nonnull
    public final EventHandler<PatrolPointRemovedEvent> onPatrolPointRemoved = new EventHandler<>();

    /**
     * The associated provider.
     */
    @Nonnull
    public final IOrderedPatrolPointListProvider provider;

    /**
     * The list of patrol points.
     */
    @Nonnull
    private final ArrayList<PatrolPoint> patrolPoints = new ArrayList<>();

    /**
     * @param provider the associated provider.
     */
    public OrderedPatrolPointList(@Nonnull IOrderedPatrolPointListProvider provider)
    {
        this.provider = provider;
    }

    @Nonnull
    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT tag)
    {
        tag.putInt("size", patrolPoints.size());

        for (int i = 0; i < patrolPoints.size(); i++)
        {
            tag.put("point_" + i, patrolPoints.get(i).writeToNBT(new CompoundNBT()));
        }

        return tag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT tag, @Nonnull Version tagVersion)
    {
        patrolPoints.clear();

        int size = tag.getInt("size");

        for (int i = 0; i < size; i++)
        {
            PatrolPoint point = new PatrolPoint();
            point.readFromNBT(tag.getCompound("point_" + i), tagVersion);
            add(point);
        }
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeInt(patrolPoints.size());

        for (PatrolPoint point : patrolPoints)
        {
            point.encode(buf);
        }
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        patrolPoints.clear();

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            PatrolPoint point = new PatrolPoint();
            point.decode(buf);
            add(point);
        }
    }

    /**
     * @return the size of the list.
     */
    public int size()
    {
        return patrolPoints.size();
    }

    /**
     * @return whether the list is empty.
     */
    public boolean isEmpty()
    {
        return patrolPoints.isEmpty();
    }

    /**
     * @return the list as a stream.
     */
    public Stream<PatrolPoint> stream()
    {
        return patrolPoints.stream();
    }

    /**
     * @param point the point to get the index of.
     * @return the index of the given patrol point.
     */
    public int indexOf(@Nonnull PatrolPoint point)
    {
        return patrolPoints.indexOf(point);
    }

    /**
     * @param point the point to check if it's in the list.
     * @return whether the list contains the given patrol point.
     */
    public boolean contains(@Nullable PatrolPoint point)
    {
        return patrolPoints.contains(point);
    }

    /**
     * Gets the {@link PatrolPoint} at the given index.
     *
     * @param index the index of the patrol point to get.
     * @return the patrol point at the given index.
     */
    @Nonnull
    public PatrolPoint get(int index)
    {
        return patrolPoints.get(index);
    }

    /**
     * Sets the {@link PatrolPoint} at the given index. Also syncs to the client/server.
     *
     * @param index the index of the patrol point to set.
     * @param patrolPoint the patrol point to set.
     */
    public void set(int index, @Nonnull PatrolPoint patrolPoint)
    {
        set(index, patrolPoint, true);
    }

    /**
     * Sets the {@link PatrolPoint} at the given index. Doesn't change the reference of the patrol point,
     * just the values.
     *
     * @param index the index of the patrol point to set.
     * @param patrolPoint the patrol point to set.
     * @param sync whether to sync to the client/server.
     */
    public void set(int index, @Nonnull PatrolPoint patrolPoint, boolean sync)
    {
        get(index).set(patrolPoint);

        if (sync)
        {
            new UpdatePatrolPointMessage(provider, index, patrolPoint).sync();
        }
    }

    /**
     * Adds a patrol point to the list. Also syncs to the client/server.
     *
     * @param patrolPoint the patrol point to add.
     */
    public void add(@Nonnull PatrolPoint patrolPoint)
    {
        add(patrolPoint, false, true);
    }

    /**
     * Adds a patrol point to the list.
     *
     * @param patrolPoint the patrol point to add.
     * @param configureInWorld whether to configure the patrol point in the world.
     * @param sync whether to sync to the client/server.
     */
    public void add(@Nonnull PatrolPoint patrolPoint, boolean configureInWorld, boolean sync)
    {
        patrolPoints.add(patrolPoint);
        patrolPoint.setPatrolPointList(this);

        if (configureInWorld)
        {
            PlayerEntity player = (PlayerEntity) provider.getBlockling().getOwner();
            player.getCapability(BlockSelectCapability.CAPABILITY).ifPresent(cap ->
            {
                cap.isSelecting = true;
            });
        }

        if (sync)
        {
            new AddPatrolPointMessage(provider, patrolPoint, configureInWorld).sync();
        }
    }

    /**
     * Removes a patrol point from the list. Also syncs to the client/server.
     *
     * @param index the index of the patrol point to remove.
     */
    public void remove(int index)
    {
        remove(index, true);
    }

    /**
     * Removes a patrol point from the list.
     *
     * @param index the index of the patrol point to remove.
     * @param sync whether to sync to the client/server.
     */
    public void remove(int index, boolean sync)
    {
        if (index == -1)
        {
            return;
        }

        PatrolPoint removedPatrolPoint = get(index);
        PatrolPoint nextPatrolPoint = next(removedPatrolPoint);
        removedPatrolPoint.setPatrolPointList(null);
        patrolPoints.remove(index);

        if (sync)
        {
            new RemovePatrolPointMessage(provider, index).sync();
        }

        onPatrolPointRemoved.handle(new PatrolPointRemovedEvent(removedPatrolPoint, nextPatrolPoint));
    }

    /**
     * Moves a patrol point before/after the given patrol point. Also syncs to the client/server.
     *
     * @param index the index of the patrol point to move.
     * @param moveIndex the index of the patrol point to move the patrol point before/after.
     * @param before whether to move the patrol point before or after the given patrol point.
     */
    public void move(int index, int moveIndex, boolean before)
    {
        move(index, moveIndex, before, true);
    }

    /**
     * Moves a patrol point before/after the given patrol point.
     *
     * @param index the index of the patrol point to move.
     * @param moveIndex the index of the patrol point to move the patrol point before/after.
     * @param before whether to move the patrol point before or after the given patrol point.
     * @param sync whether to sync to the client/server.
     */
    public void move(int index, int moveIndex, boolean before, boolean sync)
    {
        if (index != -1 && moveIndex != -1 && index != moveIndex)
        {
            PatrolPoint patrolPoint = patrolPoints.get(index);
            patrolPoints.remove(index);
            patrolPoints.add(moveIndex + (index > moveIndex ? (before ? 0 : 1) : (before ? 1 : 0)), patrolPoint);

            if (sync)
            {
                new MovePatrolPointMessage(provider, index, moveIndex, before).sync();
            }
        }
    }

    /**
     * Called when the given patrol point has changed.
     *
     * @param patrolPoint the patrol point that has changed.
     */
    public void onDataChanged(@Nonnull PatrolPoint patrolPoint)
    {
        new UpdatePatrolPointMessage(provider, patrolPoints.indexOf(patrolPoint), patrolPoint).sync();
    }

    @Override
    public Iterator<PatrolPoint> iterator()
    {
        return patrolPoints.iterator();
    }

    /**
     * Given a patrol point, gets the next patrol point in the list. If the given patrol point is the last
     * patrol point in the list, then the first patrol point in the list is returned. If the given patrol
     * point is not in the list, then return null.
     *
     * @param patrolPoint the patrol point to get the next patrol point for.
     * @return the next patrol point in the list.
     */
    @Nullable
    public PatrolPoint next(@Nonnull PatrolPoint patrolPoint)
    {
        int index = patrolPoints.indexOf(patrolPoint);

        if (index == -1)
        {
            return null;
        }

        return patrolPoints.get((index + 1) % patrolPoints.size());
    }

    /**
     * Given a patrol point, gets the previous patrol point in the list. If the given patrol point is the first
     * patrol point in the list, then the last patrol point in the list is returned. If the given patrol point
     * is not in the list, then return null.
     *
     * @param patrolPoint the patrol point to get the previous patrol point for.
     * @return the previous patrol point in the list.
     */
    @Nullable
    public PatrolPoint prev(@Nonnull PatrolPoint patrolPoint)
    {
        int index = patrolPoints.indexOf(patrolPoint);

        if (index == -1)
        {
            return null;
        }

        return patrolPoints.get((index - 1 + patrolPoints.size()) % patrolPoints.size());
    }

    /**
     * An interface used to provide an {@link OrderedPatrolPointList}.
     */
    public interface IOrderedPatrolPointListProvider
    {
        /**
         * Gets the associated {@link BlocklingEntity}.
         *
         * @return the associated {@link BlocklingEntity}.
         */
        @Nonnull
        BlocklingEntity getBlockling();

        /**
         * Gets the associated {@link Task}.
         *
         * @return the associated {@link Task}.
         */
        @Nonnull
        Task getTask();

        /**
         * Gets the {@link OrderedPatrolPointList}.
         *
         * @return the {@link OrderedPatrolPointList}.
         */
        @Nonnull
        OrderedPatrolPointList getOrderedPatrolPointList();
    }

    /**
     * A message used to sync the adding of a {@link PatrolPoint}.
     */
    public static class AddPatrolPointMessage extends GoalMessage<AddPatrolPointMessage, IOrderedPatrolPointListProvider>
    {
        /**
         * The patrol point.
         */
        private PatrolPoint patrolPoint;

        /**
         * Whether to configure the patrol point in the world.
         */
        private boolean configureInWorld;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public AddPatrolPointMessage()
        {
            super();
        }

        /**
         * @param provider the provider.
         * @param patrolPoint the patrol point.
         * @param configureInWorld whether to configure the patrol point in the world.
         */
        public AddPatrolPointMessage(@Nonnull IOrderedPatrolPointListProvider provider, @Nonnull PatrolPoint patrolPoint, boolean configureInWorld)
        {
            super(provider.getBlockling(), provider.getTask().id);
            this.patrolPoint = patrolPoint;
            this.configureInWorld = configureInWorld;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            patrolPoint.encode(buf);
            buf.writeBoolean(configureInWorld);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            patrolPoint = new PatrolPoint();
            patrolPoint.decode(buf);
            configureInWorld = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull IOrderedPatrolPointListProvider goal)
        {
            goal.getOrderedPatrolPointList().add(patrolPoint, configureInWorld, false);
        }
    }

    /**
     * A message used to sync the removal of a {@link PatrolPoint}.
     */
    public static class RemovePatrolPointMessage extends GoalMessage<RemovePatrolPointMessage, IOrderedPatrolPointListProvider>
    {
        /**
         * The index of the patrol point.
         */
        private int index;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public RemovePatrolPointMessage()
        {
            super();
        }

        /**
         * @param provider the provider.
         * @param index the patrol point index.
         */
        public RemovePatrolPointMessage(@Nonnull IOrderedPatrolPointListProvider provider, int index)
        {
            super(provider.getBlockling(), provider.getTask().id);
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
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull IOrderedPatrolPointListProvider goal)
        {
            goal.getOrderedPatrolPointList().remove(index, false);
        }
    }

    /**
     * A message used to sync the insertion of a {@link PatrolPoint}.
     */
    public static class MovePatrolPointMessage extends GoalMessage<MovePatrolPointMessage, IOrderedPatrolPointListProvider>
    {
        /**
         * The index of the point to insert.
         */
        private int index;

        /**
         * The index of patrol point to insert before/after.
         */
        private int moveIndex;

        /**
         * Whether to insert before or after.
         */
        private boolean before;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public MovePatrolPointMessage()
        {
            super();
        }

        /**
         * @param provider the provider.
         * @param index the index of the point to insert.
         * @param moveIndex the index of patrol point to insert before/after.
         * @param before whether to insert before or after.
         */
        public MovePatrolPointMessage(@Nonnull IOrderedPatrolPointListProvider provider, int index, int moveIndex, boolean before)
        {
            super(provider.getBlockling(), provider.getTask().id);
            this.index = index;
            this.moveIndex = moveIndex;
            this.before = before;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            buf.writeInt(moveIndex);
            buf.writeBoolean(before);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
            moveIndex = buf.readInt();
            before = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull IOrderedPatrolPointListProvider goal)
        {
            goal.getOrderedPatrolPointList().move(index, moveIndex, before, false);
        }
    }

    /**
     * A message used to sync the data of a {@link PatrolPoint}.
     */
    public static class UpdatePatrolPointMessage extends GoalMessage<UpdatePatrolPointMessage, IOrderedPatrolPointListProvider>
    {
        /**
         * The index of the patrol point to update.
         */
        private int index;

        /**
         * The patrol point.
         */
        protected PatrolPoint patrolPoint;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public UpdatePatrolPointMessage()
        {
            super();
        }

        /**
         * @param provider the provider.
         * @param index the patrol point index.
         * @param patrolPoint the patrol point.
         */
        public UpdatePatrolPointMessage(@Nonnull IOrderedPatrolPointListProvider provider, int index, @Nonnull PatrolPoint patrolPoint)
        {
            super(provider.getBlockling(), provider.getTask().id);
            this.index = index;
            this.patrolPoint = patrolPoint;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            patrolPoint.encode(buf);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
            patrolPoint = new PatrolPoint();
            patrolPoint.decode(buf);
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull IOrderedPatrolPointListProvider goal)
        {
            goal.getOrderedPatrolPointList().set(index, patrolPoint, false);
        }
    }
}
