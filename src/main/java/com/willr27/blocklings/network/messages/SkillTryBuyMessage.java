package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.skills.Skill;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SkillTryBuyMessage implements IMessage
{
    UUID skillId;
    UUID groupId;
    int entityId;

    private SkillTryBuyMessage() {}
    public SkillTryBuyMessage(Skill skill, int entityId)
    {
        this.skillId = skill.info.id;
        this.groupId = skill.group.info.id;;
        this.entityId = entityId;
    }

    public static void encode(SkillTryBuyMessage msg, PacketBuffer buf)
    {
        buf.writeUUID(msg.skillId);
        buf.writeUUID(msg.groupId);
        buf.writeInt(msg.entityId);
    }

    public static SkillTryBuyMessage decode(PacketBuffer buf)
    {
        SkillTryBuyMessage msg = new SkillTryBuyMessage();
        msg.skillId = buf.readUUID();
        msg.groupId = buf.readUUID();
        msg.entityId = buf.readInt();

        return msg;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            NetworkEvent.Context context = ctx.get();
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            PlayerEntity player = isClient ? Minecraft.getInstance().player : ctx.get().getSender();
            BlocklingEntity blockling = (BlocklingEntity) player.level.getEntity(entityId);

            Skill skill = blockling.getSkills().getGroup(groupId).getSkill(skillId);
            skill.tryBuy(!isClient);
        });
    }
}