package com.willr27.blocklings.client.gui.control.controls.panels;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A panel that stacks its children on top of each other.
 */
@OnlyIn(Dist.CLIENT)
public class StackPanel extends Control
{
    @Nonnull
    private Direction direction = Direction.TOP_TO_BOTTOM;

    private double spacing = 0.0;

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
        double startControlX = 0.0;
        double startControlY = 0.0;

        if (getDirection() == Direction.RIGHT_TO_LEFT)
        {
            startControlX = (getWidth() - getPaddingWidth()) / getInnerScale().x;
        }
        else if (getDirection() == Direction.BOTTOM_TO_TOP)
        {
            startControlY = (getHeight() - getPaddingHeight()) / getInnerScale().y;
        }

        double nextControlX = startControlX;
        double nextControlY = startControlY;

        for (BaseControl control : getChildrenCopy())
        {
            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            double controlX = 0.0;
            double controlY = 0.0;

            if (getDirection() == Direction.LEFT_TO_RIGHT)
            {
                nextControlX += control.getMargin().left;
            }
            if (getDirection() == Direction.RIGHT_TO_LEFT)
            {
                nextControlX -= control.getWidth() + control.getMargin().right;
            }
            else if (getDirection() == Direction.TOP_TO_BOTTOM)
            {
                nextControlY += control.getMargin().top;
            }
            else if (getDirection() == Direction.BOTTOM_TO_TOP)
            {
                nextControlY -= control.getHeight() + control.getMargin().bottom;
            }

            if (getDirection().isHorizontal)
            {
                controlX = nextControlX;
            }
            else
            {
                controlY = nextControlY;
            }

            if (getDirection().isVertical)
            {
                controlX += (((getWidthWithoutPadding() / getInnerScale().x) - control.getWidthWithMargin()) * getHorizontalAlignmentFor(control)) + control.getMargin().left;
            }

            if (getDirection().isHorizontal)
            {
                controlY += (((getHeightWithoutPadding() / getInnerScale().y) - control.getHeightWithMargin()) * getVerticalAlignmentFor(control)) + control.getMargin().top;
            }

            control.setX(controlX);
            control.setY(controlY);

            if (getDirection() == Direction.LEFT_TO_RIGHT)
            {
                nextControlX += control.getWidth() + control.getMargin().right + getSpacing();
            }
            else if (getDirection() == Direction.RIGHT_TO_LEFT)
            {
                nextControlX -= control.getMargin().left + getSpacing();
            }
            else if (getDirection() == Direction.TOP_TO_BOTTOM)
            {
                nextControlY += control.getHeight() + control.getMargin().bottom + getSpacing();
            }
            else if (getDirection() == Direction.BOTTOM_TO_TOP)
            {
                nextControlY -= control.getMargin().right + getSpacing();
            }
        }
    }

    @Nonnull
    public Direction getDirection()
    {
        return direction;
    }

    public void setDirection(@Nonnull Direction direction)
    {
        this.direction = direction;
    }

    public double getSpacing()
    {
        return spacing;
    }

    public void setSpacing(double spacing)
    {
        this.spacing = spacing;

        markArrangeDirty(true);
    }
}
