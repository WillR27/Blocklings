package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.*;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A screen to display the blockling's tasks.
 */
@OnlyIn(Dist.CLIENT)
public class TasksScreen extends TabbedScreen
{
    /**
     * The container for both the task list and config containers.
     */
    @Nonnull
    private final TaskContainerControl taskContainerControl = new TaskContainerControl();

    /**
     * The task list container control.
     */
    @Nonnull
    private final Control taskListContainerControl = new Control();

    /**
     * The task config container control.
     */
    @Nonnull
    private final Control taskConfigContainerControl = new Control();

    /**
     * @param blockling the blockling.
     */
    public TasksScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedUIControl.Tab.TASKS);
    }

    @Override
    protected void init()
    {
        super.init();

        // Remove children from controls we don't recreate when the window resizes.
        taskContainerControl.clearChildren();
        taskListContainerControl.clearChildren();
        taskConfigContainerControl.clearChildren();

        taskContainerControl.setParent(contentControl);
        taskListContainerControl.setParent(taskContainerControl);
        taskListContainerControl.setWidth(new Fill(1.0f));
        taskListContainerControl.setHeight(new Fill(1.0f));
        taskConfigContainerControl.setParent(taskContainerControl);
        taskConfigContainerControl.setWidth(new Fill(1.0f));
        taskConfigContainerControl.setHeight(new Fill(1.0f));
        taskConfigContainerControl.setVisible(false);

        FlowPanel taskListControl = new FlowPanel();
        taskListControl.setParent(taskListContainerControl);
        taskListControl.setDragReorderType(DragReorderType.INSERT_ON_MOVE);
        taskListControl.setWidth(140);
        taskListControl.setPercentHeight(1.0f);
        taskListControl.setScrollableY(true);
        taskListControl.setFlowDirection(Direction.TOP_TO_BOTTOM);
        taskListControl.setOverflowOrientation(Orientation.VERTICAL);
        taskListControl.setItemGapY(4);
        taskListControl.setPadding(4, 4, 4, 4);

        for (Task task : blockling.getTasks().getPrioritisedTasks())
        {
            TaskControl taskControl = new TaskControl(task);
            taskListControl.addChild(taskControl);
        }

        TaskControl addTaskControl = new TaskControl(null);
        taskListControl.addChild(addTaskControl);

        blockling.getTasks().onCreateTask.subscribe((e) ->
        {
            TaskControl taskControl = new TaskControl(e.task);
            taskListControl.insertChildBefore(taskControl, addTaskControl);
        });

        blockling.getTasks().onRemoveTask.subscribe((e) ->
        {
            for (Control control : taskListControl.getChildrenCopy())
            {
                TaskControl taskControl = (TaskControl) control;

                if (e.task.equals(taskControl.task))
                {
                    taskListControl.removeChild(control);
                }
            }
        });

        Control scrollbarControl = new Control();
        scrollbarControl.setParent(taskListContainerControl);
        scrollbarControl.setWidth(12);
        scrollbarControl.setPercentHeight(1.0f);
        scrollbarControl.setPercentX(1.0f);



        TextFieldControl taskNameFieldControl = new TextFieldControl();
        taskNameFieldControl.setParent(taskConfigContainerControl);
        taskNameFieldControl.setDock(Dock.TOP);
        taskNameFieldControl.setText("asdsaddsa");

        Control nameTabDividerControl = new Control();
        nameTabDividerControl.setParent(taskConfigContainerControl);
        nameTabDividerControl.setDock(Dock.TOP);
        nameTabDividerControl.setHeight(4.0f);

        Control scrollbarTabDividerControl = new Control();
        scrollbarTabDividerControl.setParent(taskConfigContainerControl);
        scrollbarTabDividerControl.setDock(Dock.RIGHT);
        scrollbarTabDividerControl.setWidth(18.0f);

        TabbedControl tabbedControl = new TabbedControl(taskConfigContainerControl);
        tabbedControl.setDock(Dock.FILL);
        tabbedControl.addTab("Hello");
        tabbedControl.addTab("Hello234");
        tabbedControl.addTab("Hlyo334");
        tabbedControl.addTab("Hello555");
        tabbedControl.addTab("Hello");

        // Reset the screen (important when resizing the window).
        taskContainerControl.closeConfig();
    }

    /**
     * Contains both the task list and task config containers.
     */
    private class TaskContainerControl extends Control
    {
        /**
         */
        public TaskContainerControl()
        {
            setWidth(new Fill(1.0f));
            setHeight(new Fill(1.0f));
        }

        /**
         * Opens the config for the given task if it is not already.
         */
        public void openConfig(@Nonnull Task task)
        {
            taskListContainerControl.setVisible(false);
            taskConfigContainerControl.setVisible(true);
            tabbedControl.backgroundControl.setTexture(GuiTextures.Tasks.CONFIG_BACKGROUND);
        }

        /**
         * Closes the config if it is open.
         */
        public void closeConfig()
        {
            taskConfigContainerControl.setVisible(false);
            taskListContainerControl.setVisible(true);
            tabbedControl.backgroundControl.setTexture(tabbedControl.selectedTab.backgroundTexture);
        }
    }

    /**
     * Represents a task.
     */
    private class TaskControl extends Control
    {
        /**
         * The associated task (null if used to add a task).
         */
        @Nullable
        private final Task task;

        /**
         * @param task the associated task.
         */
        public TaskControl(@Nullable Task task)
        {
            this.task = task;

            setWidth(new Fill(1.0f));
            setFitToContentsY(true);
            setDraggableY(task != null);
            setReorderable(task != null);

            GridControl gridControl = new GridControl(new GridControl.GridDefinition()
                    .addCol(new GridControl.GridDefinition.Auto())
                    .addCol(new GridControl.GridDefinition.Fill(1.0f))
                    .addCol(new GridControl.GridDefinition.Auto()))
            {
                @Override
                public void onHover(@Nonnull MousePosEvent mousePosEvent)
                {

                }
            };
            gridControl.setParent(this);
            gridControl.setWidth(new Fill(1.0f));
            gridControl.setFitToContentsY(true);

            Control iconBackgroundControl = task != null ? new TexturedControl(GuiTextures.Tasks.TASK_ICON_BACKGROUND_RAISED, GuiTextures.Tasks.TASK_ICON_BACKGROUND_PRESSED) : new TexturedControl(GuiTextures.Tasks.TASK_ADD_ICON_BACKGROUND);
            gridControl.addControl(iconBackgroundControl, 0, 0);

            Control iconControl = new TexturedControl(task != null ? task.getType().texture : GuiTextures.Tasks.TASK_CONFIG_ICON)
            {
                /**
                 * The icon texture.
                 */
                @Nonnull
                private GuiTexture iconTexture = getTexture();

                @Override
                public void onHoverEnter(@Nonnull MousePosEvent mousePosEvent)
                {
                    iconTexture = !isDraggingOrAncestorIsDragging() ? GuiTextures.Tasks.TASK_CONFIG_ICON : iconTexture;
                }

                @Override
                public void onHoverExit(@Nonnull MousePosEvent mousePosEvent)
                {
                    iconTexture = task != null ? task.getType().texture : GuiTextures.Tasks.TASK_CONFIG_ICON;
                }

                @Override
                public void onRenderBackground(@Nonnull RenderArgs renderArgs)
                {
                    renderTexture(renderArgs.matrixStack, iconTexture);
                }

                @Override
                public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
                {
                    if (task != null)
                    {
                        renderTooltip(renderArgs, new BlocklingsTranslationTextComponent("task.ui.configure"));
                    }
                }

                @Override
                protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
                {

                }

                @Override
                protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
                {
                    if (!isDraggingOrAncestorIsDragging())
                    {
                        taskContainerControl.openConfig(task);
                    }
                }
            };
            iconControl.setParent(iconBackgroundControl);

            GridControl taskNameBackgroundControl = new GridControl(new GridControl.GridDefinition()
                    .addRow(new GridControl.GridDefinition.Fill(1.0f))
                    .addCol(new GridControl.GridDefinition.Fixed(4.0f))
                    .addCol(new GridControl.GridDefinition.Fill(1.0f))
                    .addCol(new GridControl.GridDefinition.Fixed(16.0f)))
            {
                @Override
                public void onRenderBackground(@Nonnull RenderArgs renderArgs)
                {
                    renderTexture(renderArgs.matrixStack, GuiTextures.Tasks.TASK_NAME_BACKGROUND);
                }

                @Override
                public void onHover(@Nonnull MousePosEvent mousePosEvent)
                {

                }
            };
            gridControl.addControl(taskNameBackgroundControl, 1, 0);
            taskNameBackgroundControl.setWidth(new Fill(1.0f));
            taskNameBackgroundControl.setHeight(new Fill(1.0f));

            Control stateContainerControl = new Control();
            taskNameBackgroundControl.addControl(stateContainerControl, 2, 0);
            stateContainerControl.setWidth(new Fill(1.0f));
            stateContainerControl.setHeight(new Fill(1.0f));

            Control stateIconControl = new TexturedControl(GuiTextures.Common.NODE_UNPRESSED, GuiTextures.Common.NODE_PRESSED)
            {
                @Override
                public void onRenderBackground(@Nonnull RenderArgs renderArgs)
                {
                    if (task != null && task.isConfigured())
                    {
                        if (task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                        {
                            RenderSystem.color3f(1.0f, 0.0f, 0.0f);
                        }
                        else if (task.getGoal().getState() == BlocklingGoal.State.IDLE)
                        {
                            RenderSystem.color3f(1.0f, 0.8f, 0.0f);
                        }
                        if (task.getGoal().getState() == BlocklingGoal.State.ACTIVE)
                        {
                            RenderSystem.color3f(0.0f, 0.7f, 0.0f);
                        }
                    }
                    else
                    {
                        RenderSystem.color3f(0.6f, 0.6f, 0.6f);
                    }

                    if (task != null && task.isConfigured() && task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                    {
                        renderTexture(renderArgs.matrixStack, getPressedTexture());
                    }
                    else
                    {
                        renderTexture(renderArgs.matrixStack, getTexture());
                    }

                    RenderSystem.color3f(1.0f, 1.0f, 1.0f);
                }

                @Override
                public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
                {
                    if (task != null && task.isConfigured())
                    {
                        if (task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                        {
                            renderTooltip(renderArgs, new BlocklingsTranslationTextComponent("task.ui.enable"));
                        }
                        else
                        {
                            renderTooltip(renderArgs, new BlocklingsTranslationTextComponent("task.ui.disable"));
                        }
                    }
                }

                @Override
                protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
                {
                    if (isPressed() && !isDraggingOrAncestorIsDragging())
                    {
                        if (task != null && task.isConfigured())
                        {
                            if (task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                            {
                                task.getGoal().setState(BlocklingGoal.State.IDLE);
                            }
                            else
                            {
                                task.getGoal().setState(BlocklingGoal.State.DISABLED);
                            }
                        }
                    }

                    mouseButtonEvent.setIsHandled(true);
                }
            };
            stateIconControl.setParent(stateContainerControl);
            stateIconControl.setAlignmentX(new Alignment(0.0f));
            stateIconControl.setAlignmentY(new Alignment(0.5f));

            TextBlockControl taskNameControl = new TextBlockControl()
            {
                @Override
                public void onTick()
                {
                    setText(task == null ? BlocklingTasks.NULL.name : new StringTextComponent(task.getCustomName()));
                }

                @Override
                public void onHover(@Nonnull MousePosEvent mousePosEvent)
                {

                }
            };
            taskNameBackgroundControl.addControl(taskNameControl, 1, 0);
            taskNameControl.setWidth(new Fill(1.0f));
            taskNameControl.setHeight(new Fill(1.0f));
            taskNameControl.setVerticalAlignment(VerticalAlignment.MIDDLE);
            taskNameControl.onTick();

            Control addRemoveControl = new TexturedControl(task == null ? GuiTextures.Tasks.TASK_ADD_ICON : GuiTextures.Tasks.TASK_REMOVE_ICON)
            {
                @Override
                public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
                {
                    renderTooltip(renderArgs, new BlocklingsTranslationTextComponent(task == null ? "task.ui.add" : "task.ui.remove"));
                }

                @Override
                protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
                {
                    if (isPressed() && !isDraggingOrAncestorIsDragging())
                    {
                        if (task == null)
                        {
                            blockling.getTasks().createTask(BlocklingTasks.NULL);
                        }
                        else
                        {
                            getParent().removeChild(this);
                            task.blockling.getTasks().removeTask(task);
                        }
                    }
                }
            };
            gridControl.addControl(addRemoveControl, 2, 0);
        }

        @Override
        public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
        {
            if (task != null)
            {
                List<IReorderingProcessor> tooltip = new ArrayList<>();
                tooltip.add(new StringTextComponent(TextFormatting.GOLD + task.getCustomName()).getVisualOrderText());
                tooltip.addAll(GuiUtil.splitText(font, task.getType().desc, 200).stream().map(s -> s.getVisualOrderText()).collect(Collectors.toList()));

                renderTooltip(renderArgs, tooltip);
            }
        }
    }
}
