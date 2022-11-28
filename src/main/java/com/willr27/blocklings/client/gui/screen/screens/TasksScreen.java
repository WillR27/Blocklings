package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen to display the blockling's tasks.
 */
@OnlyIn(Dist.CLIENT)
public class TasksScreen extends TabbedScreen
{
    /**
     * @param blockling the blockling.
     */
    public TasksScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedControl.Tab.TASKS);
    }

    @Override
    protected void init()
    {
        super.init();
    }
}
