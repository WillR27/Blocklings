package com.willr27.blocklings.client.gui3.screen;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui3.control.event.events.input.MousePosEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

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
     * @return the pixel x coordinate the pressed control was pressed at.
     */
    int getPressedStartPixelX();

    /**
     * @return the pixel y coordinate the pressed control was pressed at.
     */
    int getPressedStartPixelY();

    /**
     * @return the currently focused control.
     */
    @Nullable
    Control getFocusedControl();

    /**
     * Sets the currently focused control.
     *
     * @param control the new focused control.
     */
    void setFocusedControl(@Nullable Control control);

    /**
     * @return the currently dragged control.
     */
    @Nullable
    Control getDraggedControl();

    /**
     * Sets the currently dragged control.
     *
     * @param control the new dragged control.
     * @param mousePosEvent the mouse button event to pass on to {@link Control#onDragStart(MousePosEvent)} and {@link Control#onDragEnd(MousePosEvent)}.
     */
    void setDraggedControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent);

    /**
     * @return returns an instance of {@link java.util.Random}.
     */
    @Nonnull
    Random getRandom();
}
