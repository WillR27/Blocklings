package com.willr27.blocklings.client.gui.control.controls.tasks;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Direction;
import com.willr27.blocklings.client.gui.control.Fill;
import com.willr27.blocklings.client.gui.control.Orientation;
import com.willr27.blocklings.client.gui.control.controls.DropDownControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui2.IControl;
import com.willr27.blocklings.client.gui2.controls.common.LabelControl;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.entity.blockling.task.TaskType;
import com.willr27.blocklings.entity.blockling.task.config.Property;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Contains all the miscellaneous configuration options.
 */
@OnlyIn(Dist.CLIENT)
public class MiscConfigControl extends ConfigControl
{
    /**
     * @param task the task being configured.
     */
    public MiscConfigControl(@Nonnull Task task)
    {
        super(task);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setParent(this);
        flowPanel.setWidth(new Fill(1.0f));
        flowPanel.setHeight(new Fill(1.0f));
        flowPanel.setPadding(4, 8, 4, 4);
        flowPanel.setFlowDirection(Direction.TOP_TO_BOTTOM);
        flowPanel.setOverflowOrientation(Orientation.VERTICAL);
        flowPanel.setFitMaxScrollOffsetToOverflowY(true);
        flowPanel.setScrollableY(true);

        TextBlockControl typeNameControl = new TextBlockControl();
        typeNameControl.setParent(flowPanel);
        typeNameControl.setWidth(new Fill(1.0f));
        typeNameControl.setFitToContentsY(true);
        typeNameControl.setText(new BlocklingsTranslationTextComponent("task.ui.task_type").getString());

        DropDownControl typeDropDownControl = new DropDownControl();
        typeDropDownControl.setParent(flowPanel);
        typeDropDownControl.setWidth(new Fill(1.0f));

        for (TaskType taskType : BlocklingTasks.TASK_TYPES)
        {
            typeDropDownControl.addItem(new TaskTypeItem(taskType));
        }

//        if (task.isConfigured())
//        {
//            BlocklingGoal goal = task.getGoal();
//
//            for (Property property : goal.properties)
//            {
//                new LabelControl(stackPanel, width - stackPanel.getPadding(IControl.Side.LEFT) - stackPanel.getPadding(IControl.Side.RIGHT), property.name.getString()).setMargins(0, 11, 0, 3);
//                property.createControl(stackPanel).setMargins(0, 0, 0, 0);
//            }
//        }
    }

    /**
     * Wraps a task type in an item.
     */
    private class TaskTypeItem extends DropDownControl.Item
    {
        /**
         * The task type.
         */
        @Nonnull
        private final TaskType taskType;

        /**
         * @param taskType the task type.
         */
        public TaskTypeItem(@Nonnull TaskType taskType)
        {
            super();
            this.taskType = taskType;

            setIconTexture(taskType.texture);
        }

        @Override
        protected void onSelected()
        {

        }

        @Override
        protected void onUnselected()
        {

        }

        @Override
        @Nonnull
        public String toString()
        {
            return taskType.name.getString();
        }
    }
}
