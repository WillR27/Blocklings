package com.willr27.blocklings.entity.entities.blockling.goal;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.TaskPriorityMessage;
import com.willr27.blocklings.network.messages.TaskSetCustomNameMessage;
import com.willr27.blocklings.network.messages.TaskSetTypeMessage;
import com.willr27.blocklings.network.messages.TaskSwapPriorityMessage;

import java.util.UUID;

public class Task
{
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

        this.type = type;

        goal = type.createGoal.apply(id, blockling, tasks);

        tasks.reapplyGoals();

        if (sync)
        {
            NetworkHandler.sync(blockling.level, new TaskSetTypeMessage(id, type.id, blockling.getId()));
        }
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
            NetworkHandler.sync(blockling.level, new TaskSetCustomNameMessage(id, customName, blockling.getId()));
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
            NetworkHandler.sync(blockling.level, new TaskSwapPriorityMessage(id, task.id, blockling.getId()));
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
            NetworkHandler.sync(blockling.level, new TaskPriorityMessage(id, priority, blockling.getId()));
        }
    }
}
