package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.controls.ScreenControl;
import com.willr27.blocklings.client.gui.control.event.events.TryDragEvent;
import com.willr27.blocklings.client.gui.control.event.events.TryHoverEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.*;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.util.ScissorBounds;
import com.willr27.blocklings.client.gui.util.ScissorStack;
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
    public void forwardTick()
    {
        for (BaseControl child : getChildrenCopy())
        {
            child.forwardTick();
        }

        onTick();
    }

    @Override
    protected void onTick()
    {

    }

    @Override
    public void forwardRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableDepthTest();

        applyScissor(scissorStack);
        onRenderUpdate(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
        onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        for (BaseControl child : getChildrenCopy())
        {
            child.forwardRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
        }

        undoScissor(scissorStack);
    }

    @Override
    protected void onRenderUpdate(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {

    }

    @Override
    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
//        if (!(this instanceof ScreenControl)) renderRectangle(matrixStack, getPixelX(), getPixelY(), (int) getPixelWidth(), (int) getPixelHeight(), 0xff000000);
        renderRectangle(matrixStack, getPixelX() + getPixelPadding().left, getPixelY() + getPixelPadding().top, (int) getPixelWidthWithoutPadding(), (int) getPixelHeightWithoutPadding(), getBackgroundColour());
    }

    @Override
    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {

    }

    protected void renderRectangleAsBackground(@Nonnull MatrixStack matrixStack, int colour)
    {
        renderRectangle(matrixStack, (int) getPixelX(), (int) getPixelY(), (int) getPixelWidth(), (int) getPixelHeight(), colour);
    }

    protected void renderRectangleAsBackground(@Nonnull MatrixStack matrixStack, int colour, double dx, double dy, double width, double height)
    {
        renderRectangle(matrixStack, (int) getPixelX() + dx * getPixelScaleX(), (int) getPixelY() + dy * getPixelScaleY(), (int) (width * getPixelScaleX()), (int) (height * getPixelScaleY()), colour);
    }

    protected void renderBackgroundColour(@Nonnull MatrixStack matrixStack)
    {
        renderRectangleAsBackground(matrixStack, getBackgroundColour());
    }

    protected void renderTextureAsBackground(@Nonnull MatrixStack matrixStack, @Nonnull Texture texture)
    {
        renderTexture(matrixStack, texture, getPixelX(), getPixelY(), getPixelScaleX(), getPixelScaleY());
    }

    protected void renderTextureAsBackground(@Nonnull MatrixStack matrixStack, @Nonnull Texture texture, double dx, double dy)
    {
        renderTexture(matrixStack, texture, getPixelX() + dx * getPixelScaleX(), getPixelY() + dy * getPixelScaleX(), getPixelScaleX(), getPixelScaleY());
    }

    /**
     * Applies any scissoring to the control before rendering.
     */
    protected void applyScissor(@Nonnull ScissorStack scissorStack)
    {
        if (shouldScissor())
        {
            scissorStack.push(new ScissorBounds((int) Math.round(getPixelX()), (int) Math.round(getPixelY()), (int) Math.round(getPixelWidth()), (int) Math.round(getPixelHeight())));
            scissorStack.enable();
        }
    }

    /**
     * Undoes any scissoring to the control after rendering.
     */
    protected void undoScissor(@Nonnull ScissorStack scissorStack)
    {
        if (shouldScissor())
        {
            scissorStack.pop();
            scissorStack.disable();
        }
    }

    @Override
    public void forwardHover(@Nonnull TryHoverEvent e)
    {
        if (!contains(e.mouseX, e.mouseY))
        {
            return;
        }

        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardHover(e);
            }
        }

        if (!e.isHandled() && getParent() != null)
        {
            setIsHovered(true);
            e.setIsHandled(true);
        }
    }

    @Override
    public void onHoverEnter()
    {

    }

    @Override
    public void onHoverExit()
    {

    }

    @Override
    public void onPressStart()
    {

    }

    @Override
    public void onPressEnd()
    {

    }

    @Override
    public void onFocused()
    {

    }

    @Override
    public void onUnfocused()
    {

    }

    @Override
    public void forwardTryDrag(@Nonnull TryDragEvent e)
    {
        if (!contains(e.mouseX, e.mouseY))
        {
            return;
        }

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
            setPixelX(mouseX - getPixelWidth() / 2.0);
        }

        if (isDraggableY())
        {
            setPixelY(mouseY - getPixelHeight() / 2.0);
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
                    setX(getParent().toLocalX(getParent().getPixelX()) / getParent().getInnerScale().x + getParent().getScrollX());
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
                    setX(getParent().toLocalX(getParent().getPixelX() + getParent().getPixelWidth()) / getParent().getInnerScale().x - getWidth() + getParent().getScrollX());
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
                    setY(getParent().toLocalY(getParent().getPixelY()) / getParent().getInnerScale().y + getParent().getScrollY());
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
                    setY(getParent().toLocalY(getParent().getPixelY() + getParent().getPixelHeight()) / getParent().getInnerScale().y - getHeight() + getParent().getScrollY());
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

        if (getPixelX() <= getParent().getPixelX())
        {
            sides.add(Side.LEFT);
        }

        if (getPixelX() + getPixelWidth() >= getParent().getPixelX() + getParent().getPixelWidth())
        {
            sides.add(Side.RIGHT);
        }

        if (getPixelY() <= getParent().getPixelY())
        {
            sides.add(Side.TOP);
        }

        if (getPixelY() + getPixelHeight() >= getParent().getPixelY() + getParent().getPixelHeight())
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
    public void forwardGlobalMouseClicked(@Nonnull MouseClickedEvent e)
    {
        if (!contains(e.mouseX, e.mouseY))
        {
            return;
        }

        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardGlobalMouseClicked(e);
            }
        }

        if (!e.isHandled())
        {
            onGlobalMouseClicked(e);
        }
    }

    @Override
    protected void onGlobalMouseClicked(@Nonnull MouseClickedEvent e)
    {

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
                onMouseClicked(e);

                if (e.isHandled())
                {
                    setIsPressed(true);
                    setIsFocused(true);
                }
            }
        }
    }

    @Override
    public void onMouseClicked(@Nonnull MouseClickedEvent e)
    {
        e.setIsHandled(true);
    }

    @Override
    public void forwardGlobalMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        if (!contains(e.mouseX, e.mouseY))
        {
            return;
        }

        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardGlobalMouseReleased(e);
            }
        }

        if (!e.isHandled())
        {
            onGlobalMouseReleased(e);
        }
    }

    @Override
    protected void onGlobalMouseReleased(@Nonnull MouseReleasedEvent e)
    {

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
    public void forwardGlobalMouseScrolled(@Nonnull MouseScrolledEvent e)
    {
        if (!contains(e.mouseX, e.mouseY))
        {
            return;
        }

        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardGlobalMouseScrolled(e);
            }
        }

        if (!e.isHandled())
        {
            onGlobalMouseScrolled(e);
        }
    }

    @Override
    protected void onGlobalMouseScrolled(@Nonnull MouseScrolledEvent e)
    {

    }

    @Override
    public void forwardMouseScrolled(@Nonnull MouseScrolledEvent e)
    {
        if (!contains(e.mouseX, e.mouseY))
        {
            return;
        }

        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
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
        double amountScrolled = 0.0;
        double amountToScroll = -10.0 * e.amount;

        if (GuiUtil.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))
        {
            amountScrolled = scrollX(amountToScroll);
        }
        else
        {
            amountScrolled = scrollY(amountToScroll);
        }

        if (amountScrolled == amountToScroll)
        {
            e.setIsHandled(true);
        }
        else
        {
            e.amount = e.amount * (1.0 - (amountScrolled / amountToScroll));
        }
    }

    @Override
    public void forwardGlobalKeyPressed(@Nonnull KeyPressedEvent e)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardGlobalKeyPressed(e);
            }
        }

        if (!e.isHandled())
        {
            onGlobalKeyPressed(e);
        }
    }

    @Override
    protected void onGlobalKeyPressed(@Nonnull KeyPressedEvent e)
    {

    }

    @Override
    public void onKeyPressed(@Nonnull KeyPressedEvent e)
    {

    }

    @Override
    public void forwardGlobalKeyReleased(@Nonnull KeyReleasedEvent e)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardGlobalKeyReleased(e);
            }
        }

        if (!e.isHandled())
        {
            onGlobalKeyReleased(e);
        }
    }

    @Override
    protected void onGlobalKeyReleased(@Nonnull KeyReleasedEvent e)
    {

    }

    @Override
    public void onKeyReleased(@Nonnull KeyReleasedEvent e)
    {

    }

    @Override
    public void forwardGlobalCharTyped(@Nonnull CharTypedEvent e)
    {
        for (BaseControl child : getReverseChildrenCopy())
        {
            if (!e.isHandled())
            {
                child.forwardGlobalCharTyped(e);
            }
        }

        if (!e.isHandled())
        {
            onGlobalCharTyped(e);
        }
    }

    @Override
    protected void onGlobalCharTyped(@Nonnull CharTypedEvent e)
    {

    }

    @Override
    public void onCharTyped(@Nonnull CharTypedEvent e)
    {

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
        return pixelX >= getPixelX() && pixelX <= getPixelX() + getPixelWidth() && pixelY >= getPixelY() && pixelY <= getPixelY() + getPixelHeight();
    }
}
