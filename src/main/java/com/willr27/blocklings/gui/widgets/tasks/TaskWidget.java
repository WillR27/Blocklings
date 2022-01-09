package com.willr27.blocklings.gui.widgets.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
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

    private TexturedWidget titleWidget;
    private TaskIconWidget iconWidget;
    private TaskStateWidget stateWidget;
    private TaskAddRemoveWidget addRemoveWidget;

    public TaskWidget(Task task, FontRenderer font, int x, int y, List<TaskWidget> taskWidgets, boolean isCreateWidget, Consumer<Task> onConfigure)
    {
        super(font, x, y, WIDTH, HEIGHT);
        this.task = task;
        this.taskWidgets = taskWidgets;
        this.isCreateWidget = isCreateWidget;

        titleWidget = new TexturedWidget(font, x + 20, y, new GuiTexture(GuiUtil.TASKS, 40, 166, 96, HEIGHT));
        iconWidget = new TaskIconWidget(this, font, isCreateWidget, onConfigure);
        stateWidget = new TaskStateWidget(this, font);
        addRemoveWidget = new TaskAddRemoveWidget(this, font, isCreateWidget);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        matrixStack.pushPose();

        int y = this.y;

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

            y = Math.min(taskWidgets.get(taskWidgets.size() - 1).y, Math.max(taskWidgets.get(0).y, mouseY - height / 2));

            TaskWidget closestTaskWidget = null;
            int closestDifY = Integer.MAX_VALUE;
            int closestAbsDifY = Integer.MAX_VALUE;
            int midY = y + height / 2;

            for (TaskWidget taskWidget : taskWidgets)
            {
                int testMidY = taskWidget.y + taskWidget.height / 2;
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

        titleWidget.x = x + 20;
        titleWidget.y = y;
        iconWidget.x = x;
        iconWidget.y = y;
        stateWidget.x = x + width - 42;
        stateWidget.y = y;
        addRemoveWidget.x = x + width - 20;
        addRemoveWidget.y = y;

        RenderSystem.color3f(0.8f, 0.8f, 0.8f);
        titleWidget.render(matrixStack, mouseX, mouseY);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        iconWidget.render(matrixStack, mouseX, mouseY);
        stateWidget.render(matrixStack, mouseX, mouseY);
        addRemoveWidget.render(matrixStack, mouseX, mouseY);

        font.drawShadow(matrixStack, GuiUtil.trimWithEllipses(font, task.getCustomName(), width - 65), x + iconWidget.width + 4, y + 6, 0xffffff);
        RenderSystem.enableDepthTest();

        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int state)
    {
        iconWidget.mouseClicked(mouseX, mouseY, state);
        stateWidget.mouseClicked(mouseX, mouseY, state);
        addRemoveWidget.mouseClicked(mouseX, mouseY, state);

        if (isMouseOver(mouseX, mouseY) && !addRemoveWidget.isMouseOver(mouseX, mouseY) && taskWidgets.size() > 1 && !isCreateWidget)
        {
            isTryingToDrag = true;
            dragStartY = mouseY;

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        iconWidget.mouseReleased(mouseX, mouseY, state);
        stateWidget.mouseReleased(mouseX, mouseY, state);
        addRemoveWidget.mouseReleased(mouseX, mouseY, state);

        isTryingToDrag = false;
        isDragging = false;

        return super.mouseReleased(mouseX, mouseY, state);
    }
}
