package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.GridControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
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
                    .addCol(new GridControl.Auto())
                    .addCol(new GridControl.Fill(1.0f))
                    .addCol(new GridControl.Auto()))
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
                private GuiTexture iconTexture = texture;

                @Override
                public void onHoverEnter(@Nonnull MousePosEvent mousePosEvent)
                {
                    iconTexture = GuiTextures.Tasks.TASK_CONFIG_ICON;
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

                }
            };
            iconControl.setParent(iconBackgroundControl);

            Control taskNameBackgroundControl = new TexturedControl(GuiTextures.Tasks.TASK_NAME_BACKGROUND)
            {
                @Override
                public void onHover(@Nonnull MousePosEvent mousePosEvent)
                {

                }
            };
            gridControl.addControl(taskNameBackgroundControl, 1, 0);
            taskNameBackgroundControl.setWidth(new Fill(1.0f));
            taskNameBackgroundControl.setHeight(new Fill(1.0f));
            taskNameBackgroundControl.setPadding(5, 0, 5, 0);

            TextBlockControl taskNameControl = new TextBlockControl()
            {
                @Override
                public void onHover(@Nonnull MousePosEvent mousePosEvent)
                {

                }
            };
            taskNameControl.setParent(taskNameBackgroundControl);
            taskNameControl.setVerticalAlignment(VerticalAlignment.MIDDLE);
            taskNameControl.setWidth(new Fill(1.0f));
            taskNameControl.setHeight(new Fill(1.0f));
            taskNameControl.setAlignmentX(new Alignment(0.5f));
            taskNameControl.setText(task == null ? BlocklingTasks.NULL.name : new StringTextComponent(task.getCustomName()));

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
                    if (isPressed())
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
