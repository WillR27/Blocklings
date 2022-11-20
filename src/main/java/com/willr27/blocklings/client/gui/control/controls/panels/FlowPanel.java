package com.willr27.blocklings.client.gui.control.controls.panels;

import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui2.controls.Orientation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.CallbackI;

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
    private List<Integer> rowOrColumnBoundsCoords = new ArrayList<>();

    @Override
    public void layoutContents(boolean setDraggedPosition)
    {
        // The current positions to use for the next control.
        int controlX = getPadding(Side.LEFT);
        int controlY = getPadding(Side.TOP);

        // The current max coords reached in each direction. This is needed to work out where to put a
        // control when wrapping. If there are 2 controls with different heights on one row then if the
        // next control wraps it needs to use the max y position reached.
        int maxSoFarX = 0;
        int maxSoFarY = 0;

        rowOrColumnBoundsCoords.clear();
        rowOrColumnBoundsCoords.add(getFlowDirection() == Direction.LEFT_TO_RIGHT ? getPadding(Side.LEFT) : getPadding(Side.TOP));

        for (Control control : getChildrenCopy())
        {
            // Wrap if the control is going to overlap the edge of the panel.
            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                if (controlX + control.getEffectiveWidth() > (getWidth() - getPadding(Side.RIGHT)) / getInnerScale())
                {
                    controlX = getPadding(Side.LEFT);
                    controlY = maxSoFarY + getItemGapY();
                    rowOrColumnBoundsCoords.add((controlY - getItemGapY() / 2));
                }
            }
            else
            {
                if (controlY + control.getEffectiveHeight() > (getHeight() - getPadding(Side.BOTTOM)) / getInnerScale())
                {
                    controlY = getPadding(Side.TOP);
                    controlX = maxSoFarX + getItemGapX();
                    rowOrColumnBoundsCoords.add((controlX - getItemGapX() / 2));
                }
            }

            if (!control.isDragging() || setDraggedPosition)
            {
                control.setX(controlX - getScrollOffsetX());
                control.setY(controlY - getScrollOffsetY());
            }

            if (getFlowDirection() == Direction.LEFT_TO_RIGHT)
            {
                controlX += control.getEffectiveWidth() + getItemGapX();
                maxSoFarY = Math.max(maxSoFarY, controlY + control.getEffectiveHeight());
            }
            else
            {
                controlY += control.getEffectiveHeight() + getItemGapY();
                maxSoFarX = Math.max(maxSoFarX, controlX + control.getEffectiveWidth());
            }
        }

        rowOrColumnBoundsCoords.add(getFlowDirection() == Direction.LEFT_TO_RIGHT ? maxSoFarY : maxSoFarX);

        // Update the maximum possible scroll values.
        setMaxScrollOffsetX((int) (maxSoFarX + getInnerScale() + (getPadding(Side.RIGHT) - getWidth() / getInnerScale())));
        setMaxScrollOffsetY((int) (maxSoFarY + getInnerScale() + (getPadding(Side.BOTTOM) - getHeight() / getInnerScale())));
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
        int draggedMidX = draggedControl.getMidX();
        int draggedMidY = draggedControl.getMidY();

        Control closestControl = draggedControl;
        int closestDifX = Integer.MAX_VALUE;
        int closestDifY = Integer.MAX_VALUE;
        int rowOrColumnLowerBound = 0;
        int rowOrColumnUpperBound = 0;

        List<Integer> rowOrColumnBoundsCoordsWithScrollOffset = rowOrColumnBoundsCoords.stream().map(i -> i - (getFlowDirection() == Direction.LEFT_TO_RIGHT ? getScrollOffsetY() : getScrollOffsetX())).collect(Collectors.toList());

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

                int difX = draggedMidX - control.getMidX();

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
                    insertChildAfter(draggedControl, lastControl);
                }
            }
            else if (closestControl != draggedControl)
            {
                if (closestDifX < 0)
                {
                    insertChildBefore(draggedControl, closestControl);
                }
                else
                {
                    insertChildAfter(draggedControl, closestControl);
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

                int difY = draggedMidY - control.getMidY();

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
                    insertChildAfter(draggedControl, lastControl);
                }
            }
            else if (closestControl != draggedControl)
            {
                if (closestDifY < 0)
                {
                    insertChildBefore(draggedControl, closestControl);
                }
                else
                {
                    insertChildAfter(draggedControl, closestControl);
                }
            }
        }
    }

    /**
     * @return the current flow direction.
     */
    public Direction getFlowDirection()
    {
        return flowDirection;
    }

    /**
     * Sets the current flow direction of the panel.
     */
    public void setFlowDirection(Direction flowDirection)
    {
        this.flowDirection = flowDirection;

        layoutContents();
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
