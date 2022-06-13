package com.willr27.blocklings.entity.blockling.attribute.attributes;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.Attribute;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.util.Version;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An attribute where the value is an enum.
 *
 * @param <T> the type of the enum of the attribute.
 */
public class EnumAttribute<T extends Enum<?>> extends Attribute<T>
{
    /**
     * The function used to convert from an ordinal back to the enum.
     */
    @Nonnull
    private final Function<Integer, T> ordinalConverter;

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param enumClass the class of the enum.
     * @param initialValue the initial value of the attribute.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public EnumAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, @Nonnull Class<T> enumClass, @Nonnull T initialValue, @Nullable Function<T, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        super(id, key, blockling, displayStringValueFunction, displayStringNameSupplier, isEnabled);
        this.value = initialValue;
        this.ordinalConverter = (ordinal) -> enumClass.getEnumConstants()[ordinal];
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT attributeTag)
    {
        attributeTag.putInt("value", value.ordinal());

        return super.writeToNBT(attributeTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT attributeTag, @Nonnull Version tagVersion)
    {
        super.readFromNBT(attributeTag, tagVersion);

        setValue(ordinalConverter.apply(attributeTag.getInt("value")), false);
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(value.ordinal());
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        setValue(ordinalConverter.apply(buf.readInt()), false);
    }

    @Override
    public void setValue(T value)
    {
        super.setValue(value);
    }

    @Override
    public void setValue(T value, boolean sync)
    {
        this.value = value;

        onValueChanged();

        if (sync)
        {
            new Message<T>(blockling, blockling.getStats().attributes.indexOf(this), value).sync();
        }
    }

    /**
     * The message used to sync the attribute value to the client/server.
     *
     * @param <T> the type of the enum of the attribute.
     */
    public static class Message<T extends Enum<?>> extends BlocklingMessage<Message<T>>
    {
        /**
         * The index of the attribute.
         */
        private int index;

        /**
         * The ordinal value of the enum.
         */
        private int ordinal;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public Message()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param value the enum value.
         */
        public Message(@Nonnull BlocklingEntity blockling, int index, @Nonnull T value)
        {
            super(blockling);
            this.index = index;
            this.ordinal = value.ordinal();
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            buf.writeInt(ordinal);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
            ordinal = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            EnumAttribute<T> attribute = (EnumAttribute<T>) blockling.getStats().attributes.get(index);
            attribute.setValue(attribute.ordinalConverter.apply(ordinal), false);
        }
    }
}
