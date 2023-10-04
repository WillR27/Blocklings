package com.willr27.blocklings.entity.blockling.task.config;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
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
    public final Component name;

    /**
     * The description of the property.
     */
    @Nonnull
    public final Component desc;

    /**
     * Whether the property is enabled.
     */
    private boolean isEnabled = true;

    /**
     * @param id the id of the property (used for syncing between serialising\deserialising).
     * @param goal the associated task's goal.
     * @param name the name of the property.
     * @param desc the description of the property.
     */
    public Property(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull Component name, @Nonnull Component desc)
    {
        this.id = UUID.fromString(id);
        this.goal = goal;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag propertyTag)
    {
        propertyTag.putUUID("id", id);
        propertyTag.putBoolean("is_enabled", isEnabled);

        return propertyTag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag propertyTag, @Nonnull Version tagVersion)
    {
        setEnabled(propertyTag.getBoolean("is_enabled"), false);
    }

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull FriendlyByteBuf buf)
    {
        buf.writeBoolean(isEnabled);
    }

    /**
     * Decodes the message.
     *
     * @param buf the buffer to decode from.
     */
    public void decode(@Nonnull FriendlyByteBuf buf)
    {
        setEnabled(buf.readBoolean(), false);
    }

    /**
     * @return whether the property is enabled.
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }

    /**
     * Sets whether the property is enabled and syncs to the client/server.
     *
     * @param enabled whether the property is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }

    /**
     * Sets whether the property is enabled.
     *
     * @param enabled whether the property is enabled.
     * @param sync whether to sync to the client/server.
     */
    public void setEnabled(boolean enabled, boolean sync)
    {
        isEnabled = enabled;

        if (sync)
        {
            new TaskPropertyMessage(this).sync();
        }
    }

    /**
     * @return a new instance of the control used to configure the property.
     */
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public abstract BaseControl createControl();

    /**
     * Used to sync properties between the client and server.
     */
    public static class TaskPropertyMessage extends BlocklingMessage<TaskPropertyMessage>
    {
        /**
         * The remaining buffer used to pass to the property to decode.
         */
        @Nullable
        private FriendlyByteBuf buf;

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
        public void encode(@Nonnull FriendlyByteBuf buf)
        {
            super.encode(buf);

            buf.writeUUID(property.goal.getTask().id);
            buf.writeInt(property.goal.getTask().getGoal().properties.indexOf(property));

            property.encode(buf);
        }

        @Override
        public void decode(@Nonnull FriendlyByteBuf buf)
        {
            super.decode(buf);

            taskId = buf.readUUID();
            propertyIndex = buf.readInt();

            this.buf = buf;
        }

        @Override
        protected void handle(@Nonnull Player player, @Nonnull BlocklingEntity blockling)
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
