package com.willr27.blocklings.client.gui3.control.controls.tasks;

import com.willr27.blocklings.client.gui3.control.*;
import com.willr27.blocklings.client.gui3.control.controls.DropDownControl;
import com.willr27.blocklings.client.gui3.control.controls.TextBlockControl;
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
     * The type drop down control.
     */
    @Nonnull
    private final DropDownControl typeDropDownControl;

    /**
     * @param task the task being configured.
     */
    public MiscConfigControl(@Nonnull Task task)
    {
        super(task);

        TextBlockControl typeNameControl = new TextBlockControl();
        typeNameControl.setParent(this);
        typeNameControl.setWidth(new Fill(1.0f));
        typeNameControl.setFitToContentsY(true);
        typeNameControl.setText(new BlocklingsTranslationTextComponent("task.ui.task_type").getString());

        typeDropDownControl = new DropDownControl();
        typeDropDownControl.setParent(this);
        typeDropDownControl.setWidth(new Fill(1.0f));
        typeDropDownControl.setMargin(Side.TOP, 4);

        for (TaskType taskType : BlocklingTasks.TASK_TYPES)
        {
            if (taskType == task.getType())
            {
                typeDropDownControl.setSelectedItem(new TaskTypeItem(taskType));
            }
            else
            {
                typeDropDownControl.addItem(new TaskTypeItem(taskType));
            }
        }

        if (task.isConfigured())
        {
            BlocklingGoal goal = task.getGoal();

            for (Property property : goal.properties)
            {
                TextBlockControl nameControl = new TextBlockControl();
                nameControl.setParent(this);
                nameControl.setWidth(new Fill(1.0f));
                nameControl.setFitToContentsY(true);
                nameControl.setText(property.name);

                Control propertyControl = property.createControl();
                propertyControl.setParent(this);
                propertyControl.setMargin(Side.TOP, 4);
            }
        }
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
            task.setType(taskType);
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