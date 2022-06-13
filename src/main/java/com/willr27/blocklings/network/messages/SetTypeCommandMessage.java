package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingType;
import com.willr27.blocklings.network.Message;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class SetTypeCommandMessage extends Message
{
    /**
     * The type key.
     */
    private String type;

    /**
     * Whether the type being set is the natural type or not.
     */
    private boolean natural;

    /**
     * @param type the type key.
     * @param natural whether the type being set is the natural type or not.
     */
    public SetTypeCommandMessage(@Nonnull String type, boolean natural)
    {
        this.type = type;
        this.natural = natural;
    }

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        PacketBufferUtils.writeString(buf, type);
        buf.writeBoolean(natural);
    }

    /**
     * Decodes and returns the message.
     *
     * @param buf the buffer to decode from.
     */
    @Nonnull
    public static SetTypeCommandMessage decode(@Nonnull PacketBuffer buf)
    {
        return new SetTypeCommandMessage(PacketBufferUtils.readString(buf), buf.readBoolean());
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

                    if (natural)
                    {
                        blockling.setNaturalBlocklingType(BlocklingType.find(type));
                    }
                    else
                    {
                        blockling.setBlocklingType(BlocklingType.find(type));
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
