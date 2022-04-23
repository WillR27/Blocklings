package com.willr27.blocklings.gui.controls.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.IControl;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

/**
 * A simple control used to display text.
 */
public class LabelControl extends Control
{
    /**
     * The text to display.
     */
    @Nonnull
    private String text;

    /**
     * @param parent the parent control.
     * @param width the width of the control.
     * @param text The text to display.
     */
    public LabelControl(@Nonnull IControl parent, int width, @Nonnull String text)
    {
        super(parent, 0, 0, width, 9);
        this.text = text;

        setPadding(0, 1, 0, 1);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderText(matrixStack, getTrimmedText(), getPadding(Side.LEFT), getPadding(Side.TOP), false, 0xffffffff);
    }

    @Override
    public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (!getTrimmedText().equals(text))
        {
            screen.renderTooltip(matrixStack, new StringTextComponent(text), mouseX, mouseY);
        }
    }

    @Override
    public void setPadding(@Nonnull Side side, int padding)
    {
        super.setPadding(side, padding);

        recalcHeight();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom)
    {
        super.setPadding(left, top, right, bottom);

        recalcHeight();
    }

    /**
     * Recalculates the height of the control to include padding.
     */
    private void recalcHeight()
    {
        height = height + getPadding(Side.TOP)  + getPadding(Side.BOTTOM);
    }

    /**
     * @return the trimmed text to display.
     */
    @Nonnull
    private String getTrimmedText()
    {
        return GuiUtil.trimWithEllipses(font, text, width - getPadding(Side.LEFT) - getPadding(Side.RIGHT));
    }

    /**
     * @return the text to display.
     */
    @Nonnull
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text to display.
     */
    public void setText(@Nonnull String text)
    {
        this.text = text;
    }
}
