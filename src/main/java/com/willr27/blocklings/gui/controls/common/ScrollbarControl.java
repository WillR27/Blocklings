package com.willr27.blocklings.gui.controls.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.*;

import javax.annotation.Nonnull;

public class ScrollbarControl extends Control
{
    /**
     * The grabber texture.
     */
    @Nonnull
    private static final GuiTexture GRABBER_TEXTURE = new GuiTexture(GuiTextures.COMMON_WIDGETS, 0, 0, 12, 15);

    /**
     * Is the scrollbar locked to the top (normally if there is nothing to scroll)?
     */
    public boolean isDisabled = false;

    /**
     * Is the user currently dragging the scrollbar?
     */
    private boolean isDragging = false;

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
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GRABBER_TEXTURE.texture);

        int textureOffset = 0;

        if (isDragging)
        {
            textureOffset = GRABBER_TEXTURE.width;
            scrollOffset = calcOffsetFromMouseY(mouseY);
        }
        else if (isDisabled)
        {
            textureOffset = GRABBER_TEXTURE.width;
            scrollOffset = 0;
        }

        blit(matrixStack, screenX, screenY + scrollOffset, GRABBER_TEXTURE.x + textureOffset, GRABBER_TEXTURE.y, GRABBER_TEXTURE.width, GRABBER_TEXTURE.height);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if (!isDisabled && isMouseOver(mouseX, mouseY))
        {
            isDragging = true;
            scrollOffset = calcOffsetFromMouseY(mouseY);

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        isDragging = false;

        return false;
    }

    public boolean scroll(double scroll)
    {
        int newOffset = calcOffsetFromLocalMouseY(scrollOffset - (int) (scroll * 10));

        if (newOffset == scrollOffset)
        {
            return false;
        }
        else
        {
            scrollOffset = newOffset;

            return true;
        }
    }

    public float percentageScrolled()
    {
        return scrollOffset / (float) (height - GRABBER_TEXTURE.height);
    }

    private int calcOffsetFromMouseY(int mouseY)
    {
        return calcOffsetFromLocalMouseY(toLocalY(mouseY) - GRABBER_TEXTURE.height / 2);
    }

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
}
