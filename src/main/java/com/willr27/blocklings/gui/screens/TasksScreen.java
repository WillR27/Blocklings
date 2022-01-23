package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.gui.controls.TabbedControl;
import com.willr27.blocklings.gui.controls.tasks.TaskConfigControl;
import com.willr27.blocklings.gui.controls.tasks.TaskControl;
import com.willr27.blocklings.task.Task;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The screen for rendering and handling blocklings' tasks.
 */
@OnlyIn(Dist.CLIENT)
public class TasksScreen extends TabbedScreen
{
    /**
     * The gap between each task control.
     */
    private static final int TASK_GAP = 4;

    /**
     * The gui used to configure a task.
     */
    @Nullable
    private TaskConfigControl taskConfigControl;

    /**
     * A control used to represent the bounding box of the area the tasks controls are found.
     */
    private Control tasksControl;

    /**
     * The list of task controls to add to the gui.
     */
    @Nonnull
    private final List<TaskControl> taskControls = new ArrayList<>();

    /**
     * The task control to append to the end of the task controls used to create a new task.
     */
    private TaskControl addTaskControl;

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

        removeChild(tasksControl);
        tasksControl = new Control(this, 9, 17, 140, 141);

        taskControls.clear();
        for (int i = 0; i < blockling.getTasks().getPrioritisedTasks().size(); i++)
        {
            taskControls.add(new TaskControl(tasksControl, blockling.getTasks().getPrioritisedTasks().get(i), TASK_GAP, TASK_GAP + i * (TaskControl.HEIGHT + TASK_GAP), taskControls, false, this::onConfigure));
        }

        addTaskControl = new TaskControl(tasksControl, new Task(UUID.randomUUID(), BlocklingTasks.NULL, blockling, blockling.getTasks()), TASK_GAP, TASK_GAP + taskControls.size() * (TaskControl.HEIGHT + TASK_GAP), taskControls, true, this::onConfigure);

        removeChild(tasksScrollbarControl);
        tasksScrollbarControl = new ScrollbarControl(this, 155, 17, 12, 141);

        if (taskConfigControl != null)
        {
            removeChild(taskConfigControl);
            taskConfigControl = new TaskConfigControl(this, taskConfigControl.task, 0, 0);
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
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiTextures.TASKS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedControl.CONTENT_WIDTH, TabbedControl.CONTENT_HEIGHT);

