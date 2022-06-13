package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class WhitelistIsUnlockedMessage extends BlocklingMessage<WhitelistIsUnlockedMessage>
{
    /**
     * The associated task id.
     */
    private UUID taskId;

    /**
     * The whitelist id.
     */
    private int whitelistId;

    /**
     * Whether the whitelist is unlocked.
     */
    private boolean isUnlocked;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public WhitelistIsUnlockedMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId the task id.
     * @param whitelistId the whitelist id.
     * @param isUnlocked whether the whitelist is unlocked.
     */
    public WhitelistIsUnlockedMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, int whitelistId, boolean isUnlocked)
    {
        super(blockling);
        this.taskId = taskId;
        this.whitelistId = whitelistId;
        this.isUnlocked = isUnlocked;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId);
        buf.writeInt(whitelistId);
        buf.writeBoolean(isUnlocked);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId = buf.readUUID();
        whitelistId = buf.readInt();
        isUnlocked = buf.readBoolean();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().getTask(taskId).getGoal().whitelists.get(whitelistId).setIsUnlocked(isUnlocked, false);
    }
}