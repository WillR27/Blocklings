package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

public class BlocklingScaleMessage extends BlocklingMessage<BlocklingScaleMessage>
{
    /**
     * The blockling's scale.
     */
    private float scale;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public BlocklingScaleMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param scale the blockling's scale.
     */
    public BlocklingScaleMessage(BlocklingEntity blockling, float scale)
    {
        super(blockling);
        this.scale = scale;
    }

    @Override
    public void encode(@Nonnull FriendlyByteBuf buf)
    {
        super.encode(buf);

        buf.writeFloat(scale);
    }

    @Override
    public void decode(@Nonnull FriendlyByteBuf buf)
    {
        super.decode(buf);

        scale = buf.readFloat();
    }

    @Override
    protected void handle(@Nonnull Player player, @Nonnull BlocklingEntity blockling)
    {
        blockling.setScale(scale, false);
    }
}