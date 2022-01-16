package com.willr27.blocklings.gui.widgets.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.widgets.TexturedControl;
import com.willr27.blocklings.gui.widgets.Widget;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;
import java.util.function.Consumer;

public class TaskWidget extends Widget
{
    public static final int WIDTH = 136;
    public static final int HEIGHT = 20;

    public final Task task;
    public final List<TaskWidget> taskWidgets;
    public final boolean isCreateWidget;

    public boolean isDragging = false;
    private boolean isTryingToDrag = false;
    private int dragStartY;

    private TexturedControl titleWidget;
    private TaskIconWidget iconWidget;
    private TaskStateWidget stateWidget;
    private TaskAddRemoveWidget addRemoveWidget;

    public TaskWidget(Task task, FontRenderer font, int x, int y, List<TaskWidget> taskWidgets, boolean isCreateWidget, Consumer<Task> onConfigure)
    {
        super(font, x, y, WIDTH, HEIGHT);
        this.task = task;
        this.taskWidgets = taskWidgets;
        this.isCreateWidget = isCreateWidget;

        titleWidget = new TexturedControl(font, x + 20, y, new GuiTexture(GuiTextures.TASKS, 40, 166, 96, HEIGHT));
        iconWidget = new TaskIconWidget(this, font, isCreateWidget, onConfigure);
        stateWidget = new TaskStateWidget(this, font);
        addRemoveWidget = new TaskAddRemoveWidget(this, font, isCreateWidget);
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

            y = Math.min(taskWidgets.get(taskWidgets.size() - 1).screenY, Math.max(taskWidgets.get(0).screenY, mouseY - height / 2));

            TaskWidget closestTaskWidget = null;
            int closestDifY = Integer.MAX_VALUE;
            int closestAbsDifY = Integer.MAX_VALUE;
            int midY = y + height / 2;

            for (TaskWidget taskWidget : taskWidgets)
            {
                int testMidY = taskWidget.screenY + taskWidget.height / 2;
                int difY = testMidY - midY;
                int difAbsY = Math.abs(difY);

                if (difAbsY < closestAbsDifY)
                {
                    closestDifY = difY;
                    closestAbsDifY = difAbsY;
                    closestTaskWidget = taskWidget;
                }
            }

            if (this != closestTaskWidget)
            {
                if (closestDifY > 0)
                {
                    task.setPriority(closestTaskWidget.task.getPriority());
                }
                else if (closestDifY < 0)
                {
                    closestTaskWidget.task.setPriority(task.getPriority());
                }
            }
        }

        titleWidget.screenX = screenX + 20;
        titleWidget.screenY = y;
        iconWidget.screenX = screenX;
        iconWidget.screenY = y;
        stateWidget.screenX = screenX + width - 42;
        stateWidget.screenY = y;
        addRemoveWidget.screenX = screenX + width - 20;
        addRemoveWidget.screenY = y;

        RenderSystem.color3f(0.8f, 0.8f, 0.8f);
        titleWidget.render(matrixStack, mouseX, mouseY);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        iconWidget.render(matrixStack, mouseX, mouseY);
        stateWidget.render(matrixStack, mouseX, mouseY);
        addRemoveWidget.render(matrixStack, mouseX, mouseY);

        font.drawShadow(matrixStack, GuiUtil.trimWithEllipses(font, task.getCustomName(), width - 65), screenX + iconWidget.width + 4, y + 6, 0xffffff);
        RenderSystem.enableDepthTest();

        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        iconWidget.mouseClickedNoHandle(mouseX, mouseY, button);
        stateWidget.mouseClickedNoHandle(mouseX, mouseY, button);
        addRemoveWidget.mouseClickedNoHandle(mouseX, mouseY, button);

        if (isMouseOver(mouseX, mouseY) && !addRemoveWidget.isMouseOver(mouseX, mouseY) && taskWidgets.size() > 1 && !isCreateWidget)
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
        iconWidget.mouseReleased(mouseX, mouseY, button);
        stateWidget.mouseReleased(mouseX, mouseY, button);
        addRemoveWidget.mouseReleased(mouseX, mouseY, button);

        isTryingToDrag = false;
        isDragging = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
