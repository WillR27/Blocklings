package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui2.Colour;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

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
    private ITextComponent text = new StringTextComponent("");

    private Colour textColour = new Colour(0xffffffff);

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
                textScreenY = (getScreenY() + (getScreenHeight() / 2) - (getLineHeight() * getCumulativeScale() / 2));
                break;
            case BOTTOM:
                textScreenY = (getScreenY() + (getScreenHeight() - getLineHeight() * getCumulativeScale()) - (getPadding(Side.BOTTOM) * getCumulativeScale()));
                break;
        }
    }

    @Override
    public void tryFitToContents(boolean ignoreTopLeftPadding)
    {
        if (shouldFitToContentsX())
        {
            float textWidth = font.width(getText()) / getCumulativeScale();
            float paddingWidth = getPadding(Side.LEFT) + getPadding(Side.RIGHT);

            setWidth(textWidth + paddingWidth);
        }

        if (shouldFitToContentsY())
        {
            float textHeight = getLineHeight() / getCumulativeScale();
            float paddingHeight = getPadding(Side.TOP) + getPadding(Side.BOTTOM);

            setHeight(textHeight + paddingHeight);
        }
    }

    @Override
    public void onRender(@Nonnull RenderArgs renderArgs)
    {
        float z = isDraggingOrAncestorIsDragging() ? 100.0f : 0.0f;

        try
        {
            // For some reason we can't just access the values in the matrix.
            // So we have to get the z translation via reflection. Nice.
            z = ObfuscationReflectionHelper.getPrivateValue(Matrix4f.class, renderArgs.matrixStack.last().pose(), "m23");
        }
        catch (Exception ex)
        {
//            Blocklings.LOGGER.warn(ex.toString());
        }

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(textScreenX, textScreenY, z);
        matrixStack.scale(getCumulativeScale(), getCumulativeScale(), 1.0f);
        renderShadowedText(matrixStack, text, getTextColour().argb());
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
        tryFitToContents();
    }

    /**
     * @return the text colour.
     */
    @Nonnull
    public Colour getTextColour()
    {
        return textColour;
    }

    /**
     * Sets the text colour.
     */
    public void setTextColour(Colour textColour)
    {
        this.textColour = textColour;
    }
}
