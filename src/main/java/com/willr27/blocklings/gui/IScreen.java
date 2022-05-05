package com.willr27.blocklings.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the screen control.
 */
@OnlyIn(Dist.CLIENT)
public interface IScreen
{
    /**
     * Used to render any screen-specific elements.
     * This is required to have scaling be correctly applied.
     *
     * @param matrixStack the current matrix stack.
     * @param mouseX the x position of the mouse.
     * @param mouseY the y position of the mouse.
     * @param partialTicks the partial ticks.
     */
    default void renderScreen(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {

    }

    /**
     * @return whether the key is currently held.
     */
    boolean isKeyHeld(int keyCode);

    /**
     * @return the currently focused control.
     */
    @Nonnull
    IControl getFocusedControl();

    /**
     * @param control the control to set focus to.
     */
    void setFocusedControl(@Nullable IControl control);

    /**
     * @return the currently hovered control.
     */
    @Nonnull
    IControl getHoveredControl();

    /**
     * @param control the control to set as hovered.
     */
    void setHoveredControl(@Nullable IControl control, int mouseX, int mouseY);

    /**
     * @return the currently pressed control.
     */
    @Nullable
    IControl getPressedControl();

    /**
     * @return the x position of the mouse when the control was pressed.
     */
    int getPressedMouseX();

    /**
     * @return the y position of the mouse when the control was pressed.
     */
    int getPressedMouseY();

    /**
     * @param control the control to set as pressed.
     * @param mouseX the x position of the mouse when the control was pressed.
     * @param mouseY the y position of the mouse when the control was pressed.
     */
    void setPressedControl(@Nullable IControl control, int mouseX, int mouseY);

    /**
     * @return the currently dragged control.
     */
    @Nullable
    IControl getDraggedControl();

    /**
     * Sets the currently dragged control.
     */
    void setDraggedControl(@Nullable IControl control);

    /**
     * @return the most recent control that was pressed without being released.
     */
    @Nullable
    IControl getRecentlyPressedControl();

    /**
     * @param control the control that was recently pressed without being released.
     */
    void setRecentlyClickedControl(@Nullable IControl control);
}
