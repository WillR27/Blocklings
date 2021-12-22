package com.willr27.blocklings.gui.widgets.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.goal.Task;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.widgets.Widget;
import net.minecraft.client.gui.FontRenderer;

import java.util.function.Consumer;

public class TaskIconWidget extends Widget
{
    public static final int GOAL_ICON_WIDTH = 20;
    public static final int GOAL_ICON_HEIGHT = 20;

    public static final GuiTexture BACKGROUND_TEXTURE = new GuiTexture(GuiUtil.TASKS, 0, 166, GOAL_ICON_WIDTH, GOAL_ICON_HEIGHT);
    public static final GuiTexture BACKGROUND_PRESSED_TEXTURE = new GuiTexture(GuiUtil.TASKS, 20, 166, GOAL_ICON_WIDTH, GOAL_ICON_HEIGHT);
    public static final GuiTexture CONFIGURE_TEXTURE = new GuiTexture(GuiUtil.TASKS, 176, 166, GOAL_ICON_WIDTH, GOAL_ICON_HEIGHT);

    public final TaskWidget taskWidget;
    public final boolean isCreateWidget;
    public final Consumer<Task> onConfigure;

    public TaskIconWidget(TaskWidget taskWidget, FontRenderer font, boolean isCreateWidget, Consumer<Task> onConfigure)
    {
        super(font, taskWidget.x, taskWidget.y, GOAL_ICON_WIDTH, GOAL_ICON_HEIGHT);
        this.taskWidget = taskWidget;
        this.isCreateWidget = isCreateWidget;
        this.onConfigure = onConfigure;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (isPressed || isCreateWidget)
        {
            RenderSystem.color3f(0.8f, 0.8f, 0.8f);

            renderTexture(matrixStack, BACKGROUND_PRESSED_TEXTURE);
        }
        else
        {
            renderTexture(matrixStack, BACKGROUND_TEXTURE);
        }

        if (isPressed || isMouseOver(mouseX, mouseY))
        {
            renderTexture(matrixStack, CONFIGURE_TEXTURE);
        }
        else
        {
            renderTexture(matrixStack, taskWidget.task.getType().texture);
        }

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        if (isMouseOver(mouseX, mouseY))
        {
            onConfigure.accept(taskWidget.task);
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }
}
