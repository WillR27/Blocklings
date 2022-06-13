package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlocklingAttackTargetMessage extends BlocklingMessage<BlocklingAttackTargetMessage>
{
    /**
     * The id of the target entity. -1 if target is null.
     */
    private int targetId;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public BlocklingAttackTargetMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param target the target.
     */
    public BlocklingAttackTargetMessage(@Nonnull BlocklingEntity blockling, @Nullable LivingEntity target)
    {
        super(blockling);
        this.targetId = target != null ? target.getId() : -1;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(targetId);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        targetId = buf.readInt();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.setTarget(targetId == -1 ? null : (LivingEntity) player.level.getEntity(targetId), false);
    }
}