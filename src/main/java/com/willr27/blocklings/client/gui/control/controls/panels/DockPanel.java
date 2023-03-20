package com.willr27.blocklings.client.gui.control.controls.panels;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.properties.Dock;
import com.willr27.blocklings.client.gui.properties.Visibility;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.print.Doc;
import java.util.HashMap;
import java.util.Map;

/**
 * A panel that docks its children inside the panel.
 */
@OnlyIn(Dist.CLIENT)
public class DockPanel extends Control
{
    @Nonnull
    private final Map<BaseControl, Dock> docks = new HashMap<>();

    @Override
    public void measureChildren()
    {
        double availableWidth = (getDesiredWidth() - getPaddingWidth()) / getInnerScale().x;
        double availableHeight = (getDesiredHeight() - getPaddingHeight()) / getInnerScale().y;

        for (BaseControl child : getChildren())
        {
            Dock dock = docks.get(child);

            child.doMeasure(availableWidth - child.getMarginWidth(), availableHeight - child.getMarginHeight());

            if (dock == Dock.FILL)
            {
                availableWidth -= child.getDesiredWidth() + child.getMarginWidth();
                availableHeight -= child.getDesiredHeight() + child.getMarginHeight();
            }
            else if (dock == Dock.LEFT || dock == Dock.RIGHT)
            {
                availableWidth -= child.getDesiredWidth() + child.getMarginWidth();
            }
            else if (dock == Dock.TOP || dock == Dock.BOTTOM)
            {
                availableHeight -= child.getDesiredHeight() + child.getMarginHeight();
            }
        }
    }

    @Override
    protected void arrange()
    {
        double topLeftX = 0.0;
        double topLeftY = 0.0;
        double availableWidth = (getDesiredWidth() - getPaddingWidth()) / getInnerScale().x;
        double availableHeight = (getDesiredHeight() - getPaddingHeight()) / getInnerScale().y;

        for (BaseControl control : getChildrenCopy())
        {
            if (control.getVisibility() == Visibility.COLLAPSED)
            {
                continue;
            }

            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            Dock dock = docks.get(control);
            availableWidth = Math.max(0.0, availableWidth);
            availableHeight = Math.max(0.0, availableHeight);

            if (dock == Dock.FILL)
            {
                control.setX(topLeftX + control.getMargin().left);
                control.setY(topLeftY + control.getMargin().top);

                availableWidth -= control.getWidth() + control.getMarginWidth();
                availableHeight -= control.getHeight() + control.getMarginHeight();
            }
            else if (dock == Dock.LEFT)
            {
                control.setX(topLeftX + control.getMargin().left);
                control.setY(topLeftY + control.getMargin().top);

                topLeftX += control.getWidth() + control.getMarginWidth();
                availableWidth -= control.getWidth() + control.getMarginWidth();
            }
            else if (dock == Dock.TOP)
            {
                control.setX(topLeftX + control.getMargin().left);
                control.setY(topLeftY + control.getMargin().top);

                topLeftY += control.getHeight() + control.getMarginHeight();
                availableHeight -= control.getHeight() + control.getMarginHeight();
            }
            else if (dock == Dock.RIGHT)
            {
                control.setX(topLeftX + availableWidth - control.getWidthWithMargin() + control.getMargin().left);
                control.setY(topLeftY + control.getMargin().top);

                availableWidth -= control.getWidth() + control.getMarginWidth();
            }
            else if (dock == Dock.BOTTOM)
            {
                control.setX(topLeftX + control.getMargin().left);
                control.setY(topLeftY + availableHeight - control.getHeightWithMargin() + control.getMargin().top);

                availableHeight -= control.getHeight() + control.getMarginHeight();
            }
        }
    }

    @Override
    public void addChild(@Nonnull BaseControl child)
    {
        docks.put(child, Dock.FILL);

        super.addChild(child);
    }

    public void addChild(@Nonnull BaseControl child, @Nonnull Dock dock)
    {
        docks.put(child, dock);

        super.addChild(child);
    }

    @Override
    public void removeChild(@Nonnull BaseControl child)
    {
        removeChild(child, false);
    }

    @Override
    public void removeChild(@Nullable BaseControl child, boolean preserveEventSubscribers)
    {
        docks.remove(child);

        super.removeChild(child, preserveEventSubscribers);
    }
}
