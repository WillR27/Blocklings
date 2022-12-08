package com.willr27.blocklings.client.gui.control.controls.tasks;

import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.entity.blockling.task.Task;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Displays a task.
 */
@OnlyIn(Dist.CLIENT)
public class TaskControl extends Control
{
    /**
     * The task to display.
     */
    @Nonnull
    private final Task task;

    /**
     * @param task the task to display.
     */
    public TaskControl(@Nonnull Task task)
    {
        super();
        this.task = task;

        setWidth(new Fill(1.0f));
        setFitToContentsY(true);
        setBackgroundColour(Colour.fromRGBInt(0xffffff));

        Control addRemoveControl = new Control();
        addRemoveControl.setParent(this);
        addRemoveControl.setAlignmentX(new Alignment(1.0f));

        Control taskBarControl = new Control();
        taskBarControl.setParent(this);

        Control taskIconControl = new Control();
        taskIconControl.setParent(taskBarControl);
        taskIconControl.setWidth(18);
        taskIconControl.setDock(Dock.LEFT);

    }
}
