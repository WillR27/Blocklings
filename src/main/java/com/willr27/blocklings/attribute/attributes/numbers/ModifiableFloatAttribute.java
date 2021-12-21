package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.ModifiableAttribute;
import com.willr27.blocklings.attribute.Operation;
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

public class ModifiableFloatAttribute extends ModifiableAttribute<Float>
{
    public ModifiableFloatAttribute(String id, String key, BlocklingEntity blockling, float baseValue, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, baseValue, displayStringValueSupplier, displayStringNameSupplier);
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = new CompoundNBT();

        attributeTag.putFloat("base_value", baseValue);

        tag.put(id.toString(), attributeTag);
    }

    @Override
    public void readFromNBT(CompoundNBT tag)
    {
        CompoundNBT attributeTag = (CompoundNBT) tag.get(id.toString());

        if (attributeTag != null)
        {
            baseValue = attributeTag.getFloat("base_value");
        }
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeFloat(value);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        value = buf.readFloat();
    }

    @Override
    public void calculate()
    {
        value = 0.0f;
        float tempBase = baseValue;
        boolean end = false;

        for (IModifier<Float> modifier : modifiers)
        {
            if (modifier.getOperation() == Operation.ADD)
            {
                value += modifier.getValue();
            }
            else if (modifier.getOperation() == Operation.MULTIPLY_BASE)
            {
                tempBase *= modifier.getValue();
            }
            else if (modifier.getOperation() == Operation.MULTIPLY_TOTAL)
            {
                if (!end)
                {
                    value += tempBase;
                    end = true;
                }

                value *= modifier.getValue();
            }
        }

        if (!end)
        {
            value += tempBase;
        }

        callUpdateCallbacks();
    }

    @Override
    public void incBaseValue(Float amount, boolean sync)
    {
        setBaseValue(baseValue + amount, sync);
    }

    @Override
    public void setBaseValue(Float baseValue, boolean sync)
    {
        this.baseValue = baseValue;

        calculate();

        if (sync)
        {
            NetworkHandler.sync(world, new BaseValueMessage(blockling.getStats().attributes.indexOf(this), baseValue, blockling.getId()));
        }
    }

    public static class BaseValueMessage implements IMessage
    {
        public int index;
        public float baseValue;
        public int entityId;

        private BaseValueMessage() {}
        public BaseValueMessage(int index, float baseValue, int entityId)
        {
            this.index = index;
            this.baseValue = baseValue;
            this.entityId = entityId;
        }

        public static void encode(BaseValueMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeFloat(msg.baseValue);
            buf.writeInt(msg.entityId);
        }

        public static BaseValueMessage decode(PacketBuffer buf)
        {
            BaseValueMessage msg = new BaseValueMessage();
            msg.index = buf.readInt();
            msg.baseValue = buf.readFloat();
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

                ModifiableFloatAttribute attribute = (ModifiableFloatAttribute) blockling.getStats().attributes.get(index);
                attribute.setBaseValue(baseValue, !isClient);
            });
        }
    }
}
