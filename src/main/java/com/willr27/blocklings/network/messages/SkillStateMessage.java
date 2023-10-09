package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.Skill;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class SkillStateMessage extends BlocklingMessage<SkillStateMessage>
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
     * The state of skill.
     */
    private Skill.State state;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public SkillStateMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param skill the skill.
     */
    public SkillStateMessage(@Nonnull BlocklingEntity blockling, @Nonnull Skill skill)
    {
        super(blockling);
        this.skillId = skill.info.id;
        this.groupId = skill.group.info.id;
        this.state = skill.getState();
    }

    @Override
    public void encode(@Nonnull FriendlyByteBuf buf)
    {
        super.encode(buf);

        buf.writeUUID(skillId);
        buf.writeUUID(groupId);
        buf.writeEnum(state);
    }

    @Override
    public void decode(@Nonnull FriendlyByteBuf buf)
    {
        super.decode(buf);

        skillId = buf.readUUID();
        groupId = buf.readUUID();
        state = buf.readEnum(Skill.State.class);
    }

    @Override
    protected void handle(@Nonnull Player player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getSkills().getGroup(groupId).getSkill(skillId).setState(state, false);
    }
}