package com.willr27.blocklings.client.gui;

import com.willr27.blocklings.client.gui.control.Control;
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
}
