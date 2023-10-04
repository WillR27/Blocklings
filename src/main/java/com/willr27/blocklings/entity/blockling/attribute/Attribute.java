package com.willr27.blocklings.entity.blockling.attribute;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.util.BlocklingsTranslatableComponent;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An attribute is a wrapper around a value that provides extra functionality.
 * E.g. saving/loading, syncing between client/server, creating display strings and executing callbacks when the value changes.
 *
 * @param <T> the type of the value of the attribute.
 */
public abstract class Attribute<T> implements IReadWriteNBT
{
    /**
     * The id of the attribute.
     */
    @Nonnull
    public final UUID id;

    /**
     * The key used to identify the attribute (for things like translation text components).
     */
    @Nonnull
    public final String key;

    /**
     * The blockling the attribute applies to.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The world the associated blockling is in.
     */
    @Nonnull
    public final Level world;

    /**
     * The function used to provide the string representation of the value.
     */
    @Nonnull
    public final Function<T, String> displayStringValueFunction;

    /**
     * The supplier used to provide the string representation of display name.
     */
    @Nonnull
    public final Supplier<String> displayStringNameSupplier;

    /**
     * The list of callbacks that are called when the value changes.
     */
    @Nonnull
    protected final List<Consumer<T>> updateCallbacks = new ArrayList<>();

    /**
     * Whether the attribute is currently enabled.
     */
    private boolean isEnabled = true;

