package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.event.events.SizeChangedEvent;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.awt.SystemColor.control;

/**
 * A control used to split available space into a grid.
 */
@OnlyIn(Dist.CLIENT)
public class GridControl extends Control
{
    private final List<SizeType> rowSizeTypes = new ArrayList<>();
    private final List<Float> rowSizeValues = new ArrayList<>();
    private final List<SizeType> colSizeTypes = new ArrayList<>();
    private final List<Float> colSizeValues = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private GridControl()
    {
        onSizeChanged.subscribe((e) -> rebuildGrid());
    }

    public void rebuildGrid()
    {
        if (rowSizeTypes.isEmpty())
        {
            rowSizeTypes.add(SizeType.AUTO);
            rowSizeValues.add(0.0f);
        }

        if (colSizeTypes.isEmpty())
        {
            colSizeTypes.add(SizeType.AUTO);
            colSizeValues.add(0.0f);
        }

        float availableHeight = getHeight();

        if (shouldFitToContentsY())
        {
            availableHeight = 0.0f;

            for (int i = 0; i < rowSizeTypes.size(); i++)
            {
                SizeType sizeType = rowSizeTypes.get(i);
                float value = rowSizeValues.get(i);

                if (sizeType == SizeType.FIXED)
                {
                    availableHeight += value;
                }
                else if (sizeType == SizeType.AUTO)
                {
                    float maxHeight = 0.0f;

                    for (int x = 0; x < colSizeTypes.size(); x++)
                    {
                        Control cellControl = getControl(x, i);

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

        float[] heights = new float[rowSizeTypes.size()];

        for (int i = 0; i < rowSizeTypes.size(); i++)
        {
            heights[i] = 0.0f;

            SizeType sizeType = rowSizeTypes.get(i);
            float value = rowSizeValues.get(i);

            if (sizeType == SizeType.FIXED)
            {
                float adjustedHeight = Math.min(value, availableHeight);

                heights[i] = adjustedHeight;

                availableHeight -= adjustedHeight;
            }
            else if (sizeType == SizeType.AUTO)
            {
                if (getChildren().isEmpty())
                {
                    continue;
                }

                float maxHeight = 0.0f;

                for (int x = 0; x < colSizeTypes.size(); x++)
                {
                    Control cellControl = getControl(x, i);

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
            for (int i = 0; i < rowSizeTypes.size(); i++)
            {
                SizeType sizeType = rowSizeTypes.get(i);
                float value = rowSizeValues.get(i);

                if (sizeType == SizeType.PERCENT)
                {
                    heights[i] = availableHeight * value;
                }
            }
        }

        float availableWidth = getWidth();

        if (shouldFitToContentsX())
        {
            availableWidth = 0.0f;

            for (int i = 0; i < colSizeTypes.size(); i++)
            {
                SizeType sizeType = colSizeTypes.get(i);
                float value = colSizeValues.get(i);

                if (sizeType == SizeType.FIXED)
                {
                    availableWidth += value;
                }
                else if (sizeType == SizeType.AUTO)
                {
                    float maxWidth = 0.0f;

                    for (int y = 0; y < rowSizeTypes.size(); y++)
                    {
                        Control cellControl = getControl(i, y);

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

        float[] widths = new float[colSizeTypes.size()];

        for (int i = 0; i < colSizeTypes.size(); i++)
        {
            widths[i] = 0.0f;

            SizeType sizeType = colSizeTypes.get(i);
            float value = colSizeValues.get(i);

            if (sizeType == SizeType.FIXED)
            {
                float adjustedWidth = Math.min(value, availableWidth);

                widths[i] = adjustedWidth;

                availableWidth -= adjustedWidth;
            }
            else if (sizeType == SizeType.AUTO)
            {
                if (getChildren().isEmpty())
                {
                    continue;
                }

                float maxWidth = 0.0f;

                for (int y = 0; y < rowSizeTypes.size(); y++)
                {
                    Control cellControl = getControl(i, y);

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
            for (int i = 0; i < colSizeTypes.size(); i++)
            {
                SizeType sizeType = colSizeTypes.get(i);
                float value = colSizeValues.get(i);

                if (sizeType == SizeType.PERCENT)
                {
                    widths[i] = availableWidth * value;
                }
            }
        }

        // Create the child controls if they don't exist.
        if (getChildren().size() == 0)
        {
            Random random = new Random();
            for (int i = 0; i < rowSizeTypes.size() * colSizeTypes.size(); i++)
            {
                Control control = new Control();
                control.setParent(this);
                control.setBackgroundColour(Colour.fromRGBInt(random.nextInt()));

                EventHandler.Handler<SizeChangedEvent> onChildSizeChanged = (e1) -> rebuildGrid();

                control.onChildAdded.subscribe((e) ->
                {
                    e.childAdded.onSizeChanged.subscribeFirst(onChildSizeChanged);

                    if (rowSizeTypes.get(getControlRow(control)) == SizeType.AUTO || colSizeTypes.get(getControlCol(control)) == SizeType.AUTO)
                    {
                        rebuildGrid();
                    }
                });
                control.onChildRemoved.subscribe((e) ->
                {
                    e.childRemoved.onSizeChanged.unsubscribe(onChildSizeChanged);

                    if (rowSizeTypes.get(getControlRow(control)) == SizeType.AUTO || colSizeTypes.get(getControlCol(control)) == SizeType.AUTO)
                    {
                        rebuildGrid();
                    }
                });
//                control.onSizeChanged.subscribe((e) ->
//                {
//                    if (rowSizeTypes.get(getControlRow(control)) == SizeType.AUTO || colSizeTypes.get(getControlCol(control)) == SizeType.AUTO)
//                    {
//                        rebuildGrid();
//                    }
//                });
            }
        }

        for (int y = 0; y < rowSizeTypes.size(); y++)
        {
            float currentX = 0;
            for (int x = 0; x < colSizeTypes.size(); x++)
            {
                Control control = getControl(x, y);
                control.setWidth(widths[x]);
                control.setX(currentX);
                currentX += control.getWidth();
            }
        }

        for (int x = 0; x < colSizeTypes.size(); x++)
        {
            float currentY = 0;
            for (int y = 0; y < rowSizeTypes.size(); y++)
            {
                Control control = getControl(x, y);
                control.setHeight(heights[y]);
                control.setY(currentY);
                currentY += control.getHeight();
            }
        }
    }

    public void addControl(Control control, int x, int y)
    {
        getControl(x, y).addChild(control);
    }

    private int getControlRow(Control control)
    {
        return getControlRow(getChildren().indexOf(control));
    }

    private int getControlCol(Control control)
    {
        return getControlCol(getChildren().indexOf(control));
    }

    private int getControlRow(int index)
    {
        return index / colSizeTypes.size();
    }

    private int getControlCol(int index)
    {
        return index % colSizeTypes.size();
    }

    private int getControlIndex(int x, int y)
    {
        return x + y * colSizeTypes.size();
    }

    private Control getControl(int x, int y)
    {
        return getChildren().get(getControlIndex(x, y));
    }

    public static class Builder
    {
        private GridControl gridControl;

        public Builder()
        {
            gridControl = new GridControl();
        }

        public GridControl build()
        {
            gridControl.rebuildGrid();

            return gridControl;
        }

        public Builder addRow(SizeType sizeType, float value)
        {
            gridControl.rowSizeTypes.add(sizeType);
            gridControl.rowSizeValues.add(value);

            return this;
        }

        public Builder addCol(SizeType sizeType, float value)
        {
            gridControl.colSizeTypes.add(sizeType);
            gridControl.colSizeValues.add(value);

            return this;
        }
    }

    /**
     * Represents each type of sizing for a grid row/col.
     */
    public enum SizeType
    {
        FIXED,
        AUTO,
        PERCENT,
    }
}
