package com.blocklings.network;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.main.Blocklings;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InvItemStackMessage implements IMessage
{
    ItemStack stack;
    int slot;
    int id;

    public InvItemStackMessage()
    {
    }

    public InvItemStackMessage(ItemStack stack, int slot, int entityID)
    {
        this.stack = stack;
        this.slot = slot;
        this.id = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.slot = buf.readInt();
        this.id = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeItemStack(buf, this.stack);
        buf.writeInt(this.slot);
        buf.writeInt(this.id);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<InvItemStackMessage, IMessage>
    {
        public IMessage onMessage(InvItemStackMessage message, MessageContext ctx)
        {
            Entity entity = null;

            if ((ctx.side.isClient()) && (Blocklings.proxy.getPlayer(ctx) != null))
            {
                entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(message.id);

                if (entity instanceof EntityBlockling)
                {
                    EntityBlockling blockling = (EntityBlockling) entity;

                    blockling.inv.setInventorySlotContents(message.slot, message.stack);
                }
            }
            else if (ctx.side.isServer() && Blocklings.proxy.getPlayer(ctx) != null)
            {
                entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(message.id);

                if ((entity instanceof EntityBlockling))
                {
                    EntityBlockling blockling = (EntityBlockling) entity;

                    blockling.inv.setInventorySlotContents(message.slot, message.stack);
                }
            }

            return null;
        }
    }
}