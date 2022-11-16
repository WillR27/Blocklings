package com.willr27.blocklings.client.gui.control;

import com.willr27.blocklings.client.gui.IScreen;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The root control for each screen.
 */
@OnlyIn(Dist.CLIENT)
public class ScreenControl extends Control implements IScreen
{
    /**
     * The currently hovered control.
     */
    @Nullable
    private Control hoveredControl;

    /**
     * The currently focused control.
     */
    @Nullable
    private Control focusedControl;

    /**
     */
    public ScreenControl()
    {
        this.screen = this;
    }

    @Override
    @Nullable
    public Control getHoveredControl()
    {
        return hoveredControl;
    }

    @Override
    public void setHoveredControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent)
    {
        if (hoveredControl != control)
        {
            if (hoveredControl != null)
            {
                hoveredControl.onHoverExit(mousePosEvent);
            }

            if (control != null)
            {
                control.onHoverEnter(mousePosEvent);
            }
        }

        hoveredControl = control;
    }

    @Nullable
    @Override
    public Control getFocusedControl()
    {
        return focusedControl;
    }

    @Override
    public void setFocusedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (focusedControl != control)
        {
            if (focusedControl != null)
            {
                focusedControl.onFocused(mouseButtonEvent);
            }

            if (control != null)
            {
                control.onUnfocused(mouseButtonEvent);
            }
        }

        focusedControl = control;
    }
}
