package com.willr27.blocklings.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents some kind of control (including the screen itself).
 */
@OnlyIn(Dist.CLIENT)
public interface IControl
{
    /**
     * @return the parent control.
     */
    @Nullable
    IControl getParent();

    /**
     * @return a copy of the list of child controls.
     */
    @Nonnull
    List<Control> getChildren();

    /**
     * Adds the given control as a child of this control.
     *
     * @param control the control to add as a child.
     */
    void addChild(@Nonnull Control control);

    /**
     * Removes the given control as a child of this control.
     *
     * @param control the control to remove as a child.
     */
    void removeChild(@Nullable Control control);

    /**
     * Called when the mouse is clicked but does not return whether the event is handled.
     *
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param button the mouse button.
     */
    default void mouseClickedNoHandle(int mouseX, int mouseY, int button)
    {
        getChildren().forEach(control -> control.mouseClickedNoHandle(mouseX, mouseY, button));
    }

    /**
     * Called when the mouse is released but does not return whether the event is handled.
     *
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param button the mouse button.
     */
    default void mouseReleasedNoHandle(int mouseX, int mouseY, int button)
    {
        getChildren().forEach(control -> control.mouseReleasedNoHandle(mouseX, mouseY, button));
    }
}
