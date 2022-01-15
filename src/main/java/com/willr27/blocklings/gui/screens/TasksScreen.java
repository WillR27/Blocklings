package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.guis.TabbedGui;
import com.willr27.blocklings.gui.guis.TaskConfigGui;
import com.willr27.blocklings.gui.widgets.ScrollbarWidget;
import com.willr27.blocklings.gui.widgets.Widget;
import com.willr27.blocklings.gui.widgets.tasks.TaskWidget;
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
     * The gap between each task widget.
     */
    private static final int TASK_GAP = 4;

    /**
     * The gui used to configure a task.
     */
    @Nullable
    private TaskConfigGui taskConfigGui;

    /**
     * A widget used to represent the bounding box of the area the tasks widgets are found.
     */
    private Widget tasksWidget;

    /**
     * The list of task widgets to add to the gui.
     */
    @Nonnull
    private final List<TaskWidget> taskWidgets = new ArrayList<>();

    /**
     * The task widget to append to the end of the task widgets used to create a new task.
     */
    private TaskWidget addTaskWidget;

    /**
     * The scrollbar used to scroll the list of tasks.
     */
    private ScrollbarWidget tasksScrollbarWidget;

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

        tasksWidget = new Widget(font, contentLeft + 9, contentTop + 17, 140, 141);

        taskWidgets.clear();
        for (int i = 0; i < blockling.getTasks().getPrioritisedTasks().size(); i++)
        {
            taskWidgets.add(new TaskWidget(blockling.getTasks().getPrioritisedTasks().get(i), font, tasksWidget.x + TASK_GAP, tasksWidget.y + TASK_GAP + i * (TaskWidget.HEIGHT + TASK_GAP), taskWidgets, false, this::onConfigure));
        }

        addTaskWidget = new TaskWidget(new Task(UUID.randomUUID(), BlocklingTasks.NULL, blockling, blockling.getTasks()), font, tasksWidget.x + TASK_GAP, tasksWidget.y + TASK_GAP + taskWidgets.size() * (TaskWidget.HEIGHT + TASK_GAP), taskWidgets, true, this::onConfigure);

        tasksScrollbarWidget = new ScrollbarWidget(font, contentLeft + 155, contentTop + 17, 12, 141);

        if (taskConfigGui != null)
        {
            taskConfigGui = new TaskConfigGui(taskConfigGui.task, font, this);
        }
    }


    @Override
    public void tick()
    {
        if (taskConfigGui != null)
        {
            taskConfigGui.nameField.tick();
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiTextures.TASKS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        if (taskConfigGui != null)
        {
            taskConfigGui.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            if (taskWidgets.stream().anyMatch(taskWidget -> taskWidget.isDragging))
            {
                if (mouseY < tasksWidget.y)
                {
                    tasksScrollbarWidget.scroll(1.0);
                }
                else if (mouseY > tasksWidget.y + tasksWidget.height)
                {
                    tasksScrollbarWidget.scroll(-1.0);
                }
            }

            removeOldTasks();
            addNewTasks();
            updateTaskPositions();

            renderTasks(matrixStack, mouseX, mouseY);

            tasksScrollbarWidget.render(matrixStack, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (taskConfigGui != null)
        {
            taskConfigGui.renderTooltips(matrixStack, mouseX, mouseY);
        }

        if (taskConfigGui == null)
        {
            font.drawShadow(matrixStack, new BlocklingsTranslationTextComponent("tab.tasks"), contentLeft + 8, contentTop + 5, 0xffffff);

            renderTasksTooltips(matrixStack, mouseX, mouseY);
        }
    }

    /**
     * Removes any task widgets that no longer exist as tasks.
     */
    private void removeOldTasks()
    {
        for (int i = 0; i < taskWidgets.size(); i++)
        {
            TaskWidget taskWidget = taskWidgets.get(i);

            if (!blockling.getTasks().getPrioritisedTasks().contains(taskWidget.task))
            {
                taskWidgets.remove(i);
                i--;
            }
        }
    }

    /**
     * Adds task widgets for any new tasks that have been added.
     */
    private void addNewTasks()
    {
        for (int i = 0; i < blockling.getTasks().getPrioritisedTasks().size(); i++)
        {
            Task task = blockling.getTasks().getPrioritisedTasks().get(i);
            TaskWidget taskWidget = i >= taskWidgets.size() ? null : taskWidgets.get(i);

            if (taskWidget != null && task.equals(taskWidget.task))
            {
                continue;
            }

            taskWidget = taskWidgets.stream().filter(widget -> widget.task.equals(task)).findFirst().orElse(null);

            if (taskWidget != null)
            {
                taskWidgets.remove(taskWidget);
                taskWidgets.add(i, taskWidget);
            }
            else
            {
                taskWidgets.add(i, new TaskWidget(task, font, 0, 0, taskWidgets, false, this::onConfigure));
            }
        }
    }

    /**
     * Updates the positions of the tasks depending on their position in the list and the amount the scrollbar is scrolled.
     */
    private void updateTaskPositions()
    {
        tasksScrollbarWidget.isDisabled = true;

        for (int i = 0; i < taskWidgets.size(); i++)
        {
            TaskWidget taskWidget = taskWidgets.get(i);
            taskWidget.x = tasksWidget.x + TASK_GAP;
            taskWidget.y = tasksWidget.y + TASK_GAP + i * (TaskWidget.HEIGHT + TASK_GAP);
        }

        addTaskWidget.x = tasksWidget.x + TASK_GAP;
        addTaskWidget.y = tasksWidget.y + TASK_GAP + taskWidgets.size() * (TaskWidget.HEIGHT + TASK_GAP);

        if (taskWidgets.size() >= 2)
        {
            int taskWidgetsHeight = addTaskWidget.y + addTaskWidget.height - taskWidgets.get(0).y + TASK_GAP * 2;
            int taskWidgetsHeightDif = taskWidgetsHeight - tasksWidget.height;

            if (taskWidgetsHeightDif > 0)
            {
                tasksScrollbarWidget.isDisabled = false;

                for (int i = 0; i < taskWidgets.size(); i++)
                {
                    TaskWidget taskWidget = taskWidgets.get(i);
                    taskWidget.y = tasksWidget.y + TASK_GAP + i * (TaskWidget.HEIGHT + TASK_GAP) - (int) (taskWidgetsHeightDif * tasksScrollbarWidget.percentageScrolled());
                }

                addTaskWidget.y = tasksWidget.y + TASK_GAP + taskWidgets.size() * (TaskWidget.HEIGHT + TASK_GAP) - (int) (taskWidgetsHeightDif * tasksScrollbarWidget.percentageScrolled());
            }
        }
    }

    /**
     * Renders the task widgets in the gui.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the current mouse x position.
     * @param mouseY the current mouse y position.
     */
    private void renderTasks(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        tasksWidget.enableScissor();

        for (TaskWidget taskWidget : taskWidgets)
        {
            taskWidget.render(matrixStack, mouseX, mouseY);
        }

        addTaskWidget.render(matrixStack, mouseX, mouseY);

        GuiUtil.disableScissor();
    }

    /**
     * Renders the task widget tooltips in the gui.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the current mouse x position.
     * @param mouseY the current mouse y position.
     */
    private void renderTasksTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (tasksWidget.isMouseOver(mouseX, mouseY))
        {
            TaskWidget hoveredTaskWidget = getHoveredTaskWidget(mouseX, mouseY);

            if (hoveredTaskWidget != null)
            {
                List<IReorderingProcessor> text = new ArrayList<>();
                text.add(new StringTextComponent(TextFormatting.GOLD + hoveredTaskWidget.task.getCustomName()).getVisualOrderText());
                text.add(new StringTextComponent("").getVisualOrderText());
                text.addAll(GuiUtil.splitText(font, hoveredTaskWidget.task.getType().desc.getString(), 150).stream().map(s -> new StringTextComponent(s).getVisualOrderText()).collect(Collectors.toList()));

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
        taskConfigGui = new TaskConfigGui(task, font, this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        mouseClickedNoHandle((int) mouseX, (int) mouseY, button);

        if (taskConfigGui != null)
        {
            if (taskConfigGui.mouseClicked(mouseX, mouseY, button))
            {
                return true;
            }
        }
        else
        {
            if (tasksScrollbarWidget.mouseClicked((int) mouseX, (int) mouseY, button))
            {
                return true;
            }
            else if (addTaskWidget.mouseClicked((int) mouseX, (int) mouseY, button))
            {
                return true;
            }
            else
            {
                TaskWidget hoveredWidget = getHoveredTaskWidget((int) mouseX, (int) mouseY);

                if (hoveredWidget != null)
                {
                    if (hoveredWidget.mouseClicked((int) mouseX, (int) mouseY, button))
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

        if (taskConfigGui != null)
        {
            if (taskConfigGui.mouseReleased(mouseX, mouseY, button))
            {
                ret = true;
            }
        }
        else
        {
            if (taskWidgets.stream().filter(taskWidget -> taskWidget.mouseReleased((int) mouseX, (int) mouseY, button)).findFirst().isPresent())
            {
                ret = true;
            }
            else if (addTaskWidget.mouseReleased((int) mouseX, (int) mouseY, button))
            {
                ret = true;
            }
            else if (tasksScrollbarWidget.mouseReleased((int) mouseX, (int) mouseY, button))
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
        if (taskConfigGui != null)
        {
            if (taskConfigGui.mouseScrolled(mouseX, mouseY, scroll))
            {
                return true;
            }
        }
        else
        {
            if (tasksWidget.isMouseOver((int) mouseX, (int) mouseY) || tasksScrollbarWidget.isMouseOver((int) mouseX, (int) mouseY))
            {
                if (tasksScrollbarWidget.scroll(scroll))
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
        if (taskConfigGui != null)
        {
            if (taskConfigGui.keyPressed(keyCode, i, j))
            {
                return true;
            }
            else if (!taskConfigGui.nameField.isFocused() && GuiUtil.isCloseInventoryKey(keyCode))
            {
                taskConfigGui = null;

                return true;
            }
        }

        if ((taskConfigGui == null || !taskConfigGui.nameField.isFocused()) && GuiUtil.isCloseInventoryKey(keyCode))
        {
            onClose();

            return true;
        }

        return super.keyPressed(keyCode, i, j);
    }

    @Override
    public boolean charTyped(char cah, int code)
    {
        if (taskConfigGui != null && taskConfigGui.charTyped(cah, code))
        {
            return true;
        }

        return super.charTyped(cah, code); // TODO: TIDY
    }

    /**
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @return the current task that is being hovered over or null if none are.
     */
    @Nullable
    private TaskWidget getHoveredTaskWidget(int mouseX, int mouseY)
    {
        for (TaskWidget taskWidget : taskWidgets)
        {
            if (taskWidget.isMouseOver(mouseX, mouseY))
            {
                return taskWidget;
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
