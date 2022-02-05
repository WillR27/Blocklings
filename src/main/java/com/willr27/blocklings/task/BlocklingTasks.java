package com.willr27.blocklings.task;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.IHasTargetGoal;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.goal.goals.*;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.task.TaskType;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.network.messages.TaskCreateMessage;
import com.willr27.blocklings.network.messages.TaskRemoveMessage;
import com.willr27.blocklings.network.messages.TaskTypeIsUnlockedMessage;
import com.willr27.blocklings.util.PacketBufferUtils;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.*;
import java.util.stream.Collectors;

public class BlocklingTasks
{
    public static final TaskType NULL = new TaskType("1c330075-19af-4c12-ac20-6de50e7b84a9", "null", false, false, new GuiTexture(GuiTextures.TASKS, 176, 166, 20, 20), ((i, b, t) -> null));
    public static final TaskType MELEE_ATTACK_HURT_BY = new TaskType("2888dde5-f6ee-439d-ab8d-ea9a91470c64", "hurt_by_melee", true, true, new GuiTexture.GoalGuiTexture(3, 0), BlocklingMeleeAttackHurtByGoal::new);
    public static final TaskType MELEE_ATTACK_OWNER_HURT_BY = new TaskType("72b27eb1-e5bd-48e0-b562-74dece3d144a", "owner_hurt_by_melee", false, false, new GuiTexture.GoalGuiTexture(4, 0), BlocklingMeleeAttackOwnerHurtByGoal::new);
    public static final TaskType MELEE_ATTACK_OWNER_HURT = new TaskType("51d0ae15-8605-4240-a515-89f47b2f450a", "owner_hurt_melee", false, false, new GuiTexture.GoalGuiTexture(5, 0), BlocklingMeleeAttackOwnerHurtGoal::new);
    public static final TaskType MELEE_ATTACK_HUNT = new TaskType("283e92b8-5cb8-4d19-afc7-88869a60a214", "hunt_melee", false, false, new GuiTexture.GoalGuiTexture(6, 0), BlocklingMeleeAttackHuntGoal::new);
    public static final TaskType MINE = new TaskType("657c60cf-9fac-408e-ad8d-3335409301d6", "mine_ores", false, false, new GuiTexture.GoalGuiTexture(7, 0), BlocklingMineGoal::new);
    public static final TaskType WOODCUT = new TaskType("9701e1f6-99e0-4772-88a1-906778499a8c", "chop_trees", false, false, new GuiTexture.GoalGuiTexture(8, 0), BlocklingWoodcutGoal::new);
    public static final TaskType FARM = new TaskType("190bb949-6fb0-456b-9009-991c8db9be10", "farm_crops", false, false, new GuiTexture.GoalGuiTexture(9, 0), BlocklingFarmGoal::new);
    public static final TaskType SIT = new TaskType("d64385ca-9306-4e38-b4ac-5aa8800e5e02", "sit", true, false, new GuiTexture.GoalGuiTexture(0, 0), BlocklingSitGoal::new);
    public static final TaskType FOLLOW = new TaskType("299ad70d-350b-43da-8f55-ec502ac360bd", "follow", true, false, new GuiTexture.GoalGuiTexture(1, 0), BlocklingFollowGoal::new);
    public static final TaskType WANDER = new TaskType("39246a4f-3341-4e99-a3a6-450f9501daeb", "wander", true, true, new GuiTexture.GoalGuiTexture(2, 0), BlocklingWanderGoal::new);

    /**
     * A global list of all task types.
     */
    public static final List<TaskType> TASK_TYPES = new ArrayList<TaskType>()
    {{
        add(MELEE_ATTACK_HURT_BY);
        add(MELEE_ATTACK_OWNER_HURT_BY);
        add(MELEE_ATTACK_OWNER_HURT);
        add(MELEE_ATTACK_HUNT);
        add(MINE);
        add(WOODCUT);
        add(FARM);
        add(SIT);
        add(FOLLOW);
        add(WANDER);
    }};

    public static TaskType getTaskType(UUID id)
    {
        return TASK_TYPES.stream().filter(type -> type.id.equals(id)).findFirst().orElse(NULL);
    }

    public final Map<TaskType, Boolean> taskTypeUnlockedMap = new HashMap<>();

    private final BlocklingEntity blockling;
    private final GoalSelector goalSelector;
    private final GoalSelector targetSelector;

    private TaskList prioritisedTasks = new TaskList();

