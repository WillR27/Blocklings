package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Used as the root control for a {@link BlocklingsScreen} and {@link BlocklingsContainerScreen}.
 */
@OnlyIn(Dist.CLIENT)
public class ScreenControl extends Control
{
    private final List<BaseControl> measureList = new ArrayList<>();

    private final List<BaseControl> arrangeList = new ArrayList<>();

    public void measureAndArrange()
    {
        measureList.removeIf(control -> control.getScreen() != this);
        arrangeList.removeIf(control -> control.getScreen() != this);

        while (!measureList.isEmpty())
        {
            int minDepth = Integer.MAX_VALUE;
            BaseControl minDepthControl = null;

            for (BaseControl control : measureList)
            {
                if (control.getTreeDepth() < minDepth)
                {
                    minDepth = control.getTreeDepth();
                    minDepthControl = control;
                }
            }

            if (minDepthControl == this)
            {
                minDepthControl.doMeasure(getWidth(), getHeight());
            }
            else if (minDepthControl != null && minDepthControl.getParent() != null)
            {
                minDepthControl.getParent().measureChildren();
            }
        }

        while (!arrangeList.isEmpty())
        {
            int minDepth = Integer.MAX_VALUE;
            BaseControl minDepthControl = null;

            for (BaseControl control : arrangeList)
            {
                if (control.getTreeDepth() < minDepth)
                {
                    minDepth = control.getTreeDepth();
                    minDepthControl = control;
                }
            }

            if (minDepthControl != null)
            {
                minDepthControl.doArrange();
            }
        }
    }

    @Override
    public void measureSelf(double availableWidth, double availableHeight)
    {
        setDesiredWidth(availableWidth);
        setDesiredHeight(availableHeight);
    }

    @Override
    public void arrange()
    {
        setSize(getDesiredSize());

        super.arrange();
    }

    public void addToMeasureQueue(@Nonnull BaseControl control)
    {
        if (!measureList.contains(control))
        {
            measureList.add(control);
        }
    }

    public void removeFromMeasureQueue(@Nullable BaseControl control)
    {
        measureList.remove(control);
    }

    public void addToArrangeQueue(@Nonnull BaseControl control)
    {
        if (!arrangeList.contains(control))
        {
            arrangeList.add(control);
        }
    }

    public void removeFromArrangeQueue(@Nullable BaseControl control)
    {
        arrangeList.remove(control);
    }

    @Override
    public void setParent(BaseControl parent)
    {
        // The screen control should never have a parent.
    }
}
