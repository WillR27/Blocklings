package com.willr27.blocklings.client.gui.control.controls.stats;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.VerticalAlignment;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

/**
 * Displays a single blockling statistic, e.g. attack damage, mining speed.
 */
@OnlyIn(Dist.CLIENT)
public class StatControl extends Control
{
    /**
     * Supplies the stat's value string to display.
     */
    @Nonnull
    private final Supplier<String> valueSupplier;

    /**
     * Supplies the stat's tooltip to display.
     */
    @Nonnull
    private final Supplier<List<ITextComponent>> tooltipSupplier;

    /**
     * Whether to render the value on the left of the icon or the right.
     */
    private final boolean renderValueToLeftOfIcon;

    /**
     * The icon control.
     */
    @Nonnull
    private final TexturedControl iconControl;

    /**
     * The value text control.
     */
    @Nonnull
    private final TextBlockControl valueControl;

    /**
     * @param iconTexture the stat's icon texture.
     * @param valueSupplier the stat's value supplier.
     * @param tooltipSupplier the stat's tooltip supplier.
     * @param renderValueToLeftOfIcon whether to render the value on the left of the icon or the right.
     */
    public StatControl(@Nonnull GuiTexture iconTexture, @Nonnull Supplier<String> valueSupplier, @Nonnull Supplier<List<ITextComponent>> tooltipSupplier, boolean renderValueToLeftOfIcon)
    {
        super();
        this.valueSupplier = valueSupplier;
        this.tooltipSupplier = tooltipSupplier;
        this.renderValueToLeftOfIcon = renderValueToLeftOfIcon;

        setFitToContentsXY(true);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setParent(this);
        flowPanel.setItemGapX(6);
        flowPanel.setFitToContentsXY(true);

        iconControl = new TexturedControl(iconTexture);
        iconControl.setBackgroundColour(new Colour(0xffff00ff));

        valueControl = new TextBlockControl();
        valueControl.setMargins(0, 1, 0, 0);
        valueControl.setFitToContentsXY(true);
        valueControl.setTextColour(new Colour(0xffffe100));
        valueControl.setVerticalAlignment(VerticalAlignment.TOP);
        valueControl.setMargin(Side.TOP, 1);
        valueControl.setBackgroundColour(new Colour(0xffaffaff));

        if (renderValueToLeftOfIcon)
        {
            valueControl.setParent(flowPanel);
            iconControl.setParent(flowPanel);
        }
        else
        {
            iconControl.setParent(flowPanel);
            valueControl.setParent(flowPanel);
        }
    }

    @Override
    protected void onTick()
    {
        valueControl.setText(new StringTextComponent(valueSupplier.get()));
    }
}
