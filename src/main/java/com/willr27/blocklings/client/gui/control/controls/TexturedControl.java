package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A control for all tabbed blockling controls.
 */
@OnlyIn(Dist.CLIENT)
public class TexturedControl extends Control
{
    /**
     * The background texture.
     */
    @Nonnull
    private Texture backgroundTexture;

    /**
     * The pressed background texture.
     */
    @Nullable
    private Texture pressedBackgroundTexture;

    /**
     * @param backgroundTexture the background texture.
     */
    public TexturedControl(@Nonnull Texture backgroundTexture)
    {
        this(backgroundTexture, null);
    }

    /**
     * @param backgroundTexture the background texture.
     */
    public TexturedControl(@Nonnull Texture backgroundTexture, @Nullable Texture pressedBackgroundTexture)
    {
        super();

        setFitWidthToContent(true);
        setFitHeightToContent(true);
        setBackgroundTexture(backgroundTexture);
        setPressedBackgroundTexture(pressedBackgroundTexture);

        setWidth(backgroundTexture.width);
        setHeight(backgroundTexture.height);
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
            double maxX = isPressed() && getPressedBackgroundTexture() != null ? getPressedBackgroundTexture().width : getBackgroundTexture().width;

            for (BaseControl childControl : getChildren())
            {
                if (childControl.getVisibility() == Visibility.COLLAPSED)
                {
                    continue;
                }

                if (childControl.getWidthPercentage() != null)
                {
                    continue;
                }

                double childX = (childControl.getX() + childControl.getWidth() + childControl.getMargin().right) * getInnerScale().x;

                if (childX > maxX)
                {
                    maxX = childX;
                }
            }

            width = maxX != Double.NEGATIVE_INFINITY ? maxX + getPaddingWidth() : 0.0;
        }

        if (getHeightPercentage() != null && DoubleUtil.isPositiveAndFinite(availableHeight))
        {
            height = availableHeight * getHeightPercentage();
        }
        else if (shouldFitHeightToContent())
        {
            double maxY = isPressed() && getPressedBackgroundTexture() != null ? getPressedBackgroundTexture().height : getBackgroundTexture().height;

            for (BaseControl childControl : getChildren())
            {
                if (childControl.getVisibility() == Visibility.COLLAPSED)
                {
                    continue;
                }

                if (childControl.getHeightPercentage() != null)
                {
                    continue;
                }

                double childY = (childControl.getY() + childControl.getHeight() + childControl.getMargin().bottom) * getInnerScale().y;

                if (childY > maxY)
                {
                    maxY = childY;
                }
            }

            height = maxY != Double.NEGATIVE_INFINITY ? maxY + getPaddingHeight() : 0.0;
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
    public void measureChildren()
    {
        for (BaseControl child : getChildrenCopy())
        {
            if (child.getVisibility() == Visibility.COLLAPSED)
            {
                continue;
            }

            double availableWidth = ((getDesiredWidth() - getPaddingWidth()) / getInnerScale().x) - child.getMarginWidth();
            double availableHeight = ((getDesiredHeight() - getPaddingHeight()) / getInnerScale().y) - child.getMarginHeight();

            child.doMeasure(availableWidth, availableHeight);
        }
    }

    @Override
    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        if (isPressed() && !isDraggingOrAncestor() && getPressedBackgroundTexture() != null)
        {
            renderTextureAsBackground(matrixStack, pressedBackgroundTexture);
        }
        else
        {
            renderTextureAsBackground(matrixStack, backgroundTexture);
        }
    }

    /**
     * @return the background texture.
     */
    @Nonnull
    public Texture getBackgroundTexture()
    {
        return backgroundTexture;
    }

    /**
     * Sets the background texture.
     */
    public void setBackgroundTexture(@Nonnull Texture backgroundTexture)
    {
        this.backgroundTexture = backgroundTexture;

        if (shouldFitToContent())
        {
            markMeasureDirty(true);
        }
    }

    /**
     * @return the pressed background texture.
     */
    @Nullable
    public Texture getPressedBackgroundTexture()
    {
        return pressedBackgroundTexture;
    }

    /**
     * Sets the pressed background texture.
     */
    public void setPressedBackgroundTexture(@Nullable Texture pressedBackgroundTexture)
    {
        this.pressedBackgroundTexture = pressedBackgroundTexture;

        if (shouldFitToContent())
        {
            markMeasureDirty(true);
        }
    }
}
