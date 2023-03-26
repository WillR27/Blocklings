package com.willr27.blocklings.client.gui.control.controls.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.controls.EnumeratingControl;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An enumerating control specifically for stats.
 */
@OnlyIn(Dist.CLIENT)
public class EnumeratingStatControl extends EnumeratingControl<StatControl>
{
    /**
     * The name of the enumerating control to display in the tooltip.
     */
    @Nonnull
    private final ITextComponent name;

    /**
     * @param name the name of the enumerating control to display in the tooltip.
     */
    public EnumeratingStatControl(@Nonnull ITextComponent name)
    {
        super();
        this.name = name;
    }

    @Override
    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {
        renderTooltip(matrixStack, mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), prependNameToTooltip(combineTooltips()).stream().map(t -> t.getVisualOrderText()).collect(Collectors.toList()));
    }

    private List<ITextComponent> prependNameToTooltip(@Nonnull List<ITextComponent> tooltip)
    {
//        tooltip.add(0, new StringTextComponent("").getVisualOrderText());
        tooltip.add(0, name);

        return tooltip;
    }

    @Nonnull
    private List<ITextComponent> combineTooltips()
    {
        List<ITextComponent> tooltip = new ArrayList<>();

        for (int i = 0; i < displayConditions.size(); i++)
        {
            if (!displayConditions.get(i).get())
            {
                continue;
            }

            List<ITextComponent> subTooltip = controls.get(i).tooltipSupplier.get();

            if (i == getIndexOfCurrentChild())
            {
                subTooltip.set(0, new StringTextComponent(subTooltip.get(0).getString().substring(0, 2) + TextFormatting.ITALIC + subTooltip.get(0).getString().substring(2)));
            }

            if (GuiUtil.get().isCrouchKeyDown())
            {
                tooltip.addAll(subTooltip);
            }
            else
            {
                tooltip.add(subTooltip.get(0));
            }
        }

        if (!GuiUtil.get().isCrouchKeyDown())
        {
            tooltip.add(new StringTextComponent(TextFormatting.DARK_GRAY + "" + TextFormatting.ITALIC + new BlocklingsTranslationTextComponent("gui.more_info", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString()).getString()));
        }

        return tooltip;
    }
}

