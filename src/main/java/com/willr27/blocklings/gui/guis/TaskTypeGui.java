package com.willr27.blocklings.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.task.TaskType;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.ScrollbarWidget;
import com.willr27.blocklings.gui.controls.tasks.TaskTypeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskTypeGui extends ConfigGui
{
    public static final int TASK_TYPE_GAP = 5;

    private final Task task;
    private final FontRenderer font;
    private final int contentLeft, contentTop;
    private final int width, height;
    private final ScrollbarWidget contentScrollbarWidget;
    private final Screen screen;
    private final TaskConfigGui taskConfigGui;

    private final List<TaskTypeWidget> taskTypeWidgets = new ArrayList<>();

    public TaskTypeGui(Task task, FontRenderer font, int contentLeft, int contentTop, int width, int height, ScrollbarWidget contentScrollbarWidget, TaskConfigGui taskConfigGui)
    {
        this.task = task;
        this.font = font;
        this.contentLeft = contentLeft;
        this.contentTop = contentTop;
        this.width = width;
        this.height = height;
        this.contentScrollbarWidget = contentScrollbarWidget;
        this.screen = Minecraft.getInstance().screen;
        this.taskConfigGui = taskConfigGui;

        List<TaskType> unlockedTaskTypes = BlocklingTasks.TASK_TYPES.stream().filter(taskType -> task.blockling.getTasks().isUnlocked(taskType)).collect(Collectors.toList());

        for (int i = 0; i < unlockedTaskTypes.size(); i++)
        {
            taskTypeWidgets.add(new TaskTypeWidget(task, unlockedTaskTypes.get(i), font, contentLeft + TASK_TYPE_GAP + ((i % 3) * (TaskTypeWidget.WIDTH + TASK_TYPE_GAP)), contentTop + TASK_TYPE_GAP + ((i / 3) * (TaskTypeWidget.HEIGHT + TASK_TYPE_GAP))));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        updateTaskTypePositions();

        for (TaskTypeWidget taskTypeWidget : taskTypeWidgets)
        {
            taskTypeWidget.render(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        TaskTypeWidget hoveredTaskTypeWidget = getHoveredTypeWidget(mouseX, mouseY);

        if (hoveredTaskTypeWidget != null)
        {
            List<IReorderingProcessor> text = new ArrayList<>();
            text.add(new StringTextComponent(TextFormatting.GOLD + hoveredTaskTypeWidget.taskType.name.getString()).getVisualOrderText());
            text.add(new StringTextComponent("").getVisualOrderText());
            text.addAll(GuiUtil.splitText(font, hoveredTaskTypeWidget.taskType.desc.getString(), 150).stream().map(s -> new StringTextComponent(s).getVisualOrderText()).collect(Collectors.toList()));

            screen.renderTooltip(matrixStack, text, mouseX, mouseY);
        }
    }

    private void updateTaskTypePositions()
    {
        contentScrollbarWidget.isDisabled = true;

        for (int i = 0; i < taskTypeWidgets.size(); i++)
        {
            TaskTypeWidget taskTypeWidget = taskTypeWidgets.get(i);
            taskTypeWidget.screenX = contentLeft + TASK_TYPE_GAP + ((i % 3) * (TaskTypeWidget.WIDTH + TASK_TYPE_GAP));
            taskTypeWidget.screenY = contentTop + TASK_TYPE_GAP + ((i / 3) * (TaskTypeWidget.HEIGHT + TASK_TYPE_GAP));
        }

        if (taskTypeWidgets.size() >= 2)
        {
            int taskWidgetsHeight = taskTypeWidgets.get(taskTypeWidgets.size() - 1).screenY + taskTypeWidgets.get(taskTypeWidgets.size() - 1).height - taskTypeWidgets.get(0).screenY + TASK_TYPE_GAP * 2;
            int taskWidgetsHeightDif = taskWidgetsHeight - height;

            if (taskWidgetsHeightDif > 0)
            {
                contentScrollbarWidget.isDisabled = false;

                for (int i = 0; i < taskTypeWidgets.size(); i++)
                {
                    TaskTypeWidget taskTypeWidget = taskTypeWidgets.get(i);
                    taskTypeWidget.screenY = contentTop + TASK_TYPE_GAP + ((i / 3) * (TaskTypeWidget.HEIGHT + TASK_TYPE_GAP)) - (int) (taskWidgetsHeightDif * contentScrollbarWidget.percentageScrolled());
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (taskTypeWidgets.stream().map(taskTypeWidget -> taskTypeWidget.mouseClicked((int) mouseX, (int) mouseY, state)).filter(b -> b).findAny().isPresent())
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        boolean ret = false;

        if (taskTypeWidgets.stream().map(taskTypeWidget -> taskTypeWidget.mouseReleased((int) mouseX, (int) mouseY, state)).filter(b -> b).findAny().isPresent())
        {
            taskConfigGui.recreateTabs();

            ret = true;
        }

        return ret;
    }

    private TaskTypeWidget getHoveredTypeWidget(int mouseX, int mouseY)
    {
        return taskTypeWidgets.stream().filter(taskTypeWidget -> taskTypeWidget.isMouseOver(mouseX, mouseY)).findFirst().orElse(null);
    }
}
