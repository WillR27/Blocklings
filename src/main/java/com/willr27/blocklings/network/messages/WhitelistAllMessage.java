package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.GoalWhitelist;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.Whitelist;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class WhitelistAllMessage extends BlocklingMessage<WhitelistAllMessage>
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
     * The actual underlying whitelist.
     */
    private Whitelist<ResourceLocation> whitelist;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public WhitelistAllMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param taskId the associated task id.
     * @param whitelistId the whitelist id.
     * @param whitelist the actual underlying whitelist.
     */
    public WhitelistAllMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, int whitelistId, @Nonnull GoalWhitelist whitelist)
    {
        super(blockling);
        this.taskId = taskId;
        this.whitelistId = whitelistId;
        this.whitelist = whitelist;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(taskId);
        buf.writeInt(whitelistId);
        buf.writeInt(whitelist.size());

        for (ResourceLocation entry : whitelist.keySet())
        {
            PacketBufferUtils.writeString(buf, entry.toString());
            buf.writeBoolean(whitelist.get(entry));
        }
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        taskId = buf.readUUID();
        whitelistId = buf.readInt();
        whitelist = new Whitelist<>();

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            whitelist.put(new ResourceLocation(PacketBufferUtils.readString(buf)), buf.readBoolean());
        }
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getTasks().getTask(taskId).getGoal().whitelists.get(whitelistId).setWhitelist(whitelist, false);
    }
}