package com.willr27.blocklings.attribute;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Attribute<T>
{
    public final UUID id;
    public final String key;
    public final BlocklingEntity blockling;
    public final World world;
    public final Supplier<String> displayStringValueSupplier;
    public final Supplier<String> displayStringNameSupplier;

    protected final List<Consumer<T>> updateCallbacks = new ArrayList<>();

    private boolean isEnabled = true;

    public Attribute(String id, String key, BlocklingEntity blockling)
    {
        this(id, key, blockling, null, null);
    }

    public Attribute(String id, String key, BlocklingEntity blockling, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.blockling = blockling;
        this.world = blockling.level;
        this.displayStringValueSupplier = displayStringValueSupplier == null ? () -> formatValue("%.1f") : displayStringValueSupplier;
        this.displayStringNameSupplier = displayStringNameSupplier == null ? () -> createTranslation("name").getString() : displayStringNameSupplier;
    }

    public void writeToNBT(CompoundNBT attributeTag)
    {
        attributeTag.putBoolean("is_enabled", isEnabled);
    }

    public void readFromNBT(CompoundNBT attributeTag)
    {
        isEnabled = attributeTag.getBoolean("is_enabled");
    }

    public void encode(PacketBuffer buf)
    {
        buf.writeBoolean(isEnabled);
    }

    public void decode(PacketBuffer buf)
    {
        isEnabled = buf.readBoolean();
    }

    public abstract T getValue();

    public void callUpdateCallbacks()
    {
        updateCallbacks.forEach(floatConsumer -> floatConsumer.accept(getValue()));
    }

    public void addUpdateCallback(Consumer<T> callback)
    {
        updateCallbacks.add(callback);
    }

    public String formatValue(String format)
    {
        return String.format(format, getValue());
    }

    public boolean isEnabled()
    {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled)
    {
        setIsEnabled(isEnabled, true);
    }

    public void setIsEnabled(boolean isEnabled, boolean sync)
    {
        this.isEnabled = isEnabled;

        if (sync)
        {
            new IsEnabledMessage(blockling, blockling.getStats().attributes.indexOf(this), isEnabled).sync();
        }
    }

    public TranslationTextComponent createTranslation(String key, Object... objects)
    {
        return new AttributeTranslationTextComponent(this.key + "." + key, objects);
    }

    public static class AttributeTranslationTextComponent extends BlocklingsTranslationTextComponent
    {
        public AttributeTranslationTextComponent(String key)
        {
            super("attribute." + key);
        }

        public AttributeTranslationTextComponent(String key, Object... objects)
        {
            super("attribute." + key, objects);
        }
    }

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
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(index);
            buf.writeBoolean(isEnabled);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            index = buf.readInt();
            isEnabled = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            blockling.getStats().attributes.get(index).setIsEnabled(isEnabled, false);
        }
    }
}
