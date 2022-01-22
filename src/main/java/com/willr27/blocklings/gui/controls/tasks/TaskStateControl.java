package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A control to display the current state of a task.
 */
@OnlyIn(Dist.CLIENT)
public class TaskStateControl extends Control
{
    /**
     * The width of the state icon.
     */
    public static final int TASK_STATE_WIDTH = 20;

    /**
     * The height of the state icon.
     */
    public static final int TASK_STATE_HEIGHT = 20;

    /**
     * The default state icon texture.
     */
    @Nonnull
    private static final GuiTexture STATE_TEXTURE = new GuiTexture(GuiTextures.TASKS, 196, 166, TASK_STATE_WIDTH, TASK_STATE_HEIGHT);

    /**
     * The pressed state icon texture.
     */
    @Nonnull
    private static final GuiTexture STATE_PRESSED_TEXTURE = new GuiTexture(GuiTextures.TASKS, 216, 166, TASK_STATE_WIDTH, TASK_STATE_HEIGHT);

    /**
     * The parent task control.
     */
    @Nonnull
    public final TaskControl taskControl;

    /**
     * @param parentTaskControl the parent task control.
     */
    public TaskStateControl(@Nonnull TaskControl parentTaskControl)
    {
        super(parentTaskControl, parentTaskControl.width - 42, 0, TASK_STATE_WIDTH, TASK_STATE_HEIGHT);
        this.taskControl = parentTaskControl;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (taskControl.task.isConfigured())
        {
            if (taskControl.task.getGoal().getState() == BlocklingGoal.State.DISABLED)
            {
                RenderSystem.color3f(1.0f, 0.0f, 0.0f);
            }
            else if (taskControl.task.getGoal().getState() == BlocklingGoal.State.IDLE)
            {
                RenderSystem.color3f(1.0f, 0.8f, 0.0f);
            }
            if (taskControl.task.getGoal().getState() == BlocklingGoal.State.ACTIVE)
            {
                RenderSystem.color3f(0.0f, 0.7f, 0.0f);
            }
        }
        else
        {
            RenderSystem.color3f(0.6f, 0.6f, 0.6f);
        }

        if (taskControl.task.isConfigured() && taskControl.task.getGoal().getState() == BlocklingGoal.State.DISABLED)
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
            if (taskControl.task.isConfigured())
            {
                if (taskControl.task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                {
                    taskControl.task.getGoal().setState(BlocklingGoal.State.IDLE);
                }
                else
                {
                    taskControl.task.getGoal().setState(BlocklingGoal.State.DISABLED);
                }
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
