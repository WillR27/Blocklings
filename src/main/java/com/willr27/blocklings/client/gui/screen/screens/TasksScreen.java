package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.GridControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        taskListControl.setDragReorderType(DragReorderType.INSERT_ON_MOVE);
        taskListControl.setWidth(140);
        taskListControl.setPercentHeight(1.0f);
        taskListControl.setBackgroundColour(Colour.fromRGBInt(0xa7b9e1));
        taskListControl.setScrollableY(true);
        taskListControl.setFlowDirection(Direction.TOP_TO_BOTTOM);
        taskListControl.setOverflowOrientation(Orientation.VERTICAL);
        taskListControl.setItemGapY(4);
        taskListControl.setPadding(4, 4, 4, 4);

        for (Task task : blockling.getTasks().getPrioritisedTasks())
        {
            TaskControl taskControl = new TaskControl(taskListControl, task);
        }

        TaskControl addTaskControl = new TaskControl(taskListControl, null);

        Control scrollbarControl = new Control();
        scrollbarControl.setParent(taskListContainerControl);
        scrollbarControl.setWidth(12);
        scrollbarControl.setPercentHeight(1.0f);
        scrollbarControl.setPercentX(1.0f);
        scrollbarControl.setBackgroundColour(Colour.fromRGBInt(0x134934));
    }

    /**
     * Represents a task.
     */
    private static class TaskControl extends Control
    {
        /**
         * The associated task (null if used to add a task).
         */
        @Nullable
        private final Task task;

        /**
         * @param taskListControl the list of task controls.
         * @param task the associated task.
         */
        public TaskControl(@Nonnull FlowPanel taskListControl, @Nullable Task task)
        {
            this.task = task;

            setParent(taskListControl);
            setWidth(new Fill(1.0f));
            setFitToContentsY(true);
            setDraggableY(task != null);
            setReorderable(task != null);

            GridControl gridControl = new GridControl(new GridControl.GridDefinition()
                    .addCol(new GridControl.Auto())
                    .addCol(new GridControl.Fill(1.0f)));
            gridControl.setParent(this);
            gridControl.setWidth(new Fill(1.0f));
            gridControl.setFitToContentsY(true);
            gridControl.setBackgroundColour(Colour.fromRGBInt(0xff0011));

            Control iconControl = new TexturedControl(GuiTextures.Tasks.TASK_ICON_BACKGROUND_RAISED);
            gridControl.addControl(iconControl, 0, 0);

            TextBlockControl taskNameControl = new TextBlockControl();
            gridControl.addControl(taskNameControl, 1, 0);
            taskNameControl.setText(task == null ? "OOGA" : task.getCustomName());
            taskNameControl.setVerticalAlignment(VerticalAlignment.MIDDLE);
            taskNameControl.setWidth(new Fill(1.0f));
            taskNameControl.setHeight(new Fill(1.0f));
        }
    }
}