        if (taskConfigControl != null)
        {
            taskConfigControl.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            if (taskControls.stream().anyMatch(taskControl -> taskControl.isDragging))
            {
                if (mouseY < tasksControl.screenY)
                {
                    tasksScrollbarControl.scroll(1.0);
                }
                else if (mouseY > tasksControl.screenY + tasksControl.height)
                {
                    tasksScrollbarControl.scroll(-1.0);
                }
            }

            removeOldTasks();
            addNewTasks();
            updateTaskPositions();

            renderTasks(matrixStack, mouseX, mouseY);

            tasksScrollbarControl.render(matrixStack, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (taskConfigControl != null)
        {
            taskConfigControl.renderTooltips(matrixStack, mouseX, mouseY);
        }

        if (taskConfigControl == null)
        {
            font.drawShadow(matrixStack, new BlocklingsTranslationTextComponent("tab.tasks"), contentLeft + 8, contentTop + 5, 0xffffff);

            renderTasksTooltips(matrixStack, mouseX, mouseY);
        }
    }

    /**
     * Removes any task controls that no longer exist as tasks.
     */
    private void removeOldTasks()
    {
        for (int i = 0; i < taskControls.size(); i++)
        {
            TaskControl taskControl = taskControls.get(i);

            if (!blockling.getTasks().getPrioritisedTasks().contains(taskControl.task))
            {
                taskControls.remove(i);
                i--;
            }
        }
    }

    /**
     * Adds task controls for any new tasks that have been added.
     */
    private void addNewTasks()
    {
        for (int i = 0; i < blockling.getTasks().getPrioritisedTasks().size(); i++)
        {
            Task task = blockling.getTasks().getPrioritisedTasks().get(i);
            TaskControl taskControl = i >= taskControls.size() ? null : taskControls.get(i);

            if (taskControl != null && task.equals(taskControl.task))
            {
                continue;
            }

            taskControl = taskControls.stream().filter(control -> control.task.equals(task)).findFirst().orElse(null);

            if (taskControl != null)
            {
                taskControls.remove(taskControl);
                taskControls.add(i, taskControl);
            }
            else
            {
                taskControls.add(i, new TaskControl(tasksControl, task, 0, 0, taskControls, false, this::onConfigure));
            }
        }
    }

    /**
     * Updates the positions of the tasks depending on their position in the list and the amount the scrollbar is scrolled.
     */
    private void updateTaskPositions()
    {
        tasksScrollbarControl.isDisabled = true;

        for (int i = 0; i < taskControls.size(); i++)
        {
            TaskControl taskControl = taskControls.get(i);
            taskControl.screenX = tasksControl.screenX + TASK_GAP;
            taskControl.screenY = tasksControl.screenY + TASK_GAP + i * (TaskControl.HEIGHT + TASK_GAP);
        }

        addTaskControl.screenX = tasksControl.screenX + TASK_GAP;
        addTaskControl.screenY = tasksControl.screenY + TASK_GAP + taskControls.size() * (TaskControl.HEIGHT + TASK_GAP);

        if (taskControls.size() >= 2)
        {
            int taskControlsHeight = addTaskControl.screenY + addTaskControl.height - taskControls.get(0).screenY + TASK_GAP * 2;
            int taskControlsHeightDif = taskControlsHeight - tasksControl.height;

            if (taskControlsHeightDif > 0)
            {
                tasksScrollbarControl.isDisabled = false;

                for (int i = 0; i < taskControls.size(); i++)
                {
                    TaskControl taskControl = taskControls.get(i);
                    taskControl.screenY = tasksControl.screenY + TASK_GAP + i * (TaskControl.HEIGHT + TASK_GAP) - (int) (taskControlsHeightDif * tasksScrollbarControl.percentageScrolled());
                }

                addTaskControl.screenY = tasksControl.screenY + TASK_GAP + taskControls.size() * (TaskControl.HEIGHT + TASK_GAP) - (int) (taskControlsHeightDif * tasksScrollbarControl.percentageScrolled());
            }
        }
    }

    /**
     * Renders the task controls in the gui.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the current mouse x position.
     * @param mouseY the current mouse y position.
     */
    private void renderTasks(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        tasksControl.enableScissor();

        for (TaskControl taskControl : taskControls)
        {
            taskControl.render(matrixStack, mouseX, mouseY);
        }

        addTaskControl.render(matrixStack, mouseX, mouseY);

        GuiUtil.disableScissor();
    }

    /**
     * Renders the task control tooltips in the gui.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the current mouse x position.
     * @param mouseY the current mouse y position.
     */
    private void renderTasksTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (tasksControl.isMouseOver(mouseX, mouseY))
        {
            TaskControl hoveredTaskControl = getHoveredTaskControl(mouseX, mouseY);

            if (hoveredTaskControl != null)
            {
                List<IReorderingProcessor> text = new ArrayList<>();
                text.add(new StringTextComponent(TextFormatting.GOLD + hoveredTaskControl.task.getCustomName()).getVisualOrderText());
                text.add(new StringTextComponent("").getVisualOrderText());
                text.addAll(GuiUtil.splitText(font, hoveredTaskControl.task.getType().desc.getString(), 150).stream().map(s -> new StringTextComponent(s).getVisualOrderText()).collect(Collectors.toList()));

                renderTooltip(matrixStack, text, mouseX, mouseY);
            }
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

        taskConfigControl = new TaskConfigControl(this, task, 0, 0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        mouseClickedNoHandle((int) mouseX, (int) mouseY, button);

        if (taskConfigControl != null)
        {
            if (taskConfigControl.mouseClicked((int) mouseX, (int) mouseY, button))
            {
                return true;
            }
        }
        else
        {
            if (tasksScrollbarControl.mouseClicked((int) mouseX, (int) mouseY, button))
            {
                return true;
            }
            else if (addTaskControl.mouseClicked((int) mouseX, (int) mouseY, button))
            {
                return true;
            }
            else
            {
                TaskControl hoveredControl = getHoveredTaskControl((int) mouseX, (int) mouseY);

                if (hoveredControl != null)
                {
                    if (hoveredControl.mouseClicked((int) mouseX, (int) mouseY, button))
                    {
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        boolean ret  = false;

        if (taskConfigControl != null)
        {
            if (taskConfigControl.mouseReleased((int) mouseX, (int) mouseY, button))
            {
                ret = true;
            }
        }
        else
        {
            if (taskControls.stream().filter(taskControl -> taskControl.mouseReleased((int) mouseX, (int) mouseY, button)).findFirst().isPresent())
            {
                ret = true;
            }
            else if (addTaskControl.mouseReleased((int) mouseX, (int) mouseY, button))
            {
                ret = true;
            }
            else if (tasksScrollbarControl.mouseReleased((int) mouseX, (int) mouseY, button))
            {
                ret = true;
            }
        }

        mouseReleasedNoHandle((int) mouseX, (int) mouseY, button);

        return ret || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        if (taskConfigControl != null)
        {
            if (taskConfigControl.mouseScrolled((int) mouseX, (int) mouseY, scroll))
            {
                return true;
            }
        }
        else
        {
            if (tasksControl.isMouseOver((int) mouseX, (int) mouseY) || tasksScrollbarControl.isMouseOver((int) mouseX, (int) mouseY))
            {
                if (tasksScrollbarControl.scroll(scroll))
                {
                    return true;
                }
            }
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (taskConfigControl != null)
        {
            if (taskConfigControl.keyPressed(keyCode, i, j))
            {
                return true;
            }
            else if (!taskConfigControl.nameTextFieldControl.isFocused() && GuiUtil.isCloseInventoryKey(keyCode))
            {
                removeChild(taskConfigControl);
                taskConfigControl = null;

                return true;
            }
        }

        if ((taskConfigControl == null || !taskConfigControl.nameTextFieldControl.isFocused()) && GuiUtil.isCloseInventoryKey(keyCode))
        {
            onClose();

            return true;
        }

        return super.keyPressed(keyCode, i, j);
    }

    @Override
    public boolean charTyped(char cah, int code)
    {
        if (taskConfigControl != null && taskConfigControl.charTyped(cah, code))
        {
            return true;
        }

        return super.charTyped(cah, code);
    }

    /**
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @return the current task that is being hovered over or null if none are.
     */
    @Nullable
    private TaskControl getHoveredTaskControl(int mouseX, int mouseY)
    {
        for (TaskControl taskControl : taskControls)
        {
            if (taskControl.isMouseOver(mouseX, mouseY))
            {
                return taskControl;
            }
        }

        return null;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
