package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.controls.tasks.TaskControl;
import com.willr27.blocklings.client.gui.control.event.events.ReorderEvent;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.Task;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen to show and configure the tasks of a blockling.
 */
@OnlyIn(Dist.CLIENT)
public class TasksScreen extends TabbedScreen
{
    /**
     * The grid panel containing the tasks.
     */
    @Nonnull
    private final GridPanel tasksGrid;

    /**
     * The container for the config panel.
     */
    @Nonnull
    private final Control configContainer;

    /**
     * @param blockling the blockling associated with the screen.
     */
    public TasksScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedUIControl.Tab.TASKS);

        tasksGrid = new GridPanel();
        tasksGrid.setParent(tabbedUIControl.contentControl);
        tasksGrid.setWidthPercentage(1.0);
        tasksGrid.setHeightPercentage(1.0);
        tasksGrid.setMargins(1.0);
        tasksGrid.addRowDefinition(GridDefinition.RATIO, 1.0);
        tasksGrid.addColumnDefinition(GridDefinition.FIXED, 140.0);
        tasksGrid.addColumnDefinition(GridDefinition.FIXED, 6.0);
        tasksGrid.addColumnDefinition(GridDefinition.FIXED, 12.0);

        StackPanel tasksPanel = new StackPanel();
        tasksGrid.addChild(tasksPanel, 0, 0);
        tasksPanel.setWidthPercentage(1.0);
        tasksPanel.setHeightPercentage(1.0);
        tasksPanel.setDirection(Direction.TOP_TO_BOTTOM);
        tasksPanel.setPadding(4.0);
        tasksPanel.setSpacing(4.0);
        tasksPanel.setCanScrollVertically(true);
        tasksPanel.eventBus.subscribe((BaseControl control, ReorderEvent e) ->
        {
            TaskControl taskControl = (TaskControl) e.draggedControl;
            TaskControl closestTaskControl = (TaskControl) e.closestControl;

            if (e.insertBefore)
            {
                taskControl.task.setPriority(closestTaskControl.task.getPriority());
            }
            else
            {
                closestTaskControl.task.setPriority(taskControl.task.getPriority());
            }
        });

        for (Task task : blockling.getTasks().getPrioritisedTasks())
        {
            TaskControl taskControl = new TaskControl(task, this);
            tasksPanel.addChild(taskControl);
        }

        TaskControl addTaskControl = new TaskControl(null, this);
        tasksPanel.addChild(addTaskControl);

        blockling.getTasks().onCreateTask.subscribe((e) ->
        {
            TaskControl taskControl = new TaskControl(e.task, this);
            tasksPanel.insertChildBefore(taskControl, addTaskControl);
        });

        blockling.getTasks().onRemoveTask.subscribe((e) ->
        {
            for (BaseControl control : tasksPanel.getChildrenCopy())
            {
                TaskControl taskControl = (TaskControl) control;

                if (e.task.equals(taskControl.task))
                {
                    tasksPanel.removeChild(control);
                }
            }
        });

        Control scrollBar = new Control();
        tasksGrid.addChild(scrollBar, 0, 2);
        scrollBar.setWidthPercentage(1.0);
        scrollBar.setHeightPercentage(1.0);
        scrollBar.setBackgroundColour(0xff00ff00);

        configContainer = new Control();
        configContainer.setWidthPercentage(2.0);
        configContainer.setHeightPercentage(1.0);

        TextFieldControl configNameField = new TextFieldControl();
        configContainer.addChild(configNameField);
        configNameField.setWidthPercentage(1.0);
        configNameField.setHeight(20.0);
        configNameField.setShouldRenderBackground(false);
        configNameField.setText("asdasd");

        GridPanel configGrid = new GridPanel();
        configGrid.setParent(configContainer);
        configGrid.setWidthPercentage(1.0);
        configGrid.setHeight(126.0);
        configGrid.setVerticalAlignment(1.0);
        configGrid.addRowDefinition(GridDefinition.RATIO, 1.0);
        configGrid.addColumnDefinition(GridDefinition.FIXED, 142.0);
        configGrid.addColumnDefinition(GridDefinition.FIXED, 5.0);
        configGrid.addColumnDefinition(GridDefinition.FIXED, 12.0);

        Control tabbedControl = new Control();
        configGrid.addChild(tabbedControl, 0, 0);
        tabbedControl.setWidthPercentage(1.0);
        tabbedControl.setHeightPercentage(1.0);
        tabbedControl.setBackgroundColour(0xff0000ff);

        Control scrollBar2 = new Control();
        configGrid.addChild(scrollBar2, 0, 2);
        scrollBar2.setWidthPercentage(1.0);
        scrollBar2.setHeightPercentage(1.0);
        scrollBar2.setMargins(0.0, 1.0, 0.0, 1.0);
        scrollBar2.setBackgroundColour(0xff00ff00);
    }

    /**
     * Opens the config for the given task.
     */
    public void openConfig(@Nonnull Task task)
    {
        tabbedUIControl.backgroundControl.setBackgroundTexture(Textures.Tasks.CONFIG_BACKGROUND, true);
        tabbedUIControl.contentControl.clearChildren();
        tabbedUIControl.contentControl.addChild(configContainer);
    }

    /**
     * Closes the config.
     */
    public void closeConfig()
    {
        tabbedUIControl.backgroundControl.setBackgroundTexture(Textures.Tasks.BACKGROUND, true);
        tabbedUIControl.contentControl.clearChildren();
        tabbedUIControl.contentControl.addChild(tasksGrid);
    }
}
