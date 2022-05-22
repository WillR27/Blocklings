package com.willr27.blocklings.task.config;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Used to provide a UI control and handle configuring part of a task.
 */
public abstract class Property
{
    /**
     * The associated task's goal.
     */
    @Nonnull
    public final BlocklingGoal goal;

    /**
     * The name of the property.
     */
    @Nonnull
    public final ITextComponent name;

    /**
     * @param goal the associated task's goal.
     * @param name the name of the property.
     */
    public Property(@Nonnull BlocklingGoal goal, @Nonnull ITextComponent name)
    {
        this.goal = goal;
        this.name = name;
    }

    /**
     * Writes the property to the given list.
     */
    public void writeToNBT(@Nonnull ListNBT list)
    {

    }

    /**
     * Reads the property from the given tag.
     */
    public void readFromNBT(@Nonnull CompoundNBT tag)
    {

    }

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {

    }

    /**
     * Decodes the message.
     *
     * @param buf the buffer to decode from.
     */
    public void decode(@Nonnull PacketBuffer buf)
    {

    }

    /**
     * @return a new instance of the control used to configure the property.
     */
    @Nonnull
    public abstract IControl createControl(@Nonnull IControl parent);

    /**
     * Used to sync properties between the client and server.
     */
    public static abstract class TaskPropertyMessage<T extends Property, M extends BlocklingMessage<M>> extends BlocklingMessage<M>
    {
        /**
         * The property (could be null on the receiving end if the client and server are no synced).
         */
        @Nullable
        protected T property;

        /**
         * The associated task id.
         */
        @Nullable
        private UUID taskId;

        /**
         * The index of the property.
         */
        private int propertyIndex;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public TaskPropertyMessage()
        {
            super(null);
        }

        /**
         * @param property the property.
         */
        public TaskPropertyMessage(@Nonnull T property)
        {
            super(property.goal.blockling);
            this.property = property;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeUUID(property.goal.getTask().id);
            buf.writeInt(property.goal.getTask().getGoal().properties.indexOf(property));
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            taskId = buf.readUUID();
            propertyIndex = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            Task task = blockling.getTasks().getTask(taskId);

            if (task != null && task.isConfigured())
            {
                property = (T) task.getGoal().properties.get(propertyIndex);
            }
        }
    }
}
