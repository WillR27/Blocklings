package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes.Level;
import com.willr27.blocklings.entity.blockling.attribute.attributes.numbers.IntAttribute;
import com.willr27.blocklings.network.Message;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SetLevelCommandMessage extends Message
{
    /**
     * The level.
     */
    private Level level;

    /**
     * The value to set the level to.
     */
    private int value;

    /**
     * @param level the level.
     * @param value the value to set the level to.
     */
    public SetLevelCommandMessage(@Nonnull Level level, int value)
    {
        this.level = level;
        this.value = value;
    }

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull PacketBuffer buf)
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
    public static SetLevelCommandMessage decode(@Nonnull PacketBuffer buf)
    {
        return new SetLevelCommandMessage(Level.valueOf(PacketBufferUtils.readString(buf)), buf.readInt());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();

        context.enqueueWork(() ->
        {
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            PlayerEntity player = isClient ? getClientPlayer() : context.getSender();
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
                            ((IntAttribute) blockling.getStats().getLevelAttribute(level)).setValue(value);
                        }
                    }
                    else
                    {
                        ((IntAttribute) blockling.getStats().getLevelAttribute(level)).setValue(value);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
