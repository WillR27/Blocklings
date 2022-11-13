package com.willr27.blocklings.client.gui2.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui2.Control;
import com.willr27.blocklings.client.gui2.GuiTextures;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.client.gui2.controls.TabbedControl;
import com.willr27.blocklings.client.gui2.controls.common.ScrollbarControl;
import com.willr27.blocklings.client.gui2.controls.common.panel.StackPanel;
import com.willr27.blocklings.client.gui2.controls.tasks.TaskControl;
import com.willr27.blocklings.client.gui2.controls.tasks.config.TaskConfigContainerControl;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.ITextComponent;
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
    public StackPanel tasksPanel;

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

        boolean isVisible = true;

        if (tasksPanel != null)
        {
            isVisible = tasksPanel.isVisible();
        }

        tasksPanel = new StackPanel(this, contentLeft + 9, contentTop + 9, 140, 148)
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
                e.setIsCancelled(true);

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
        tasksPanel.setIsVisible(isVisible);

        for (int i = 0; i < blockling.getTasks().getPrioritisedTasks().size(); i++)
        {
            new TaskControl(tasksPanel, blockling.getTasks().getPrioritisedTasks().get(i), false, this::onConfigure);
        }

        new TaskControl(tasksPanel, new Task(UUID.randomUUID(), BlocklingTasks.NULL, blockling, blockling.getTasks()), true, this::onConfigure);

        ScrollbarControl tasksScrollbarControl = new ScrollbarControl(this, contentLeft + 155, contentTop + 9, 12, 148);

        tasksPanel.setScrollbarY(tasksScrollbarControl);

        if (taskConfigControl != null)
        {
            taskConfigControl = new TaskConfigContainerControl(this, taskConfigControl.task, contentLeft, contentTop);
        }
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    public void renderScreen(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.renderScreen(matrixStack, mouseX, mouseY, partialTicks);

        GuiUtil.bindTexture(GuiTextures.TASKS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedControl.CONTENT_WIDTH, TabbedControl.CONTENT_HEIGHT);
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

        tasksPanel.setIsVisible(false);
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
    public void setIsVisible(boolean isVisible)
    {
        super.setIsVisible(isVisible);

        if (isVisible)
        {
            tasksPanel.setIsVisible(true);
        }
    }

    @Override
    public ITextComponent getTitle()
    {
        if (taskConfigControl != null && taskConfigControl.isVisible())
        {
            return new BlocklingsTranslationTextComponent("gui.configure_task");
        }

        return super.getTitle();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
