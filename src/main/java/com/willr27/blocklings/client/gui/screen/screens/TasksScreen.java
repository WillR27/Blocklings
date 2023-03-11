package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.ComboBoxControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.TabbedPanel;
import com.willr27.blocklings.client.gui.control.controls.tasks.TaskControl;
import com.willr27.blocklings.client.gui.control.event.events.FocusChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.ReorderEvent;
import com.willr27.blocklings.client.gui.control.event.events.SelectionChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyPressedEvent;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.entity.blockling.task.TaskType;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * The task's name.
     */
    @Nonnull
    private final TextFieldControl configNameField;

    /**
     * The tabbed panel containing the task config's tabs.
     */
    @Nonnull
    private final TabbedPanel configTabbedPanel;

    /**
     * The task currently being configured.
     */
    @Nullable
    private Task currentTask;

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
        configContainer.setParent(tabbedUIControl.contentControl);
        configContainer.setWidthPercentage(2.0);
        configContainer.setHeightPercentage(1.0);

        configNameField = new TextFieldControl();
        configContainer.addChild(configNameField);
        configNameField.setWidthPercentage(1.0);
        configNameField.setHeight(20.0);
        configNameField.setShouldRenderBackground(false);
        configNameField.eventBus.subscribe((BaseControl control, FocusChangedEvent e) ->
        {
            if (!configNameField.isFocused() && currentTask != null)
            {
                currentTask.setCustomName(configNameField.getText().trim());
                configNameField.setText(currentTask.getCustomName());
            }
        });

        GridPanel configGrid = new GridPanel();
        configGrid.setParent(configContainer);
        configGrid.setWidthPercentage(1.0);
        configGrid.setHeight(126.0);
        configGrid.setVerticalAlignment(1.0);
        configGrid.addRowDefinition(GridDefinition.RATIO, 1.0);
        configGrid.addColumnDefinition(GridDefinition.FIXED, 142.0);
        configGrid.addColumnDefinition(GridDefinition.FIXED, 5.0);
        configGrid.addColumnDefinition(GridDefinition.FIXED, 12.0);

        configTabbedPanel = new TabbedPanel();
        configGrid.addChild(configTabbedPanel, 0, 0);

        Control scrollBar2 = new Control();
        configGrid.addChild(scrollBar2, 0, 2);
        scrollBar2.setWidthPercentage(1.0);
        scrollBar2.setHeightPercentage(1.0);
        scrollBar2.setMargins(0.0, 1.0, 0.0, 1.0);
        scrollBar2.setBackgroundColour(0xff00ff00);

        screenControl.eventBus.subscribe((BaseControl control, KeyPressedEvent e) ->
        {
            if (configContainer.getVisibility() == Visibility.VISIBLE && e.keyCode == GLFW.GLFW_KEY_ESCAPE)
            {
                closeConfig();
                e.setIsHandled(true);
            }
        });

        closeConfig();
    }

    private void initConfigTabs(@Nonnull Task task)
    {
        configTabbedPanel.clearTabs();

        BaseControl miscControl = configTabbedPanel.addTab(new BlocklingsTranslationTextComponent("task.ui.tab.misc"));
        miscControl.setPadding(4.0, 7.0, 4.0, 4.0);
        miscControl.setCanScrollVertically(true);

        StackPanel miscStackPanel = new StackPanel();
        miscStackPanel.setParent(miscControl);
        miscStackPanel.setWidthPercentage(1.0);
        miscStackPanel.setFitHeightToContent(true);
        miscStackPanel.setDirection(Direction.TOP_TO_BOTTOM);

        TextBlockControl taskTypeTextBlock = new TextBlockControl();
        taskTypeTextBlock.setParent(miscStackPanel);
        taskTypeTextBlock.setWidthPercentage(1.0);
        taskTypeTextBlock.setText("asdsadad");

        ComboBoxControl taskTypeComboBox = new ComboBoxControl();
        taskTypeComboBox.setParent(miscStackPanel);
        taskTypeComboBox.setWidthPercentage(1.0);
        taskTypeComboBox.eventBus.subscribe((BaseControl c, SelectionChangedEvent e) ->
        {
            TaskType type = (TaskType) e.newItem.value;
            task.setType(type);
        });

        for (TaskType taskType : BlocklingTasks.TASK_TYPES)
        {
            List<IReorderingProcessor> tooltip = new ArrayList<>();
            tooltip.add(taskType.name.withStyle(TextFormatting.GOLD).getVisualOrderText());
            tooltip.add(StringTextComponent.EMPTY.getVisualOrderText());
            tooltip.addAll(GuiUtil.splitText(font, taskType.desc, 200).stream().map(s -> s.withStyle(TextFormatting.WHITE).getVisualOrderText()).collect(Collectors.toList()));
            ComboBoxControl.Item item = new ComboBoxControl.Item(taskType.name, taskType, taskType.texture.toTexture().dy(1).dHeight(-2), tooltip);

            if (taskType == task.getType())
            {
                taskTypeComboBox.setSelectedItem(item);
            }
            else
            {
                taskTypeComboBox.addItem(item);
            }
        }

        List<IReorderingProcessor> tooltip = new ArrayList<>();
        tooltip.add(BlocklingTasks.NULL.name.withStyle(TextFormatting.GOLD).getVisualOrderText());
        ComboBoxControl.Item item = new ComboBoxControl.Item(BlocklingTasks.NULL.name, BlocklingTasks.NULL, BlocklingTasks.NULL.texture.toTexture().dy(1).dHeight(-2), tooltip);

        if (BlocklingTasks.NULL == task.getType())
        {
            taskTypeComboBox.setSelectedItem(item);
        }
        else
        {
            taskTypeComboBox.addItem(item);
        }
    }

    /**
     * Called when the current task's type changes.
     */
    private void onTaskTypeChanged(@Nonnull Task.TypeChangedEvent typeChangedEvent)
    {
        initConfigTabs(typeChangedEvent.task);
        configNameField.setText(currentTask.getCustomName());
    }

    /**
     * Opens the config for the given task.
     */
    public void openConfig(@Nonnull Task task)
    {
        currentTask = task;

        tabbedUIControl.backgroundControl.setBackgroundTexture(Textures.Tasks.CONFIG_BACKGROUND);
        tasksGrid.setVisibility(Visibility.COLLAPSED);
        configContainer.setVisibility(Visibility.VISIBLE);
        configNameField.setText(currentTask.getCustomName());

        currentTask.onTypeChanged.subscribe(this::onTaskTypeChanged);

        initConfigTabs(currentTask);
    }

    /**
     * Closes the config.
     */
    public void closeConfig()
    {
        tabbedUIControl.backgroundControl.setBackgroundTexture(Textures.Tasks.BACKGROUND);
        tasksGrid.setVisibility(Visibility.VISIBLE);
        configContainer.setVisibility(Visibility.COLLAPSED);

        if (currentTask != null)
        {
            currentTask.onTypeChanged.subscribe(this::onTaskTypeChanged);
        }

        currentTask = null;
    }
}
