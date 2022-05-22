package com.willr27.blocklings.task.config;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.controls.common.RangeControl;
import com.willr27.blocklings.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

/**
 * Used to configure a range property.
 */
public class RangeProperty extends Property
{
    /**
     * The minimum value of the range.
     */
    public int min;

    /**
     * The maximum value of the range.
     */
    public int max;

    /**
     * The current value.
     */
    public int value;

    /**
     * @param goal the associated task's goal.
     * @param name the name of the property.
     * @param min the minimum value of the range.
     * @param max the maximum value of the range.
     * @param startingValue the range starting value.
     */
    public RangeProperty(@Nonnull BlocklingGoal goal, @Nonnull ITextComponent name, int min, int max, int startingValue)
    {
        super(goal, name);
        this.min = min;
        this.max = max;
        this.value = startingValue;
    }

    @Override
    public void writeToNBT(@Nonnull ListNBT list)
    {
        CompoundNBT propertyTag = new CompoundNBT();

        propertyTag.putInt("min", min);
        propertyTag.putInt("max", max);
        propertyTag.putInt("value", value);

        list.add(propertyTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT tag)
    {
        min = tag.getInt("min");
        max = tag.getInt("max");
        value = tag.getInt("value");
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeInt(min);
        buf.writeInt(max);
        buf.writeInt(value);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        min = buf.readInt();
        max = buf.readInt();
        value = buf.readInt();
    }

    @Override
    @Nonnull
    public IControl createControl(@Nonnull IControl parent)
    {
        return new RangeControl(parent, min, max, value, 20)
        {
            @Override
            public void setValue(int value)
            {
                super.setValue(value);

                RangeProperty.this.setValue(getValue(), true);
            }
        };
    }

    /**
     * @return the current value of the range.
     */
    public int getValue()
    {
        return value;
    }

    /**
     * @param value the new value.
     * @param sync whether to sync to the client/server.
     */
    public void setValue(int value, boolean sync)
    {
        this.value = value;

        if (sync)
        {
            new TaskRangePropertyMessage(this).sync();
        }
    }

    /**
     * Used to sync properties between the client and server.
     */
    public static class TaskRangePropertyMessage extends TaskPropertyMessage<RangeProperty, TaskRangePropertyMessage>
    {
        /**
         * The current value of the range.
         */
        private int value;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public TaskRangePropertyMessage()
        {
            super();
        }

        /**
         * @param property the property.
         */
        public TaskRangePropertyMessage(@Nonnull RangeProperty property)
        {
            super(property);
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(property.value);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            value = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            super.handle(player, blockling);

            if (property != null)
            {
                property.value = value;
            }
        }
    }
}
