package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.task.Task;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * A control used to display a task's and the configure icon.
 */
@OnlyIn(Dist.CLIENT)
public class TaskIconControl extends Control
{
    /**
     * The width of the icon.
     */
    public static final int TASK_ICON_WIDTH = BlocklingTasks.NULL.texture.width;

    /**
     * The height of the icon.
     */
    public static final int TASK_ICON_HEIGHT = BlocklingTasks.NULL.texture.height;

    /**
     * The icon background texture.
     */
    @Nonnull
    public static final GuiTexture BACKGROUND_TEXTURE = new GuiTexture(GuiTextures.TASKS, 0, 166, TASK_ICON_WIDTH, TASK_ICON_HEIGHT);

    /**
     * The icon background texture when pressed.
     */
    @Nonnull
    public static final GuiTexture BACKGROUND_PRESSED_TEXTURE = new GuiTexture(GuiTextures.TASKS, 20, 166, TASK_ICON_WIDTH, TASK_ICON_HEIGHT);

    /**
     * The configure icon texture.
     */
    @Nonnull
    public static final GuiTexture CONFIGURE_TEXTURE = BlocklingTasks.NULL.texture;

    /**
     * The parent task control.
     */
    @Nonnull
    public final TaskControl taskControl;

    /**
     * Whether the control is add or remove.
     */
    public final boolean isCreateControl;

    /**
     * The consumer called when the configure button is pressed.
     */
    @Nonnull
    public final Consumer<Task> onConfigure;

    /**
     * @param parentTaskControl the parent task control.
     * @param isAddControl whether the control is add or remove.
     * @param onConfigure the callback called when the configure button is pressed.
     */
    public TaskIconControl(@Nonnull TaskControl parentTaskControl, boolean isAddControl, @Nonnull Consumer<Task> onConfigure)
    {
        super(parentTaskControl, 0, 0, TASK_ICON_WIDTH, TASK_ICON_HEIGHT);
        this.taskControl = parentTaskControl;
        this.isCreateControl = isAddControl;
        this.onConfigure = onConfigure;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (isPressed() || isCreateControl)
        {
            RenderSystem.color3f(0.8f, 0.8f, 0.8f);

            renderTexture(matrixStack, BACKGROUND_PRESSED_TEXTURE);
        }
        else
        {
            renderTexture(matrixStack, BACKGROUND_TEXTURE);
        }

        if (isPressed() || isMouseOver(mouseX, mouseY))
        {
            renderTexture(matrixStack, CONFIGURE_TEXTURE);
        }
        else
        {
            renderTexture(matrixStack, taskControl.task.getType().texture);
        }

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        if (isMouseOver(mouseX, mouseY))
        {
            onConfigure.accept(taskControl.task);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
