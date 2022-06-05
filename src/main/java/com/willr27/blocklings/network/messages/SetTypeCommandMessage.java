package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class SetTypeCommandMessage implements IMessage
{
    /**
     * The type key.
     */
    @Nullable
    private String type;

    /**
     * @param type the type key.
     */
    public SetTypeCommandMessage(@Nonnull String type)
    {
        this.type = type;
    }

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        PacketBufferUtils.writeString(buf, type);
    }

    /**
     * Decodes and returns the message.
     *
     * @param buf the buffer to decode from.
     */
    @Nonnull
    public static SetTypeCommandMessage decode(@Nonnull PacketBuffer buf)
    {
        return new SetTypeCommandMessage(PacketBufferUtils.readString(buf));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();

        context.enqueueWork(() ->
        {
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            PlayerEntity player = isClient ? Minecraft.getInstance().player : context.getSender();
            Objects.requireNonNull(player, "No player entity found when handling message.");

            if (isClient)
            {
                Entity entity = Minecraft.getInstance().crosshairPickEntity;

                if (entity instanceof BlocklingEntity)
                {
                    ((BlocklingEntity) entity).setBlocklingType(BlocklingType.find(type));
                }
            }
        });
        context.setPacketHandled(true);
    }
}
