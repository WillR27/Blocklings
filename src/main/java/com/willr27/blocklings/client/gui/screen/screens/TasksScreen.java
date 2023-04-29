package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.*;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.TabbedPanel;
import com.willr27.blocklings.client.gui.control.controls.tasks.TaskControl;
import com.willr27.blocklings.client.gui.control.event.events.FocusChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.ReorderEvent;
import com.willr27.blocklings.client.gui.control.event.events.SelectionChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.TabChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyPressedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.entity.blockling.task.TaskType;
import com.willr27.blocklings.entity.blockling.task.config.Property;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

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

        Control addTaskContainer = new Control();
        tasksPanel.addChild(addTaskContainer);
        addTaskContainer.setWidthPercentage(1.0);
        addTaskContainer.setFitHeightToContent(true);
        addTaskContainer.setReorderable(false);

        TexturedControl addTaskButton = new TexturedControl(Textures.Common.PLUS_ICON)
        {
            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (blockling.getTasks().isTaskListFull())
                {
                    renderTextureAsBackground(matrixStack, Textures.Common.PLUS_ICON_DISABLED);
                }
                else
                {
                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                List<IReorderingProcessor> tooltip = new ArrayList<>();
                tooltip.add(new BlocklingsTranslationTextComponent("task.ui.add").withStyle(blockling.getTasks().isTaskListFull() ? TextFormatting.GRAY : TextFormatting.WHITE).getVisualOrderText());
                tooltip.add(new BlocklingsTranslationTextComponent("task.ui.task_amount", blockling.getTasks().getPrioritisedTasks().size(), BlocklingTasks.MAX_TASKS).withStyle(TextFormatting.GRAY).getVisualOrderText());
                renderTooltip(matrixStack, mouseX, mouseY, tooltip);
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed() && !blockling.getTasks().isTaskListFull())
                {
                    blockling.getTasks().createTask(BlocklingTasks.NULL);

                    e.setIsHandled(true);
                }
            }
        };
        addTaskContainer.addChild(addTaskButton);
        addTaskButton.setHorizontalAlignment(0.5);
        addTaskButton.setMargins(0.0, 1.0, 0.0, 1.0);

        blockling.getTasks().onCreateTask.subscribe((e) ->
        {
            TaskControl taskControl = new TaskControl(e.task, this);
            tasksPanel.insertChildBefore(taskControl, addTaskContainer);
        });

        blockling.getTasks().onRemoveTask.subscribe((e) ->
        {
            for (BaseControl control : tasksPanel.getChildrenCopy())
            {
                if (control instanceof TaskControl)
                {
                    TaskControl taskControl = (TaskControl) control;

                    if (e.task.equals(taskControl.task))
                    {
                        tasksPanel.removeChild(control);
                    }
                }
            }
        });

        ScrollbarControl tasksScrollbar = new ScrollbarControl();
        tasksGrid.addChild(tasksScrollbar, 0, 2);
        tasksScrollbar.setHeightPercentage(1.0);
        tasksScrollbar.setAttachedControl(tasksPanel);

        configContainer = new Control();
        configContainer.setParent(tabbedUIControl.contentControl);
        configContainer.setWidthPercentage(2.0);
        configContainer.setHeightPercentage(1.0);

        configNameField = new TextFieldControl();
        configContainer.addChild(configNameField);
        configNameField.setWidthPercentage(1.0);
        configNameField.setHeight(20.0);
        configNameField.setShouldRenderBackground(false);
        configNameField.setBackgroundColour(0x00000000);
        configNameField.eventBus.subscribe((BaseControl control, FocusChangedEvent e) ->
        {
            if (!configNameField.isFocused() && currentTask != null)
            {
                if (!currentTask.getCustomName().equals(configNameField.getText().trim()))
                {
                    currentTask.setCustomName(configNameField.getText().trim());
                }

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

        ScrollbarControl configScrollbar = new ScrollbarControl();
        configGrid.addChild(configScrollbar, 0, 2);
        configScrollbar.setHeightPercentage(1.0);
        configScrollbar.setMargins(0.0, 1.0, 0.0, 1.0);

        configTabbedPanel = new TabbedPanel();
        configGrid.addChild(configTabbedPanel, 0, 0);
        configTabbedPanel.eventBus.subscribe((BaseControl c, TabChangedEvent e) ->
        {
            configScrollbar.setAttachedControl(e.containerControl);
        });

        screenControl.eventBus.subscribe((BaseControl control, KeyPressedEvent e) ->
        {
            if (configContainer.getVisibility() == Visibility.VISIBLE && GuiUtil.get().isCloseKey(e.keyCode))
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

        BaseControl miscContainer = configTabbedPanel.addTab(new BlocklingsTranslationTextComponent("task.ui.tab.misc"));
        miscContainer.setPadding(4.0, 10.0, 4.0, 4.0);
        miscContainer.setCanScrollVertically(true);

        StackPanel miscStackPanel = new StackPanel();
        miscStackPanel.setParent(miscContainer);
        miscStackPanel.setWidthPercentage(1.0);
        miscStackPanel.setFitHeightToContent(true);
        miscStackPanel.setDirection(Direction.TOP_TO_BOTTOM);

        TextBlockControl taskTypeTextBlock = new TextBlockControl();
        taskTypeTextBlock.setParent(miscStackPanel);
        taskTypeTextBlock.setWidthPercentage(1.0);
        taskTypeTextBlock.setText(new BlocklingsTranslationTextComponent("task.ui.task_type").getString());

        ComboBoxControl taskTypeComboBox = new ComboBoxControl();
        taskTypeComboBox.setParent(miscStackPanel);
        taskTypeComboBox.setWidthPercentage(1.0);
        taskTypeComboBox.setMarginTop(3.0);
        taskTypeComboBox.eventBus.subscribe((BaseControl c, SelectionChangedEvent<ComboBoxControl.Item> e) ->
        {
            TaskType type = (TaskType) e.newItem.value;
            task.setType(type);
        });

        for (TaskType taskType : BlocklingTasks.TASK_TYPES.stream().filter(t -> blockling.getTasks().taskTypeUnlockedMap.get(t)).collect(Collectors.toList()))
        {
            List<IReorderingProcessor> tooltip = new ArrayList<>();
            tooltip.add(taskType.name.copy().withStyle(TextFormatting.GOLD).getVisualOrderText());
            tooltip.addAll(GuiUtil.get().split(taskType.desc.getString(), 200).stream().map(s -> new StringTextComponent(s).getVisualOrderText()).collect(Collectors.toList()));
            ComboBoxControl.Item item = new ComboBoxControl.Item(taskType.name, taskType, taskType.texture.dx(1).dy(1).dWidth(-2).dHeight(-2), tooltip);

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
        tooltip.add(BlocklingTasks.NULL.name.copy().withStyle(TextFormatting.GOLD).getVisualOrderText());
        ComboBoxControl.Item item = new ComboBoxControl.Item(BlocklingTasks.NULL.name, BlocklingTasks.NULL, BlocklingTasks.NULL.texture.dx(1).dy(1).dWidth(-2).dHeight(-2), tooltip);

        if (BlocklingTasks.NULL == task.getType())
        {
            taskTypeComboBox.setSelectedItem(item);
        }
        else
        {
            taskTypeComboBox.addItem(item);
        }

        if (task.isConfigured())
        {
            BlocklingGoal goal = task.getGoal();

            for (Property property : goal.properties)
            {
                if (!property.isEnabled())
                {
                    continue;
                }

                TextBlockControl nameControl = new TextBlockControl();
                nameControl.setParent(miscStackPanel);
                nameControl.setWidthPercentage(1.0);
                nameControl.setFitHeightToContent(true);
                nameControl.setText(property.name);
                nameControl.setMarginTop(8.0);

                BaseControl propertyControl = property.createControl();
                propertyControl.setParent(miscStackPanel);
                propertyControl.setMarginTop(3.0);
            }

            task.getGoal().addConfigTabControls(configTabbedPanel);
        }
    }

    /**
     * Called when the current task's type changes.
     */
    private void onTaskTypeChanged(@Nonnull Task.TypeChangedEvent typeChangedEvent)
    {
        try
        {
            initConfigTabs(typeChangedEvent.task);
            configNameField.setText(currentTask.getCustomName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
