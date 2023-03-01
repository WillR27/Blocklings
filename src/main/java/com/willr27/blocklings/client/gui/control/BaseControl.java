package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.Offset;
import com.willr27.blocklings.client.gui.Position;
import com.willr27.blocklings.client.gui.Scale;
import com.willr27.blocklings.client.gui.Size;
import com.willr27.blocklings.client.gui.control.controls.ScreenControl;
import com.willr27.blocklings.client.gui.control.event.events.TryDragEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui.properties.Margin;
import com.willr27.blocklings.client.gui.properties.Padding;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.client.gui3.util.GuiUtil;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A base class for {@link Control}. Used to reduce the size of {@link Control}.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BaseControl extends GuiControl
{
    @Nonnull
    public final Random random = new Random();

    @Nonnull
    public final ControlEventBus eventBus = new ControlEventBus();

    private boolean isMeasuring = false;

    private boolean isArranging = false;

    @Nullable
    private BaseControl parent = null;

    @Nonnull
    private final List<BaseControl> children = new ArrayList<>();

    @Nonnull
    private final Scale innerScale = new Scale(1.0, 1.0);

    @Nonnull
    private final Size desiredSize = new Size(100.0, 30.0);

    @Nonnull
    private final Size minSize = new Size(0.0, 0.0);
    @Nonnull
    private final Size maxSize = new Size(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    @Nonnull
    private final Size size = new Size(desiredSize.width, desiredSize.height);

    private boolean shouldFitWidthToContent = false;

    private boolean shouldFitHeightToContent = false;

    @Nullable
    private Double widthPercentage = null;

    @Nullable
    private Double heightPercentage = null;

    @Nonnull
    private final Margin margin = new Margin(0.0, 0.0, 0.0, 0.0);

    @Nonnull
    private final Padding padding = new Padding(0.0, 0.0, 0.0, 0.0);

    @Nonnull
    private final Position position = new Position(0.0, 0.0);

    @Nullable
    private Double horizontalAlignment = null;
    @Nullable
    private Double verticalAlignment = null;

    @Nullable
    private Double horizontalContentAlignment = null;
    @Nullable
    private Double verticalContentAlignment = null;

    @Nonnull
    private final Offset scroll = new Offset(0.0, 0.0);

    @Nonnull
    private final Offset minScroll = new Offset(0.0, 0.0);

    @Nonnull
    private final Offset maxScroll = new Offset(0.0, 0.0);

    private boolean canScrollHorizontally = false;

    private boolean canScrollVertically = false;

    private boolean isDraggableX = true;

    private boolean isDraggableY = true;

    private double dragThreshold = 4.0;

    private boolean shouldBlockDrag = true;

    private boolean shouldScissor = true;

    private int backgroundColour = 0x00000000;

    /**
     * Calls {@link #measureSelf(double, double)} then {@link #measureChildren()} while also setting
     * {@link #isMeasuring} appropriately and {@link #markMeasureDirty(boolean)} to false after {@link #measureSelf(double, double)}.
     *
     * @param availableWidth the available width to measure with.
     * @param availableHeight the available height to measure with.
     */
    public abstract void doMeasure(double availableWidth, double availableHeight);

    /**
     * Forwards the call to {@link #doMeasure(double, double)} to each child control. Might be overridden to calculate
     * the available width and height for each child control.
     */
    public abstract void measureChildren();

    /**
     * Uses the given {@param availableWidth} and {@param availableHeight} to calculate the desired width and height of
     * this control.
     *
     * @param availableWidth the available width to measure with.
     * @param availableHeight the available height to measure with.
     */
    protected abstract void measureSelf(double availableWidth, double availableHeight);

    public abstract void doArrange();
    protected abstract void arrange();

    protected abstract void calculateScroll();

    public abstract void forwardRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks);
    protected abstract void onRenderUpdate(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks);
    protected abstract void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks);

    public abstract void forwardTryDrag(@Nonnull TryDragEvent e);
    public abstract void onDragStart();
    public abstract void onDrag(double mouseX, double mouseY, float partialTicks);
    public abstract void onDragEnd();

    abstract public void forwardMouseClicked(@Nonnull MouseClickedEvent e);
    abstract protected void onMouseClicked(@Nonnull MouseClickedEvent e);

    abstract public void forwardMouseReleased(@Nonnull MouseReleasedEvent e);
    abstract protected void onMouseReleased(@Nonnull MouseReleasedEvent e);

    abstract public void forwardMouseScrolled(@Nonnull MouseScrolledEvent e);
    abstract protected void onMouseScrolled(@Nonnull MouseScrolledEvent e);

    abstract protected void onChildDesiredSizeChanged(@Nonnull BaseControl child);
    abstract protected void onChildSizeChanged(@Nonnull BaseControl child);
    abstract protected void onChildMarginChanged(@Nonnull BaseControl child);
    abstract protected void onChildPositionSizeChanged(@Nonnull BaseControl child);
    abstract protected void onChildAlignmentChanged(@Nonnull BaseControl child);

    abstract public boolean contains(double pixelX, double pixelY);

    public boolean isMeasuring()
    {
        return isMeasuring;
    }

    public void setMeasuring(boolean measuring)
    {
        isMeasuring = measuring;
    }

    public void markMeasureDirty(boolean isDirty)
    {
        if (getScreen() != null)
        {
            if (isDirty)
            {
                getScreen().addToMeasureQueue(this);
            }
            else
            {
                getScreen().removeFromMeasureQueue(this);
            }
        }
    }

