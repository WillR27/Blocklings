package com.willr27.blocklings.client.gui.control.controls.panels;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Panel;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui2.controls.Orientation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Lays out its children from left to right, top to bottom or vice versa.
 */
@OnlyIn(Dist.CLIENT)
public class FlowPanel extends Panel
{
    /**
     * The orientation to lay out the contents in. The contents will overflow in the
     * opposite orientation.
     */
    private Orientation orientation = Orientation.HORIZONTAL;

    /**
     * The horizontal gap between each item.
     */
    private int itemGapX = 0;

    /**
     * The vertical gap between each item.
     */
    private int itemGapY = 0;

    @Override
    public void layoutContents()
    {
        // The current positions to use for the next control.
        int controlX = getPadding(Side.LEFT);
        int controlY = getPadding(Side.TOP);

        // The current max coords reached in each direction. This is needed to work out where to put a
        // control when wrapping. If there are 2 controls with different heights on one row then if the
        // next control wraps it needs to use the max y position reached.
        int maxSoFarX = 0;
        int maxSoFarY = 0;

        for (Control control : getChildrenCopy())
        {
            // Wrap if the control is going to overlap the edge of the panel.
            if (getOrientation() == Orientation.HORIZONTAL)
            {
                if (controlX + control.getEffectiveWidth() > (getWidth() - getPadding(Side.RIGHT)) / getInnerScale())
                {
                    controlX = getPadding(Side.LEFT);
                    controlY = maxSoFarY + getItemGapY();
                }
            }
            else
            {
                if (controlY + control.getEffectiveHeight() > (getHeight() - getPadding(Side.BOTTOM)) / getInnerScale())
                {
                    controlY = getPadding(Side.TOP);
                    controlX = maxSoFarX + getItemGapX();
                }
            }

            if (!control.isDragging())
            {
                control.setX(controlX - getScrollOffsetX());
                control.setY(controlY - getScrollOffsetY());
            }

            if (getOrientation() == Orientation.HORIZONTAL)
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

        // Update the maximum possible scroll values.
        setMaxScrollOffsetX((int) (maxSoFarX + getInnerScale() + (getPadding(Side.RIGHT) - getWidth() / getInnerScale())));
        setMaxScrollOffsetY((int) (maxSoFarY + getInnerScale() + (getPadding(Side.BOTTOM) - getHeight() / getInnerScale())));
    }

    /**
     * @return the current orientation.
     */
    public Orientation getOrientation()
    {
        return orientation;
    }

    /**
     * Sets the current orientation of the panel.
     */
    public void setOrientation(Orientation orientation)
    {
        this.orientation = orientation;

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
