package com.willr27.blocklings.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;

public class ScrollbarWidget extends TexturedWidget
{
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

    public ScrollbarWidget(FontRenderer font, int x, int y, int width, int height)
    {
        super(font, x, y, width, height, new GuiTexture(GuiTextures.COMMON_WIDGETS, 0, 0, 12, 15));
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(texture.texture);

        int textureOffset = 0;

        if (isDragging)
        {
            textureOffset = texture.width;
            scrollOffset = calcOffsetFromMouseY((int) mouseY);
        }
        else if (isDisabled)
        {
            textureOffset = texture.width;
            scrollOffset = 0;
        }

        blit(matrixStack, screenX, screenY + scrollOffset, textureX + textureOffset, textureY, texture.width, texture.height);
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
        return scrollOffset / (float) (height - texture.height);
    }

    private int calcOffsetFromMouseY(int mouseY)
    {
        return calcOffsetFromLocalMouseY(toLocalY(mouseY) - texture.height / 2);
    }

    private int calcOffsetFromLocalMouseY(int localMouseY)
    {
        int offset = 0;

        if (localMouseY < 0)
        {
            offset = 0;
        }
        else if (localMouseY > height - texture.height)
        {
            offset = height - texture.height;
        }
        else
        {
            offset = localMouseY;
        }

        return offset;
    }
}
