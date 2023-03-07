package com.willr27.blocklings.client.gui.control.controls.panels;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A panel that arranges its children in a grid.
 */
@OnlyIn(Dist.CLIENT)
public class GridPanel extends Control
{
    @Nonnull
    private final List<Definition> rowDefinitions = new ArrayList<>();

    @Nonnull
    private final List<Definition> columnDefinitions = new ArrayList<>();

    @Nonnull
    private final Map<BaseControl, GridCell> cells = new HashMap<>();

    @Nonnull
    private double[] rowHeights;

    @Nonnull
    private double[] columnWidths;

    @Override
    public void measureChildren()
    {
        double availableWidth = (getDesiredWidth() - getPaddingWidth()) / getInnerScale().x;
        double availableHeight = (getDesiredHeight() - getPaddingHeight()) / getInnerScale().y;

        rowHeights = new double[rowDefinitions.size()];
        columnWidths = new double[columnDefinitions.size()];

        // Get the fixed row heights.
        int i = 0;
        for (Definition definition : rowDefinitions)
        {
            if (definition.definition == GridDefinition.FIXED)
            {
                rowHeights[i] = definition.value;
            }

            i++;
        }

        // Get the fixed column widths.
        i = 0;
        for (Definition definition : columnDefinitions)
        {
            if (definition.definition == GridDefinition.FIXED)
            {
                columnWidths[i] = definition.value;
            }

            i++;
        }

        // Measure all children inside fixed/auto cells.
        for (int row = 0; row < rowHeights.length; row++)
        {
            for (int col = 0; col < columnWidths.length; col++)
            {
                GridDefinition rowDefinition = getRowDefinition(row);
                GridDefinition columnDefinition = getColumnDefinition(col);

                if (columnDefinition != GridDefinition.RATIO)
                {
                    double remainingWidth = availableWidth - Arrays.stream(columnWidths).sum();
                    remainingWidth += columnWidths[col];

                    for (BaseControl control : getCellControls(row, col))
                    {
                        // Get cell dimensions if available.
                        double availableCellWidth = columnWidths[col];

                        // Get remaining space for an auto sized column.
                        if (columnDefinition == GridDefinition.AUTO)
                        {
                            availableCellWidth = Math.max(0.0, remainingWidth);
                        }

                        control.doMeasure(availableCellWidth, 0.0);

                        // Update the current column width.
                        if (control.getDesiredWidth() > columnWidths[col])
                        {
                            columnWidths[col] = control.getDesiredWidth();
                        }
                    }
                }

                if (rowDefinition != GridDefinition.RATIO)
                {
                    double remainingHeight = availableHeight - Arrays.stream(rowHeights).sum();
                    remainingHeight += rowHeights[row];

                    for (BaseControl control : getCellControls(row, col))
                    {
                        // Get cell dimensions if available.
                        double availableCellHeight = rowHeights[row];

                        // Get remaining space for an auto sized row.
                        if (rowDefinition == GridDefinition.AUTO)
                        {
                            availableCellHeight = Math.max(0.0, remainingHeight);
                        }

                        control.doMeasure(0.0, availableCellHeight);

                        // Update the current row height.
                        if (control.getDesiredHeight() > rowHeights[row])
                        {
                            rowHeights[row] = control.getDesiredHeight();
                        }
                    }
                }
            }
        }

        double totalRowRatio = rowDefinitions.stream().filter(d -> d.definition == GridDefinition.RATIO).mapToDouble(d -> d.value).sum();
        double totalColumnRatio = columnDefinitions.stream().filter(d -> d.definition == GridDefinition.RATIO).mapToDouble(d -> d.value).sum();
        double sumOfNonRatioRowHeights = 0.0;
        double sumOfNonRatioColumnWidths = 0.0;

        i = 0;
        for (Definition definition : rowDefinitions)
        {
            if (definition.definition != GridDefinition.RATIO)
            {
                sumOfNonRatioRowHeights += rowHeights[i];
            }

            i++;
        }

        i = 0;
        for (Definition definition : columnDefinitions)
        {
            if (definition.definition != GridDefinition.RATIO)
            {
                sumOfNonRatioColumnWidths += columnWidths[i];
            }

            i++;
        }

        double remainingWidth = availableWidth - sumOfNonRatioColumnWidths;
        double remainingHeight = availableHeight - sumOfNonRatioRowHeights;

        for (int row = 0; row < rowHeights.length; row++)
        {
            for (int col = 0; col < columnWidths.length; col++)
            {
                GridDefinition rowDefinition = getRowDefinition(row);
                GridDefinition columnDefinition = getColumnDefinition(col);

                if (rowDefinition != GridDefinition.RATIO && columnDefinition != GridDefinition.RATIO)
                {
                    continue;
                }

                // Get the height for a ratio sized row.
                if (rowDefinition == GridDefinition.RATIO)
                {
                    rowHeights[row] = Math.max(0.0, remainingHeight * (rowDefinitions.get(row).value / totalRowRatio));
                }

                // Get the width for a ratio sized column.
                if (columnDefinition == GridDefinition.RATIO)
                {
                    columnWidths[col] = Math.max(0.0, remainingWidth * (columnDefinitions.get(col).value / totalColumnRatio));
                }

                for (BaseControl control : getCellControls(row, col))
                {
                    double availableCellWidth = columnWidths[col];
                    double availableCellHeight = rowHeights[row];

                    control.doMeasure(availableCellWidth - control.getMarginWidth(), availableCellHeight - control.getMarginHeight());
                }
            }
        }
    }

