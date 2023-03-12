package com.willr27.blocklings.entity.blockling.task.config.range;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.controls.config.FloatRangeControl;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
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
 * Configures an float range property.
 */
public class FloatRangeProperty extends RangeProperty<Float>
{
    /**
     * @param id            the id of the property (used for serialising\deserialising).
     * @param goal          the associated task's goal.
     * @param name          the name of the property.
     * @param desc          the description of the property.
     * @param min           the minimum value of the range.
     * @param max           the maximum value of the range.
     * @param startingValue the range starting value.
     */
    public FloatRangeProperty(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull ITextComponent name, @Nonnull ITextComponent desc, float min, float max, float startingValue)
    {
        super(id, goal, name, desc, min, max, startingValue);
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT propertyTag)
    {
        propertyTag.putFloat("value", value);

        return super.writeToNBT(propertyTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT propertyTag, @Nonnull Version tagVersion)
    {
        value = propertyTag.getFloat("value");
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeFloat(min);
        buf.writeFloat(max);
        buf.writeFloat(value);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        min = buf.readFloat();
        max = buf.readFloat();
        value = buf.readFloat();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @Nonnull
    public BaseControl createControl()
    {
        return new FloatRangeControl(min, max, value)
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                if (!grabberControl.isPressed())
                {
                    List<StringTextComponent> tooltip = GuiUtil.splitText(font, desc, 200);
                    tooltip.add(0, new StringTextComponent(""));
                    tooltip.add(0, new StringTextComponent(TextFormatting.GOLD + name.getString()));

                    renderTooltip(matrixStack, mouseX, mouseY, GuiUtil.toReorderingProcessorList(tooltip));
                }
            }

            @Override
            public void setValue(@Nonnull Float value, boolean updateGrabberPosition, boolean postEvent)
            {
                super.setValue(value, updateGrabberPosition, postEvent);

                FloatRangeProperty.this.setValue(getValue(), true);
            }
        };
    }
}
