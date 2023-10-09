package com.willr27.blocklings.entity.blockling.task.config.range;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.controls.config.IntRangeControl;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configures an int range property.
 */
public class IntRangeProperty extends RangeProperty<Integer>
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
    public IntRangeProperty(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull ITextComponent name, @Nonnull ITextComponent desc, int min, int max, int startingValue)
    {
        super(id, goal, name, desc, min, max, startingValue);
    }

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag propertyTag)
    {
        propertyTag.putInt("value", value);

        return super.writeToNBT(propertyTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag propertyTag, @Nonnull Version tagVersion)
    {
        value = propertyTag.getInt("value");

        super.readFromNBT(propertyTag, tagVersion);
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(min);
        buf.writeInt(max);
        buf.writeInt(value);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        min = buf.readInt();
        max = buf.readInt();
        value = buf.readInt();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @Nonnull
    public BaseControl createControl()
    {
        return new IntRangeControl(min, max, value)
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                if (!grabberControl.isPressed())
                {
                    List<IReorderingProcessor> tooltip = GuiUtil.get().split(desc.copy().withStyle(ChatFormatting.GRAY), 200);
                    tooltip.add(0, name.copy().withStyle(ChatFormatting.WHITE).getVisualOrderText());

                    renderTooltip(matrixStack, mouseX, mouseY, tooltip);
                }
            }

            @Override
            public void setValue(@Nonnull Integer value, boolean updateGrabberPosition, boolean postEvent)
            {
                super.setValue(value, updateGrabberPosition, postEvent);

                IntRangeProperty.this.setValue(getValue(), true);
            }
        };
    }
}
