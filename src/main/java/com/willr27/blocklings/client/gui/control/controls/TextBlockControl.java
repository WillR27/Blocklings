package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Side;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Displays a block of text.
 */
@OnlyIn(Dist.CLIENT)
public class TextBlockControl extends TextControl
{
    /**
     * The text component to render.
     */
    @Nonnull
    private ITextComponent text;

    /**
     * The screen x position to render the text.
     */
    private float textScreenX = 0;

    /**
     * The screen y position to render the text.
     */
    private float textScreenY = 0;

    @Override
    protected void recalcTextPosition()
    {
        switch (getHorizontalAlignment())
        {
            case LEFT:
                textScreenX = (getScreenX() + (getPadding(Side.LEFT) * getCumulativeScale()));
                break;
            case MIDDLE:
                textScreenX = (getScreenX() + (getScreenWidth() / 2) - (font.width(text) * getCumulativeScale() / 2));
                break;
            case RIGHT:
                textScreenX = (getScreenX() + getScreenWidth() - font.width(text) * getCumulativeScale() - (getPadding(Side.RIGHT) * getCumulativeScale()));
                break;
        }

        switch (getVerticalAlignment())
        {
            case TOP:
                textScreenY = (getScreenY() + (getPadding(Side.TOP) * getCumulativeScale()));
                break;
            case MIDDLE:
                textScreenY = (getScreenY() + (getScreenHeight() / 2) - (font.lineHeight * getCumulativeScale() / 2));
                break;
            case BOTTOM:
                textScreenY = (getScreenY() + (getScreenHeight() - font.lineHeight * getCumulativeScale()) - (getPadding(Side.BOTTOM) * getCumulativeScale()));
                break;
        }
    }

    @Override
    protected void onRender(@Nonnull RenderArgs renderArgs)
    {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(textScreenX, textScreenY, 0.0);
        matrixStack.scale(getCumulativeScale(), getCumulativeScale(), 1.0f);
        Screen.drawString(matrixStack, font, text, 0, 0, 0xffffffff);
    }

    @Override
    @Nonnull
    public String getText()
    {
        return text.getString();
    }

    @Override
    public void setText(@Nonnull ITextComponent text)
    {
        this.text = text;

        recalcTextPosition();
    }
}
