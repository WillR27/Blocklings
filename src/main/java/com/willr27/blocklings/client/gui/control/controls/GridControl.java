package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.event.events.MarginsChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.SizeChangedEvent;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A control used to split available space into a grid.
 */
@OnlyIn(Dist.CLIENT)
public class GridControl extends Control
{
    /**
     * The grid definition.
     */
    @Nonnull
    private final GridDefinition gridDefinition;

    /**
     * @param gridDefinition the grid definition.
     */
    public GridControl(@Nonnull GridDefinition gridDefinition)
    {
        this.gridDefinition = gridDefinition;

        if (gridDefinition.rows.isEmpty())
        {
            gridDefinition.rows.add(new Auto());
        }

        if (gridDefinition.cols.isEmpty())
        {
            gridDefinition.cols.add(new Auto());
        }

        rebuildGrid();

        onSizeChanged.subscribe((e) -> rebuildGrid());
    }

    public void rebuildGrid()
    {
        float availableHeight = getHeight();

        if (shouldFitToContentsY())
        {
            availableHeight = 0.0f;

            for (int i = 0; i < gridDefinition.rows.size(); i++)
            {
                IDefinition definition = gridDefinition.rows.get(i);

                if (definition instanceof Fixed)
                {
                    availableHeight += ((Fixed) definition).size;
                }
                else if (definition instanceof Auto)
                {
                    float maxHeight = 0.0f;

                    for (int x = 0; x < gridDefinition.cols.size(); x++)
                    {
                        Control cellControl = getCellControl(x, i);

                        for (Control control : cellControl.getChildren())
                        {
                            float testHeight = control.getY() + control.getHeight();

                            if (control.getFillHeight() != null)
                            {
                                testHeight = cellControl.getHeight() / cellControl.getInnerScale() * control.getFillHeight().percent;
                            }

                            testHeight += control.getMargin(Side.TOP) + control.getMargin(Side.BOTTOM) - cellControl.getPadding(Side.TOP) - cellControl.getPadding(Side.BOTTOM);

                            if (testHeight > maxHeight)
                            {
                                maxHeight = testHeight;
                            }
                        }
                    }

                    availableHeight += maxHeight;
                }
            }
        }

        float[] heights = new float[gridDefinition.rows.size()];

        for (int i = 0; i < gridDefinition.rows.size(); i++)
        {
            heights[i] = 0.0f;

            IDefinition definition = gridDefinition.rows.get(i);

            if (definition instanceof Fixed)
            {
                float adjustedHeight = Math.min(((Fixed) definition).size, availableHeight);

                heights[i] = adjustedHeight;

                availableHeight -= adjustedHeight;
            }
            else if (definition instanceof Auto)
            {
                if (getChildren().isEmpty())
                {
                    continue;
                }

                float maxHeight = 0.0f;

                for (int x = 0; x < gridDefinition.cols.size(); x++)
                {
                    Control cellControl = getCellControl(x, i);

                    for (Control control : cellControl.getChildren())
                    {
                        float testHeight = control.getY() + control.getHeight();

                        if (control.getFillHeight() != null)
                        {
                            testHeight = cellControl.getHeight() / cellControl.getInnerScale() * control.getFillHeight().percent;
                        }

                        testHeight += control.getMargin(Side.TOP) + control.getMargin(Side.BOTTOM) - cellControl.getPadding(Side.TOP) - cellControl.getPadding(Side.BOTTOM);

                        if (testHeight > maxHeight)
                        {
                            maxHeight = testHeight;
                        }
                    }
                }

                float adjustedHeight = Math.min(maxHeight, availableHeight);

                heights[i] = adjustedHeight;

                availableHeight -= adjustedHeight;
            }
        }

        if (availableHeight > 0.0f)
        {
            for (int i = 0; i < gridDefinition.rows.size(); i++)
            {
                IDefinition definition = gridDefinition.rows.get(i);

                if (definition instanceof Fill)
                {
                    heights[i] = availableHeight * ((Fill) definition).percent;
                }
            }
        }

        float availableWidth = getWidth();

        if (shouldFitToContentsX())
        {
            availableWidth = 0.0f;

            for (int i = 0; i < gridDefinition.cols.size(); i++)
            {
                IDefinition definition = gridDefinition.cols.get(i);

                if (definition instanceof Fixed)
                {
                    availableWidth += ((Fixed) definition).size;
                }
                else if (definition instanceof Auto)
                {
                    float maxWidth = 0.0f;

                    for (int y = 0; y < gridDefinition.rows.size(); y++)
                    {
                        CellControl cellControl = getCellControl(i, y);

                        for (Control control : cellControl.getChildren())
                        {
                            float testWidth = control.getX() + control.getWidth();

                            if (control.getFillWidth() != null)
                            {
                                testWidth = cellControl.getWidth() / cellControl.getInnerScale() * control.getFillWidth().percent;
                            }

                            testWidth += control.getPadding(Side.LEFT) + getPadding(Side.RIGHT) - cellControl.getPadding(Side.LEFT) - cellControl.getPadding(Side.RIGHT);

                            if (testWidth > maxWidth)
                            {
                                maxWidth = testWidth;
                            }
                        }
                    }

                    availableWidth += maxWidth;
                }
            }
        }

        float[] widths = new float[gridDefinition.cols.size()];

        for (int i = 0; i < gridDefinition.cols.size(); i++)
        {
            widths[i] = 0.0f;

            IDefinition definition = gridDefinition.cols.get(i);

            if (definition instanceof Fixed)
            {
                float adjustedWidth = Math.min(((Fixed) definition).size, availableWidth);

                widths[i] = adjustedWidth;

                availableWidth -= adjustedWidth;
            }
            else if (definition instanceof Auto)
            {
                if (getChildren().isEmpty())
                {
                    continue;
                }

                float maxWidth = 0.0f;

                for (int y = 0; y < gridDefinition.rows.size(); y++)
                {
                    CellControl cellControl = getCellControl(i, y);

                    for (Control control : cellControl.getChildren())
                    {
                        float testWidth = control.getX() + control.getWidth();

                        if (control.getFillWidth() != null)
                        {
                            testWidth = cellControl.getWidth() / cellControl.getInnerScale() * control.getFillWidth().percent;
                        }

                        testWidth += control.getPadding(Side.LEFT) + getPadding(Side.RIGHT) - cellControl.getPadding(Side.LEFT) - cellControl.getPadding(Side.RIGHT);

                        if (testWidth > maxWidth)
                        {
                            maxWidth = testWidth;
                        }
                    }
                }

                float adjustedWidth = Math.min(maxWidth, availableWidth);

                widths[i] = adjustedWidth;

                availableWidth -= adjustedWidth;
            }
        }

        if (availableWidth > 0.0f)
        {
            for (int i = 0; i < gridDefinition.cols.size(); i++)
            {
                IDefinition definition = gridDefinition.cols.get(i);

                if (definition instanceof Fill)
                {
                    widths[i] = availableWidth * ((Fill) definition).percent;
                }
            }
        }

        // Create the child controls if they don't exist.
        if (getChildren().size() == 0)
        {
            Random random = new Random();
            for (int i = 0; i < gridDefinition.rows.size() * gridDefinition.cols.size(); i++)
            {
                CellControl cellControl = new CellControl();
                cellControl.setParent(this);

                EventHandler.Handler<SizeChangedEvent> onChildSizeChanged = (e1) -> rebuildGrid();
                EventHandler.Handler<MarginsChangedEvent> onChildMarginsChanged = (e1) -> rebuildGrid();

                cellControl.onChildAdded.subscribe((e) ->
                {
                    e.childAdded.onSizeChanged.subscribeFirst(onChildSizeChanged);
                    e.childAdded.onMarginsChanged.subscribeFirst(onChildMarginsChanged);

                    if (gridDefinition.rows.get(getCellControlRow(cellControl)) instanceof Auto || gridDefinition.cols.get(getCellControlCol(cellControl)) instanceof Auto)
                    {
                        rebuildGrid();
                    }
                });
                cellControl.onChildRemoved.subscribe((e) ->
                {
                    e.childRemoved.onSizeChanged.unsubscribe(onChildSizeChanged);
                    e.childRemoved.onMarginsChanged.unsubscribe(onChildMarginsChanged);

                    if (gridDefinition.rows.get(getCellControlRow(cellControl)) instanceof Auto || gridDefinition.cols.get(getCellControlCol(cellControl)) instanceof Auto)
                    {
                        rebuildGrid();
                    }
                });
            }
        }

        for (int y = 0; y < gridDefinition.rows.size(); y++)
        {
            float currentX = 0;
            for (int x = 0; x < gridDefinition.cols.size(); x++)
            {
                CellControl cellControl = getCellControl(x, y);
                cellControl.setWidth(widths[x]);
                cellControl.setX(currentX);
                currentX += cellControl.getWidth();
            }
        }

        for (int x = 0; x < gridDefinition.cols.size(); x++)
        {
            float currentY = 0;
            for (int y = 0; y < gridDefinition.rows.size(); y++)
            {
                CellControl cellControl = getCellControl(x, y);
                cellControl.setHeight(heights[y]);
                cellControl.setY(currentY);
                currentY += cellControl.getHeight();
            }
        }
    }

