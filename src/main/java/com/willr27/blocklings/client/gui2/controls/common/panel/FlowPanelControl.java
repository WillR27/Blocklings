package com.willr27.blocklings.client.gui2.controls.common.panel;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui2.Control;
import com.willr27.blocklings.client.gui2.IControl;
import com.willr27.blocklings.client.gui2.controls.Orientation;
import com.willr27.blocklings.client.gui2.controls.common.ScrollbarControl;
import com.willr27.blocklings.util.event.CancelableEvent;
import com.willr27.blocklings.util.event.Event;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A panel the displays items in a horizontal/vertical direction then wraps in the other direction
 * when out of space.
 */
@OnlyIn(Dist.CLIENT)
public class FlowPanelControl extends PanelControl
{
    /**
     * The event that is fired when the panel is reordered.
     */
    public final EventHandler<ReorderEvent> onReorder = new EventHandler<>();

    /**
     * The event that is fire when something inside the panel is dragged outside the bounds of the parent.
     */
    public final EventHandler<DraggedOutsideParentEvent> onDraggedOutsideParent = new EventHandler<>();

    /**
     * The primary direction to try layout controls.
     */
    private Orientation orientation = Orientation.HORIZONTAL;

    /**
     * The gap to put between each item in the x-axis.
     */
    private int itemGapX = 0;

    /**
     * The gap to put between each item in the y-axis.
     */
    private int itemGapY = 0;

    /**
     * Whether the items in the panel can be reordered.
     */
    private boolean isReorderable = false;

    /**
     * @param parent the parent control.
     * @param x the local x position.
     * @param y the local y position.
     * @param width the width.
     * @param height the height.
     */
    public FlowPanelControl(@Nullable IControl parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
    }

