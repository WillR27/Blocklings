package com.willr27.blocklings.task.config;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.Version;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * Used to provide a UI control and handle configuring part of a task.
 */
public abstract class Property implements IReadWriteNBT
{
    /**
     * The id of the property (used for syncing between serialising\deserialising).
     */
    @Nonnull
    public final UUID id;

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
     * @param id the id of the property (used for syncing between serialising\deserialising).
     * @param goal the associated task's goal.
     * @param name the name of the property.
     */
    public Property(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull ITextComponent name)
    {
        this.id = UUID.fromString(id);
        this.goal = goal;
        this.name = name;
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT propertyTag)
    {
        propertyTag.putUUID("id", id);

        return propertyTag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT propertyTag, @Nonnull Version tagVersion)
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
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public abstract IControl createControl(@Nonnull IControl parent);

    /**
     * Used to sync properties between the client and server.
     */
    public static class TaskPropertyMessage extends BlocklingMessage<TaskPropertyMessage>
    {
        /**
         * The remaining buffer used to pass to the property to decode.
         */
        @Nullable
        private PacketBuffer buf;

        /**
         * The property (could be null on the receiving end if the client and server are no synced).
         */
        @Nullable
        protected Property property;

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
        public TaskPropertyMessage(@Nonnull Property property)
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

            property.encode(buf);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            taskId = buf.readUUID();
            propertyIndex = buf.readInt();

            this.buf = buf;
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            Task task = blockling.getTasks().getTask(taskId);

            if (task != null && task.isConfigured())
            {
                property = task.getGoal().properties.get(propertyIndex);
                property.decode(Objects.requireNonNull(buf));
            }
        }
    }
}
