package com.willr27.blocklings.attribute;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
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
        setIsEnabled(attributeTag.getBoolean("is_enabled"), false);
    }

    public void encode(PacketBuffer buf)
    {
        buf.writeBoolean(isEnabled);
    }

    public void decode(PacketBuffer buf)
    {
        setIsEnabled(buf.readBoolean(), false);
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
        setIsEnabled(isEnabled);
    }

    public void setIsEnabled(boolean isEnabled, boolean sync)
    {
        this.isEnabled = isEnabled;

        if (sync)
        {
            NetworkHandler.sync(world, new IsEnabledMessage(blockling.getStats().attributes.indexOf(this), isEnabled, blockling.getId()));
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

    public static class IsEnabledMessage implements IMessage
    {
        private int index;
        private boolean isEnabled;
        private int entityId;

        private IsEnabledMessage() {}
        public IsEnabledMessage(int index, boolean isEnabled, int entityId)
        {
            this.index = index;
            this.isEnabled = isEnabled;
            this.entityId = entityId;
        }

        public static void encode(IsEnabledMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeBoolean(msg.isEnabled);
            buf.writeInt(msg.entityId);
        }

        public static IsEnabledMessage decode(PacketBuffer buf)
        {
            IsEnabledMessage msg = new IsEnabledMessage();
            msg.index = buf.readInt();
            msg.isEnabled = buf.readBoolean();
            msg.entityId = buf.readInt();

            return msg;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                NetworkEvent.Context context = ctx.get();
                boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

                PlayerEntity player = isClient ? Minecraft.getInstance().player : ctx.get().getSender();
                BlocklingEntity blockling = (BlocklingEntity) player.level.getEntity(entityId);

                Attribute<?> attribute = blockling.getStats().attributes.get(index);
                attribute.setIsEnabled(isEnabled, !isClient);
            });
        }
    }
}
