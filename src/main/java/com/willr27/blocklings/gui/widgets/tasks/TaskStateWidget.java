package com.willr27.blocklings.gui.widgets.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.widgets.Widget;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;

public class TaskStateWidget extends Widget
{
    public static final int GOAL_STATE_WIDTH = 20;
    public static final int GOAL_STATE_HEIGHT = 20;

    private static final GuiTexture STATE_TEXTURE = new GuiTexture(GuiTextures.TASKS, 196, 166, GOAL_STATE_WIDTH, GOAL_STATE_HEIGHT);
    private static final GuiTexture STATE_PRESSED_TEXTURE = new GuiTexture(GuiTextures.TASKS, 216, 166, GOAL_STATE_WIDTH, GOAL_STATE_HEIGHT);

    public final TaskWidget taskWidget;

    public TaskStateWidget(TaskWidget taskWidget, FontRenderer font)
    {
        super(font, taskWidget.x + taskWidget.width - 42, taskWidget.y, GOAL_STATE_WIDTH, GOAL_STATE_HEIGHT);
        this.taskWidget = taskWidget;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (taskWidget.task.isConfigured())
        {
            if (taskWidget.task.getGoal().getState() == BlocklingGoal.State.DISABLED)
            {
                RenderSystem.color3f(1.0f, 0.0f, 0.0f);
            }
            else if (taskWidget.task.getGoal().getState() == BlocklingGoal.State.IDLE)
            {
                RenderSystem.color3f(1.0f, 0.8f, 0.0f);
            }
            if (taskWidget.task.getGoal().getState() == BlocklingGoal.State.ACTIVE)
            {
                RenderSystem.color3f(0.0f, 0.7f, 0.0f);
            }
        }
        else
        {
            RenderSystem.color3f(0.6f, 0.6f, 0.6f);
        }

        if (taskWidget.task.isConfigured() && taskWidget.task.getGoal().getState() == BlocklingGoal.State.DISABLED)
        {
            renderTexture(matrixStack, STATE_PRESSED_TEXTURE);
        }
        else
        {
            renderTexture(matrixStack, STATE_TEXTURE);
        }

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        if (isPressed() && isMouseOver(mouseX, mouseY))
        {
            if (taskWidget.task.isConfigured())
            {
                if (taskWidget.task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                {
                    taskWidget.task.getGoal().setState(BlocklingGoal.State.IDLE);
                }
                else
                {
                    taskWidget.task.getGoal().setState(BlocklingGoal.State.DISABLED);
                }
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
