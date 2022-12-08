package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Direction;
import com.willr27.blocklings.client.gui.control.Orientation;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui2.Colour;
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

        Control taskListContainerControl = new Control();
        taskListContainerControl.setParent(contentControl);
        taskListContainerControl.setPercentWidth(1.0f);
        taskListContainerControl.setPercentHeight(1.0f);
        taskListContainerControl.setBackgroundColour(Colour.fromRGBInt(0xffff00));

        FlowPanel taskListControl = new FlowPanel();
        taskListControl.setParent(taskListContainerControl);
        taskListControl.setWidth(140);
        taskListControl.setPercentHeight(1.0f);
        taskListControl.setBackgroundColour(Colour.fromRGBInt(0xa7b9e1));
        taskListControl.setScrollableY(true);
        taskListControl.setFlowDirection(Direction.TOP_TO_BOTTOM);
        taskListControl.setOverflowOrientation(Orientation.VERTICAL);
        taskListControl.setItemGapY(2);
        taskListControl.setPadding(4, 4, 4, 4);

        for (int i = 0; i < 10; i++)
        {
            Control control = new Control();
            control.setDraggableY(true);
            control.setParent(taskListControl);
            control.setWidth(10);
            control.setHeight(30);
            control.setBackgroundColour(Colour.fromRGBInt(0xff0011));
        }

        Control scrollbarControl = new Control();
        scrollbarControl.setParent(taskListContainerControl);
        scrollbarControl.setWidth(12);
        scrollbarControl.setPercentHeight(1.0f);
        scrollbarControl.setPercentX(1.0f);
        scrollbarControl.setBackgroundColour(Colour.fromRGBInt(0x134934));
    }
}