    @Override
    public void TODOrenamePreRender(int mouseX, int mouseY, float partialTicks)
    {
        updateChildrenBasePositions();
        tryResizeToFitContents();
        updateScrollbar();
//        updateDraggedControl(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void updateChildrenBasePositions()
    {
        // The current positions to use for the next control.
        int controlX = 0;
        int controlY = 0;

        // The current max coords reached in each direction. This is needed to work out where to put a
        // control when wrapping. If there are 2 controls with different heights on one row then if the
        // next control wraps it needs to use the max y position reached.
        int maxSoFarX = 0;
        int maxSoFarY = 0;

        for (Control control : getChildren())
        {
            // Wrap if the control is going to overlap the edge of the panel.
            if (orientation == Orientation.HORIZONTAL)
            {
                if (controlX + control.getEffectiveWidth() > width - (getPadding(Side.LEFT) + getPadding(Side.RIGHT)))
                {
                    controlX = 0;
                    controlY = maxSoFarY + itemGapY;
                }
            }
            else
            {
                if (controlY + control.getEffectiveHeight() > height - (getPadding(Side.TOP) + getPadding(Side.BOTTOM)))
                {
                    controlY = 0;
                    controlX = maxSoFarX + itemGapX;
                }
            }

            if (!control.isDragging())
            {
                control.setX(controlX);
                control.setY(controlY);
            }

            if (orientation == Orientation.HORIZONTAL)
            {
                controlX += control.getEffectiveWidth() + itemGapX;
                maxSoFarY = Math.max(maxSoFarY, controlY + control.getEffectiveHeight());
            }
            else
            {
                controlY += control.getEffectiveHeight() + itemGapY;
                maxSoFarX = Math.max(maxSoFarX, controlX + control.getEffectiveWidth());
            }
        }

        // Update the maximum possible scroll values.
        setMaxScrollX(maxSoFarX + getPadding(Side.LEFT) + getPadding(Side.RIGHT) - getWidth());
        setMaxScrollY(maxSoFarY + getPadding(Side.TOP) + getPadding(Side.BOTTOM) - getHeight());
    }

    /**
     * Updates the dragged control and tries to reorder the items.
     */
    private void updateDraggedControl(int mouseX, int mouseY, float partialTicks)
    {
        if (!isReorderable())
        {
            return;
        }

        // Ensure we are dragging a child control.
        if (!getChildren().contains(getScreen().getDraggedControl()))
        {
            return;
        }

        Control draggedControl = (Control) getScreen().getDraggedControl();

        // Find the local scaled mouse coordinates.
        int scaledLocalMouseX = (int) (toLocalX(mouseX) / getEffectiveScale());
        int scaledLocalMouseY = (int) (toLocalY(mouseY) / getEffectiveScale());

        // Drag based on the center of the control.
        int draggedControlX = scaledLocalMouseX - (draggedControl.getEffectiveWidth() / 2);
        int draggedControlY = scaledLocalMouseY - (draggedControl.getEffectiveHeight() / 2);

        // Include the scroll amounts in the dragged controls position.
        draggedControlX += getScrollX();
        draggedControlY += getScrollY();

        // Find the maximum coords the control can be dragged to.
        // E.g. 3 boxes 20 pixels high would give a max y of 60.
        int maxX = getChildren().stream().map(control -> control.getX() + control.getEffectiveWidth()).max(Integer::compare).get();
        int maxY = getChildren().stream().map(control -> control.getY() + control.getEffectiveHeight()).max(Integer::compare).get();

        // Prevent the dragged control from going beyond the bounds of the panel.
        draggedControlX = Math.min(Math.max(draggedControlX, 0), maxX - draggedControl.getEffectiveWidth());
        draggedControlY = Math.min(Math.max(draggedControlY, 0), maxY - draggedControl.getEffectiveHeight());

        Control closestControl = null;

        if (orientation == Orientation.HORIZONTAL)
        {
            // Find the y coord for each row in the panel.
            List<Integer> rowYs = getChildren().stream().map(control -> control.getY()).collect(Collectors.toSet()).stream().sorted().collect(Collectors.toList());
            List<Integer> rowMidYs = new ArrayList<>(rowYs);

            if (rowYs.size() > 1)
            {
                // Calculate the midpoints for all rows except the last.
                for (int i = 0; i < rowYs.size() - 1; i++)
                {
                    rowMidYs.set(i, rowYs.get(i) + (rowYs.get(i + 1) - rowYs.get(i)) / 2);
                }
            }

            // Calculate the last row's midpoint.
            rowMidYs.set(rowYs.size() - 1, rowYs.get(rowYs.size() - 1) + (maxY - rowYs.get(rowYs.size() - 1)) / 2);

            // Calculate the midpoints for the dragged control.
            int draggedControlMidX = draggedControlX + draggedControl.getEffectiveWidth() / 2;
            int draggedControlMidY = draggedControlY + draggedControl.getEffectiveHeight() / 2;

            int closestRowY = 0;
            int closestDifY = Integer.MAX_VALUE;

            // Find the closest row.
            for (int i = 0; i < rowYs.size(); i++)
            {
                int rowMidY = rowMidYs.get(i);
                int difY = Math.abs(draggedControlMidY - rowMidY);

                if (difY < closestDifY)
                {
                    closestRowY = rowYs.get(i);
                    closestDifY = difY;
                }
            }

            // Stop Java 8 complaining the variable isn't final inside the lambda.
            final int closestRowY2 = closestRowY;

            // Find all the controls in the closest row.
            List<Control> controlsInRow = getChildren().stream().filter(control -> control.getY() == closestRowY2).collect(Collectors.toList());

            int closestDifX = Integer.MAX_VALUE;

            // Find the closest control.
            for (Control control : controlsInRow)
            {
                int difX = Math.abs(draggedControlMidX - control.getX() - (control.getEffectiveWidth() / 2));

                if (difX < closestDifX)
                {
                    closestControl = control;
                    closestDifX = difX;
                }
            }
        }
        else
        {
            // Find the x coord for each row in the panel.
            List<Integer> colXs = getChildren().stream().map(control -> control.getX()).collect(Collectors.toSet()).stream().sorted().collect(Collectors.toList());
            List<Integer> colMidXs = new ArrayList<>(colXs);

            if (colXs.size() > 1)
            {
                // Calculate the midpoints for all columns except the last.
                for (int i = 0; i < colXs.size() - 1; i++)
                {
                    colMidXs.set(i, colXs.get(i) + (colXs.get(i + 1) - colXs.get(i)) / 2);
                }
            }

            // Calculate the last column's midpoint.
            colMidXs.set(colXs.size() - 1, colXs.get(colXs.size() - 1) + (maxX - colXs.get(colXs.size() - 1)) / 2);

            // Calculate the midpoints for the dragged control.
            int draggedControlMidX = draggedControlX + draggedControl.getEffectiveWidth() / 2;
            int draggedControlMidY = draggedControlY + draggedControl.getEffectiveHeight() / 2;

            int closestRowX = 0;
            int closestDifX = Integer.MAX_VALUE;

            // Find the closest column.
            for (int i = 0; i < colXs.size(); i++)
            {
                int rowMidX = colMidXs.get(i);
                int difX = Math.abs(draggedControlMidX - rowMidX);

                if (difX < closestDifX)
                {
                    closestRowX = colXs.get(i);
                    closestDifX = difX;
                }
            }

            // Stop Java 8 complaining the variable isn't final inside the lambda.
            final int closestRowX2 = closestRowX;

            // Find all the controls in the closest column.
            List<Control> controlsInCol = getChildren().stream().filter(control -> control.getX() == closestRowX2).collect(Collectors.toList());

            int closestDifY = Integer.MAX_VALUE;

            // Find the closest control.
            for (Control control : controlsInCol)
            {
                int difY = Math.abs(draggedControlMidY - control.getY() - (control.getEffectiveHeight() / 2));

                if (difY < closestDifY)
                {
                    closestControl = control;
                    closestDifY = difY;
                }
            }
        }

        if (closestControl != null)
        {
            int oldIndex = getChildren().indexOf(draggedControl);
            int newIndex = getChildren().indexOf(closestControl);

            // Send out an event if the controls should be reordered.
            if (oldIndex != newIndex)
            {
                ReorderEvent e = new ReorderEvent(oldIndex, newIndex);

                onReorder.handle(e);

                if (!e.isCancelled())
                {
                    // Moving back.
                    if (newIndex < oldIndex)
                    {
                        draggedControl.moveDirectlyBefore(closestControl);
                    }
                    // Moving forward.
                    else
                    {
                        draggedControl.moveDirectlyAfter(closestControl);
                    }
                }
            }
        }

        // Update the dragged control's position.
        draggedControl.setX(draggedControlX);
        draggedControl.setY(draggedControlY);

        // Scroll the panel if the mouse is dragging outside the control.
        if (scaledLocalMouseX < 0)
        {
            setScrollX((int) (getScrollX() - 12 * partialTicks));
        }
        else if (scaledLocalMouseX > width)
        {
            setScrollX((int) (getScrollX() + 12 * partialTicks));
        }

        // Scroll the panel if the mouse is dragging outside the control.
        if (scaledLocalMouseY - getPadding(Side.TOP) < 0)
        {
            setScrollY((int) (getScrollY() - 12 * partialTicks));
        }
        else if (scaledLocalMouseY + getPadding(Side.TOP) > height)
        {
            setScrollY((int) (getScrollY() + 12 * partialTicks));
        }

        // Scroll the parent control if the mouse is dragging outside the parent control.
        if (scaledLocalMouseX < 0)
        {
            getParent().setScrollX((int) (getParent().getScrollX() - 12 * partialTicks));
        }
        else if (scaledLocalMouseX > getParent().getWidth())
        {
            getParent().setScrollX((int) (getParent().getScrollX() + 12 * partialTicks));
        }

        // Scroll the parent control if the mouse is dragging outside the parent control.
        if (scaledLocalMouseY - getParent().getPadding(Side.TOP) < 0)
        {
            getParent().setScrollY((int) (getParent().getScrollY() - 12 * partialTicks));
        }
        else if (scaledLocalMouseY + getParent().getPadding(Side.TOP) > getParent().getHeight())
        {
            getParent().setScrollY((int) (getParent().getScrollY() + 12 * partialTicks));
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
//        fill(matrixStack, getScreenX(), getScreenY(), getScreenX() + getEffectiveWidth(), getScreenY() + getEffectiveHeight(), 0xFF000000);
    }

    /**
     * Handles the attached x-axis scrollbar's scroll event.
     */
    private void onScrollbarScrollX(@Nonnull ScrollbarControl.ScrollEvent e)
    {
        setScrollX((int) Math.ceil(e.scrollPercentage * getMaxScrollX()));
    }

    @Override
    public void setScrollbarX(@Nullable ScrollbarControl scrollbarControl)
    {
        if (this.scrollbarControlX != null)
        {
            this.scrollbarControlX.onScroll.unsubscribe(this::onScrollbarScrollX);
        }

        this.scrollbarControlX = scrollbarControl;

        if (this.scrollbarControlX != null)
        {
            this.scrollbarControlX.onScroll.subscribe(this::onScrollbarScrollX);

            this.scrollbarControlX.setScrollPercentage(getScrollX(), getMaxScrollX());
        }
    }

    /**
     * Handles the attached y-axis scrollbar's scroll event.
     */
    private void onScrollbarScrollY(@Nonnull ScrollbarControl.ScrollEvent e)
    {
        setScrollY((int) Math.ceil(e.scrollPercentage * getMaxScrollY()));
    }

    @Override
    public void setScrollbarY(@Nullable ScrollbarControl scrollbarControl)
    {
        if (this.scrollbarControlY != null)
        {
            this.scrollbarControlY.onScroll.unsubscribe(this::onScrollbarScrollY);
        }

        this.scrollbarControlY = scrollbarControl;

        if (this.scrollbarControlY != null)
        {
            this.scrollbarControlY.onScroll.subscribe(this::onScrollbarScrollY);

            this.scrollbarControlY.setScrollPercentage(getScrollY(), getMaxScrollY());
        }
    }

    /**
     * @return the primary direction to try layout the controls.
     */
    @Nonnull
    public Orientation getOrientation()
    {
        return orientation;
    }

    /**
     * Sets the primary direction to try layout the controls.
     */
    public void setOrientation(@Nonnull Orientation orientation)
    {
        this.orientation = orientation;
    }

    /**
     * @return the gap to put between each item in the x-axis.
     */
    public int getItemGapX()
    {
        return itemGapX;
    }

    /**
     * Sets the gap to put between each item in the x-axis.
     */
    public void setItemGapX(int itemGapX)
    {
        this.itemGapX = itemGapX;
    }

    /**
     * @return the gap to put between each item in the y-axis.
     */
    public int getItemGapY()
    {
        return itemGapY;
    }

    /**
     * Sets the gap to put between each item in the y-axis.
     */
    public void setItemGapY(int itemGapY)
    {
        this.itemGapY = itemGapY;
    }

    /**
     * @return whether the items in the panel can be reordered.
     */
    public boolean isReorderable()
    {
        return isReorderable;
    }

    /**
     * Sets whether the items in the panel can be reordered.
     */
    public void setIsReorderable(boolean reorderable)
    {
        isReorderable = reorderable;
    }

    /**
     * An event that occurs when the panel is reordered.
     */
    public static class ReorderEvent extends CancelableEvent
    {
        /**
         * The old index of the control.
         */
        public final int oldIndex;

        /**
         * The new index of the control.
         */
        public final int newIndex;

        /**
         * @param oldIndex the old index of the control.
         * @param newIndex the new index of the control.
         */
        public ReorderEvent(int oldIndex, int newIndex)
        {
            this.oldIndex = oldIndex;
            this.newIndex = newIndex;
        }
    }

    /**
     * An event that occurs when a child control is dragged outside the bounds of the parent control.
     */
    public static class DraggedOutsideParentEvent extends Event
    {
        /**
         * The horizontal edge of the drag. Negative indicates left, 0 indicates no drag and positive indicates right.
         */
        public final int dragX;

        /**
         * The vertical edge of the drag. Negative indicates top, 0 indicates no drag and positive indicates bottom.
         */
        public final int dragY;

        /**
         * @param dragX the horizontal edge of the drag.
         * @param dragY the vertical edge of the drag.
         */
        public DraggedOutsideParentEvent(int dragX, int dragY)
        {
            this.dragX = dragX;
            this.dragY = dragY;
        }
    }
}
