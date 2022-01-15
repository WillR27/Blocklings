package com.willr27.blocklings.gui.screens;

import com.willr27.blocklings.gui.widgets.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Defines a screen that handles widgets.
 */
@OnlyIn(Dist.CLIENT)
public interface IWidgetHandler
{
    /**
     * @return the list of widgets to handle.
     */
    @Nonnull
    List<Widget> getWidgets();

    /**
     * Adds a widget to the list of widgets to handle.
     *
     * @param widget the widget to add.
     */
    void addWidget(@Nonnull Widget widget);

    /**
     * Called when the mouse is clicked.
     *
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     * @param button the mouse button.
     */
    default void onMouseClicked(int mouseX, int mouseY, int button)
    {
        getWidgets().forEach(widget -> widget.onMouseClicked(mouseX, mouseY, button));
    }

    /**
     * Called when the mouse is released.
     *
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     * @param button the mouse button.
     */
    default void onMouseReleased(int mouseX, int mouseY, int button)
    {
        getWidgets().forEach(widget -> widget.onMouseReleased(mouseX, mouseY, button));
    }
}
