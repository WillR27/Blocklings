package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Displays a block of text.
 */
@OnlyIn(Dist.CLIENT)
public class TextBlockControl extends Control
{
    /**
     * The text component to render.
     */
    @Nonnull
    private ITextComponent text = new StringTextComponent("");

    /**
     * Whether to trim the text to fit the width of the control.
     */
    private boolean shouldTrimText = true;

    /**
     * Whether to draw shadowed text or not.
     */
    private boolean shouldRenderShadow = true;

    /**
     * The text colour.
     */
    private int textColour = 0xffffffff;

    /**
     * The screen x position to render the text.
     */
    private float textScreenX = 0;

    /**
     * The screen y position to render the text.
     */
    private float textScreenY = 0;

    @Override
    protected void measureSelf(double availableWidth, double availableHeight)
    {
        double width = getWidth();
        double height = getHeight();

        if (getWidthPercentage() != null && DoubleUtil.isPositiveAndFinite(availableWidth))
        {
            width = availableWidth * getWidthPercentage();
        }
        else if (shouldFitWidthToContent())
        {
            double textWidth = font.width(getText());
            width = textWidth + getPaddingWidth();
        }

        if (getHeightPercentage() != null && DoubleUtil.isPositiveAndFinite(availableHeight))
        {
            height = availableHeight * getHeightPercentage();
        }
        else if (shouldFitHeightToContent())
        {
            double textHeight = font.lineHeight;
            height = textHeight + getPaddingHeight();
        }

        setDesiredWidth(width);
        setDesiredHeight(height);
    }

    @Override
    public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        textScreenX = (float) (getPixelX() / getGuiScale());
        textScreenY = (float) (getPixelY() / getGuiScale());

        float x = getGuiScale() == 1.0f ? Math.round(textScreenX) : textScreenX;
        float y = getGuiScale() == 1.0f ? Math.round(textScreenY) : textScreenY;

        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(x, y, 5.0);
        matrixStack2.scale((float) getScaleX(), (float) getScaleY(), 1.0f);

        if (shouldRenderShadow())
        {
            renderShadowedText(matrixStack2, getTextToRender(), getTextColour());
        }
        else
        {
            renderText(matrixStack2, getTextToRender(), getTextColour());
        }
    }

    @Override
    public void addChild(@Nonnull BaseControl child)
    {
        throw new UnsupportedOperationException("TextBlockControl does not support adding children.");
    }

    @Override
    public void insertChildBefore(@Nonnull BaseControl controlToInsert, @Nonnull BaseControl controlToInsertBefore)
    {
        throw new UnsupportedOperationException("TextBlockControl does not support adding children.");
    }

    @Override
    public void insertChildAfter(@Nonnull BaseControl controlToInsert, @Nonnull BaseControl controlToInsertAfter)
    {
        throw new UnsupportedOperationException("TextBlockControl does not support adding children.");
    }

    /**
     * @return gets the text to render (e.g. might be trimmed to fit).
     */
    public ITextComponent getTextToRender()
    {
        ITextComponent textToRender = text;

        if (shouldTrimText())
        {
            textToRender = new StringTextComponent(GuiUtil.trimWithEllipses(font, getText(), (int) Math.round(getWidthWithoutPadding())));
        }

        return textToRender;
    }

    @Nonnull
    public String getText()
    {
        return text.getString();
    }

    public void setText(@Nonnull String text)
    {
        this.text = new StringTextComponent(text);
    }

    public void setText(@Nonnull ITextComponent text)
    {
        this.text = text;
    }

    /**
     * @return whether to trim the text to fit the width of the control.
     */
    public boolean shouldTrimText()
    {
        return shouldTrimText;
    }

    /**
     * Sets whether to trim the text to fit the width of the control.
     */
    public void setShouldTrimText(boolean shouldTrimText)
    {
        this.shouldTrimText = shouldTrimText;
    }

    /**
     * @return whether to render shadowed text.
     */
    public boolean shouldRenderShadow()
    {
        return shouldRenderShadow;
    }

    /**
     * Sets whether to render shadowed text.
     */
    public void setShouldRenderShadow(boolean shouldRenderShadow)
    {
        this.shouldRenderShadow = shouldRenderShadow;
    }

    /**
     * @return the text colour.
     */
    @Nonnull
    public int getTextColour()
    {
        return textColour;
    }

    /**
     * Sets the text colour.
     */
    public void setTextColour(int textColour)
    {
        this.textColour = textColour;
    }
}
