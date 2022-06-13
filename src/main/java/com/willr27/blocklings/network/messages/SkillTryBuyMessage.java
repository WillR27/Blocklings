package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.Skill;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class SkillTryBuyMessage extends BlocklingMessage<SkillTryBuyMessage>
{
    /**
     * The skill id.
     */
    private UUID skillId;

    /**
     * The skill group id.
     */
    private UUID groupId;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public SkillTryBuyMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param skill the skill.
     */
    public SkillTryBuyMessage(@Nonnull BlocklingEntity blockling, @Nonnull Skill skill)
    {
        super(blockling);
        this.skillId = skill.info.id;
        this.groupId = skill.group.info.id;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeUUID(skillId);
        buf.writeUUID(groupId);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        skillId = buf.readUUID();
        groupId = buf.readUUID();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getSkills().getGroup(groupId).getSkill(skillId).tryBuy(false);
    }
}