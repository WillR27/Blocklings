package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.controls.ScreenControl;
import com.willr27.blocklings.client.gui.control.event.events.TryDragEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiTextures;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.client.gui3.control.Side;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
    public void forwardRender(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {
        onRenderUpdate(matrixStack, mouseX, mouseY, partialTicks);
        onRender(matrixStack, mouseX, mouseY, partialTicks);

        for (BaseControl child : getChildrenCopy())
        {
            child.forwardRender(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void onRenderUpdate(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {

    }

    @Override
    public void onRender(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
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

    @Override
    public void forwardTryDrag(@Nonnull TryDragEvent e)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardTryDrag(e);
            }
        }

        if (!e.isHandled() && getParent() != null)
        {
            if (isPressed() && isDraggable())
            {
                double pixelDragDifX = e.mouseX - getScreen().getPressedStartPixelX();
                double pixelDragDifY = e.mouseY - getScreen().getPressedStartPixelY();
                double localDragDifX = pixelDragDifX / getPixelScaleX();
                double localDragDifY = pixelDragDifY / getPixelScaleY();
                double absLocalDragDifX = Math.abs(localDragDifX);
                double absLocalDragDifY = Math.abs(localDragDifY);

                boolean isDraggedX = absLocalDragDifX >= getDragThreshold();
                boolean isDraggedY = absLocalDragDifY >= getDragThreshold();

                if (isDraggedX || isDraggedY)
                {
                    setIsDragging(true);
                    e.setIsHandled(true);
                }
            }
        }
    }

    @Override
    public void onDragStart()
    {

    }

    @Override
    public void onDrag(double mouseX, double mouseY, float partialTicks)
    {
        if (isDraggableX())
        {
            setX(((getParent().toLocalX(mouseX) / getParent().getInnerScale().x) - getWidth() / 2.0) + getParent().getScrollX());
        }

        if (isDraggableY())
        {
            setY(((getParent().toLocalY(mouseY) / getParent().getInnerScale().y) - getHeight() / 2.0) + getParent().getScrollY());
        }

        List<Side> atParentBounds = getParentBoundsAt();
        double scrollAmount = 10.0 * partialTicks;

        if (isDraggableX())
        {
            if (atParentBounds.contains(Side.LEFT))
            {
                if (getParent().canScrollHorizontally())
                {
                    getParent().scrollX(scrollAmount * -1);
                }

                if (getParent().shouldBlockDrag())
                {
                    setX(getParent().toLocalX(getParent().getActualPixelX()) / getParent().getInnerScale().x + getParent().getScrollX());
                }
            }
            else if (atParentBounds.contains(Side.RIGHT))
            {
                if (getParent().canScrollHorizontally())
                {
                    getParent().scrollX(scrollAmount);
                }

                if (getParent().shouldBlockDrag())
                {
                    setX(getParent().toLocalX(getParent().getActualPixelX() + getParent().getPixelWidth()) / getParent().getInnerScale().x - getWidth() + getParent().getScrollX());
                }
            }
        }

        if (isDraggableY())
        {
            if (atParentBounds.contains(Side.TOP))
            {
                if (getParent().canScrollVertically())
                {
                    getParent().scrollY(scrollAmount * -1);
                }

                if (getParent().shouldBlockDrag())
                {
                    setY(getParent().toLocalY(getParent().getActualPixelY()) / getParent().getInnerScale().y + getParent().getScrollY());
                }
            }
            else if (atParentBounds.contains(Side.BOTTOM))
            {
                if (getParent().canScrollVertically())
                {
                    getParent().scrollY(scrollAmount);
                }

                if (getParent().shouldBlockDrag())
                {
                    setY(getParent().toLocalY(getParent().getActualPixelY() + getParent().getPixelHeight()) / getParent().getInnerScale().y - getHeight() + getParent().getScrollY());
                }
            }
        }
    }

    /**
     * @return the sides of the parent the control is currently at or exceeding, null if not.
     */
    @Nonnull
    public List<Side> getParentBoundsAt()
    {
        List<Side> sides = new ArrayList<>();

        if (getActualPixelX() <= getParent().getActualPixelX())
        {
            sides.add(Side.LEFT);
        }

        if (getActualPixelX() + getPixelWidth() >= getParent().getActualPixelX() + getParent().getPixelWidth())
        {
            sides.add(Side.RIGHT);
        }

        if (getActualPixelY() <= getParent().getActualPixelY())
        {
            sides.add(Side.TOP);
        }

        if (getActualPixelY() + getPixelHeight() >= getParent().getActualPixelY() + getParent().getPixelHeight())
        {
            sides.add(Side.BOTTOM);
        }

        return sides;
    }

    @Override
    public void onDragEnd()
    {
        if (getParent() != null)
        {
            getParent().markArrangeDirty(true);
        }
    }

    @Override
    public void forwardMouseClicked(@Nonnull MouseClickedEvent e)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
            if (child.contains(e.mouseX, e.mouseY))
            {
                child.forwardMouseClicked(e);
            }
        }

        if (!e.isHandled())
        {
            eventBus.post(this, e);

            if (!e.isHandled())
            {
                setIsPressed(true);
                setIsFocused(true);
                onMouseClicked(e);
            }
        }
    }

    @Override
    public void onMouseClicked(@Nonnull MouseClickedEvent e)
    {
        e.setIsHandled(true);
    }

    @Override
    public void forwardMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
            if (child.contains(e.mouseX, e.mouseY))
            {
                child.forwardMouseReleased(e);
            }
        }

        if (!e.isHandled())
        {
            eventBus.post(this, e);

            if (!e.isHandled())
            {
                onMouseReleased(e);
            }
        }
    }

    @Override
    protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        e.setIsHandled(true);
    }

    @Override
    public void forwardMouseScrolled(@Nonnull MouseScrolledEvent e)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
            if (child.contains(e.mouseX, e.mouseY))
            {
                child.forwardMouseScrolled(e);
            }
        }

        if (!e.isHandled())
        {
            eventBus.post(this, e);

            if (!e.isHandled())
            {
                onMouseScrolled(e);
            }
        }
    }

    @Override
    protected void onMouseScrolled(@Nonnull MouseScrolledEvent e)
    {
        if (GuiUtil.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))
        {
            scrollX(-10.0 * e.amount);
        }
        else
        {
            scrollY(-10.0 * e.amount);
        }

        e.setIsHandled(true);
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
    public boolean contains(double pixelX, double pixelY)
    {
        return pixelX >= getActualPixelX() && pixelX <= getActualPixelX() + getPixelWidth() && pixelY >= getActualPixelY() && pixelY <= getActualPixelY() + getPixelHeight();
    }
}
