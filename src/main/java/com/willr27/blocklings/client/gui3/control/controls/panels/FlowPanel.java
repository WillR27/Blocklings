package com.willr27.blocklings.client.gui3.control.controls.panels;

import com.willr27.blocklings.client.gui3.control.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lays out its children from left to right, top to bottom or vice versa.
 */
@OnlyIn(Dist.CLIENT)
public class FlowPanel extends Panel
{
    /**
     * The direction the panel will attempt to lay out its contents.
     */
    @Nonnull
    private Direction flowDirection = Direction.LEFT_TO_RIGHT;

    /**
     * The orientation the panel will lay out contents that don't fit within the panel's bounds.
     */
    @Nonnull
    private Orientation overflowOrientation = Orientation.VERTICAL;

    /**
     * Whether to distribute the contents in a grid like pattern using the largest child control
     * as the division size.
     */
    private boolean shouldDistributeEvenly = false;

    /**
     * Whether to automatically adjust the max scroll offset to fit any overflow in the x-axis. So if the items
     * overflow by 10, the max scroll offset will be set to 10.
     */
    private boolean fitMaxScrollOffsetToOverflowX = true;

    /**
     * Whether to automatically adjust the max scroll offset to fit any overflow in the y-axis. So if the items
     * overflow by 10, the max scroll offset will be set to 10.
     */
    private boolean fitMaxScrollOffsetToOverflowY = true;

    /**
     * The horizontal gap between each item.
     */
    private int itemGapX = 0;

    /**
     * The vertical gap between each item.
     */
    private int itemGapY = 0;

    /**
     * The x or y coordinates that make up the boundaries of each row or column. This includes midpoints between rows
     * columns, not just start coordinate of each row or column.
     */
    @Nonnull
    private List<Float> rowOrColumnBoundsCoords = new ArrayList<>();

    @Override
    public void layoutContents(boolean setDraggedPosition)
    {
        if (shouldDistributeEvenly())
        {
            layoutContentsEvenly(setDraggedPosition);
        }
        else
        {
            layoutContentsUnevenly(setDraggedPosition);
        }
    }

    /**
     * Lays out the contents of the panel unevenly.
     */
    private void layoutContentsUnevenly(boolean setDraggedPosition)
    {
        // The current positions to use for the next control.
        float controlX = getPadding(Side.LEFT);
        float controlY = getPadding(Side.TOP);
        float rowY = controlY;
        float colX = controlX;

        // The current max coords reached in each direction. This is needed to work out where to put a
        // control when wrapping. If there are 2 controls with different heights on one row then if the
        // next control wraps it needs to use the max y position reached.
        float maxSoFarX = 0;
        float maxSoFarY = 0;

        float width = shouldFitToContentsX() ? getMaxWidth() : getWidth();
        float height = shouldFitToContentsY() ? getMaxHeight() : getHeight();

        rowOrColumnBoundsCoords.clear();
        rowOrColumnBoundsCoords.add(0.0f);

        for (Control control : getChildren())
        {
            if (!control.isVisible())
            {
                continue;
            }

            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlY = rowY;
            }
            else
            {
                controlX = colX;
            }

            // Wrap if the control is going to overlap the edge of the panel.
            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                if (getOverflowOrientation() != Orientation.HORIZONTAL && controlX + control.getEffectiveWidth() > (width - getPadding(Side.RIGHT)) / getInnerScale())
                {
                    controlX = getPadding(Side.LEFT);
                    rowY = maxSoFarY + getItemGapY();
                    controlY = rowY;
                    rowOrColumnBoundsCoords.add((controlY - getItemGapY() / 2));
                }
            }
            else
            {
                if (getOverflowOrientation() != Orientation.VERTICAL && controlY + control.getEffectiveWidth() > (height - getPadding(Side.BOTTOM)) / getInnerScale())
                {
                    controlY = getPadding(Side.TOP);
                    colX = maxSoFarX + getItemGapX();
                    controlX = colX;
                    rowOrColumnBoundsCoords.add((controlX - getItemGapX() / 2));
                }
            }

