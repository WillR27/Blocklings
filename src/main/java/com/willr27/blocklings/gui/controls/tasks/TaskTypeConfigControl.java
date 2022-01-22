package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.controls.TexturedControl;
import com.willr27.blocklings.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.task.TaskType;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A control to handle the configuration of a task's type.
 */
@OnlyIn(Dist.CLIENT)
public class TaskTypeConfigControl extends ConfigControl
{
    /**
     * The gap between each task type.
     */
    public static final int TASK_TYPE_GAP = 5;

    /**
     * The scrollbar control to use.
     */
    @Nonnull
    private final ScrollbarControl contentScrollbarControl;

    /**
     * The parent task config control.
     */
    @Nonnull
    private final TaskConfigControl taskConfigGui;

    /**
     * The list of all the task type controls.
     */
    @Nonnull
    private final List<TaskTypeControl> taskTypeControls = new ArrayList<>();

    /**
     * @param parentTaskConfigControl the parent control.
     * @param task the task being configured.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     * @param height the height.
     * @param contentScrollbarControl the scrollbar control to use.
     */
    public TaskTypeConfigControl(@Nonnull TaskConfigControl parentTaskConfigControl, @Nonnull Task task, int x, int y, int width, int height, @Nonnull ScrollbarControl contentScrollbarControl)
    {
        super(parentTaskConfigControl, x, y, width, height);
        this.contentScrollbarControl = contentScrollbarControl;
        this.taskConfigGui = parentTaskConfigControl;

        List<TaskType> unlockedTaskTypes = BlocklingTasks.TASK_TYPES.stream().filter(taskType -> task.blockling.getTasks().isUnlocked(taskType)).collect(Collectors.toList());

        for (int i = 0; i < unlockedTaskTypes.size(); i++)
        {
            taskTypeControls.add(new TaskTypeControl(this, task, unlockedTaskTypes.get(i), TASK_TYPE_GAP + ((i % 3) * (TaskTypeControl.WIDTH + TASK_TYPE_GAP)), TASK_TYPE_GAP + ((i / 3) * (TaskTypeControl.HEIGHT + TASK_TYPE_GAP))));
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        updateTaskTypePositions();

        for (TaskTypeControl taskTypeControl : taskTypeControls)
        {
            taskTypeControl.render(matrixStack, mouseX, mouseY);
        }
    }

    /**
     * Renders the tooltips.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y potision.
     */
    public void renderTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        TaskTypeControl hoveredTaskTypeControl = getHoveredTypeControl(mouseX, mouseY);

        if (hoveredTaskTypeControl != null)
        {
            List<IReorderingProcessor> text = new ArrayList<>();
            text.add(new StringTextComponent(TextFormatting.GOLD + hoveredTaskTypeControl.taskType.name.getString()).getVisualOrderText());
            text.add(new StringTextComponent("").getVisualOrderText());
            text.addAll(GuiUtil.splitText(font, hoveredTaskTypeControl.taskType.desc.getString(), 150).stream().map(s -> new StringTextComponent(s).getVisualOrderText()).collect(Collectors.toList()));

            screen.renderTooltip(matrixStack, text, mouseX, mouseY);
        }
    }

    /**
     * Updates the task type positions based on the scrollbar.
     */
    private void updateTaskTypePositions()
    {
        contentScrollbarControl.isDisabled = true;

        for (int i = 0; i < taskTypeControls.size(); i++)
        {
            TaskTypeControl taskTypeControl = taskTypeControls.get(i);
            taskTypeControl.setX(TASK_TYPE_GAP + ((i % 3) * (TaskTypeControl.WIDTH + TASK_TYPE_GAP)));
            taskTypeControl.setY(TASK_TYPE_GAP + ((i / 3) * (TaskTypeControl.HEIGHT + TASK_TYPE_GAP)));
        }

        if (taskTypeControls.size() >= 2)
        {
            int taskControlsHeight = taskTypeControls.get(taskTypeControls.size() - 1).screenY + taskTypeControls.get(taskTypeControls.size() - 1).height - taskTypeControls.get(0).screenY + TASK_TYPE_GAP * 2;
            int taskControlsHeightDif = taskControlsHeight - height;

            if (taskControlsHeightDif > 0)
            {
                contentScrollbarControl.isDisabled = false;

                for (int i = 0; i < taskTypeControls.size(); i++)
                {
                    TaskTypeControl taskTypeControl = taskTypeControls.get(i);
                    taskTypeControl.setY(TASK_TYPE_GAP + ((i / 3) * (TaskTypeControl.HEIGHT + TASK_TYPE_GAP)) - (int) (taskControlsHeightDif * contentScrollbarControl.percentageScrolled()));
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if (taskTypeControls.stream().map(taskTypeControl -> taskTypeControl.mouseClicked((int) mouseX, (int) mouseY, button)).filter(b -> b).findAny().isPresent())
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        boolean ret = false;

        if (taskTypeControls.stream().map(taskTypeControl -> taskTypeControl.mouseReleased((int) mouseX, (int) mouseY, button)).filter(b -> b).findAny().isPresent())
        {
            taskConfigGui.recreateTabs();

            ret = true;
        }

        return ret;
    }

    /**
     * @return the task type control underneath the mouse, else null.
     */
    private TaskTypeControl getHoveredTypeControl(int mouseX, int mouseY)
    {
        return taskTypeControls.stream().filter(taskTypeControl -> taskTypeControl.isMouseOver(mouseX, mouseY)).findFirst().orElse(null);
    }

    /**
     * A control to display a task type.
     */
    private static class TaskTypeControl extends TexturedControl
    {
        /**
         * The width of the control.
         */
        public static final int WIDTH = 40;

        /**
         * The height of the control.
         */
        public static final int HEIGHT = 20;

        /**
         * The radio button texture.
         */
        @Nonnull
        private static final GuiTexture RADIO_TEXTURE = new GuiTexture(GuiTextures.TASKS, 196, 166, 20, HEIGHT);

        /**
         * The selected radio button texture.
         */
        @Nonnull
        private static final GuiTexture RADIO_SELECTED_TEXTURE = new GuiTexture(GuiTextures.TASKS, 196, 166, 20, HEIGHT);

        /**
         * The underlying task.
         */
        @Nonnull
        public final Task task;

        /**
         * The task type to display.
         */
        @Nonnull
        public final TaskType taskType;

        public TaskTypeControl(@Nonnull IControl parent, @Nonnull Task task, @Nonnull TaskType taskType, int x, int y)
        {
            super(parent, x, y, new GuiTexture(GuiTextures.TASK_CONFIGURE, 0, 166, WIDTH, HEIGHT));
            this.task = task;
            this.taskType = taskType;
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
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
        public boolean mouseReleased(int mouseX, int mouseY, int button)
        {
            if (isPressed() && isMouseOver(mouseX, mouseY))
            {
                task.setType(taskType);

                return true;
            }

            return super.mouseReleased(mouseX, mouseY, button);
        }
    }
}
