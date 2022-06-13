package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingType;
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
     * Whether the type being set is the natural type or not.
     */
    private boolean natural;

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
     * @param natural whether the type being set is the natural type or not.
     */
    public BlocklingTypeMessage(@Nonnull BlocklingEntity blockling, @Nonnull BlocklingType type, boolean natural)
    {
        super(blockling);
        this.type = type;
        this.natural = natural;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(BlocklingType.TYPES.indexOf(type));
        buf.writeBoolean(natural);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        type = BlocklingType.TYPES.get(buf.readInt());
        natural = buf.readBoolean();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        if (natural)
        {
            blockling.setNaturalBlocklingType(type, false);
        }
        else
        {
            blockling.setBlocklingType(type, false);
        }
    }
}