package com.willr27.blocklings.client.gui3.control.controls.tasks;

import com.willr27.blocklings.client.gui3.control.Direction;
import com.willr27.blocklings.client.gui3.control.Fill;
import com.willr27.blocklings.client.gui3.control.Orientation;
import com.willr27.blocklings.client.gui3.control.controls.panels.FlowPanel;
import com.willr27.blocklings.entity.blockling.task.Task;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Contains configuration options for a task.
 */
@OnlyIn(Dist.CLIENT)
public class ConfigControl extends FlowPanel
{
    /**
     * The task being configured.
     */
    @Nonnull
    protected final Task task;

    /**
     * @param task the task being configured.
     */
    public ConfigControl(@Nonnull Task task)
    {
        super();
        this.task = task;

        setWidth(new Fill(1.0f));
        setHeight(new Fill(1.0f));
        setPadding(4, 8, 4, 4);
        setFlowDirection(Direction.TOP_TO_BOTTOM);
        setOverflowOrientation(Orientation.VERTICAL);
        setFitMaxScrollOffsetToOverflowY(true);
        setScrollableY(true);
    }
}
