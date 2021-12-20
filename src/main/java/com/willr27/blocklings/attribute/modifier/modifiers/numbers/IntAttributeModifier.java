package com.willr27.blocklings.attribute.modifier.modifiers.numbers;

import com.willr27.blocklings.attribute.ModifiableAttribute;
import com.willr27.blocklings.attribute.modifier.AttributeModifier;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class IntAttributeModifier extends AttributeModifier<Integer>
{
    public IntAttributeModifier(String id, String key, ModifiableAttribute<Integer> attribute, int value, Operation operation, Supplier<String> displayStringSupplier)
    {
        super(id, key, attribute, value, operation, displayStringSupplier);
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        CompoundNBT modifierTag = new CompoundNBT();

        modifierTag.putInt("value", value);

        tag.put(id.toString(), modifierTag);
    }

    @Override
    public void readFromNBT(CompoundNBT tag)
    {
        CompoundNBT modifierTag = (CompoundNBT) tag.get(id.toString());

        if (modifierTag != null)
        {
            value = modifierTag.getInt("value");
        }
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeInt(value);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        value = buf.readInt();
    }

    @Override
    public Integer getValue()
    {
        return value;
    }

    @Override
    public void setValue(Integer value, boolean sync)
    {
        this.value = value;

        attribute.calculate();

        if (sync)
        {
            NetworkHandler.sync(blockling.level, new ValueMesage(blockling.getStats().attributes.indexOf(attribute), attribute.indexOf(this), value, blockling.getId()));
        }
    }

    public static class ValueMesage implements IMessage
    {
        int attributeIndex;
        int attributeModifierIndex;
        int value;
        int entityId;

        private ValueMesage() {}
        public ValueMesage(int attributeIndex, int attributeModifierIndex, int value, int entityId)
        {
            this.attributeIndex = attributeIndex;
            this.attributeModifierIndex = attributeModifierIndex;
            this.value = value;
            this.entityId = entityId;
        }

        public static void encode(ValueMesage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.attributeIndex);
            buf.writeInt(msg.attributeModifierIndex);
            buf.writeInt(msg.value);
            buf.writeInt(msg.entityId);
        }

        public static ValueMesage decode(PacketBuffer buf)
        {
            ValueMesage msg = new ValueMesage();
            msg.attributeIndex = buf.readInt();
            msg.attributeModifierIndex = buf.readInt();
            msg.value = buf.readInt();
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
                if (player != null)
                {
                    BlocklingEntity blockling = (BlocklingEntity) player.level.getEntity(entityId);
                    if (blockling != null)
                    {
                        IntAttributeModifier modifier = (IntAttributeModifier) ((ModifiableAttribute<?>) blockling.getStats().attributes.get(attributeIndex)).findModifier(attributeModifierIndex);
                        modifier.setValue(value, !isClient);
                    }
                }
            });
        }
    }
}