    public BlocklingTasks(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.goalSelector = blockling.goalSelector;
        this.targetSelector = blockling.targetSelector;

        TASK_TYPES.forEach(type -> taskTypeUnlockedMap.put(type, type.isUnlockedByDefault));

        if (!blockling.level.isClientSide) // Only do this server side, and let it sync to clients
        {
            prioritisedTasks = new TaskList(TASK_TYPES.stream().filter(type -> type.isActiveByDefault && taskTypeUnlockedMap.get(type)).map(type -> new Task(UUID.randomUUID(), type, blockling, this)).collect(Collectors.toList()));

            reapplyGoals();
        }
    }

    /**
     * Removes and re-adds the unlocked and active goals.
     */
    public void reapplyGoals()
    {
        Set<PrioritizedGoal> goals = ObfuscationReflectionHelper.getPrivateValue(GoalSelector.class, goalSelector, "field_220892_d");
        Set<PrioritizedGoal> targets = ObfuscationReflectionHelper.getPrivateValue(GoalSelector.class, targetSelector, "field_220892_d");
        goals.forEach(prioritizedGoal -> prioritizedGoal.stop());
        goals.clear();
        targets.forEach(prioritizedGoal -> prioritizedGoal.stop());
        targets.clear();

        Map<Goal.Flag, PrioritizedGoal> goalLockedFlags  = ObfuscationReflectionHelper.getPrivateValue(GoalSelector.class, goalSelector, "field_220891_c");
        Map<Goal.Flag, PrioritizedGoal> targetLockedFlags  = ObfuscationReflectionHelper.getPrivateValue(GoalSelector.class, targetSelector, "field_220891_c");
        goalLockedFlags.clear();
        targetLockedFlags.clear();

        goalSelector.addGoal(0, new SwimGoal(blockling));

        for (Task task : prioritisedTasks)
        {
            if (!task.isConfigured())
            {
                continue;
            }

            BlocklingGoal goal = task.getGoal();

            goalSelector.addGoal(task.getPriority(), goal);

            if (goal instanceof IHasTargetGoal)
            {
                IHasTargetGoal hasTargetGoal = (IHasTargetGoal) goal;

                targetSelector.addGoal(task.getPriority() + 1, hasTargetGoal.getTargetGoal());
            }
        }
    }

    public void writeToNBT(CompoundNBT c)
    {
        CompoundNBT typesTag = new CompoundNBT();

        for (TaskType type : taskTypeUnlockedMap.keySet())
        {
            typesTag.putBoolean(type.id.toString(), taskTypeUnlockedMap.get(type));
        }

        CompoundNBT tasksTag = new CompoundNBT();

        for (Task task : prioritisedTasks)
        {
            CompoundNBT taskTag = new CompoundNBT();

            taskTag.putUUID("type_id", task.getType().id);
            taskTag.putInt("priority", task.getPriority());
            taskTag.putString("custom_name", task.getActualCustomName());

            if (task.isConfigured())
            {
                CompoundNBT whitelistsTag = new CompoundNBT();

                for (GoalWhitelist whitelist : task.getGoal().whitelists)
                {
                    whitelist.writeToNBT(whitelistsTag);
                }

                taskTag.put("whitelists", whitelistsTag);
                taskTag.putInt("state", task.getGoal().getState().ordinal());
            }

            tasksTag.put(task.id.toString(), taskTag);
        }

        c.put("task_types", typesTag);
        c.put("tasks", tasksTag);
    }

    public void readFromNBT(CompoundNBT c)
    {
        CompoundNBT typesTag = (CompoundNBT) c.get("task_types");

        for (String typeIdString : typesTag.getAllKeys())
        {
            taskTypeUnlockedMap.put(getTaskType(UUID.fromString(typeIdString)), typesTag.getBoolean(typeIdString));
        }

        CompoundNBT tasksTag = (CompoundNBT) c.get("tasks");

        prioritisedTasks.clear();

        List<UUID> taskIds = new ArrayList<>();
        List<Integer> taskPriorities = new ArrayList<>();

        for (String taskIdString : tasksTag.getAllKeys())
        {
            CompoundNBT taskTag = (CompoundNBT) tasksTag.get(taskIdString);

            UUID taskId = UUID.fromString(taskIdString);
            TaskType type = getTaskType(taskTag.getUUID("type_id"));
            createTask(type, taskId, false);

            taskIds.add(taskId);
            taskPriorities.add(taskTag.getInt("priority"));

            Task task = getTask(taskId);
            task.setCustomName(taskTag.getString("custom_name"), false);

            if (task.isConfigured())
            {
                CompoundNBT whitelistsTag = (CompoundNBT) taskTag.get("whitelists");

                for (GoalWhitelist whitelist : task.getGoal().whitelists)
                {
                    whitelist.readFromNBT(whitelistsTag);
                }

                task.getGoal().setState(BlocklingGoal.State.values()[taskTag.getInt("state")], false);
            }
        }

        for (int i = 0; i < taskIds.size(); i++)
        {
            getTask(taskIds.get(i)).setPriority(taskPriorities.get(i), false);
        }
    }

