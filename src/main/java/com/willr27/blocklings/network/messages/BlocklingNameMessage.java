package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A message used to sync the blockling's name from the client to the server.
 */
public class BlocklingNameMessage extends BlocklingMessage<BlocklingNameMessage>
{
    /**
     * The blockling's name.
     */
    @Nonnull
    private String name = "";

    /**
     * Empty constructor used ONLY for decoding.
     */
    public BlocklingNameMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param name the blockling's name.
     */
    public BlocklingNameMessage(@Nonnull BlocklingEntity blockling, @Nullable StringTextComponent name)
    {
        super(blockling, false);
        this.name = name == null ? "" : name.getString();
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        PacketBufferUtils.writeString(buf, name);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        name = PacketBufferUtils.readString(buf);
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.setCustomName(name.equals("") ? null : new StringTextComponent(name), false);
    }
}