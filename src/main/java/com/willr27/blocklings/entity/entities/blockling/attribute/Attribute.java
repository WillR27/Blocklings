package com.willr27.blocklings.entity.entities.blockling.attribute;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.IMessage;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Attribute
{
    public final BlocklingEntity blockling;
    public final World world;
    public final String key;
    public float baseValue;
    private float value;
    private final List<AttributeModifier> modifiers = new ArrayList<>();
    protected Runnable onCalculate;

    public Attribute(BlocklingEntity blockling, String key, float baseValue)
    {
        this.blockling = blockling;
        this.world = blockling.level;
        this.key = key;
        this.baseValue = baseValue;
        this.value = baseValue;
    }

    public void setOnCalculate(Runnable onCalculate)
    {
        this.onCalculate = onCalculate;
    }

    public void calculateValue()
    {
        value = 0;
        float tempBase = baseValue;
        boolean end = false;

        for (AttributeModifier modifier : modifiers)
        {
            if (modifier.operation == AttributeModifier.Operation.ADD)
            {
                value += modifier.value;
            }
            else if (modifier.operation == AttributeModifier.Operation.MULTIPLY_BASE)
            {
                tempBase *= modifier.value;
            }
            else if (modifier.operation == AttributeModifier.Operation.MULTIPLY_TOTAL)
            {
                if (!end)
                {
                    value += tempBase;
                    end = true;
                }
                value *= modifier.value;
            }
        }

        if (!end)
        {
            value += tempBase;
        }

        if (onCalculate != null)
        {
            onCalculate.run();
        }
    }

    public int indexOf(AttributeModifier modifier)
    {
        return modifiers.indexOf(modifier);
    }

    public AttributeModifier getModifier(int index)
    {
        return modifiers.get(index);
    }

    public void addModifier(AttributeModifier modifier)
    {
        // Don't add if modifier is already applied
        if (modifiers.contains(modifier))
        {
            return;
        }

        // Add total multiplications last
        if (modifier.operation != AttributeModifier.Operation.MULTIPLY_TOTAL)
        {
            modifiers.add(0, modifier);
        }
        else
        {
            modifiers.add(modifier);
        }

        // Calculate new value
        calculateValue();
    }

    public void removeModifier(AttributeModifier modifier)
    {
        // Remove if exists
        modifiers.remove(modifier);

        // Recalculate value
        calculateValue();
    }

    public float getBaseValue()
    {
        return baseValue;
    }

    public void incBaseValue(float amount)
    {
        incBaseValue(amount, true);
    }

    public void incBaseValue(float amount, boolean sync)
    {
        setBaseValue(baseValue + amount, sync);
    }

    public void setBaseValue(float baseValue)
    {
        setBaseValue(baseValue, true);
    }

    public void setBaseValue(float baseValue, boolean sync)
    {
        this.baseValue = baseValue;

        calculateValue();

        if (sync)
        {
            NetworkHandler.sync(world, new BlocklingAttributeBaseValueMessage(blockling.getStats().attributes.indexOf(this), baseValue, blockling.getId()));
        }
    }

    public float getFloat()
    {
        return value;
    }

    /*
    ** Rounds value to nearest int.
     */
    public int getInt()
    {
        return Math.round(value);
    }

    public TranslationTextComponent createTranslation(String key, Object... objects)
    {
        return new AttributeTranslationTextComponent(this.key + "." + key, objects);
    }

    public static class BlocklingAttributeBaseValueMessage implements IMessage
    {
        int index;
        float baseValue;
        int entityId;

        private BlocklingAttributeBaseValueMessage() {}
        public BlocklingAttributeBaseValueMessage(int index, float baseValue, int entityId)
        {
            this.index = index;
            this.baseValue = baseValue;
            this.entityId = entityId;
        }

        public static void encode(BlocklingAttributeBaseValueMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeFloat(msg.baseValue);
            buf.writeInt(msg.entityId);
        }

        public static BlocklingAttributeBaseValueMessage decode(PacketBuffer buf)
        {
            BlocklingAttributeBaseValueMessage msg = new BlocklingAttributeBaseValueMessage();
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

                Attribute attribute = blockling.getStats().attributes.get(index);
                attribute.setBaseValue(baseValue, !isClient);
            });
        }
    }

    public static class AttributeTranslationTextComponent extends BlocklingsTranslationTextComponent
    {
        public AttributeTranslationTextComponent(String key)
        {
            super("attribute." + key);
        }

        public AttributeTranslationTextComponent(String key, Object... objects)
        {
            super("attribute." + key, objects);
        }
    }
}