    public void encode(PacketBuffer buf)
    {
        for (TaskType type : TASK_TYPES)
        {
            buf.writeBoolean(taskTypeUnlockedMap.get(type));
        }

        buf.writeInt(prioritisedTasks.size());

        for (Task task : prioritisedTasks)
        {
            buf.writeUUID(task.getType().id);
            buf.writeUUID(task.id);
            PacketBufferUtils.writeString(buf, task.getActualCustomName());

            if (task.isConfigured())
            {
                for (GoalWhitelist whitelist : task.getGoal().whitelists)
                {
                    whitelist.encode(buf);
                }

                buf.writeEnum(task.getGoal().getState());
            }
        }
    }

    public void decode(PacketBuffer buf)
    {
        for (TaskType type : TASK_TYPES)
        {
            taskTypeUnlockedMap.put(type, buf.readBoolean());
        }

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            UUID taskTypeId = buf.readUUID();
            UUID taskId = buf.readUUID();

            createTask(getTaskType(taskTypeId), taskId, false);

            Task task = getTask(taskId);
            task.setCustomName(PacketBufferUtils.readString(buf), false);

            if (task.isConfigured())
            {
                for (GoalWhitelist whitelist : task.getGoal().whitelists)
                {
                    whitelist.decode(buf);
                }

                task.getGoal().setState(buf.readEnum(BlocklingGoal.State.class), false);
            }
        }
    }

    public TaskList getPrioritisedTasks()
    {
        return prioritisedTasks;
    }

    public Task getTask(UUID id)
    {
        return prioritisedTasks.stream().filter(task -> task.id.equals(id)).findFirst().orElse(null);
    }

    public void createTask(TaskType type)
    {
        createTask(type, null, true);
    }

    public void createTask(TaskType type, UUID taskId, boolean sync)
    {
        if (taskId != null && getTask(taskId) == null)
        {
            Task task = new Task(taskId, type, blockling, this);

            prioritisedTasks.add(task);

            reapplyGoals();
        }

        if (sync)
        {
            new TaskCreateMessage(blockling, type.id, taskId == null ? UUID.randomUUID() : taskId).sync();
        }
    }

    public void removeTask(Task task)
    {
        removeTask(task.id, true);
    }

    public void removeTask(UUID id, boolean sync)
    {
        Task task = getTask(id);

        prioritisedTasks.remove(task);

        reapplyGoals();

        if (sync)
        {
            new TaskRemoveMessage(blockling, id).sync();
        }
    }

    public boolean isUnlocked(TaskType type)
    {
        return taskTypeUnlockedMap.get(type);
    }

    public void setIsUnlocked(TaskType type, boolean isUnlocked)
    {
        setIsUnlocked(type, isUnlocked, true);
    }

    public void setIsUnlocked(TaskType type, boolean isUnlocked, boolean sync)
    {
        taskTypeUnlockedMap.put(type, isUnlocked);

        if (sync)
        {
            new TaskTypeIsUnlockedMessage(blockling, type, isUnlocked).sync();
        }
    }

    public int getTaskPriority(Task task)
    {
        return prioritisedTasks.indexOf(task);
 }

    public void swapTaskPriorities(Task task1, Task task2)
    {
        prioritisedTasks.swap(task1, task2);

        reapplyGoals();
    }

    public void setGoalPriority(Task task, int priority)
    {
        prioritisedTasks.moveTo(task, priority);

        reapplyGoals();
    }

    public class TaskList extends ArrayList<Task>
    {
        public TaskList()
        {
            super();
        }

        public TaskList(List<Task> tasks)
        {
            super();
            tasks.stream().forEach(goal -> add(goal));
        }

        public void insertAtFront(Task task)
        {
            if (isEmpty())
            {
                add(task);
            }
            else
            {
                add(task);
                moveTo(task, indexOf(get(0)));
            }
        }

        public void insertBefore(Task task, Task target)
        {
            moveTo(task, indexOf(target));
        }

        public void insertAfter(Task task, Task target)
        {
            moveTo(task, indexOf(target) + 1);
        }

        public void moveTo(Task task, int index)
        {
            int oldIndex = indexOf(task);
            ensureCapacity(index + 1);
            set(oldIndex, null);
            add(index > oldIndex ? index + 1 : index, task);
            remove(null);
        }

        public void swap(Task task1, Task task2)
        {
            int oldIndex = indexOf(task2);
            set(indexOf(task1), task2);
            set(oldIndex, task1);
        }
    }
}
