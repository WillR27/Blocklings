package com.willr27.blocklings.gui.controls.tasks.config.configs;

import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.common.LabelControl;
import com.willr27.blocklings.gui.controls.common.RangeControl;
import com.willr27.blocklings.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.gui.controls.common.DropdownControl;
import com.willr27.blocklings.gui.controls.common.panel.StackPanel;
import com.willr27.blocklings.gui.controls.tasks.config.ConfigControl;
import com.willr27.blocklings.gui.controls.tasks.config.TaskConfigContainerControl;
import com.willr27.blocklings.task.BlocklingTasks;
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
 * A control to handle the miscellaneous configuration of a task.
 */
@OnlyIn(Dist.CLIENT)
public class TaskMiscConfigControl extends ConfigControl
{
    /**
     * The scrollbar control to use.
     */
    @Nonnull
    private final ScrollbarControl contentScrollbarControl;

    /**
     * The parent task config control.
     */
    @Nonnull
    private final TaskConfigContainerControl taskConfigControl;

    /**
     * The dropdown used to select the task's type.
     */
    @Nonnull
    private final DropdownControl taskTypeDropdownControl;

    /**
     * @param parentTaskConfigContainerControl the parent control.
     * @param task the task being configured.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     * @param height the height.
     * @param contentScrollbarControl the scrollbar control to use.
     */
    public TaskMiscConfigControl(@Nonnull TaskConfigContainerControl parentTaskConfigContainerControl, @Nonnull Task task, int x, int y, int width, int height, @Nonnull ScrollbarControl contentScrollbarControl)
    {
        super(parentTaskConfigContainerControl, x, y, width, height);
        this.contentScrollbarControl = contentScrollbarControl;
        this.taskConfigControl = parentTaskConfigContainerControl;

        List<TaskType> unlockedTaskTypes = BlocklingTasks.TASK_TYPES.stream().filter(taskType -> task.blockling.getTasks().isUnlocked(taskType)).collect(Collectors.toList());

        StackPanel stackPanel = new StackPanel(this, 0, 0, width, height);
        stackPanel.setPadding(4, 9, 4, 4);
        stackPanel.setScrollbarY(contentScrollbarControl);

        LabelControl taskTypeLabelControl = new LabelControl(stackPanel, width - stackPanel.getPadding(Side.LEFT) - stackPanel.getPadding(Side.RIGHT), "Task Type");
        taskTypeLabelControl.setMargins(0, 0, 0, 3);

        taskTypeDropdownControl = new DropdownControl(stackPanel, 0, 0, width - 8);
        taskTypeDropdownControl.setMargins(0, 0, 0, 0);
        taskTypeDropdownControl.addItems(unlockedTaskTypes.stream().map(taskType -> new DropdownControl.Item(taskType, taskType.texture, createTooltip(taskType))).collect(Collectors.toList()));
        taskTypeDropdownControl.setSelectedItem(new DropdownControl.Item(task.getType(), task.getType().texture, createTooltip(task.getType())));
        taskTypeDropdownControl.onDropDownSelectionChanged.subscribe((e) ->
                {
                    if (e.newSelection != null)
                    {
                        TaskType newTaskType = (TaskType) e.newSelection.item;

                        if (newTaskType != task.getType())
                        {
                            task.setType(newTaskType);
                            taskConfigControl.recreateTabs();
                        }
                    }
                });

        RangeControl rangeControl = new RangeControl(stackPanel, 1, 10, 20);
    }

    /**
     * @return the tooltip for the given task type.
     */
    @Nonnull
    public List<IReorderingProcessor> createTooltip(@Nonnull TaskType taskType)
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();
        tooltip.add(new StringTextComponent(TextFormatting.GOLD + taskType.name.getString()).getVisualOrderText());
        tooltip.add(new StringTextComponent("").getVisualOrderText());
        tooltip.addAll(GuiUtil.splitText(font, taskType.desc.getString(), 150).stream().map(s -> new StringTextComponent(s).getVisualOrderText()).collect(Collectors.toList()));

        return tooltip;
    }
}
