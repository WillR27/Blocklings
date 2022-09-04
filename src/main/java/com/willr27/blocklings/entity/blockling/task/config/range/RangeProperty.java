package com.willr27.blocklings.entity.blockling.task.config.range;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.GuiUtil;
import com.willr27.blocklings.client.gui.IControl;
import com.willr27.blocklings.client.gui.controls.common.range.IntRangeControl;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.config.Property;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Used to configure a range property.
 */
public abstract class RangeProperty<T extends Number> extends Property
{
    /**
     * The minimum value of the range.
     */
    protected T min;

    /**
     * The maximum value of the range.
     */
    protected T max;

    /**
     * The current value.
     */
    protected T value;

    /**
     * @param id            the id of the property (used for serialising\deserialising).
     * @param goal          the associated task's goal.
     * @param name          the name of the property.
     * @param desc          the description of the property.
     * @param min           the minimum value of the range.
     * @param max           the maximum value of the range.
     * @param startingValue the range starting value.
     */
    public RangeProperty(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull ITextComponent name, @Nonnull ITextComponent desc, T min, T max, T startingValue)
    {
        super(id, goal, name, desc);
        this.min = min;
        this.max = max;
        this.value = startingValue;
    }

    /**
     * @return the current value of the range.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * @param value the new value.
     * @param sync whether to sync to the client/server.
     */
    public void setValue(T value, boolean sync)
    {
        this.value = value;

        if (sync)
        {
            new TaskPropertyMessage(this).sync();
        }
    }
}
