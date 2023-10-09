package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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
    private Component text = new TextComponent("");

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

    /**
     * The line height.
     */
    private int lineHeight = GuiUtil.get().getLineHeight();

    /**
     */
    public TextBlockControl()
    {
        super();

        setInteractive(false);
        setFitHeightToContent(true);
    }

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
            double textWidth = GuiUtil.get().getTextWidth(getTextString());
            width = textWidth + getPaddingWidth();
        }

        if (getHeightPercentage() != null && DoubleUtil.isPositiveAndFinite(availableHeight))
        {
            height = availableHeight * getHeightPercentage();
        }
        else if (shouldFitHeightToContent())
        {
            double textHeight = getLineHeight();
            height = textHeight + getPaddingHeight();
        }

        if (availableWidth >= 0.0)
        {
            setDesiredWidth(width);
        }

        if (availableHeight >= 0.0)
        {
            setDesiredHeight(height);
        }
    }

    @Override
    public void onRender(@Nonnull PoseStack poseStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(poseStack, scissorStack, mouseX, mouseY, partialTicks);

        double screenWidth = getPixelWidthWithoutPadding() / getGuiScale();
        double screenHeight = getPixelHeightWithoutPadding() / getGuiScale();
        double horizontalAlignment = getHorizontalContentAlignment() != null ? getHorizontalContentAlignment() : 0.0;
        double verticalAlignment = getVerticalContentAlignment() != null ? getVerticalContentAlignment() : 0.0;

        textScreenX = (float) (getPixelX() / getGuiScale() + (screenWidth - GuiUtil.get().getTextWidth(getTextToRender())) * horizontalAlignment + getPadding().left);
        textScreenY = (float) (getPixelY() / getGuiScale() + (screenHeight - getLineHeight()) * verticalAlignment + getPadding().top);

        float x = (float) (Math.round(textScreenX * getGuiScale()) / getGuiScale());
        float y = (float) (Math.round(textScreenY * getGuiScale()) / getGuiScale());

        float z = isDraggingOrAncestor() ? (float) getDraggedControl().getDragZ() : (float) getRenderZ();

        try
        {
            // For some reason we can't just access the values in the matrix.
            // So we have to get the z translation via reflection. Nice.
            z = ObfuscationReflectionHelper.getPrivateValue(Matrix4f.class, poseStack.last().pose(), "f_27614_");
        }
        catch (Exception ex)
        {
//            Blocklings.LOGGER.warn(ex.toString());
        }

        PoseStack poseStack2 = new PoseStack();
        poseStack2.translate(x, y, z);
        poseStack2.scale((float) getScaleX(), (float) getScaleY(), 1.0f);

        if (shouldRenderShadow())
        {
            renderShadowedText(poseStack2, getTextToRender(), getTextColour());
        }
        else
        {
            renderText(poseStack2, getTextToRender(), getTextColour());
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
    public FormattedCharSequence getTextToRender()
    {
        FormattedCharSequence textToRender = text.getVisualOrderText();

        if (shouldTrimText())
        {
            textToRender = Language.getInstance().getVisualOrder(GuiUtil.get().trimWithEllipsis(getText(), (int) Math.round(getWidthWithoutPadding())));
        }

        return textToRender;
    }

    /**
     * @return the text to render as a string.
     */
    @Nonnull
    public String getTextString()
    {
        return text.getString();
    }

    /**
     * @return the text to render.
     */
    @Nonnull
    public Component getText()
    {
        return text;
    }

    /**
     * Sets the text to render.
     *
     * @param text the text to render.
     */
    public void setText(@Nonnull String text)
    {
        this.text = new TextComponent(text);
    }

    /**
     * Sets the text to render.
     *
     * @param text the text to render.
     */
    public void setText(@Nonnull Component text)
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

    /**
     * @return the line height.
     */
    public int getLineHeight()
    {
        return lineHeight - (shouldRenderShadow() ? 0 : 1);
    }

    /**
     * Sets the line height.
     *
     * @param lineHeight the line height.
     */
    public void setLineHeight(int lineHeight)
    {
        this.lineHeight = lineHeight;
    }

    /**
     * Sets the line height to be the default line height.
     */
    public void useDefaultLineHeight()
    {
        setLineHeight(GuiUtil.get().getLineHeight());
    }

    /**
     * Sets the line height to be the default line height minus the height of descenders.
     */
    public void useDescenderlessLineHeight()
    {
        setLineHeight(GuiUtil.get().getLineHeight() - 1);
    }
}