            controlX += control.getMargin(Side.LEFT);
            controlY += control.getMargin(Side.TOP);

            // Set the position on the control.
            if (!control.isDragging() || setDraggedPosition)
            {
                control.setX(controlX - getScrollOffsetX());
                control.setY(controlY - getScrollOffsetY());
            }

            maxSoFarX = Math.max(maxSoFarX, controlX + control.getWidth() + control.getMargin(Side.RIGHT));
            maxSoFarY = Math.max(maxSoFarY, controlY + control.getHeight() + control.getMargin(Side.BOTTOM));

            // Update the values for the next control.
            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlX += control.getWidth() + control.getMargin(Side.RIGHT) + getItemGapX();
            }
            else
            {
                controlY += control.getHeight() + control.getMargin(Side.BOTTOM) + getItemGapY();
            }
        }

        maxSoFarX += getPadding(Side.RIGHT);
        maxSoFarY += getPadding(Side.BOTTOM);

        rowOrColumnBoundsCoords.add(getFlowDirection() == Direction.LEFT_TO_RIGHT ? maxSoFarY : maxSoFarX);

        if (shouldFitMaxScrollOffsetToOverflowX() && getOverflowOrientation() == Orientation.HORIZONTAL)
        {
            setMaxScrollOffsetX((int) (maxSoFarX - (getWidth() / getInnerScale())));
        }
        else if (shouldFitMaxScrollOffsetToOverflowY() && getOverflowOrientation() == Orientation.VERTICAL)
        {
            setMaxScrollOffsetY((int) (maxSoFarY - (getHeight() / getInnerScale())));
        }

        tryFitToContents();
    }

    /**
     * Lays out the contents of the panel evenly.
     */
    private void layoutContentsEvenly(boolean setDraggedPosition)
    {
        // The current positions to use for the next control.
        float controlX = getPadding(Side.LEFT);
        float controlY = getPadding(Side.TOP);
        float rowY = controlY;
        float colX = controlX;

        // The current max coords reached in each direction. This is needed to work out where to put a
        // control when wrapping. If there are 2 controls with different heights on one row then if the
        // next control wraps it needs to use the max y position reached.
        float maxSoFarX = 0;
        float maxSoFarY = 0;

        float width = shouldFitToContentsX() ? getMaxWidth() : getWidth();
        float height = shouldFitToContentsY() ? getMaxHeight() : getHeight();

        rowOrColumnBoundsCoords.clear();
        rowOrColumnBoundsCoords.add(0.0f);

        float maxControlWidth = getChildren().stream().map(control -> control.getEffectiveWidth()).max(Float::compareTo).get();
        float maxControlHeight = getChildren().stream().map(control -> control.getEffectiveHeight()).max(Float::compareTo).get();

        for (Control control : getChildren())
        {
            if (!control.isVisible())
            {
                continue;
            }

            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlY = rowY;
            }
            else
            {
                controlX = colX;
            }

            // Wrap if the control is going to overlap the edge of the panel.
            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                if (getOverflowOrientation() != Orientation.HORIZONTAL && controlX + maxControlWidth > (width - getPadding(Side.RIGHT)) / getInnerScale())
                {
                    controlX = getPadding(Side.LEFT);
                    rowY = maxSoFarY + getItemGapY();
                    controlY = rowY;
                    rowOrColumnBoundsCoords.add((controlY - getItemGapY() / 2));
                }
            }
            else
            {
                if (getOverflowOrientation() != Orientation.VERTICAL && controlY + maxControlHeight > (height - getPadding(Side.BOTTOM)) / getInnerScale())
                {
                    controlY = getPadding(Side.TOP);
                    colX = maxSoFarX + getItemGapX();
                    controlX = colX;
                    rowOrColumnBoundsCoords.add((controlX - getItemGapX() / 2));
                }
            }

            // Set the position on the control.
            if (!control.isDragging() || setDraggedPosition)
            {
                control.setX(controlX + control.getMargin(Side.LEFT) - getScrollOffsetX());
                control.setY(controlY + control.getMargin(Side.TOP) - getScrollOffsetY());
            }

            maxSoFarX = Math.max(maxSoFarX, controlX + maxControlWidth);
            maxSoFarY = Math.max(maxSoFarY, controlY + maxControlHeight);

            // Update the values for the next control.
            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlX += maxControlWidth + getItemGapX();
            }
            else
            {
                controlY += maxControlHeight + getItemGapY();
            }
        }

        maxSoFarX += getPadding(Side.RIGHT);
        maxSoFarY += getPadding(Side.BOTTOM);

        rowOrColumnBoundsCoords.add(getFlowDirection() == Direction.LEFT_TO_RIGHT ? maxSoFarY : maxSoFarX);

        if (shouldFitMaxScrollOffsetToOverflowX() && getOverflowOrientation() == Orientation.HORIZONTAL)
        {
            setMaxScrollOffsetX((int) (maxSoFarX - (getWidth() / getInnerScale())));
        }
        else if (shouldFitMaxScrollOffsetToOverflowY() && getOverflowOrientation() == Orientation.VERTICAL)
        {
            setMaxScrollOffsetY((int) (maxSoFarY - (getHeight() / getInnerScale())));
        }

        tryFitToContents();
    }

    @Override
    public void layoutDockedContents()
    {
        // Flow panels do not support docked content.
    }

    @Override
    protected void updateDraggedControlOnRelease(@Nonnull Control draggedChild)
    {
        if (getDragReorderType() == DragReorderType.INSERT_ON_RELEASE)
        {
            reorderFromDrag(draggedChild);
        }
    }

    @Override
    protected void updateDraggedControl(@Nonnull Control draggedChild)
    {
        if (getDragReorderType() == DragReorderType.INSERT_ON_MOVE)
        {
            reorderFromDrag(draggedChild);
        }
    }

    /**
     * Reorders the controls based on the dragged control.
     *
     * @param draggedControl the child control currently being dragged.
     */
    protected void reorderFromDrag(@Nonnull Control draggedControl)
    {
        float draggedMidX = draggedControl.getMidX();
        float draggedMidY = draggedControl.getMidY();

        Control closestControl = draggedControl;
        float closestDifX = Integer.MAX_VALUE;
        float closestDifY = Integer.MAX_VALUE;
        float rowOrColumnLowerBound = 0;
        float rowOrColumnUpperBound = 0;

        List<Float> rowOrColumnBoundsCoordsWithScrollOffset = rowOrColumnBoundsCoords.stream().map(i -> i - (getFlowDirection() == Direction.LEFT_TO_RIGHT ? getScrollOffsetY() : getScrollOffsetX())).collect(Collectors.toList());

        if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
        {
            // If the dragged control is above the first row.
            if (draggedControl.getMidY() <= rowOrColumnBoundsCoordsWithScrollOffset.get(0))
            {
                rowOrColumnLowerBound = rowOrColumnBoundsCoordsWithScrollOffset.get(0);
                rowOrColumnUpperBound = rowOrColumnBoundsCoordsWithScrollOffset.get(1);
            }
            // If the dragged control is below the last row.
            else if (draggedControl.getMidY() >= rowOrColumnBoundsCoordsWithScrollOffset.get(rowOrColumnBoundsCoordsWithScrollOffset.size() - 1))
            {
                rowOrColumnLowerBound = rowOrColumnBoundsCoordsWithScrollOffset.get(rowOrColumnBoundsCoordsWithScrollOffset.size() - 2);
                rowOrColumnUpperBound = rowOrColumnBoundsCoordsWithScrollOffset.get(rowOrColumnBoundsCoordsWithScrollOffset.size() - 1);
            }
            // Otherwise work out which row it is on.
            else
            {
                for (int i = rowOrColumnBoundsCoordsWithScrollOffset.size() - 1; i >= 0; i--)
                {
                    if (draggedControl.getMidY() > rowOrColumnBoundsCoordsWithScrollOffset.get(i))
                    {
                        rowOrColumnLowerBound = rowOrColumnBoundsCoordsWithScrollOffset.get(i);
                        rowOrColumnUpperBound = rowOrColumnBoundsCoordsWithScrollOffset.get(i + 1);

                        break;
                    }
                }
            }

            List<Control> controlsInRow = new ArrayList<>();

            for (Control control : getChildrenCopy())
            {
                if (!control.isVisible())
                {
                    continue;
                }

                if (control == draggedControl)
                {
                    continue;
                }

                // Ignore any controls outside the row.
                if (control.getMidY() < rowOrColumnLowerBound || control.getMidY() > rowOrColumnUpperBound)
                {
                    continue;
                }

                controlsInRow.add(control);

                float difX = draggedMidX - control.getMidX();

                if (Math.abs(difX) < Math.abs(closestDifX))
                {
                    closestControl = control;
                    closestDifX = difX;
                }
            }

            if (controlsInRow.size() == 0)
            {
                Control lastControl = getChildren().get(getChildren().size() - 1);

                if (lastControl != draggedControl)
                {
                    insertOrMoveChildAfter(draggedControl, lastControl);
                }
            }
            else if (closestControl != draggedControl && closestControl.isReorderable())
            {
                if (closestDifX < 0)
                {
                    insertOrMoveChildBefore(draggedControl, closestControl);
                }
                else
                {
                    insertOrMoveChildAfter(draggedControl, closestControl);
                }
            }
        }
        else
        {
            // If the dragged control is to the left of the first column.
            if (draggedControl.getMidX() < rowOrColumnBoundsCoordsWithScrollOffset.get(0))
            {
                rowOrColumnLowerBound = rowOrColumnBoundsCoordsWithScrollOffset.get(0);
                rowOrColumnUpperBound = rowOrColumnBoundsCoordsWithScrollOffset.get(1);
            }
            // If the dragged control is to the right of the last column.
            else if (draggedControl.getMidX() > rowOrColumnBoundsCoordsWithScrollOffset.get(rowOrColumnBoundsCoordsWithScrollOffset.size() - 1))
            {
                rowOrColumnLowerBound = rowOrColumnBoundsCoordsWithScrollOffset.get(rowOrColumnBoundsCoordsWithScrollOffset.size() - 2);
                rowOrColumnUpperBound = rowOrColumnBoundsCoordsWithScrollOffset.get(rowOrColumnBoundsCoordsWithScrollOffset.size() - 1);
            }
            // Otherwise work out which column it is on.
            else
            {
                for (int i = rowOrColumnBoundsCoordsWithScrollOffset.size() - 1; i >= 0; i--)
                {
                    if (draggedControl.getMidX() > rowOrColumnBoundsCoordsWithScrollOffset.get(i))
                    {
                        rowOrColumnLowerBound = rowOrColumnBoundsCoordsWithScrollOffset.get(i);
                        rowOrColumnUpperBound = rowOrColumnBoundsCoordsWithScrollOffset.get(i + 1);

                        break;
                    }
                }
            }

            List<Control> controlsInColumn = new ArrayList<>();

            for (Control control : getChildrenCopy())
            {
                if (!control.isVisible())
                {
                    continue;
                }

                if (control == draggedControl)
                {
                    continue;
                }

                // Ignore any controls outside the column.
                if (control.getMidX() < rowOrColumnLowerBound || control.getMidX() > rowOrColumnUpperBound)
                {
                    continue;
                }

                controlsInColumn.add(control);

                float difY = draggedMidY - control.getMidY();

                if (Math.abs(difY) < Math.abs(closestDifY))
                {
                    closestControl = control;
                    closestDifY = difY;
                }
            }

            if (controlsInColumn.size() == 0)
            {
                Control lastControl = getChildren().get(getChildren().size() - 1);

                if (lastControl != draggedControl)
                {
                    insertOrMoveChildAfter(draggedControl, lastControl);
                }
            }
            else if (closestControl != draggedControl && closestControl.isReorderable())
            {
                if (closestDifY < 0)
                {
                    insertOrMoveChildBefore(draggedControl, closestControl);
                }
                else
                {
                    insertOrMoveChildAfter(draggedControl, closestControl);
                }
            }
        }
    }

    /**
     * @return the current flow direction.
     */
    @Nonnull
    public Direction getFlowDirection()
    {
        return flowDirection;
    }

    /**
     * Sets the current flow direction of the panel.
     */
    public void setFlowDirection(@Nonnull Direction flowDirection)
    {
        this.flowDirection = flowDirection;

        layoutContents();
    }

    /**
     * @return the overflow direction of the panel.
     */
    @Nonnull
    public Orientation getOverflowOrientation()
    {
        return overflowOrientation;
    }

    /**
     * Sets the overflow direction of the panel.
     */
    public void setOverflowOrientation(@Nonnull Orientation overflowOrientation)
    {
        this.overflowOrientation = overflowOrientation;

        layoutContents();
    }

    /**
     * @return whether to distribute the contents evenly.
     */
    public boolean shouldDistributeEvenly()
    {
        return shouldDistributeEvenly;
    }

    /**
     * Sets whether to distribute the contents evenly.
     */
    public void setShouldDistributeEvenly(boolean shouldDistributeEvenly)
    {
        this.shouldDistributeEvenly = shouldDistributeEvenly;

        layoutContents();
    }

    /**
     * Whether to automatically adjust the max scroll offset to fit the overflow in both axes.
     */
    public boolean shouldFitMaxScrollOffsetToOverflowXY()
    {
        return shouldFitMaxScrollOffsetToOverflowX() && shouldFitMaxScrollOffsetToOverflowY();
    }

    /**
     * Sets whether to automatically adjust the max scroll offset to fit the overflow in both axes.
     */
    public void setFitMaxScrollOffsetToOverflowXY(boolean fitMaxScrollOffsetToOverflowXY)
    {
        setFitMaxScrollOffsetToOverflowX(true);
        setFitMaxScrollOffsetToOverflowY(true);
    }

    /**
     * Whether to automatically adjust the max scroll offset to fit the overflow in the x-axis.
     */
    public boolean shouldFitMaxScrollOffsetToOverflowX()
    {
        return fitMaxScrollOffsetToOverflowX;
    }

    /**
     * Sets whether to automatically adjust the max scroll offset to fit the overflow in the x-axis.
     */
    public void setFitMaxScrollOffsetToOverflowX(boolean fitMaxScrollOffsetToOverflowX)
    {
        this.fitMaxScrollOffsetToOverflowX = fitMaxScrollOffsetToOverflowX;
    }

    /**
     * Whether to automatically adjust the max scroll offset to fit the overflow in the y-axis.
     */
    public boolean shouldFitMaxScrollOffsetToOverflowY()
    {
        return fitMaxScrollOffsetToOverflowY;
    }

    /**
     * Sets whether to automatically adjust the max scroll offset to fit the overflow in the y-axis.
     */
    public void setFitMaxScrollOffsetToOverflowY(boolean fitMaxScrollOffsetToOverflowY)
    {
        this.fitMaxScrollOffsetToOverflowY = fitMaxScrollOffsetToOverflowY;
    }

    /**
     * @return the horizontal item gap.
     */
    public int getItemGapX()
    {
        return itemGapX;
    }

    /**
     * Sets the horizontal item gap.
     */
    public void setItemGapX(int itemGapX)
    {
        this.itemGapX = itemGapX;

        layoutContents();
    }

    /**
     * @return the vertical item gap.
     */
    public int getItemGapY()
    {
        return itemGapY;
    }

    /**
     * Sets the vertical item gap.
     */
    public void setItemGapY(int itemGapY)
    {
        this.itemGapY = itemGapY;

        layoutContents();
    }
}
