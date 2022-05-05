package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.controls.common.panel.StackPanel;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.gui.controls.TabbedControl;
import com.willr27.blocklings.gui.controls.tasks.config.TaskConfigContainerControl;
import com.willr27.blocklings.gui.controls.tasks.TaskControl;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * The screen for rendering and handling blocklings' tasks.
 */
@OnlyIn(Dist.CLIENT)
public class TasksScreen extends TabbedScreen
{
    /**
     * The gui used to configure a task.
     */
    @Nullable
    private TaskConfigContainerControl taskConfigControl;

    /**
     * The panel containing the task controls.
     */
    private StackPanel tasksPanel;

    /**
     * The scrollbar used to scroll the list of tasks.
     */
    private ScrollbarControl tasksScrollbarControl;

    /**
     * @param blockling the blockling.
     */
    public TasksScreen(BlocklingEntity blockling)
    {
        super(blockling);
    }

    @Override
    protected void init()
    {
        super.init();

        removeChild(tasksPanel);
        tasksPanel = new StackPanel(this, contentLeft + 9, contentTop + 17, 140, 141)
        {
            @Override
            public void setupEventHandlers()
            {
                super.setupEventHandlers();

                blockling.getTasks().onCreateTask.subscribe((e) ->
                {
                    new TaskControl(this, e.task, false, (task) -> { ((TasksScreen) getScreen()).onConfigure(task); });
                });

                blockling.getTasks().onRemoveTask.subscribe((e) ->
                {
                    for (Control control : getChildrenCopy())
                    {
                        TaskControl taskControl = (TaskControl) control;

                        if (taskControl.task.equals(e.task))
                        {
                            taskControl.remove();
                        }
                    }
                });
            }

            @Override
            public void addChild(@Nonnull Control control)
            {
                children.add(control);

                children.sort((c1, c2) ->
                {
                    TaskControl taskControl1 = (TaskControl) c1;
                    TaskControl taskControl2 = (TaskControl) c2;

                    return taskControl1.isAddControl ? 1 : taskControl2.isAddControl ? -1 : 0;
                });
            }

            @Override
            protected List<Control> getItems()
            {
                // Strip out the add task control.
                return getChildrenCopy().subList(0, getChildren().size() - 1);
            }
        };
        tasksPanel.setIsReorderable(true);
        tasksPanel.onReorder.subscribe(e ->
        {
            int numberOfTasks = blockling.getTasks().getPrioritisedTasks().size();

            if (e.newIndex >= numberOfTasks || e.oldIndex >= numberOfTasks)
            {
                e.setCancelled(true);

                return;
            }

            TaskControl taskControl = (TaskControl) tasksPanel.getChildren().get(e.oldIndex);
            TaskControl closestTaskControl = (TaskControl) tasksPanel.getChildren().get(e.newIndex);

            if (e.newIndex > e.oldIndex)
            {
                taskControl.task.setPriority(closestTaskControl.task.getPriority());
            }
            else
            {
                closestTaskControl.task.setPriority(taskControl.task.getPriority());
            }
        });

        for (int i = 0; i < blockling.getTasks().getPrioritisedTasks().size(); i++)
        {
            new TaskControl(tasksPanel, blockling.getTasks().getPrioritisedTasks().get(i), false, this::onConfigure);
        }

        new TaskControl(tasksPanel, new Task(UUID.randomUUID(), BlocklingTasks.NULL, blockling, blockling.getTasks()), true, this::onConfigure);

        removeChild(tasksScrollbarControl);
        tasksScrollbarControl = new ScrollbarControl(this, contentLeft + 155, contentTop + 17, 12, 141);

        tasksPanel.setScrollbarY(tasksScrollbarControl);

        if (taskConfigControl != null)
        {
            removeChild(taskConfigControl);
            taskConfigControl = new TaskConfigContainerControl(this, taskConfigControl.task, contentLeft, contentTop);
        }
    }

    @Override
    public void tick()
    {
        if (taskConfigControl != null)
        {
            taskConfigControl.nameTextFieldControl.tick();
        }
    }

    @Override
    public void renderScreen(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiTextures.TASKS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedControl.CONTENT_WIDTH, TabbedControl.CONTENT_HEIGHT);

        if (taskConfigControl == null)
        {
            font.drawShadow(matrixStack, new BlocklingsTranslationTextComponent("tab.tasks"), contentLeft + 8, contentTop + 5, 0xffffff);
        }
    }

    /**
     * Called when the configure button is pressed on a task.
     *
     * @param task the task to configure.
     */
    private void onConfigure(@Nonnull Task task)
    {
        if (taskConfigControl != null)
        {
            removeChild(taskConfigControl);
        }

        taskConfigControl = new TaskConfigContainerControl(this, task, contentLeft, contentTop);
    }

    @Override
    public void controlKeyPressed(@Nonnull KeyEvent e)
    {
        if (GuiUtil.isCloseInventoryKey(e.keyCode))
        {
            onClose();

            e.setIsHandled(true);
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
