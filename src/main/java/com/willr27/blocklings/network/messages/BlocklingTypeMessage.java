package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;

public class BlocklingTypeMessage extends BlocklingMessage<BlocklingTypeMessage>
{
    /**
     * The blockling type.
     */
    private BlocklingType type;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public BlocklingTypeMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param type the blockling type.
     */
    public BlocklingTypeMessage(@Nonnull BlocklingEntity blockling, @Nonnull BlocklingType type)
    {
        super(blockling);
        this.type = type;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(BlocklingType.TYPES.indexOf(type));
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        type = BlocklingType.TYPES.get(buf.readInt());
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.setBlocklingType(type, false);
    }
}