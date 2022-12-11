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
            TaskControl taskControl = new TaskControl(taskListControl, task);
        }

        TaskControl addTaskControl = new TaskControl(taskListControl, null);

        Control scrollbarControl = new Control();
        scrollbarControl.setParent(taskListContainerControl);
        scrollbarControl.setWidth(12);
        scrollbarControl.setPercentHeight(1.0f);
        scrollbarControl.setPercentX(1.0f);
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
                    .addCol(new GridControl.Fill(1.0f))
                    .addCol(new GridControl.Auto()));
            gridControl.setParent(this);
            gridControl.setWidth(new Fill(1.0f));
            gridControl.setFitToContentsY(true);

            Control iconBackgroundControl = new TexturedControl(GuiTextures.Tasks.TASK_ICON_BACKGROUND_RAISED, GuiTextures.Tasks.TASK_ICON_BACKGROUND_PRESSED);
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
                protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
                {

                }

                @Override
                protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
                {

                }
            };
            iconControl.setParent(iconBackgroundControl);

            Control taskNameBackgroundControl = new TexturedControl(GuiTextures.Tasks.TASK_NAME_BACKGROUND);
            gridControl.addControl(taskNameBackgroundControl, 1, 0);
            taskNameBackgroundControl.setWidth(new Fill(1.0f));
            taskNameBackgroundControl.setHeight(new Fill(1.0f));
            taskNameBackgroundControl.setPadding(5, 0, 5, 0);

            TextBlockControl taskNameControl = new TextBlockControl();
            taskNameControl.setParent(taskNameBackgroundControl);
            taskNameControl.setVerticalAlignment(VerticalAlignment.MIDDLE);
            taskNameControl.setWidth(new Fill(1.0f));
            taskNameControl.setHeight(new Fill(1.0f));
            taskNameControl.setAlignmentX(new Alignment(0.5f));
            taskNameControl.setText(task == null ? "OOGA" : task.getCustomName());

            Control addRemoveControl = new TexturedControl(task == null ? GuiTextures.Tasks.TASK_ADD_ICON : GuiTextures.Tasks.TASK_REMOVE_ICON);
            gridControl.addControl(addRemoveControl, 2, 0);
        }
    }
}