    /**
     * The value of the attribute.
     */
    protected T value;

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public Attribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, boolean isEnabled)
    {
        this(id, key, blockling, null, null, isEnabled);
    }

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param displayStringValueFunction the supplier used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public Attribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, @Nullable Function<T, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.blockling = blockling;
        this.world = blockling.level;
        this.displayStringValueFunction = displayStringValueFunction == null ? (v) -> formatValue("%.1f") : displayStringValueFunction;
        this.displayStringNameSupplier = displayStringNameSupplier == null ? () -> createTranslation("name").getString() : displayStringNameSupplier;
        this.isEnabled = isEnabled;
    }

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag attributeTag)
    {
        attributeTag.putBoolean("is_enabled", isEnabled);

        return attributeTag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag attributeTag, @Nonnull Version tagVersion)
    {
        isEnabled = attributeTag.getBoolean("is_enabled");
    }

    /**
     * Writes the attribute data to the given buffer.
     *
     * @param buf the buffer to write to.
     */
    public void encode(@Nonnull FriendlyByteBuf buf)
    {
        buf.writeBoolean(isEnabled);
    }

    /**
     * Reads the attribute data from the given buffer.
     *
     * @param buf the buffer to read from.
     */
    public void decode(@Nonnull FriendlyByteBuf buf)
    {
        isEnabled = buf.readBoolean();
    }

    /**
     * @return the underlying attribute value.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * Sets the value to the given value.
     * Syncs to the client/server.
     *
     * @param value the new value.
     */
    protected void setValue(T value)
    {
        setValue(value, true);
    }

    /**
     * Sets the value to the given value.
     * Syncs to the client/server if sync is true.
     *
     * @param value the new value.
     * @param sync whether to sync to the client/server.
     */
    protected abstract void setValue(T value, boolean sync);


    /**
     * Called when the value of the attribute changes.
     */
    public void onValueChanged()
    {
        updateCallbacks.forEach(floatConsumer -> floatConsumer.accept(getValue()));
    }

    /**
     * Adds the given callback to the list of callbacks to call when the attribute value changes.
     *
     * @param callback the callback to add.
     */
    public void addUpdateCallback(@Nonnull Consumer<T> callback)
    {
        updateCallbacks.add(callback);
    }

    /**
     * @param format the format string.
     * @return the attribute value formatted into the given string.
     */
    @Nonnull
    public String formatValue(@Nonnull String format)
    {
        return String.format(format, getValue());
    }

    /**
     * @return true if the attribute is enabled.
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }

    /**
     * Sets whether the attribute is enabled.
     * Syncs to the client/server.
     *
     * @param isEnabled whether the attribute is enabled.
     */
    public void setIsEnabled(boolean isEnabled)
    {
        setIsEnabled(isEnabled, true);
    }

    /**
     * Sets whether the attribute is enabled.
     * Syncs to the client/server if sync is true.
     *
     * @param isEnabled whether the attribute is enabled.
     * @param sync whether to sync to the client/server.
     */
    public void setIsEnabled(boolean isEnabled, boolean sync)
    {
        this.isEnabled = isEnabled;

        onValueChanged();

        if (sync)
        {
            new IsEnabledMessage(blockling, blockling.getStats().attributes.indexOf(this), isEnabled).sync();
        }
    }

    /**
     * @return the function used to provide the string representation of the value.
     */
    @Nonnull
    public Function<T, String> getDisplayStringValueFunction()
    {
        return displayStringValueFunction;
    }

    /**
     * @return the supplier used to provide the string representation of display name.
     */
    @Nonnull
    public Supplier<String> getDisplayStringNameSupplier()
    {
        return displayStringNameSupplier;
    }

    /**
     * @param key the key to prefix to the attribute key.
     * @param objects the objects to be passed on to the string format.
     * @return a translation text component for the attribute.
     */
    @Nonnull
    public TranslatableComponent createTranslation(@Nonnull String key, @Nonnull Object... objects)
    {
        return new AttributeTranslatableComponent(this.key + "." + key, objects);
    }

    /**
     * Represents a translation text component for an attribute.
     */
    public static class AttributeTranslatableComponent extends BlocklingsTranslatableComponent
    {
        public AttributeTranslatableComponent(String key, Object... objects)
        {
            super("attribute." + key, objects);
        }
    }

    /**
     * The message used to sync whether an attribute is enabled.
     */
    public static class IsEnabledMessage extends BlocklingMessage<IsEnabledMessage>
    {
        /**
         * The index of the attribute.
         */
        private int index;

        /**
         * Whether the attribute is enabled.
         */
        private boolean isEnabled;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public IsEnabledMessage()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param isEnabled whether the attribute is enabled.
         */
        public IsEnabledMessage(@Nonnull BlocklingEntity blockling, int index, boolean isEnabled)
        {
            super(blockling);
            this.index = index;
            this.isEnabled = isEnabled;
        }

        @Override
        public void encode(@Nonnull FriendlyByteBuf buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            buf.writeBoolean(isEnabled);
        }

        @Override
        public void decode(@Nonnull FriendlyByteBuf buf)
        {
            super.decode(buf);

            index = buf.readInt();
            isEnabled = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull Player player, @Nonnull BlocklingEntity blockling)
        {
            blockling.getStats().attributes.get(index).setIsEnabled(isEnabled, false);
        }
    }

    /**
     * The message used to sync the attribute value to the client/server.
     */
    public static abstract class ValueMessage<T, M extends BlocklingMessage<M>> extends BlocklingMessage<M>
    {
        /**
         * The index of the attribute.
         */
        protected int index;

        /**
         * The value of the attribute.
         */
        protected T value;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ValueMessage()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param value the value of the attribute.
         */
        public ValueMessage(@Nonnull BlocklingEntity blockling, int index, T value)
        {
            super(blockling);
            this.index = index;
            this.value = value;
        }

        @Override
        public void encode(@Nonnull FriendlyByteBuf buf)
        {
            super.encode(buf);

            buf.writeInt(index);

            encodeValue(buf);
        }

        /**
         * Writes the value to the given buffer.
         *
         * @param buf the buffer to write to.
         */
        protected abstract void encodeValue(@Nonnull FriendlyByteBuf buf);

        @Override
        public void decode(@Nonnull FriendlyByteBuf buf)
        {
            super.decode(buf);

            index = buf.readInt();

            decodeValue(buf);
        }

        /**
         * Reads the value from the given buffer.
         *
         * @param buf the buffer to read from.
         */
        protected abstract void decodeValue(@Nonnull FriendlyByteBuf buf);

        @Override
        protected void handle(@Nonnull Player player, @Nonnull BlocklingEntity blockling)
        {
            ((Attribute<T>) blockling.getStats().attributes.get(index)).setValue(value, false);
        }
    }
}
