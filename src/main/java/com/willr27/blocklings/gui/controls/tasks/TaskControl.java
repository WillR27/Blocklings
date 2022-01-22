package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.*;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.gui.controls.TexturedControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

/**
 * A control to display a task in the task list.
 */
@OnlyIn(Dist.CLIENT)
public class TaskControl extends Control
{
    /**
     * The width of the control.
     */
    public static final int WIDTH = 136;

    /**
     * The height of the control.
     */
    public static final int HEIGHT = 20;

    /**
     * The task to display.
     */
    @Nonnull
    public final Task task;

    /**
     * The task controls being displayed.
     */
    @Nonnull
    public final List<TaskControl> taskControls;

    /**
     * Whether the control is add or remove.
     */
    public final boolean isAddControl;

    /**
     * Whether the control is currently being dragged.
     */
    public boolean isDragging = false;

    /**
     * Whether we are attempting to drag the control.
     */
    private boolean isTryingToDrag = false;

    /**
     * The y position where a drag was started.
     */
    private int dragStartY;

    /**
     * The task name control.
     */
    @Nonnull
    private final TexturedControl nameControl;

    /**
     * The task icon control.
     */
    @Nonnull
    private final TaskIconControl iconControl;

    /**
     * The task state control.
     */
    @Nonnull
    private final TaskStateControl stateControl;

    /**
     * The task add/remove control.
     */
    @Nonnull
    private final TaskAddRemoveControl addRemoveControl;

    /**
     * @param parent the parent control.
     * @param task the task to display.
     * @param x the x position of the control.
     * @param y the y position of the control.
     * @param taskControls the task controls being displayed.
     * @param isAddControl whether the control is add or remove.
     * @param onConfigure the callback called when the configure button is pressed.
     */
    public TaskControl(@Nonnull IControl parent, @Nonnull Task task, int x, int y, @Nonnull List<TaskControl> taskControls, boolean isAddControl, @Nonnull Consumer<Task> onConfigure)
    {
        super(parent, x, y, WIDTH, HEIGHT);
        this.task = task;
        this.taskControls = taskControls;
        this.isAddControl = isAddControl;

        nameControl = new TexturedControl(this, x + 20, y, new GuiTexture(GuiTextures.TASKS, 40, 166, 96, HEIGHT));
        iconControl = new TaskIconControl(this, isAddControl, onConfigure);
        stateControl = new TaskStateControl(this);
        addRemoveControl = new TaskAddRemoveControl(this, isAddControl);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        matrixStack.pushPose();

        int y = this.screenY;

        if (isTryingToDrag)
        {
            if (Math.abs(dragStartY - mouseY) > 4)
            {
                isDragging = true;
                isTryingToDrag = false;
            }
        }
        else if (isDragging)
        {
            matrixStack.translate(0.0f, 0.0f, 10.0f);

            y = Math.min(taskControls.get(taskControls.size() - 1).screenY, Math.max(taskControls.get(0).screenY, mouseY - height / 2));

            TaskControl closestTaskControl = null;
            int closestDifY = Integer.MAX_VALUE;
            int closestAbsDifY = Integer.MAX_VALUE;
            int midY = y + height / 2;

            for (TaskControl taskControl : taskControls)
            {
                int testMidY = taskControl.screenY + taskControl.height / 2;
                int difY = testMidY - midY;
                int difAbsY = Math.abs(difY);

                if (difAbsY < closestAbsDifY)
                {
                    closestDifY = difY;
                    closestAbsDifY = difAbsY;
                    closestTaskControl = taskControl;
                }
            }

            if (this != closestTaskControl)
            {
                if (closestDifY > 0)
                {
                    task.setPriority(closestTaskControl.task.getPriority());
                }
                else if (closestDifY < 0)
                {
                    closestTaskControl.task.setPriority(task.getPriority());
                }
            }
        }

        nameControl.screenX = screenX + 20;
        nameControl.screenY = y;
        iconControl.screenX = screenX;
        iconControl.screenY = y;
        stateControl.screenX = screenX + width - 42;
        stateControl.screenY = y;
        addRemoveControl.screenX = screenX + width - 20;
        addRemoveControl.screenY = y;

        RenderSystem.color3f(0.8f, 0.8f, 0.8f);
        nameControl.render(matrixStack, mouseX, mouseY);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        iconControl.render(matrixStack, mouseX, mouseY);
        stateControl.render(matrixStack, mouseX, mouseY);
        addRemoveControl.render(matrixStack, mouseX, mouseY);

        font.drawShadow(matrixStack, GuiUtil.trimWithEllipses(font, task.getCustomName(), width - 65), screenX + iconControl.width + 4, y + 6, 0xffffff);
        RenderSystem.enableDepthTest();

        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        iconControl.mouseClickedNoHandle(mouseX, mouseY, button);
        stateControl.mouseClickedNoHandle(mouseX, mouseY, button);
        addRemoveControl.mouseClickedNoHandle(mouseX, mouseY, button);

        if (isMouseOver(mouseX, mouseY) && !addRemoveControl.isMouseOver(mouseX, mouseY) && taskControls.size() > 1 && !isAddControl)
        {
            isTryingToDrag = true;
            dragStartY = mouseY;

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        iconControl.mouseReleased(mouseX, mouseY, button);
        stateControl.mouseReleased(mouseX, mouseY, button);
        addRemoveControl.mouseReleased(mouseX, mouseY, button);

        isTryingToDrag = false;
        isDragging = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
