package com.willr27.blocklings.gui.controls.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.*;
import com.willr27.blocklings.util.event.Event;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A control used to perform scrolling.
 */
@OnlyIn(Dist.CLIENT)
public class ScrollbarControl extends Control
{
    /**
     * The grabber texture.
     */
    @Nonnull
    private static final GuiTexture GRABBER_TEXTURE = new GuiTexture(GuiTextures.COMMON_WIDGETS, 0, 0, 12, 15);

    /**
     * The event handler that handles any scroll events.
     */
    public final EventHandler<ScrollEvent> onScroll = new EventHandler<>();

    /**
     * @return whether the scrollbar is disabled.
     */
    public boolean isDisabled()
    {
        return isDisabled;
    }

    /**
     * Sets whether the scrollbar is disabled.
     */
    public void setIsDisabled(boolean isDisabled)
    {
        this.isDisabled = isDisabled;
    }

    /**
     * Is the scrollbar locked to the top (normally if there is nothing to scroll)?
     */
    private boolean isDisabled = false;

    /**
     * The distance through from the top of the scrollbar to the bottom minus
     * the height of the grab handle.
     */
    private int scrollOffset = 0;

    /**
     * @param parent the parent control.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     * @param height the height.
     */
    public ScrollbarControl(@Nonnull IControl parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.enableDepthTest();

        GuiUtil.bindTexture(GRABBER_TEXTURE.texture);

        int textureOffset = 0;

        if (isDragging())
        {
            textureOffset = GRABBER_TEXTURE.width;
            setScrollOffset(calcOffsetFromMouseY(mouseY));
        }
        else if (isDisabled)
        {
            textureOffset = GRABBER_TEXTURE.width;
            setScrollOffset(0);
        }

        blit(matrixStack, screenX, screenY + scrollOffset, GRABBER_TEXTURE.x + textureOffset, GRABBER_TEXTURE.y, GRABBER_TEXTURE.width, GRABBER_TEXTURE.height);
    }

    @Override
    public void controlMouseClicked(@Nonnull MouseButtonEvent e)
    {
        if (!isDisabled)
        {
            setScrollOffset(calcOffsetFromMouseY(e.mouseY));
        }

        e.setIsHandled(true);
    }

    @Override
    public void controlMouseScrolled(@Nonnull MouseScrollEvent e)
    {
        e.setIsHandled(scroll(e.scroll));
    }

    @Override
    public void globalMouseReleased(@Nonnull MouseButtonEvent e)
    {
    }

    /**
     * Calculates the scroll offset given an amount to scroll.
     *
     * @return whether the scroll offset changed.
     */
    public boolean scroll(double scroll)
    {
        int newOffset = calcOffsetFromLocalMouseY(scrollOffset - (int) (scroll * 10));

        if (newOffset == scrollOffset)
        {
            return false;
        }
        else
        {
            setScrollOffset(newOffset);

            return true;
        }
    }

    /**
     * Sets the scroll offset to a percentage of the maximum scroll.
     */
    public void setScrollPercentage(double percentage)
    {
        scrollOffset = (int) ((height - GRABBER_TEXTURE.height) * percentage);
    }

    /**
     * Sets the scroll offset to a percentage of the maximum scroll based on the given scroll.
     */
    public void setScrollPercentage(int scroll, int maxScroll)
    {
        setScrollPercentage((double) scroll / maxScroll);
    }

    /**
     * @return the percentage of the maximum scroll.
     */
    public double percentageScrolled()
    {
        return scrollOffset / (double) (height - GRABBER_TEXTURE.height);
    }

    /**
     * @return the scroll offset based on the mouse y position.
     */
    private int calcOffsetFromMouseY(int mouseY)
    {
        return calcOffsetFromLocalMouseY((int) (toLocalY(mouseY) / getEffectiveScale() - GRABBER_TEXTURE.height / 2));
    }

    /**
     * @return the scroll offset based on the local mouse y position.
     */
    private int calcOffsetFromLocalMouseY(int localMouseY)
    {
        int offset = 0;

        if (localMouseY < 0)
        {
            offset = 0;
        }
        else if (localMouseY > height - GRABBER_TEXTURE.height)
        {
            offset = height - GRABBER_TEXTURE.height;
        }
        else
        {
            offset = localMouseY;
        }

        return offset;
    }

    /**
     * Sets the scroll offset and invokes a scroll event.
     */
    private void setScrollOffset(int scrollOffset)
    {
        this.scrollOffset = scrollOffset;

        onScroll.handle(new ScrollEvent(percentageScrolled()));
    }

    /**
     * Represents a scroll event.
     */
    public static class ScrollEvent extends Event
    {
        /**
         * The scroll percentage.
         */
        public final double scrollPercentage;

        /**
         * @param scrollPercentage the scroll percentage.
         */
        public ScrollEvent(double scrollPercentage)
        {
            this.scrollPercentage = scrollPercentage;
        }
    }
}
