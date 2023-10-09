package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes.Level;
import com.willr27.blocklings.entity.blockling.attribute.attributes.numbers.IntAttribute;
import com.willr27.blocklings.network.Message;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SetXpCommandMessage extends Message
{
    /**
     * The level.
     */
    private Level level;

    /**
     * The value to set the level's xp to.
     */
    private int value;

    /**
     * @param level the level.
     * @param value the value to set the level's xp to.
     */
    public SetXpCommandMessage(@Nonnull Level level, int value)
    {
        this.level = level;
        this.value = value;
    }

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull FriendlyByteBuf buf)
    {
        PacketBufferUtils.writeString(buf, level.name());
        buf.writeInt(value);
    }

    /**
     * Decodes and returns the message.
     *
     * @param buf the buffer to decode from.
     */
    @Nonnull
    public static SetXpCommandMessage decode(@Nonnull FriendlyByteBuf buf)
    {
        return new SetXpCommandMessage(Level.valueOf(PacketBufferUtils.readString(buf)), buf.readInt());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();

        context.enqueueWork(() ->
        {
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Player player = isClient ? getClientPlayer() : context.getSender();
            Objects.requireNonNull(player, "No player entity found when handling message.");

            if (isClient)
            {
                Entity entity = Minecraft.getInstance().crosshairPickEntity;

                if (entity instanceof BlocklingEntity)
                {
                    BlocklingEntity blockling = (BlocklingEntity) entity;

                    if (level == Level.TOTAL)
                    {
                        for (Level level : Arrays.stream(Level.values()).filter(lvl -> lvl != Level.TOTAL).collect(Collectors.toList()))
                        {
                            ((IntAttribute) blockling.getStats().getLevelXpAttribute(level)).setValue(value);
                        }
                    }
                    else
                    {
                        ((IntAttribute) blockling.getStats().getLevelXpAttribute(level)).setValue(value);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
