package com.willr27.blocklings.gui.widgets.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.widgets.Widget;
import net.minecraft.client.gui.FontRenderer;

public class TaskAddRemoveWidget extends Widget
{
    public static final int GOAL_ADD_REMOVE_WIDTH = 20;
    public static final int GOAL_ADD_REMOVE_HEIGHT = 20;

    private static final GuiTexture ADD_TEXTURE = new GuiTexture(GuiUtil.TASKS, 136, 166, GOAL_ADD_REMOVE_WIDTH, GOAL_ADD_REMOVE_HEIGHT);
    private static final GuiTexture REMOVE_TEXTURE = new GuiTexture(GuiUtil.TASKS, 156, 166, GOAL_ADD_REMOVE_WIDTH, GOAL_ADD_REMOVE_HEIGHT);

    public final TaskWidget taskWidget;
    public final boolean isCreateWidget;

    public TaskAddRemoveWidget(TaskWidget taskWidget, FontRenderer font, boolean isCreateWidget)
    {
        super(font, taskWidget.x + taskWidget.width - 20, taskWidget.y, GOAL_ADD_REMOVE_WIDTH, GOAL_ADD_REMOVE_HEIGHT);
        this.taskWidget = taskWidget;
        this.isCreateWidget = isCreateWidget;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (isCreateWidget)
        {
            renderTexture(matrixStack, ADD_TEXTURE);
        }
        else
        {
            renderTexture(matrixStack, REMOVE_TEXTURE);
        }
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        if (isPressed && isMouseOver(mouseX, mouseY))
        {
            if (isCreateWidget)
            {
                taskWidget.task.blockling.getTasks().createTask(BlocklingTasks.NULL);
            }
            else
            {
                taskWidget.task.blockling.getTasks().removeTask(taskWidget.task);
            }
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }
}
