package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.TryDragEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui3.util.GuiUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used as the root control for a {@link BlocklingsScreen} and {@link BlocklingsContainerScreen}.
 */
@OnlyIn(Dist.CLIENT)
public class ScreenControl extends Control
{
    private final List<BaseControl> measureList = new ArrayList<>();

    private final List<BaseControl> arrangeList = new ArrayList<>();

    @Nullable
    private BaseControl hoveredControl;

    @Nullable
    private BaseControl pressedControl;

    /**
     * The pixel x coordinate that the pressed control was pressed at.
     */
    private double pressedStartPixelX = 0.0;

    /**
     * The pixel y coordinate that the pressed control was pressed at.
     */
    private double pressedStartPixelY = 0.0;

    @Nullable
    private BaseControl focusedControl;

    @Nullable
    private BaseControl draggedControl;

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

    /**
     * Mimics {@link Screen#render(MatrixStack, int, int, float)}.
     */
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        float guiScale = GuiUtil.getInstance().getGuiScale();
        double mouseX = GuiUtil.getInstance().getPixelMouseX();
        double mouseY = GuiUtil.getInstance().getPixelMouseY();

        forwardTryDrag(new TryDragEvent(mouseX, mouseY));

        if (getDraggedControl() != null)
        {
            getDraggedControl().onDrag(mouseX, mouseY, partialTicks);
        }

        measureAndArrange();

        matrixStack.pushPose();
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        forwardRender(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.popPose();
    }

    @Override
    public void forwardMouseClicked(@Nonnull MouseClickedEvent e)
    {
        super.forwardMouseClicked(e);
    }

    @Override
    public void forwardMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        super.forwardMouseReleased(e);

        setPressedControl(null);
        setDraggedControl(null);
    }

    @Override
    public void forwardMouseScrolled(@Nonnull MouseScrolledEvent e)
    {
        super.forwardMouseScrolled(e);
    }

    @Override
    public void setParent(BaseControl parent)
    {
        // The screen control should never have a parent.
    }

    @Nullable
    public BaseControl getHoveredControl()
    {
        return hoveredControl;
    }

    public void setHoveredControl(@Nullable BaseControl hoveredControl)
    {
        this.hoveredControl = hoveredControl;
    }

    @Nullable
    public BaseControl getPressedControl()
    {
        return pressedControl;
    }

    public void setPressedControl(@Nullable BaseControl control)
    {
        if (pressedControl != control)
        {
            if (control != null)
            {
                pressedStartPixelX = GuiUtil.getInstance().getPixelMouseX();
                pressedStartPixelY = GuiUtil.getInstance().getPixelMouseY();
            }
        }

        pressedControl = control;
    }

    @Nullable
    public BaseControl getFocusedControl()
    {
        return focusedControl;
    }

    public void setFocusedControl(@Nullable BaseControl focusedControl)
    {
        this.focusedControl = focusedControl;
    }

    @Nullable
    public BaseControl getDraggedControl()
    {
        return draggedControl;
    }

    public void setDraggedControl(@Nullable BaseControl control)
    {
        if (draggedControl != control)
        {
            if (draggedControl != null)
            {
                draggedControl.onDragEnd();
            }

            if (control != null)
            {
                control.onDragStart();
            }
        }

        draggedControl = control;
    }

    public double getPressedStartPixelX()
    {
        return pressedStartPixelX;
    }

    public double getPressedStartPixelY()
    {
        return pressedStartPixelY;
    }
}