    @Override
    public void arrange()
    {
        for (int row = 0; row < rowHeights.length; row++)
        {
            for (int col = 0; col < columnWidths.length; col++)
            {
                double x = col == 0 ? 0.0 : Arrays.stream(columnWidths).limit(col).sum();
                double y = row == 0 ? 0.0 : Arrays.stream(rowHeights).limit(row).sum();
                double width = columnWidths[col];
                double height = rowHeights[row];

                for (BaseControl control : getCellControls(row, col))
                {
                    control.setWidth(control.getDesiredWidth());
                    control.setHeight(control.getDesiredHeight());

                    control.setX(x + (width - control.getWidthWithMargin()) * getHorizontalAlignmentFor(control) + control.getMargin().left);
                    control.setY(y + (height - control.getHeightWithMargin()) * getVerticalAlignmentFor(control) + control.getMargin().top);
                }
            }
        }
    }

    @Nonnull
    private List<BaseControl> getCellControls(int row, int column)
    {
        List<BaseControl> controls = new ArrayList<>();

        for (BaseControl control : cells.keySet())
        {
            GridCell cell = cells.get(control);
            if (cell.row == row && cell.column == column)
            {
                controls.add(control);
            }
        }

        return controls;
    }

    @Nonnull
    private GridDefinition getRowDefinition(int row)
    {
        int i = 0;
        for (Definition definition : rowDefinitions)
        {
            if (i == row)
            {
                return definition.definition;
            }

            i++;
        }

        return GridDefinition.AUTO;
    }

    @Nonnull
    private GridDefinition getColumnDefinition(int column)
    {
        int i = 0;
        for (Definition definition : columnDefinitions)
        {
            if (i == column)
            {
                return definition.definition;
            }

            i++;
        }

        return GridDefinition.AUTO;
    }

    @Override
    public void addChild(@Nonnull BaseControl child)
    {
        cells.put(child, new GridCell(0, 0));

        super.addChild(child);
    }

    public void addChild(@Nonnull BaseControl child, int row, int column)
    {
        cells.put(child, new GridCell(row, column));

        super.addChild(child);
    }

    @Override
    public void removeChild(@Nonnull BaseControl child)
    {
        cells.remove(child);

        super.removeChild(child);
    }

    /**
     * Adds a row definition to the grid with an optional value.
     *
     * @param definition the definition.
     * @param value      the value.
     */
    public void addRowDefinition(GridDefinition definition, double value)
    {
        rowDefinitions.add(new Definition(definition, value));
    }

    /**
     * Adds a column definition to the grid with an optional value.
     *
     * @param definition the definition.
     * @param value      the value.
     */
    public void addColumnDefinition(GridDefinition definition, double value)
    {
        columnDefinitions.add(new Definition(definition, value));
    }

    private static class Definition
    {
        private final GridDefinition definition;
        private final double value;

        /**
         * @param definition the definition.
         * @param value      the value.
         */
        public Definition(GridDefinition definition, double value)
        {
            this.definition = definition;
            this.value = value;
        }
    }

    private static class GridCell
    {
        private final int row;
        private final int column;

        /**
         * @param row    the row.
         * @param column the column.
         */
        public GridCell(int row, int column)
        {
            this.row = row;
            this.column = column;
        }
    }
}
