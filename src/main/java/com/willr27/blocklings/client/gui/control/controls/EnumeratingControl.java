package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Enumerates a list of controls in place of each other.
 */
@OnlyIn(Dist.CLIENT)
public class EnumeratingControl extends Control
{
    /**
     * The controls to enumerate.
     */
    @Nonnull
    private final List<Control> controls = new ArrayList<>();

    /**
     * The number of ticks between switching to the next control.
     */
    private int interval = 60;

    /**
     * The number of ticks the current control has been displayed.
     */
    private int tickCount = 0;

    /**
     */
    public EnumeratingControl()
    {
        setFitToContentsXY(true);
    }

    @Override
    protected void onTick()
    {
        tickCount++;

        if (tickCount > interval)
        {
            switchToControl(true);
        }
    }

    /**
     * Switches to the next control in the list.
     *
     * @param forwards whether to enumerate forwards or backwards.
     */
    private void switchToControl(boolean forwards)
    {
        if (controls.isEmpty())
        {
            return;
        }

        int indexOfCurrentChild = getChildren().isEmpty() ? -1 : controls.indexOf(getChildren().get(0));

        clearChildren();

        int indexOfNewChild = (indexOfCurrentChild + (forwards ? 1 : -1)) % controls.size();

        addChild(controls.get(indexOfNewChild));

        tickCount = 0;
    }

    @Override
    protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (mouseButtonEvent.mouseButton == GLFW.GLFW_MOUSE_BUTTON_1)
        {
            switchToControl(true);
        }
        else
        {
            switchToControl(false);
        }

        mouseButtonEvent.setIsHandled(true);
    }

    /**
     * @return whether the control is part of the enumeration.
     */
    public boolean contains(@Nonnull Control control)
    {
        return controls.contains(control);
    }

    /**
     * Adds the given control to the list of control to be enumerated.
     */
    public void addControl(@Nonnull Control control)
    {
        controls.add(control);

        if (controls.size() == 1)
        {
            switchToControl(true);
        }
    }

    /**
     * Removes the given control from the list of control to be enumerated.
     */
    public void removeControl(@Nonnull Control control)
    {
        // If we are removing the displayed control, switch to a new one.
        if (!getChildren().isEmpty() && getChildren().get(0) == control)
        {
            switchToControl(true);
        }

        controls.remove(control);
    }

    /**
     * @return the current interval ticks.
     */
    public int getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval to the given number of ticks.
     */
    public void setInterval(int interval)
    {
        this.interval = interval;
    }
}
