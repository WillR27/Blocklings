package com.willr27.blocklings.client.gui.controls.common.panel;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.Control;
import com.willr27.blocklings.client.gui.GuiUtil;
import com.willr27.blocklings.client.gui.IControl;
import com.willr27.blocklings.client.gui.controls.common.ScrollbarControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A panel the displays items in a horizontal/vertical direction then wraps in the other direction
 * when out of space.
 */
@OnlyIn(Dist.CLIENT)
public class PanelControl extends Control
{
    /**
     * @param parent the parent control.
     * @param x the local x position.
     * @param y the local y position.
     * @param width the width.
     * @param height the height.
     */
    public PanelControl(@Nullable IControl parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
    }

    @Override
    public void preRender(int mouseX, int mouseY, float partialTicks)
    {
        updateScrollbar();
    }

    /**
     * Updates the scrollbar's maximum scroll and enables/disables accordingly.
     */
    protected void updateScrollbar()
    {
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

        if (scrollbarControlX != null)
        {
            if (getMaxScrollX() > 0)
            {
                scrollbarControlX.setIsDisabled(false);
                scrollbarControlX.setScrollPercentage(getScrollX(), getMaxScrollX());
            }
            else
            {
                scrollbarControlX.setIsDisabled(true);
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
}
