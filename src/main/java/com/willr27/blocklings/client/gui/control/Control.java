package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.controls.ScreenControl;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiTextures;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

/**
 * A base class for all controls.
 */
@OnlyIn(Dist.CLIENT)
public class Control extends BaseControl
{
    @Override
    public void doMeasure(double availableWidth, double availableHeight)
    {
        setMeasuring(true);
        measureSelf(availableWidth, availableHeight);
        setMeasuring(false);
        markMeasureDirty(false);
        measureChildren();
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
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren())
            {
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
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren())
            {
                double childY = (childControl.getY() + childControl.getHeight() + childControl.getMargin().bottom) * getInnerScale().y;

                if (childY > maxY)
                {
                    maxY = childY;
                }
            }

            height = maxY != Double.NEGATIVE_INFINITY ? maxY + getPaddingHeight() : 0.0;
        }

        setDesiredWidth(width);
        setDesiredHeight(height);
    }

    @Override
    public void measureChildren()
    {
        for (BaseControl child : getChildrenCopy())
        {
            double availableWidth = ((getDesiredWidth() - getPaddingWidth()) / getInnerScale().x) - child.getMarginWidth();
            double availableHeight = ((getDesiredHeight() - getPaddingHeight()) / getInnerScale().y) - child.getMarginHeight();

            if (shouldFitWidthToContent())
            {
                availableWidth = getMaxWidth();
            }

            if (shouldFitHeightToContent())
            {
                availableHeight = getMaxHeight();
            }

            child.doMeasure(availableWidth, availableHeight);
        }
    }

    @Override
    public void doArrange()
    {
        setArranging(true);
        arrange();
        setArranging(false);
        markArrangeDirty(false);
        calculateScroll();

        for (BaseControl child : getChildrenCopy())
        {
            child.doArrange();
        }
    }

    @Override
    protected void arrange()
    {
        for (BaseControl control : getChildrenCopy())
        {
            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            double x = (((getWidthWithoutPadding() / getInnerScale().x) - control.getWidthWithMargin()) * getHorizontalAlignmentFor(control)) + control.getMargin().left;
            double y = (((getHeightWithoutPadding() / getInnerScale().y) - control.getHeightWithMargin()) * getVerticalAlignmentFor(control)) + control.getMargin().top;

            control.setX(x);
            control.setY(y);
        }
    }

    @Override
    public void calculateScroll()
    {
        if (canScrollHorizontally())
        {
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl child : getChildren())
            {
                double childMinX = child.getX() - child.getMargin().left;
                double childMaxX = child.getX() + child.getWidth() + child.getMargin().right;

                if (childMinX < minX)
                {
                    minX = childMinX;
                }

                if (childMaxX > maxX)
                {
                    maxX = childMaxX;
                }
            }

            if (minX != Double.POSITIVE_INFINITY && maxX != Double.NEGATIVE_INFINITY)
            {
                double scaledWidth = getWidthWithoutPadding() / getInnerScale().x;
                double scrollableWidth = maxX - minX - scaledWidth;

                if (scrollableWidth > 0.0)
                {
                    setMinScrollX(minX);
                    setMaxScrollX(maxX - scaledWidth);
                }
                else
                {
                    setMinScrollX(0.0);
                    setMaxScrollX(0.0);
                }
            }
        }

        if (canScrollVertically())
        {
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl child : getChildren())
            {
                double childMinY = child.getY() - child.getMargin().top;
                double childMaxY = child.getY() + child.getHeight() + child.getMargin().bottom;

                if (childMinY < minY)
                {
                    minY = childMinY;
                }

                if (childMaxY > maxY)
                {
                    maxY = childMaxY;
                }
            }

            if (minY != Double.POSITIVE_INFINITY && maxY != Double.NEGATIVE_INFINITY)
            {
                double scaledHeight = getHeightWithoutPadding() / getInnerScale().y;
                double scrollableHeight = maxY - minY - scaledHeight;

                if (scrollableHeight > 0.0)
                {
                    setMinScrollY(minY);
                    setMaxScrollY(maxY - scaledHeight);
                }
                else
                {
                    setMinScrollY(0.0);
                    setMaxScrollY(0.0);
                }
            }
            else
            {
                setMinScrollX(0.0);
                setMaxScrollX(0.0);
            }
        }
    }

    @Override
    public void forwardRender(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        // Scale the control, but also make sure to cancel out the translation caused by the scaling.
        matrixStack.pushPose();
//        matrixStack.scale((float) getPixelScaleX(), (float) getPixelScaleY(), 1.0f);
//        matrixStack.translate((getActualPixelX() / getPixelScaleX()) - getActualPixelX(), (getActualPixelY() / getPixelScaleY()) - getActualPixelY(), 0.0);

        render(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.popPose();

        for (BaseControl child : getChildrenCopy())
        {
            child.forwardRender(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        int x = (int) getActualPixelX();
        int y = (int) getActualPixelY();
        int px = (int) (x + getPixelPadding().left);
        int py = (int) (y + getPixelPadding().top);
        int width = (int) getPixelWidth();
        int height = (int) getPixelHeight();
        int pwidth = (int) getPixelWidthWithoutPadding();
        int pheight = (int) getPixelHeightWithoutPadding();

        if (!(this instanceof ScreenControl)) renderRectangle(matrixStack, x, y, width, height, 0xff000000);
        renderRectangle(matrixStack, px, py, pwidth, pheight, getBackgroundColour());

        renderTextureAsBackground(matrixStack, GuiTextures.DROPDOWN_DOWN_ARROW);
    }

    protected void renderRectangleAsBackground(@Nonnull MatrixStack matrixStack, int colour)
    {
        renderRectangle(matrixStack, (int) getActualPixelX(), (int) getActualPixelY(), (int) getPixelWidth(), (int) getPixelHeight(), colour);
    }

    protected void renderBackgroundColour(@Nonnull MatrixStack matrixStack)
    {
        renderRectangleAsBackground(matrixStack, getBackgroundColour());
    }

    protected void renderTextureAsBackground(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture)
    {
        renderTexture(matrixStack, texture, getActualPixelX(), getActualPixelY(), getPixelScaleX(), getPixelScaleY());
    }

    public void forwardMouseReleased(double mouseX, double mouseY, int button)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
//            if (child.isMouseOver(mouseX, mouseY))
            {
                child.forwardMouseReleased(mouseX, mouseY, button);
            }
        }

        mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(double mouseX, double mouseY, int button)
    {

    }

    @Override
    public void forwardMouseScrolled(double mouseX, double mouseY, double amount)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
//            if (child.isMouseOver(mouseX, mouseY))
            {
                child.forwardMouseScrolled(mouseX, mouseY, amount);
            }
        }

        mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    protected void mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (GuiUtil.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))
        {
            scrollX(-10.0 * amount);
        }
        else
        {
            scrollY(-10.0 * amount);
        }
    }

    @Override
    protected void onChildDesiredSizeChanged(@Nonnull BaseControl child)
    {
        if (shouldFitToContent())
        {
            markMeasureDirty(true);
        }

        if (!isArranging())
        {
            markArrangeDirty(true);
        }
    }

    @Override
    protected void onChildSizeChanged(@Nonnull BaseControl child)
    {
        if (!isArranging() && shouldFitToContent())
        {
            markMeasureDirty(true);
        }

        if (!isArranging())
        {
            markArrangeDirty(true);
        }
    }

    @Override
    protected void onChildMarginChanged(@Nonnull BaseControl child)
    {
        if (!isArranging())
        {
            markArrangeDirty(true);
        }
    }

    @Override
    protected void onChildPositionSizeChanged(@Nonnull BaseControl child)
    {
        if (!isArranging())
        {
            markArrangeDirty(true);
        }
    }

    @Override
    protected void onChildAlignmentChanged(@Nonnull BaseControl child)
    {
        markArrangeDirty(true);
    }

    @Override
    public boolean isInside(double screenX, double screenY)
    {
        return screenX >= getActualPixelX() && screenX <= getActualPixelX() + getWidth() && screenY >= getActualPixelY() && screenY <= getActualPixelY() + getHeight();
    }
}