//    public void markParentMeasureDirty(boolean isDirty)
//    {
//        if (getParent() != null)
//        {
//            getParent().markMeasureDirty(isDirty);
//        }
//    }

    public boolean isArranging()
    {
        return isArranging;
    }

    public void setArranging(boolean arranging)
    {
        isArranging = arranging;
    }

    public void markArrangeDirty(boolean isDirty)
    {
        if (getScreen() != null)
        {
            if (isDirty)
            {
                getScreen().addToArrangeQueue(this);
            }
            else
            {
                getScreen().removeFromArrangeQueue(this);
            }
        }
    }

//    public void markParentArrangeDirty(boolean isDirty)
//    {
//        if (getParent() != null)
//        {
//            getParent().markArrangeDirty(isDirty);
//        }
//    }

    @Nullable
    public BaseControl getParent()
    {
        return parent;
    }

    public void setParent(@Nullable BaseControl parent)
    {
        parent.addChild(this);
    }

    public List<BaseControl> getChildren()
    {
        return children;
    }

    public List<BaseControl> getChildrenCopy()
    {
        return new ArrayList<>(children);
    }

    public List<BaseControl> getReverseChildrenCopy()
    {
        List<BaseControl> reverseChildren = new ArrayList<>(children);
        Collections.reverse(reverseChildren);

        return reverseChildren;
    }

    public boolean isChild(@Nonnull BaseControl child)
    {
        return getChildren().contains(child);
    }

    public void addChild(@Nonnull BaseControl child)
    {
        if (getChildren().contains(child))
        {
            return;
        }

        children.add(child);
        child.parent = this;

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    public void insertChildBefore(@Nonnull BaseControl controlToInsert, @Nonnull BaseControl controlToInsertBefore)
    {
        int index = children.indexOf(controlToInsert);

        if (index != -1)
        {
            children.remove(index);
        }

        int beforeIndex = children.indexOf(controlToInsertBefore);

        if (beforeIndex == -1)
        {
            if (index != -1)
            {
                children.add(index, controlToInsert);
            }

            throw new IllegalArgumentException("The given control to insert before is not a child of this control.");
        }

        if (index == beforeIndex)
        {
            if (index != -1)
            {
                children.add(index, controlToInsert);
            }

            return;
        }

        children.add(beforeIndex, controlToInsert);
        controlToInsert.parent = this;

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    public void insertChildAfter(@Nonnull BaseControl controlToInsert, @Nonnull BaseControl controlToInsertAfter)
    {
        int index = children.indexOf(controlToInsert);

        if (index != -1)
        {
            children.remove(index);
        }

        int afterIndex = children.indexOf(controlToInsertAfter);

        if (afterIndex == -1)
        {
            if (index != -1)
            {
                children.add(index, controlToInsert);
            }

            throw new IllegalArgumentException("The given control to insert after is not a child of this control.");
        }

        if (afterIndex + 1 == index)
        {
            if (index != -1)
            {
                children.add(index, controlToInsert);
            }

            return;
        }

        children.add(afterIndex + 1, controlToInsert);
        controlToInsert.parent = this;

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    public int getTreeDepth()
    {
        return getParent() != null ? getParent().getTreeDepth() + 1 : 0;
    }

    @Nullable
    public ScreenControl getScreen()
    {
        return getParent() != null ? getParent().getScreen() : null;
    }

    @Nonnull
    public Scale getInnerScale()
    {
        return new Scale(innerScale);
    }

    public void setInnerScale(double innerScaleX, double innerScaleY)
    {
        if (innerScaleX == innerScale.x && innerScaleY == innerScale.y)
        {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(innerScaleX))
        {
            throw new IllegalArgumentException("The inner scale X must be positive and finite.");
        }

        if (!DoubleUtil.isPositiveAndFinite(innerScaleY))
        {
            throw new IllegalArgumentException("The inner scale Y must be positive and finite.");
        }

        innerScale.x = innerScaleX;
        innerScale.y = innerScaleY;

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    @Nonnull
    public Size getDesiredSize()
    {
        return new Size(desiredSize);
    }

    public void setDesiredSize(double desiredWidth, double desiredHeight)
    {
        desiredWidth = DoubleUtil.clamp(desiredWidth, getMinWidth(), getMaxWidth());
        desiredHeight = DoubleUtil.clamp(desiredHeight, getMinHeight(), getMaxHeight());

        if (desiredWidth == desiredSize.width && desiredHeight == desiredSize.height)
        {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(desiredWidth))
        {
            throw new IllegalArgumentException("The desired width must be positive and finite.");
        }

        if (!DoubleUtil.isPositiveAndFinite(desiredHeight))
        {
            throw new IllegalArgumentException("The desired height must be positive and finite.");
        }

        desiredSize.width = desiredWidth;
        desiredSize.height = desiredHeight;

        if (getParent() != null && isMeasuring())
        {
            getParent().onChildDesiredSizeChanged(this);
        }
    }

    public void setDesiredSize(@Nonnull Size desiredSize)
    {
        setDesiredSize(desiredSize.width, desiredSize.height);
    }

    public double getDesiredWidth()
    {
        return desiredSize.width;
    }

    protected void setDesiredWidth(double desiredWidth)
    {
        setDesiredSize(desiredWidth, desiredSize.height);
    }

    public double getDesiredHeight()
    {
        return desiredSize.height;
    }

    protected void setDesiredHeight(double desiredHeight)
    {
        setDesiredSize(desiredSize.width, desiredHeight);
    }

    @Nonnull
    public Size getMinSize()
    {
        return new Size(minSize);
    }

    public void setMinSize(double minWidth, double minHeight)
    {
        minWidth = Math.max(0.0, minWidth);
        minHeight = Math.max(0.0, minHeight);

        if (minWidth == minSize.width && minHeight == minSize.height)
        {
            return;
        }

        minSize.width = minWidth;
        minSize.height = minHeight;

        markMeasureDirty(true);
    }

    public void setMinSize(@Nonnull Size minSize)
    {
        setMinSize(minSize.width, minSize.height);
    }

    public double getMinWidth()
    {
        return minSize.width;
    }

    public double getMinHeight()
    {
        return minSize.height;
    }

    public void setMinWidth(double minWidth)
    {
        setMinSize(minWidth, minSize.height);
    }

    public void setMinHeight(double minHeight)
    {
        setMinSize(minSize.width, minHeight);
    }

    @Nonnull
    public Size getMaxSize()
    {
        return new Size(maxSize);
    }

    public void setMaxSize(double maxWidth, double maxHeight)
    {
        maxWidth = Math.max(0.0, maxWidth);
        maxHeight = Math.max(0.0, maxHeight);

        if (maxWidth == maxSize.width && maxHeight == maxSize.height)
        {
            return;
        }

        maxSize.width = maxWidth;
        maxSize.height = maxHeight;

        markMeasureDirty(true);
    }

    public void setMaxSize(@Nonnull Size maxSize)
    {
        setMaxSize(maxSize.width, maxSize.height);
    }

    public double getMaxWidth()
    {
        return maxSize.width;
    }

    public double getMaxHeight()
    {
        return maxSize.height;
    }

    public void setMaxWidth(double maxWidth)
    {
        setMaxSize(maxWidth, maxSize.height);
    }

    public void setMaxHeight(double maxHeight)
    {
        setMaxSize(maxSize.width, maxHeight);
    }

    @Nonnull
    public Size getSize()
    {
        return new Size(size);
    }

    @Nonnull
    public Size getPixelSize()
    {
        return new Size(getPixelWidth(), getPixelHeight());
    }

    public void setSize(double width, double height)
    {
        width = DoubleUtil.clamp(width, getMinWidth(), getMaxWidth());
        height = DoubleUtil.clamp(height, getMinHeight(), getMaxHeight());

        if (width == size.width && height == size.height)
        {
            return;
        }

        size.width = width;
        size.height = height;

        if (!isMeasuring())
        {
            markMeasureDirty(true);
        }

        if (getParent() != null)
        {
            getParent().onChildSizeChanged(this);
        }
    }

    public void setSize(@Nonnull Size size)
    {
        setSize(size.width, size.height);
    }

    public double getWidth()
    {
        return size.width;
    }

    public double getPixelWidth()
    {
        return getWidth() * getPixelScaleX();
    }

    public double getWidthWithMargin()
    {
        return getWidth() + getMarginWidth();
    }

    public double getWidthWithoutPadding()
    {
        return getWidth() - getPaddingWidth();
    }

    public double getPixelWidthWithoutPadding()
    {
        return getPixelWidth() - getPixelPaddingWidth();
    }

    public void setWidth(double width)
    {
        setSize(width, size.height);
    }

    public double getHeight()
    {
        return size.height;
    }

    public double getPixelHeight()
    {
        return getHeight() * getPixelScaleY();
    }

    public double getHeightWithMargin()
    {
        return getHeight() + getMarginHeight();
    }

    public double getHeightWithoutPadding()
    {
        return getHeight() - getPaddingHeight();
    }

    public double getPixelHeightWithoutPadding()
    {
        return getPixelHeight() - getPixelPaddingHeight();
    }

    public void setHeight(double height)
    {
        setSize(size.width, height);
    }

    public boolean shouldFitWidthToContent()
    {
        return shouldFitWidthToContent;
    }

    public void setShouldFitWidthToContent(boolean shouldFitWidthToContent)
    {
        if (this.shouldFitWidthToContent == shouldFitWidthToContent)
        {
            return;
        }

        this.shouldFitWidthToContent = shouldFitWidthToContent;

        markMeasureDirty(true);
    }

    public boolean shouldFitHeightToContent()
    {
        return shouldFitHeightToContent;
    }

    public void setShouldFitHeightToContent(boolean shouldFitHeightToContent)
    {
        if (this.shouldFitHeightToContent == shouldFitHeightToContent)
        {
            return;
        }

        this.shouldFitHeightToContent = shouldFitHeightToContent;

        markMeasureDirty(true);
    }

    public boolean shouldFitToContent()
    {
        return shouldFitWidthToContent || shouldFitHeightToContent;
    }

    @Nullable
    public Double getWidthPercentage()
    {
        return widthPercentage;
    }

    public void setWidthPercentage(@Nullable Double widthPercentage)
    {
        if (widthPercentage == this.widthPercentage)
        {
            return;
        }

        this.widthPercentage = widthPercentage == null ? null : Math.max(0.0, Math.min(1.0, widthPercentage));

        if (getParent() != null)
        {
            onChildSizeChanged(this);
        }
    }

    @Nullable
    public Double getHeightPercentage()
    {
        return heightPercentage;
    }

    public void setHeightPercentage(@Nullable Double heightPercentage)
    {
        if (heightPercentage == this.heightPercentage)
        {
            return;
        }

        this.heightPercentage = heightPercentage == null ? null : Math.max(0.0, Math.min(1.0, heightPercentage));

        if (getParent() != null)
        {
            onChildSizeChanged(this);
        }
    }

    @Nonnull
    public Margin getMargin()
    {
        return new Margin(margin);
    }

    @Nonnull
    public Margin getPixelMargin()
    {
        return new Margin(margin.left * getPixelScaleX(), margin.top * getPixelScaleY(), margin.right * getPixelScaleX(), margin.bottom * getPixelScaleY());
    }

    public double getMarginWidth()
    {
        return margin.left + margin.right;
    }

    public double getMarginHeight()
    {
        return margin.top + margin.bottom;
    }

    public void setMargin(double left, double top, double right, double bottom)
    {
        if (left == margin.left && top == margin.top && right == margin.right && bottom == margin.bottom)
        {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(left) || !DoubleUtil.isPositiveAndFinite(top) || !DoubleUtil.isPositiveAndFinite(right) || !DoubleUtil.isPositiveAndFinite(bottom))
        {
            throw new IllegalArgumentException("The margins must be positive and finite.");
        }

        margin.left = left;
        margin.top = top;
        margin.right = right;
        margin.bottom = bottom;

        if (getParent() != null)
        {
            getParent().onChildMarginChanged(this);
        }
    }

    public void setMargin(double margin)
    {
        setMargin(margin, margin, margin, margin);
    }

    public void setMargin(@Nonnull Margin margin)
    {
        setMargin(margin.left, margin.top, margin.right, margin.bottom);
    }

    @Nonnull
    public Padding getPadding()
    {
        return padding;
    }

    @Nonnull
    public Padding getPixelPadding()
    {
        return new Padding(padding.left * getPixelScaleX(), padding.top * getPixelScaleY(), padding.right * getPixelScaleX(), padding.bottom * getPixelScaleY());
    }

    public double getPaddingWidth()
    {
        return padding.left + padding.right;
    }

    public double getPixelPaddingWidth()
    {
        return getPixelPadding().left + getPixelPadding().right;
    }

    public double getPaddingHeight()
    {
        return padding.top + padding.bottom;
    }

    public double getPixelPaddingHeight()
    {
        return getPixelPadding().top + getPixelPadding().bottom;
    }

    public void setPadding(double left, double top, double right, double bottom)
    {
        if (left == padding.left && top == padding.top && right == padding.right && bottom == padding.bottom)
        {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(left) || !DoubleUtil.isPositiveAndFinite(top) || !DoubleUtil.isPositiveAndFinite(right) || !DoubleUtil.isPositiveAndFinite(bottom))
        {
            throw new IllegalArgumentException("The paddings must be positive and finite.");
        }

        padding.left = left;
        padding.top = top;
        padding.right = right;
        padding.bottom = bottom;

        if (!isMeasuring())
        {
            for (BaseControl childControl : children)
            {
                childControl.markMeasureDirty(true);
            }
        }
    }

    public void setPadding(double padding)
    {
        setPadding(padding, padding, padding, padding);
    }

    public void setPadding(@Nonnull Padding padding)
    {
        setPadding(padding.left, padding.top, padding.right, padding.bottom);
    }

    @Nonnull
    public Position getPosition()
    {
        return new Position(position);
    }

    public void setPosition(double x, double y)
    {
        if (x == position.x && y == position.y)
        {
            return;
        }

        position.x = x;
        position.y = y;

        if (getParent() != null && !isDragging())
        {
            getParent().onChildPositionSizeChanged(this);
        }
    }

    public void setPosition(@Nonnull Position position)
    {
        setPosition(position.x, position.y);
    }

    public double getX()
    {
        return position.x;
    }

    public void setX(double x)
    {
        setPosition(x, getY());
    }

    public double getY()
    {
        return position.y;
    }

    public void setY(double y)
    {
        setPosition(getX(), y);
    }

    /**
     * Converts a local x coordinate to a pixel x coordinate inside the control. I.e. if a child control had
     * an x coordinate of 10 what would the pixel x coordinate be? So this takes into account the inner scaling
     * of this control.
     *
     * @param x the local x coordinate to convert.
     * @return the pixel x coordinate.
     */
    public double toPixelX(double x)
    {
        double pixelX = x * getChildPixelScaleX();
        pixelX += getPixelX() + getPixelPadding().left;
        pixelX -= getScrollX() * getChildPixelScaleX();

        return pixelX;
    }

    public double getPixelX()
    {
        if (getParent() != null)
        {
            return getParent().toPixelX(getX());
        }
        else
        {
            return getX() * getPixelScaleX();
        }
    }

    public void setPixelX(double pixelX)
    {
        if (getParent() != null)
        {
            pixelX -= getParent().getPixelX() + getParent().getPixelPadding().left;
            pixelX += getParent().getScrollX() * getPixelScaleX();
        }

        setX(pixelX / getPixelScaleX());
    }

    /**
     * Converts a local y coordinate to a pixel y coordinate inside the control. I.e. if a child control had
     * an y coordinate of 10 what would the pixel y coordinate be? So this takes into account the inner scaling
     * of this control.
     *
     * @param y the local y coordinate to convert.
     * @return the pixel y coordinate.
     */
    public double toPixelY(double y)
    {
        double pixelY = y * getChildPixelScaleY();
        pixelY += getPixelY() + getPixelPadding().top;
        pixelY -= getScrollY() * getChildPixelScaleY();

        return pixelY;
    }

    public double getPixelY()
    {
        if (getParent() != null)
        {
            return getParent().toPixelY(getY());
        }
        else
        {
            return getY() * getPixelScaleY();
        }
    }

    public void setPixelY(double pixelY)
    {
        if (getParent() != null)
        {
            pixelY -= getParent().getPixelY() + getParent().getPixelPadding().top;
            pixelY += getParent().getScrollY() * getPixelScaleY();
        }

        setY(pixelY / getPixelScaleY());
    }

    public double getPixelMidX()
    {
        return getPixelX() + getPixelWidth() / 2.0;
    }

    public double getPixelMidY()
    {
        return getPixelY() + getPixelHeight() / 2.0;
    }

    public double getPixelLeft()
    {
        return getPixelX();
    }

    public double getPixelTop()
    {
        return getPixelY();
    }

    public double getPixelRight()
    {
        return getPixelX() + getPixelWidth();
    }

    public double getPixelBottom()
    {
        return getPixelY() + getPixelHeight();
    }

    @Nullable
    public Double getHorizontalAlignment()
    {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(@Nullable Double horizontalAlignment)
    {
        if (this.horizontalAlignment == horizontalAlignment)
        {
            return;
        }

        if (horizontalAlignment != null)
        {
            horizontalAlignment = DoubleUtil.clamp(horizontalAlignment, 0.0, 1.0);
        }

        this.horizontalAlignment = horizontalAlignment;

        if (getParent() != null)
        {
            getParent().onChildAlignmentChanged(this);
        }
    }

    @Nullable
    public Double getVerticalAlignment()
    {
        return verticalAlignment;
    }

    public void setVerticalAlignment(@Nullable Double verticalAlignment)
    {
        if (this.verticalAlignment == verticalAlignment)
        {
            return;
        }

        if (verticalAlignment != null)
        {
            verticalAlignment = DoubleUtil.clamp(verticalAlignment, 0.0, 1.0);
        }

        this.verticalAlignment = verticalAlignment;

        if (getParent() != null)
        {
            getParent().onChildAlignmentChanged(this);
        }
    }

    @Nullable
    public Double getHorizontalContentAlignment()
    {
        return horizontalContentAlignment;
    }

    public boolean isHorizontalContentAlignmentSet()
    {
        return horizontalContentAlignment != null;
    }

    public void setHorizontalContentAlignment(@Nullable Double horizontalContentAlignment)
    {
        if (this.horizontalContentAlignment == horizontalContentAlignment)
        {
            return;
        }

        if (horizontalContentAlignment != null)
        {
            horizontalContentAlignment = DoubleUtil.clamp(horizontalContentAlignment, 0.0, 1.0);
        }

        this.horizontalContentAlignment = horizontalContentAlignment;

        markArrangeDirty(true);
    }

    @Nullable
    public Double getVerticalContentAlignment()
    {
        return verticalContentAlignment;
    }

    public boolean isVerticalContentAlignmentSet()
    {
        return verticalContentAlignment != null;
    }

    public void setVerticalContentAlignment(@Nullable Double verticalContentAlignment)
    {
        if (this.verticalContentAlignment == verticalContentAlignment)
        {
            return;
        }

        if (verticalContentAlignment != null)
        {
            verticalContentAlignment = DoubleUtil.clamp(verticalContentAlignment, 0.0, 1.0);
        }

        this.verticalContentAlignment = verticalContentAlignment;

        markArrangeDirty(true);
    }

    public double getHorizontalAlignmentFor(@Nonnull BaseControl control)
    {
        if (isChild(control))
        {
            if (getHorizontalContentAlignment() != null)
            {
                return getHorizontalContentAlignment();
            }
            else if (control.getHorizontalAlignment() != null)
            {
                return control.getHorizontalAlignment();
            }
        }

        return 0.0;
    }

    public double getVerticalAlignmentFor(@Nonnull BaseControl control)
    {
        if (isChild(control))
        {
            if (getVerticalContentAlignment() != null)
            {
                return getVerticalContentAlignment();
            }
            else if (control.getVerticalAlignment() != null)
            {
                return control.getVerticalAlignment();
            }
        }

        return 0.0;
    }

    @Nonnull
    public Offset getScroll()
    {
        return new Offset(scroll);
    }

    public void setScroll(double x, double y)
    {
        x = DoubleUtil.clamp(x, minScroll.x, maxScroll.x);
        y = DoubleUtil.clamp(y, minScroll.y, maxScroll.y);

        if (x == scroll.x && y == scroll.y)
        {
            return;
        }

        scroll.x = x;
        scroll.y = y;
    }

    public void setScroll(@Nonnull Offset offset)
    {
        setScroll(offset.x, offset.y);
    }

    public double getParentScrollX()
    {
        if (getParent() != null)
        {
            getParent().getScrollX();
        }

        return 0.0;
    }

    public double getScrollX()
    {
        return scroll.x;
    }

    public void setScrollX(double x)
    {
        setScroll(x, getScrollY());
    }

    public double getParentScrollY()
    {
        if (getParent() != null)
        {
            getParent().getScrollY();
        }

        return 0.0;
    }

    public double getScrollY()
    {
        return scroll.y;
    }

    public void setScrollY(double y)
    {
        setScroll(getScrollX(), y);
    }

    public void scrollX(double x)
    {
        setScrollX(getScrollX() + x);
    }

    public void scrollY(double y)
    {
        setScrollY(getScrollY() + y);
    }

    public double getScrollXPercent()
    {
        return (getScrollX() - getMinScrollX()) / (getMaxScrollX() - getMinScrollX());
    }

    public void setScrollXPercent(double percent)
    {
        setScrollX(getMinScrollX() + (getMaxScrollX() - getMinScrollX()) * percent);
    }

    public void scrollXPercent(double percent)
    {
        setScrollX(getScrollX() + (getMaxScrollX() - getMinScrollX()) * percent);
    }

    public double getScrollYPercent()
    {
        return (getScrollY() - getMinScrollY()) / (getMaxScrollY() - getMinScrollY());
    }

    public void setScrollYPercent(double percent)
    {
        setScrollY(getMinScrollY() + (getMaxScrollY() - getMinScrollY()) * percent);
    }

    public void scrollYPercent(double percent)
    {
        setScrollY(getScrollY() + (getMaxScrollY() - getMinScrollY()) * percent);
    }

    @Nonnull
    public Offset getMinScroll()
    {
        return new Offset(minScroll);
    }

    public void setMinScroll(double x, double y)
    {
        if (x == minScroll.x && y == minScroll.y)
        {
            return;
        }

        minScroll.x = x;
        minScroll.y = y;

        setScroll(getScroll());
    }

    public void setMinScroll(@Nonnull Offset offset)
    {
        setMinScroll(offset.x, offset.y);
    }

    public double getMinScrollX()
    {
        return minScroll.x;
    }

    public void setMinScrollX(double x)
    {
        setMinScroll(x, getMinScrollY());
    }

    public double getMinScrollY()
    {
        return minScroll.y;
    }

    public void setMinScrollY(double y)
    {
        setMinScroll(getMinScrollX(), y);
    }

    @Nonnull
    public Offset getMaxScroll()
    {
        return new Offset(maxScroll);
    }

    public void setMaxScroll(double x, double y)
    {
        if (x == maxScroll.x && y == maxScroll.y)
        {
            return;
        }

        maxScroll.x = x;
        maxScroll.y = y;

        setScroll(getScroll());
    }

    public void setMaxScroll(@Nonnull Offset offset)
    {
        setMaxScroll(offset.x, offset.y);
    }

    public double getMaxScrollX()
    {
        return maxScroll.x;
    }

    public void setMaxScrollX(double x)
    {
        setMaxScroll(x, getMaxScrollY());
    }

    public double getMaxScrollY()
    {
        return maxScroll.y;
    }

    public void setMaxScrollY(double y)
    {
        setMaxScroll(getMaxScrollX(), y);
    }

    public boolean canScroll()
    {
        return canScrollHorizontally || canScrollVertically;
    }

    public boolean canScrollHorizontally()
    {
        return canScrollHorizontally;
    }

    public void setCanScrollHorizontally(boolean canScrollHorizontally)
    {
        this.canScrollHorizontally = canScrollHorizontally;

        if (!canScrollHorizontally())
        {
            setScrollX(0.0);
            setMinScrollX(0.0);
            setMaxScrollX(0.0);
        }
    }

    public boolean canScrollVertically()
    {
        return canScrollVertically;
    }

    public void setCanScrollVertically(boolean canScrollVertically)
    {
        this.canScrollVertically = canScrollVertically;

        if (!canScrollVertically())
        {
            setScrollY(0.0);
            setMinScrollY(0.0);
            setMaxScrollY(0.0);
        }
    }

    @Nullable
    public BaseControl getHoveredControl()
    {
        return getScreen() == null ? null : getScreen().getHoveredControl();
    }

    public boolean isHovered()
    {
        return getHoveredControl() == this;
    }

    public void setIsHovered(boolean isHovered)
    {
        if (isHovered)
        {
            getScreen().setHoveredControl(this);
        }
        else if (isHovered())
        {
            getScreen().setHoveredControl(null);
        }
    }

    @Nullable
    public BaseControl getPressedControl()
    {
        return getScreen() == null ? null : getScreen().getPressedControl();
    }

    public boolean isPressed()
    {
        return getPressedControl() == this;
    }

    public void setIsPressed(boolean isPressed)
    {
        if (isPressed)
        {
            getScreen().setPressedControl(this);
        }
        else if (isPressed())
        {
            getScreen().setPressedControl(null);
        }
    }

    @Nullable
    public BaseControl getFocusedControl()
    {
        return getScreen() == null ? null : getScreen().getFocusedControl();
    }

    public boolean isFocused()
    {
        return getFocusedControl() == this;
    }

    public void setIsFocused(boolean isFocused)
    {
        if (isFocused)
        {
            getScreen().setFocusedControl(this);
        }
        else if (isFocused())
        {
            getScreen().setFocusedControl(null);
        }
    }

    @Nullable
    public BaseControl getDraggedControl()
    {
        return getScreen() == null ? null : getScreen().getDraggedControl();
    }

    public boolean isDragging()
    {
        return getDraggedControl() == this;
    }

    public void setIsDragging(boolean isDragging)
    {
        if (isDragging)
        {
            getScreen().setDraggedControl(this);
        }
        else if (isDragging())
        {
            getScreen().setDraggedControl(null);
        }
    }

    public boolean isDraggable()
    {
        return isDraggableX || isDraggableY;
    }

    public boolean isDraggableX()
    {
        return isDraggableX;
    }

    public void setDraggableX(boolean draggableX)
    {
        isDraggableX = draggableX;
    }

    public boolean isDraggableY()
    {
        return isDraggableY;
    }

    public void setDraggableY(boolean draggableY)
    {
        isDraggableY = draggableY;
    }

    public double getDragThreshold()
    {
        return dragThreshold;
    }

    public void setDragThreshold(double dragThreshold)
    {
        this.dragThreshold = dragThreshold;
    }

    public boolean shouldBlockDrag()
    {
        return shouldBlockDrag;
    }

    public void setShouldBlockDrag(boolean shouldBlockDrag)
    {
        this.shouldBlockDrag = shouldBlockDrag;
    }

    public boolean shouldScissor()
    {
        return shouldScissor;
    }

    public void setShouldScissor(boolean shouldScissor)
    {
        this.shouldScissor = shouldScissor;
    }

    public int getBackgroundColour()
    {
        return backgroundColour;
    }

    public void setBackgroundColour(int backgroundColour)
    {
        this.backgroundColour = backgroundColour;
    }

    /**
     * @return converts a pixel x coordinate into a local x coordinate.
     */
    public double toLocalX(double pixelX)
    {
        return (pixelX - getPixelPadding().left - getPixelX()) / getPixelScaleX();
    }

    /**
     * @return converts a pixel y coordinate into a local y coordinate.
     */
    public double toLocalY(double pixelY)
    {
        return (pixelY - getPixelPadding().top - getPixelY()) / getPixelScaleY();
    }

    public double getGuiScale()
    {
        return GuiUtil.getInstance().getGuiScale();
    }

    public double getScaleX()
    {
        return getParent() != null ? getParent().getScaleX() * getParent().getInnerScale().x : 1.0;
    }

    /**
     * Returns how many pixels a single unit takes up for this control. E.g. if the control had a scale of 4.0
     * and the gui scale was 2.0 then the pixel scale would be 4.0 * 2.0 = 8.0, so the control would take up 8
     * pixels for every unit.
     *
     * @return the pixel scale.
     */
    public double getPixelScaleX()
    {
    	return getGuiScale() * getScaleX();
    }

    public double getChildPixelScaleX()
    {
    	return getPixelScaleX() * getInnerScale().x;
    }

    public double getScaleY()
    {
        return getParent() != null ? getParent().getScaleY() * getParent().getInnerScale().y : 1.0;
    }

    /**
     * Returns how many pixels a single unit takes up for this control. E.g. if the control had a scale of 4.0
     * and the gui scale was 2.0 then the pixel scale would be 4.0 * 2.0 = 8.0, so the control would take up 8
     * pixels for every unit.
     *
     * @return the pixel scale.
     */
    public double getPixelScaleY()
    {
    	return getGuiScale() * getScaleY();
    }

    public double getChildPixelScaleY()
    {
    	return getPixelScaleY() * getInnerScale().y;
    }

    public int randomColour()
    {
        int a = 255;
    	int r = random.nextInt(255);
    	int g = random.nextInt(255);
    	int b = random.nextInt(255);
    	int colour = (a << 24) | (r << 16) | (g << 8) | b;

        return colour;
    }

    public int randomInt(int min, int max)
    {
    	return random.nextInt((max - min) + 1) + min;
    }

    public int randomInt(int max)
    {
    	return random.nextInt(max);
    }
}
