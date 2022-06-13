package com.willr27.blocklings.client.gui.controls.common.panel;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.Control;
import com.willr27.blocklings.client.gui.GuiUtil;
import com.willr27.blocklings.client.gui.IControl;
import com.willr27.blocklings.client.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.util.event.CancelableEvent;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A class that represents a panel with stacked children.
 */
@OnlyIn(Dist.CLIENT)
public class StackPanel extends Control
{
    /**
     * The event that is fired when the panel is reordered.
     */
    public final EventHandler<ReorderEvent> onReorder = new EventHandler<>();

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
    public StackPanel(@Nullable IControl parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);

        setIsScrollableY(true);
    }

    @Override
    public void preRender(int mouseX, int mouseY, float partialTicks)
    {
        int cumulativeHeight = 0;

        for (Control control : getChildren())
        {
            control.setY(cumulativeHeight);

            cumulativeHeight += control.getEffectiveHeight();
        }

        setMaxScrollY(cumulativeHeight - getHeight() + getPadding(Side.TOP) + getPadding(Side.BOTTOM));

        if (isReorderable() && getItems().size() > 1)
        {
            if (getScreen().getDraggedControl() instanceof Control)
            {
                Control draggedControl = (Control) getScreen().getDraggedControl();

                if (draggedControl != null && getItems().contains(draggedControl))
                {
                    int minY = Math.max(getItems().get(0).getScreenY(), getScreenY());
                    int maxY = (int) Math.min(getItems().get(getItems().size() - 1).getScreenY(), getScreenY() + getEffectiveHeight() * getEffectiveScale());

                    int draggedY = Math.min(maxY, Math.max(minY, mouseY - draggedControl.getEffectiveHeight() / 2));

                    Control closestControl = draggedControl;
                    int closestDifY = Integer.MAX_VALUE;
                    int closestAbsDifY = Integer.MAX_VALUE;
                    int midY = draggedY + draggedControl.getEffectiveHeight() / 2;

                    for (Control control : getItems())
                    {
                        int testMidY = control.getScreenY() + control.getEffectiveHeight() / 2;
                        int difY = testMidY - midY;
                        int difAbsY = Math.abs(difY);

                        if (difAbsY < closestAbsDifY)
                        {
                            closestDifY = difY;
                            closestAbsDifY = difAbsY;
                            closestControl = control;
                        }
                    }

                    if (mouseY < minY)
                    {
                        setScrollY((int) (getScrollY() - 12 * partialTicks));
                    }
                    else if (mouseY > maxY)
                    {
                        setScrollY((int) (getScrollY() + 12 * partialTicks));
                    }

                    draggedControl.setY((int) (toLocalY(draggedY) / draggedControl.getEffectiveScale() + getScrollY() - draggedControl.getMargin(Side.TOP)));

                    int oldIndex = getItems().indexOf(draggedControl);
                    int newIndex = getItems().indexOf(closestControl);

                    if (oldIndex != newIndex)
                    {
                        ReorderEvent e = new ReorderEvent(oldIndex, newIndex);

                        onReorder.handle(e);

                        if (!e.isCancelled())
                        {
                            if (newIndex < oldIndex)
                            {
                                draggedControl.setZIndex(newIndex);
                            }
                            else
                            {
                                closestControl.setZIndex(oldIndex);
                            }
                        }
                    }
                }
            }
        }

        if (scrollbarControlY != null)
        {
            if (getMaxScrollY() > 0)
            {
                scrollbarControlY.setIsDisabled(false);
                scrollbarControlY.setScrollPercentage(getScrollY(), getMaxScrollY());
            }
            else
            {
                scrollbarControlY.setIsDisabled(true);
            }
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
//        fill(matrixStack, getScreenX(), getScreenY(), getScreenX() + getEffectiveWidth(), getScreenY() + getEffectiveHeight(), 0xFF000000);
    }

    @Override
    public void controlMouseScrolled(@Nonnull MouseScrollEvent e)
    {
        int adjustedScroll = (int) (e.scroll * 8);

        if (GuiUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || GuiUtil.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))
        {
            setScrollX(getScrollX() - adjustedScroll);
        }
        else
        {
            setScrollY(getScrollY() - adjustedScroll);
        }

        e.setIsHandled(true);
    }

    /**
     * Handles the attached scrollbar's scroll event.
     */
    private void onScrollbarScroll(@Nonnull ScrollbarControl.ScrollEvent e)
    {
        setScrollY((int) Math.ceil(e.scrollPercentage * getMaxScrollY()));
    }

    protected List<Control> getItems()
    {
        return getChildrenCopy();
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

    @Override
    public void setScrollbarY(@Nullable ScrollbarControl scrollbarControl)
    {
        if (this.scrollbarControlY != null)
        {
            this.scrollbarControlY.onScroll.unsubscribe(this::onScrollbarScroll);
        }

        this.scrollbarControlY = scrollbarControl;

        if (this.scrollbarControlY != null)
        {
            this.scrollbarControlY.onScroll.subscribe(this::onScrollbarScroll);

            this.scrollbarControlY.setScrollPercentage(getScrollY(), getMaxScrollY());
        }
    }

    /**
     * Represents an event that occurs when the panel is reordered.
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
}
