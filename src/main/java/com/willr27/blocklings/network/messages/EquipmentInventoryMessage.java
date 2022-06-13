package com.willr27.blocklings.network.messages;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;

public class EquipmentInventoryMessage extends BlocklingMessage<EquipmentInventoryMessage>
{
    /**
     * The index of the stack in the inventory.
     */
    private int index;

    /**
     *  The stack.
     */
    private ItemStack stack;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public EquipmentInventoryMessage()
    {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param index the index of the stack in the inventory.
     * @param stack the stack.
     */
    public EquipmentInventoryMessage(@Nonnull BlocklingEntity blockling, int index, @Nonnull ItemStack stack)
    {
        super(blockling);
        this.index = index;
        this.stack = stack;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(index);
        buf.writeItem(stack);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        index = buf.readInt();
        stack = buf.readItem();
    }

    @Override
    protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
    {
        blockling.getEquipment().setItem(index, stack);
    }
}