    /**
     * Adds a control to the given cell.
     *
     * @param control the control to add.
     * @param x the column index.
     * @param y the row index.
     */
    public void addControl(@Nonnull Control control, int x, int y)
    {
        getCellControl(x, y).addChild(control);
    }

    /**
     * Removes a control from the given cell if it exists.
     *
     * @param control the control to remove.
     * @param x the column index.
     * @param y the row index.
     */
    public void removeControl(@Nonnull Control control, int x, int y)
    {
        getCellControl(x, y).removeChild(control);
    }

    /**
     * Gets the row index of the {@link CellControl}.
     *
     * @param cellControl the cell control.
     * @return the row index.
     */
    public int getCellControlRow(Control cellControl)
    {
        return getCellControlRow(getChildren().indexOf(cellControl));
    }

    /**
     * Gets the column index of the {@link CellControl}.
     *
     * @param cellControl the cell control.
     * @return the column index.
     */
    public int getCellControlCol(@Nonnull Control cellControl)
    {
        return getCellControlCol(getChildren().indexOf(cellControl));
    }

    /**
     * Gets the row index of the given child index.
     *
     * @param index the child index.
     * @return the row index.
     */
    public int getCellControlRow(int index)
    {
        return index / gridDefinition.cols.size();
    }

    /**
     * Gets the column index of the given child index.
     *
     * @param index the child index.
     * @return the column index.
     */
    public int getCellControlCol(int index)
    {
        return index % gridDefinition.cols.size();
    }

