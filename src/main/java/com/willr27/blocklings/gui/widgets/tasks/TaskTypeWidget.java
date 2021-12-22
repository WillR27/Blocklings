package com.willr27.blocklings.gui.widgets.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.goal.Task;
import com.willr27.blocklings.goal.TaskType;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import net.minecraft.client.gui.FontRenderer;

public class TaskTypeWidget extends TexturedWidget
{
    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;

    private static final GuiTexture RADIO_TEXTURE = new GuiTexture(GuiUtil.TASKS, 196, 166, 20, HEIGHT);
    private static final GuiTexture RADIO_SELECTED_TEXTURE = new GuiTexture(GuiUtil.TASKS, 196, 166, 20, HEIGHT);

    public final Task task;
    public final TaskType taskType;

    public TaskTypeWidget(Task task, TaskType taskType, FontRenderer font, int x, int y)
    {
        super(font, x, y, new GuiTexture(GuiUtil.TASK_CONFIGURE, 0, 166, WIDTH, HEIGHT));
        this.task = task;
        this.taskType = taskType;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        super.render(matrixStack, mouseX, mouseY);

        renderTexture(matrixStack, taskType.texture);

        if (task.getType() == taskType)
        {
            RenderSystem.color3f(0.7f, 0.3f, 0.7f);
            renderTexture(matrixStack, width - 20, 0, RADIO_SELECTED_TEXTURE);
        }
        else
        {
            RenderSystem.color3f(0.6f, 0.6f, 0.6f);
            renderTexture(matrixStack, width - 20, 0, RADIO_TEXTURE);
        }

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        if (isPressed && isMouseOver(mouseX, mouseY))
        {
            task.setType(taskType);

            return true;
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }
}
