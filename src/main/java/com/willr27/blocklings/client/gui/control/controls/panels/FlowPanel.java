package com.willr27.blocklings.client.gui.control.controls.panels;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.properties.Corner;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.properties.Flow;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel that stacks its children on top of each other until it needs to wrap.
 */
@OnlyIn(Dist.CLIENT)
public class FlowPanel extends Control
{
    @Nonnull
    private Flow flow = Flow.TOP_LEFT_LEFT_TO_RIGHT;

    private double horizontalSpacing = 0.0;

    private double verticalSpacing = 0.0;

    @Nullable
    private Double lineAlignment = null;

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
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren())
            {
                double childMinX = (childControl.getX() - childControl.getMargin().left) * getInnerScale().x;
                double childMaxX = (childControl.getX() + childControl.getWidth() + childControl.getMargin().right) * getInnerScale().x;

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
                width = maxX - minX + getPaddingWidth();
            }
            else if (maxX != Double.NEGATIVE_INFINITY)
            {
                width = maxX + getPaddingWidth();
            }
            else
            {
                width = 0.0;
            }
        }

        if (getHeightPercentage() != null && DoubleUtil.isPositiveAndFinite(availableHeight))
        {
            height = availableHeight * getHeightPercentage();
        }
        else if (shouldFitHeightToContent())
        {
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren())
            {
                double childMinY = (childControl.getY() - childControl.getMargin().top) * getInnerScale().y;
                double childY = (childControl.getY() + childControl.getHeight() + childControl.getMargin().bottom) * getInnerScale().y;

                if (childMinY < minY)
                {
                    minY = childMinY;
                }

                if (childY > maxY)
                {
                    maxY = childY;
                }
            }

            if (minY != Double.POSITIVE_INFINITY && maxY != Double.NEGATIVE_INFINITY)
            {
                height = maxY - minY + getPaddingHeight();
            }
            else if (maxY != Double.NEGATIVE_INFINITY)
            {
                height = maxY + getPaddingHeight();
            }
            else
            {
                height = 0.0;
            }
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

            if (getDirection().isHorizontal)
            {
                availableWidth = Double.POSITIVE_INFINITY;
                availableHeight -= child.getMarginHeight() + getPaddingHeight();
            }
            else
            {
                availableWidth -= child.getMarginWidth() + getPaddingWidth();
                availableHeight = Double.POSITIVE_INFINITY;
            }

            child.doMeasure(availableWidth, availableHeight);
        }
    }

    @Override
    protected void arrange()
    {
        double availableWidth = getWidthWithoutPadding() / getInnerScale().x;
        double availableHeight = getHeightWithoutPadding() / getInnerScale().y;

        double newLineControlX = 0.0;
        double newLineControlY = 0.0;

        if (getStartCorner() == Corner.TOP_RIGHT || getStartCorner() == Corner.BOTTOM_RIGHT)
        {
            newLineControlX = getWidthWithoutPadding() / getInnerScale().x;
        }

        if (getStartCorner() == Corner.BOTTOM_LEFT || getStartCorner() == Corner.BOTTOM_RIGHT)
        {
            newLineControlY = getHeightWithoutPadding() / getInnerScale().y;
        }

        double controlX = newLineControlX;
        double controlLeft = 0.0;
        double controlRight = 0.0;
        double controlY = newLineControlY;
        double controlTop = 0.0;
        double controlBottom = 0.0;
        boolean hasJustReset = true;
        List<BaseControl> controlsToAlign = new ArrayList<>();
        List<List<BaseControl>> linesOfControlsToAlign = new ArrayList<>();

        for (BaseControl control : getChildrenCopy())
        {
            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            if (getDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlX += control.getMargin().left;
                controlRight = controlX + control.getWidth() + control.getMargin().right;

                if (!hasJustReset && controlRight > availableWidth)
                {
                    controlX = newLineControlX + control.getMargin().left;
                    controlY = newLineControlY;
                    hasJustReset = true;

                    linesOfControlsToAlign.add(controlsToAlign);
                    alignVertically(controlsToAlign);
                    controlsToAlign = new ArrayList<>();
                }
                else
                {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM)
                {
                    controlY += control.getMargin().top;
                }
                else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP)
                {
                    controlY -= control.getHeight() + control.getMargin().bottom;
                }
            }
            if (getDirection() == Direction.RIGHT_TO_LEFT)
            {
                controlX -= control.getWidth() + control.getMargin().right;
                controlLeft = controlX - control.getMargin().left;

                if (!hasJustReset && controlLeft < 0.0)
                {
                    controlX = newLineControlX - control.getWidth() - control.getMargin().right;
                    controlY = newLineControlY;
                    hasJustReset = true;

                    linesOfControlsToAlign.add(controlsToAlign);
                    alignVertically(controlsToAlign);
                    controlsToAlign = new ArrayList<>();
                }
                else
                {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM)
                {
                    controlY += control.getMargin().top;
                }
                else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP)
                {
                    controlY -= control.getHeight() + control.getMargin().bottom;
                }
            }
            else if (getDirection() == Direction.TOP_TO_BOTTOM)
            {
                controlY += control.getMargin().top;
                controlBottom = controlY + control.getHeight() + control.getMargin().bottom;

                if (!hasJustReset && controlBottom > availableHeight)
                {
                    controlX = newLineControlX;
                    controlY = newLineControlY + control.getMargin().top;
                    hasJustReset = true;

                    linesOfControlsToAlign.add(controlsToAlign);
                    alignHorizontally(controlsToAlign);
                    controlsToAlign = new ArrayList<>();
                }
                else
                {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT)
                {
                    controlX += control.getMargin().left;
                }
                else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT)
                {
                    controlX -= control.getWidth() + control.getMargin().right;
                }
            }
            else if (getDirection() == Direction.BOTTOM_TO_TOP)
            {
                controlY -= control.getHeight() + control.getMargin().bottom;
                controlTop = controlY - control.getMargin().top;

                if (!hasJustReset && controlTop < 0.0)
                {
                    controlX = newLineControlX;
                    controlY = newLineControlY - control.getHeight() - control.getMargin().bottom;
                    hasJustReset = true;

                    linesOfControlsToAlign.add(controlsToAlign);
                    alignHorizontally(controlsToAlign);
                    controlsToAlign = new ArrayList<>();
                }
                else
                {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT)
                {
                    controlX += control.getMargin().left;
                }
                else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT)
                {
                    controlX -= control.getWidth() + control.getMargin().right;
                }
            }

            control.setX(controlX);
            control.setY(controlY);

            controlsToAlign.add(control);

            if (getDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlX += control.getWidth() + control.getMargin().right + getHorizontalSpacing();

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM)
                {
                    controlY -= control.getMargin().top;
                }
                else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP)
                {
                    controlY += control.getHeight() + control.getMargin().bottom;
                }
            }
            else if (getDirection() == Direction.RIGHT_TO_LEFT)
            {
                controlX -= control.getMargin().left + getHorizontalSpacing();

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM)
                {
                    controlY -= control.getMargin().top;
                }
                else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP)
                {
                    controlY += control.getHeight() + control.getMargin().bottom;
                }
            }
            else if (getDirection() == Direction.TOP_TO_BOTTOM)
            {
                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT)
                {
                    controlX -= control.getMargin().left;
                }
                else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT)
                {
                    controlX += control.getWidth() + control.getMargin().right;
                }

                controlY += control.getHeight() + control.getMargin().bottom + getVerticalSpacing();
            }
            else if (getDirection() == Direction.BOTTOM_TO_TOP)
            {
                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT)
                {
                    controlX -= control.getMargin().left;
                }
                else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT)
                {
                    controlX += control.getWidth() + control.getMargin().right;
                }

                controlY -= control.getMargin().top + getVerticalSpacing();
            }

            if (getOverflowDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlRight = controlX + control.getWidthWithMargin() + getHorizontalSpacing();

                if (controlRight > newLineControlX)
                {
                    newLineControlX = controlRight;
                }
            }
            else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT)
            {
                controlLeft = controlX - control.getWidthWithMargin() - getHorizontalSpacing();

                if (controlLeft < newLineControlX)
                {
                    newLineControlX = controlLeft;
                }
            }
            else if (getOverflowDirection() == Direction.TOP_TO_BOTTOM)
            {
                controlBottom = controlY + control.getHeightWithMargin() + getVerticalSpacing();

                if (controlBottom > newLineControlY)
                {
                    newLineControlY = controlBottom;
                }
            }
            else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP)
            {
                controlTop = controlY - control.getHeightWithMargin() - getVerticalSpacing();

                if (controlTop < newLineControlY)
                {
                    newLineControlY = controlTop;
                }
            }
        }

        if (!controlsToAlign.isEmpty())
        {
            linesOfControlsToAlign.add(controlsToAlign);

            if (getDirection() == Direction.LEFT_TO_RIGHT || getDirection() == Direction.RIGHT_TO_LEFT)
            {
                alignVertically(controlsToAlign);
            }
            else
            {
                alignHorizontally(controlsToAlign);
            }
        }

        if (getHorizontalContentAlignment() != null)
        {
            if (getDirection().isVertical)
            {
                double minX = Double.POSITIVE_INFINITY;
                double maxX = Double.NEGATIVE_INFINITY;

                for (BaseControl controlToAlign : getChildren())
                {
                    double controlToAlignMinX = controlToAlign.getX() - controlToAlign.getMargin().left;
                    double controlToAlignMaxX = controlToAlign.getX() + controlToAlign.getWidth() + controlToAlign.getMargin().right;

                    if (controlToAlignMinX < minX)
                    {
                        minX = controlToAlignMinX;
                    }

                    if (controlToAlignMaxX > maxX)
                    {
                        maxX = controlToAlignMaxX;
                    }
                }
                double dif = ((maxX - minX - availableWidth)) * (getOverflowDirection() == Direction.LEFT_TO_RIGHT ? -getHorizontalContentAlignment() : 1.0 - getHorizontalContentAlignment());

                for (BaseControl controlToAlign : getChildren())
                {
                    controlToAlign.setX(controlToAlign.getX() + dif);
                }
            }
            else
            {
                for (List<BaseControl> line : linesOfControlsToAlign)
                {
                    double lineMinX = Double.POSITIVE_INFINITY;
                    double lineMaxX = Double.NEGATIVE_INFINITY;

                    for (BaseControl controlToAlign : line)
                    {
                        double controlToAlignMinX = controlToAlign.getX() - controlToAlign.getMargin().left;
                        double controlToAlignMaxX = controlToAlign.getX() + controlToAlign.getWidth() + controlToAlign.getMargin().right;

                        if (controlToAlignMinX < lineMinX)
                        {
                            lineMinX = controlToAlignMinX;
                        }

                        if (controlToAlignMaxX > lineMaxX)
                        {
                            lineMaxX = controlToAlignMaxX;
                        }
                    }

                    double dif = ((availableWidth - (lineMaxX - lineMinX))) * (getDirection() == Direction.RIGHT_TO_LEFT ? 1.0 - getHorizontalContentAlignment() : -getHorizontalContentAlignment());

                    for (BaseControl controlToAlign : line)
                    {
                        controlToAlign.setX(controlToAlign.getX() - dif);
                    }
                }
            }
        }

        if (getVerticalContentAlignment() != null)
        {
            if (getDirection().isHorizontal)
            {
                double minY = Double.POSITIVE_INFINITY;
                double maxY = Double.NEGATIVE_INFINITY;

                for (BaseControl controlToAlign : getChildren())
                {
                    double controlToAlignMinY = controlToAlign.getY() - controlToAlign.getMargin().top;
                    double controlToAlignMaxY = controlToAlign.getY() + controlToAlign.getHeight() + controlToAlign.getMargin().bottom;

                    if (controlToAlignMinY < minY)
                    {
                        minY = controlToAlignMinY;
                    }

                    if (controlToAlignMaxY > maxY)
                    {
                        maxY = controlToAlignMaxY;
                    }
                }
                double dif = ((maxY - minY - availableHeight)) * (getOverflowDirection() == Direction.TOP_TO_BOTTOM ? -getVerticalContentAlignment() : 1.0 - getVerticalContentAlignment());

                for (BaseControl controlToAlign : getChildren())
                {
                    controlToAlign.setY(controlToAlign.getY() + dif);
                }
            }
            else
            {
                for (List<BaseControl> line : linesOfControlsToAlign)
                {
                    double lineMinY = Double.POSITIVE_INFINITY;
                    double lineMaxY = Double.NEGATIVE_INFINITY;

                    for (BaseControl controlToAlign : line)
                    {
                        double controlToAlignMinY = controlToAlign.getY() - controlToAlign.getMargin().top;
                        double controlToAlignMaxY = controlToAlign.getY() + controlToAlign.getHeight() + controlToAlign.getMargin().bottom;

                        if (controlToAlignMinY < lineMinY)
                        {
                            lineMinY = controlToAlignMinY;
                        }

                        if (controlToAlignMaxY > lineMaxY)
                        {
                            lineMaxY = controlToAlignMaxY;
                        }
                    }

                    double dif = ((availableHeight - (lineMaxY - lineMinY))) * (getDirection() == Direction.BOTTOM_TO_TOP ? 1.0 - getVerticalContentAlignment() : -getVerticalContentAlignment());

                    for (BaseControl controlToAlign : line)
                    {
                        controlToAlign.setY(controlToAlign.getY() - dif);
                    }
                }
            }
        }
    }

    private void alignHorizontally(@Nonnull List<BaseControl> controlsToAlign)
    {
        if (getLineAlignment() != null)
        {
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl controlToAlign : controlsToAlign)
            {
                double controlToAlignMinX = controlToAlign.getX() - controlToAlign.getMargin().left;
                double controlToAlignMaxX = controlToAlign.getX() + controlToAlign.getWidth() + controlToAlign.getMargin().right;

                if (controlToAlignMinX < minX)
                {
                    minX = controlToAlignMinX;
                }

                if (controlToAlignMaxX > maxX)
                {
                    maxX = controlToAlignMaxX;
                }
            }

            double dif = maxX - minX;

            for (BaseControl controlToAlign : controlsToAlign)
            {
                controlToAlign.setX(minX + controlToAlign.getMargin().left + (dif - controlToAlign.getWidthWithMargin()) * getLineAlignment());
            }
        }
    }

    private void alignVertically(@Nonnull List<BaseControl> controlsToAlign)
    {
        if (getLineAlignment() != null)
        {
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl controlToAlign : controlsToAlign)
            {
                double controlToAlignMinY = controlToAlign.getY() - controlToAlign.getMargin().top;
                double controlToAlignMaxY = controlToAlign.getY() + controlToAlign.getHeight() + controlToAlign.getMargin().bottom;

                if (controlToAlignMinY < minY)
                {
                    minY = controlToAlignMinY;
                }

                if (controlToAlignMaxY > maxY)
                {
                    maxY = controlToAlignMaxY;
                }
            }

            double dif = maxY - minY;

            for (BaseControl controlToAlign : controlsToAlign)
            {
                controlToAlign.setY(minY + controlToAlign.getMargin().top + (dif - controlToAlign.getHeightWithMargin()) * getLineAlignment());
            }
        }
    }

    @Nonnull
    public Flow getFlow()
    {
        return flow;
    }

    public void setFlow(@Nonnull Flow flow)
    {
        this.flow = flow;

        markArrangeDirty(true);
    }

    @Nonnull
    public Corner getStartCorner()
    {
        return flow.startCorner;
    }

    @Nonnull
    public Direction getDirection()
    {
        return flow.direction;
    }

    @Nonnull
    public Direction getOverflowDirection()
    {
        return flow.overflowDirection;
    }

    public double getHorizontalSpacing()
    {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(double horizontalSpacing)
    {
        this.horizontalSpacing = horizontalSpacing;

        markArrangeDirty(true);
    }

    public double getVerticalSpacing()
    {
        return verticalSpacing;
    }

    public void setVerticalSpacing(double verticalSpacing)
    {
        this.verticalSpacing = verticalSpacing;

        markArrangeDirty(true);
    }

    @Nullable
    public Double getLineAlignment()
    {
        return lineAlignment;
    }

    public void setLineAlignment(@Nullable Double lineAlignment)
    {
        this.lineAlignment = lineAlignment;

        markArrangeDirty(true);
    }
}
