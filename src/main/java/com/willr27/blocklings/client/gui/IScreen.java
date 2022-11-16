package com.willr27.blocklings.client.gui;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a blocklings screen. Includes functionality shared by regular screens and container screens.
 */
@OnlyIn(Dist.CLIENT)
public interface IScreen
{
    /**
     * @return the currently hovered control.
     */
    @Nullable
    Control getHoveredControl();

    /**
     * Sets the currently hovered control.
     *
     * @param control the new hovered control.
     * @param mousePosEvent the mouse event to pass on to {@link Control#onHoverEnter(MousePosEvent)} and {@link Control#onHoverExit(MousePosEvent)}.
     */
    void setHoveredControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent);

    /**
     * @return the currently pressed control.
     */
    @Nullable
    Control getPressedControl();

    /**
     * Sets the currently pressed control.
     *
     * @param control the new pressed control.
     * @param mouseButtonEvent the mouse button event to pass on to {@link Control#onPressed(MouseButtonEvent)} and {@link Control#onReleased(MouseButtonEvent)}.
     */
    void setPressedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent);

    /**
     * @return the currently focused control.
     */
    @Nullable
    Control getFocusedControl();

    /**
     * Sets the currently focused control.
     *
     * @param control the new focused control.
     * @param mouseButtonEvent the mouse button event to pass on to {@link Control#onFocused(MouseButtonEvent)} and {@link Control#onUnfocused(MouseButtonEvent)}.
     */
    void setFocusedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent);
}
