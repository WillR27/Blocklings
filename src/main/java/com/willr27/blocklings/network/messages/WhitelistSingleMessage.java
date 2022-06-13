package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class WhitelistSingleMessage extends BlocklingMessage<WhitelistSingleMessage>
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
     * The entry to set value of.
     */
    private ResourceLocation entry;

    /**
     * Whether the entry is whitelisted.
     */
    private boolean value;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public WhitelistSingleMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId the associated task id.
     * @param whitelistId the whitelist id.
     * @param entry the entry to set the value of.
     * @param value whether the entry is whitelisted.
     */
    public WhitelistSingleMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, int whitelistId, @Nonnull ResourceLocation entry, boolean value)
    {
        super(blockling);
        this.taskId = taskId;
        this.whitelistId = whitelistId;
        this.entry = entry;
        this.value = value;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId);
        buf.writeInt(whitelistId);
        PacketBufferUtils.writeString(buf, entry.toString());
        buf.writeBoolean(value);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId = buf.readUUID();
        whitelistId = buf.readInt();
        entry = new ResourceLocation(PacketBufferUtils.readString(buf));
        value = buf.readBoolean();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().getTask(taskId).getGoal().whitelists.get(whitelistId).setEntry(entry, value, false);
    }
}