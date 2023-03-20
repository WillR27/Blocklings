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
import javax.annotation.Nullable;
import java.util.UUID;

public class Task
{
    /**
     * Invoked when the task's type changes.
     */
    @Nonnull
    public final EventHandler<TypeChangedEvent> onTypeChanged = new EventHandler<>();

    /**
     * The task's id.
     */
    @Nonnull
    public final UUID id;

    /**
     * The associated blockling.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The associated blockling tasks.
     */
    @Nonnull
    public final BlocklingTasks tasks;

    /**
     * The task's type.
     */
    @Nonnull
    private TaskType type;

    /**
     * The task's custom name.
     */
    @Nonnull
    private String customName = "";

    /**
     * The task's goal.
     */
    @Nullable
    private BlocklingGoal goal;

    /**
     * @param id the task's id.
     * @param type the task's type.
     * @param blockling the associated blockling.
     * @param tasks the associated blockling tasks.
     */
    public Task(@Nonnull UUID id, @Nonnull TaskType type, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        this.id = id != null ? id : UUID.randomUUID();
        this.blockling = blockling;
        this.tasks = tasks;

        setType(type, false);
    }

    /**
     * @return whether the task is configured.
     */
    public boolean isConfigured()
    {
        return type != BlocklingTasks.NULL;
    }

    /**
     * @return whether the task is active.
     */
    @Nonnull
    public TaskType getType()
    {
        return type;
    }

    /**
     * Sets the task's type.
     *
     * @param type the new type.
     */
    public void setType(@Nonnull TaskType type)
    {
        setType(type, true);
    }

    /**
     * Sets the task's type.
     *
     * @param type the new type.
     * @param sync whether to sync the change with the client/server.
     */
    public void setType(@Nonnull TaskType type, boolean sync)
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

    /**
     * @return the task's underlying custom name text, not necessarily what is displayed.
     */
    @Nonnull
    public String getActualCustomName()
    {
        return customName;
    }

    /**
     * @return the task's custom name, or the task's type name if no custom name is set.
     */
    @Nonnull
    public String getCustomName()
    {
        return !customName.equals("") ? customName : type.name.getString();
    }

    /**
     * Sets the task's custom name.
     *
     * @param customName the new custom name.
     */
    public void setCustomName(@Nonnull String customName)
    {
        setCustomName(customName, true);
    }

    /**
     * Sets the task's custom name.
     *
     * @param customName the new custom name.
     * @param sync whether to sync the change with the client/server.
     */
    public void setCustomName(@Nonnull String customName, boolean sync)
    {
        this.customName = customName;

        if (sync)
        {
            new TaskCustomNameMessage(blockling, id, customName).sync();
        }
    }

    /**
     * @return the task's goal.
     */
    @Nullable
    public BlocklingGoal getGoal()
    {
        return goal;
    }

    /**
     * @return the task's priority.
     */
    public int getPriority()
    {
        return tasks.getTaskPriority(this);
    }

    /**
     * Swaps the task's priority with another task.
     *
     * @param task the task to swap with.
     */
    public void swapPriority(@Nonnull Task task)
    {
        swapPriority(task, true);
    }

    /**
     * Swaps the task's priority with another task.
     *
     * @param task the task to swap with.
     * @param sync whether to sync the change with the client/server.
     */
    public void swapPriority(@Nonnull Task task, boolean sync)
    {
        tasks.swapTaskPriorities(this, task);

        if (sync)
        {
            new TaskSwapPriorityMessage(blockling, id, task.id).sync();
        }
    }

    /**
     * Sets the task's priority.
     *
     * @param priority the new priority.
     */
    public void setPriority(int priority)
    {
        setPriority(priority, true);
    }

    /**
     * Sets the task's priority.
     *
     * @param priority the new priority.
     * @param sync whether to sync the change with the client/server.
     */
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
