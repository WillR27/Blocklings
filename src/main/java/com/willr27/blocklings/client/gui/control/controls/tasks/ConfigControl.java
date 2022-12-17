package com.willr27.blocklings.client.gui.control.controls.tasks;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Fill;
import com.willr27.blocklings.entity.blockling.task.Task;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Contains configuration options for a task.
 */
@OnlyIn(Dist.CLIENT)
public class ConfigControl extends Control
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
    }
}
