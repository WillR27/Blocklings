package com.willr27.blocklings.entity.blockling.task;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.network.messages.TaskCustomNameMessage;
import com.willr27.blocklings.network.messages.TaskPriorityMessage;
import com.willr27.blocklings.network.messages.TaskSwapPriorityMessage;
import com.willr27.blocklings.network.messages.TaskTypeMessage;
import com.willr27.blocklings.util.event.EventHandler;
import com.willr27.blocklings.util.event.HandleableEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

public class Task
{
    /**
     * Invoked when the task's type changes.
     */
    @Nonnull
    public final EventHandler<TypeChangedEvent> onTypeChanged = new EventHandler<>();

    public final UUID id;
    public final BlocklingEntity blockling;
    public final BlocklingTasks tasks;

    private TaskType type;
    private String customName = "";

    private BlocklingGoal goal;

    public Task(UUID id, TaskType type, BlocklingEntity blockling, BlocklingTasks tasks)
    {
        this.id = id != null ? id : UUID.randomUUID();
        this.blockling = blockling;
        this.tasks = tasks;

        setType(type, false);
    }

    public boolean isConfigured()
    {
        return type != BlocklingTasks.NULL;
    }

    public TaskType getType()
    {
        return type;
    }

    public void setType(TaskType type)
    {
        setType(type, true);
    }

    public void setType(TaskType type, boolean sync)
    {
        if (this.type == type)
        {
            return;
        }

        TaskType prevType = this.type;

        this.type = type;

        goal = type.createGoal.apply(id, blockling, tasks);

        tasks.reapplyGoals();

        if (sync)
        {
            new TaskTypeMessage(blockling, id, type.id).sync();
        }

        onTypeChanged.handle(new TypeChangedEvent(this, prevType));
    }

    public String getActualCustomName()
    {
        return customName;
    }

    public String getCustomName()
    {
        return !customName.equals("") ? customName : type.name.getString();
    }

    public void setCustomName(String customName)
    {
        setCustomName(customName, true);
    }

    public void setCustomName(String customName, boolean sync)
    {
        this.customName = customName;

        if (sync)
        {
            new TaskCustomNameMessage(blockling, id, customName).sync();
        }
    }

    public BlocklingGoal getGoal()
    {
        return goal;
    }

    public int getPriority()
    {
        return tasks.getTaskPriority(this);
    }

    public void swapPriority(Task task)
    {
        swapPriority(task, true);
    }

    public void swapPriority(Task task, boolean sync)
    {
        tasks.swapTaskPriorities(this, task);

        if (sync)
        {
            new TaskSwapPriorityMessage(blockling, id, task.id).sync();
        }
    }

    public void setPriority(int priority)
    {
        setPriority(priority, true);
    }

    public void setPriority(int priority, boolean sync)
    {
        tasks.setGoalPriority(this, priority);

        if (sync)
        {
            new TaskPriorityMessage(blockling, id, priority).sync();
        }
    }

    /**
     * Occurs when the task's type changes.
     */
    public class TypeChangedEvent extends HandleableEvent
    {
        /**
         * The associated task.
         */
        @Nonnull
        public final Task task;

        /**
         * The previous task type.
         */
        @Nonnull
        public final TaskType prevType;

        /**
         * @param task the associated task.
         * @param prevType the previous task type.
         */
        public TypeChangedEvent(@Nonnull Task task, @Nonnull TaskType prevType)
        {
            this.task = task;
            this.prevType = prevType;
        }
    }
}
