package com.willr27.blocklings.task.config;

import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.controls.common.RangeControl;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundNBT;
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
     * @param id the id of the property (used for serialising\deserialising).
     * @param goal the associated task's goal.
     * @param name the name of the property.
     * @param min the minimum value of the range.
     * @param max the maximum value of the range.
     * @param startingValue the range starting value.
     */
    public RangeProperty(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull ITextComponent name, int min, int max, int startingValue)
    {
        super(id, goal, name);
        this.min = min;
        this.max = max;
        this.value = startingValue;
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT propertyTag)
    {
        propertyTag.putInt("min", min);
        propertyTag.putInt("max", max);
        propertyTag.putInt("value", value);

        return super.writeToNBT(propertyTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT propertyTag, @Nonnull Version tagVersion)
    {
        min = propertyTag.getInt("min");
        max = propertyTag.getInt("max");
        value = propertyTag.getInt("value");
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
            new TaskPropertyMessage(this).sync();
        }
    }
}