    /**
     * Gets the index of the {@link CellControl} at the given cell in {@link #getChildren()}.
     *
     * @param x the column index.
     * @param y the row index.
     * @return the index.
     */
    public int getCellControlIndex(int x, int y)
    {
        return x + y * gridDefinition.cols.size();
    }

    /**
     * Gets the {@link CellControl} at the given cell.
     *
     * @param x the column index.
     * @param y the row index.
     * @return the cell control.
     */
    @Nonnull
    public CellControl getCellControl(int x, int y)
    {
        return (CellControl) getChildren().get(getCellControlIndex(x, y));
    }

    /**
     * A control that contains all the contents of a single cell.
     */
    public static class CellControl extends Control
    {

    }

    /**
     * Defines the rows and columns of a grid.
     */
    public static class GridDefinition
    {
        /**
         * The row definitions.
         */
        @Nonnull
        private final List<IDefinition> rows = new ArrayList<>();

        /**
         * The column definitions.
         */
        @Nonnull
        private final List<IDefinition> cols = new ArrayList<>();

        /**
         * Adds the given row definition.
         */
        public GridDefinition addRow(@Nonnull IDefinition definition)
        {
            rows.add(definition);

            return this;
        }

        /**
         * Adds the given column definition.
         */
        public GridDefinition addCol(@Nonnull IDefinition definition)
        {
            cols.add(definition);

            return this;
        }
    }

    /**
     * Defines a fixed sized row/column.
     */
    public static class Fixed implements IDefinition
    {
        /**
         * The absolute width/height of the row/col.
         */
        private final float size;

        /**
         * @param size the absolute width/height of the row/col.
         */
        public Fixed(float size)
        {
            this.size = size;
        }
    }

    /**
     * Defines a row/col that fills the available remaining space the given percent.
     */
    public static class Fill implements IDefinition
    {
        /**
         * The percent of the remaining space the row/col should fill.
         */
        private final float percent;

        /**
         * @param percent the percent of the remaining space the row/col should full.
         */
        public Fill(float percent)
        {
            this.percent = percent;
        }
    }

    /**
     * Defines a row/col that sizes to fit its contents.
     */
    public static class Auto implements IDefinition
    {
        /**
         * Empty constructor.
         */
        public Auto()
        {

        }
    }

    /**
     * Common interface for the grid definition types.
     */
    public interface IDefinition { }
}
