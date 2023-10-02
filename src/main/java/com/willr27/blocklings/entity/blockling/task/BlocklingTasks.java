package com.willr27.blocklings.entity.blockling.task;

import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.combat.BlocklingMeleeAttackHuntGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.combat.BlocklingMeleeAttackHurtByGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.combat.BlocklingMeleeAttackOwnerHurtByGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.combat.BlocklingMeleeAttackOwnerHurtGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.container.BlocklingDepositContainerGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.container.BlocklingTakeContainerGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.gather.BlocklingFarmGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.gather.BlocklingMineGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.gather.BlocklingWoodcutGoal;
import com.willr27.blocklings.entity.blockling.goal.goals.misc.*;
import com.willr27.blocklings.entity.blockling.task.config.Property;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.GoalWhitelist;
import com.willr27.blocklings.network.messages.TaskCreateMessage;
import com.willr27.blocklings.network.messages.TaskRemoveMessage;
import com.willr27.blocklings.network.messages.TaskTypeIsUnlockedMessage;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.PacketBufferUtils;
import com.willr27.blocklings.util.Version;
import com.willr27.blocklings.util.event.EventHandler;
import com.willr27.blocklings.util.event.HandleableEvent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class BlocklingTasks implements IReadWriteNBT
{
    public static final int MAX_TASKS = 32;

    public static final TaskType NULL = new TaskType("1c330075-19af-4c12-ac20-6de50e7b84a9", "null", false, false, new Texture(Textures.Tasks.TASKS, 176, 166, 20, 20), ((i, b, t) -> null));
    public static final TaskType MELEE_ATTACK_HURT_BY = new TaskType("2888dde5-f6ee-439d-ab8d-ea9a91470c64", "hurt_by_melee", true, true, new GoalTexture(3, 0), BlocklingMeleeAttackHurtByGoal::new);
    public static final TaskType MELEE_ATTACK_OWNER_HURT_BY = new TaskType("72b27eb1-e5bd-48e0-b562-74dece3d144a", "owner_hurt_by_melee", false, false, new GoalTexture(5, 0), BlocklingMeleeAttackOwnerHurtByGoal::new);
    public static final TaskType MELEE_ATTACK_OWNER_HURT = new TaskType("51d0ae15-8605-4240-a515-89f47b2f450a", "owner_hurt_melee", false, false, new GoalTexture(4, 0), BlocklingMeleeAttackOwnerHurtGoal::new);
    public static final TaskType MELEE_ATTACK_HUNT = new TaskType("283e92b8-5cb8-4d19-afc7-88869a60a214", "hunt_melee", false, false, new GoalTexture(6, 0), BlocklingMeleeAttackHuntGoal::new);
    public static final TaskType MINE = new TaskType("657c60cf-9fac-408e-ad8d-3335409301d6", "mine_ores", false, false, new GoalTexture(7, 0), BlocklingMineGoal::new);
    public static final TaskType WOODCUT = new TaskType("9701e1f6-99e0-4772-88a1-906778499a8c", "chop_trees", false, false, new GoalTexture(8, 0), BlocklingWoodcutGoal::new);
    public static final TaskType FARM = new TaskType("190bb949-6fb0-456b-9009-991c8db9be10", "farm_crops", false, false, new GoalTexture(9, 0), BlocklingFarmGoal::new);
    public static final TaskType DEPOSIT_ITEMS = new TaskType("9e745b46-ecb7-4497-8324-b2da80cf10ef", "deposit_items", false, false, new GoalTexture(11, 0), BlocklingDepositContainerGoal::new);
    public static final TaskType TAKE_ITEMS = new TaskType("bcf20336-6cd0-4540-bba0-e4cd8975ad4c", "take_items", false, false, new GoalTexture(0, 1), BlocklingTakeContainerGoal::new);
    public static final TaskType FIND_BLOCKLINGS = new TaskType("439c8877-ace6-4ffe-be76-d474aecf030f", "find_blocklings", false, false, new GoalTexture(10, 0), BlocklingFindBlocklingsGoal::new);
    public static final TaskType PATROL = new TaskType("c826c306-1015-4e5d-b5c6-b134b74c1133", "patrol", false, false, new GoalTexture(1, 1), BlocklingPatrolGoal::new);
    public static final TaskType FOLLOW = new TaskType("299ad70d-350b-43da-8f55-ec502ac360bd", "follow", true, false, new GoalTexture(1, 0), BlocklingFollowGoal::new);
    public static final TaskType WANDER = new TaskType("39246a4f-3341-4e99-a3a6-450f9501daeb", "wander", true, true, new GoalTexture(2, 0), BlocklingWanderGoal::new);
    public static final TaskType SIT = new TaskType("d64385ca-9306-4e38-b4ac-5aa8800e5e02", "sit", true, false, new GoalTexture(0, 0), BlocklingSitGoal::new);

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
        add(DEPOSIT_ITEMS);
        add(TAKE_ITEMS);
        add(FIND_BLOCKLINGS);
        add(PATROL);
        add(FOLLOW);
        add(WANDER);
        add(SIT);
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

    /**
     * The event handler for task create events.
     */
    public final EventHandler<CreateTaskEvent> onCreateTask = new EventHandler<>();

    /**
     * The event handler for task remove events.
     */
    public final EventHandler<RemoveTaskEvent> onRemoveTask = new EventHandler<>();

    /**
     * @param blockling the associated blockling.
     */
    public BlocklingTasks(@Nonnull BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.goalSelector = blockling.goalSelector;
        this.targetSelector = blockling.targetSelector;

        TASK_TYPES.forEach(type -> taskTypeUnlockedMap.put(type, type.isUnlockedByDefault));
    }

    /**
     * Set up the default tasks.
     */
    public void initDefaultTasks()
    {
        if (!blockling.level.isClientSide) // Only do this server side, and let it sync to clients.
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
        goals.forEach(PrioritizedGoal::stop);
        goals.clear();
        targets.forEach(PrioritizedGoal::stop);
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

            goalSelector.addGoal(task.getPriority(), task.getGoal());
        }
    }

    /**
     * Called once per tick the blockling is alive.
     */
    public void tick()
    {

    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT tasksTag)
    {
        CompoundNBT unlockedTypesTag = new CompoundNBT();

        for (TaskType type : taskTypeUnlockedMap.keySet())
        {
            unlockedTypesTag.putBoolean(type.id.toString(), taskTypeUnlockedMap.get(type));
        }

        CompoundNBT taskListTag = new CompoundNBT();

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
                    whitelistsTag.put(whitelist.id.toString(), whitelist.writeToNBT());
                }

                ListNBT propertiesTag = new ListNBT();

                for (Property property : task.getGoal().properties)
                {
                    propertiesTag.add(property.writeToNBT());
                }

                taskTag.put("whitelists", whitelistsTag);
                taskTag.put("properties", propertiesTag);
                taskTag.putInt("state", task.getGoal().getState().ordinal());

                task.getGoal().writeToNBT(taskTag);
            }

            taskListTag.put(task.id.toString(), taskTag);
        }

        tasksTag.put("unlocked_task_types", unlockedTypesTag);
        tasksTag.put("tasks", taskListTag);

        return tasksTag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT tasksTag, @Nonnull Version tagVersion)
    {
        CompoundNBT unlockedTypesTag = (CompoundNBT) tasksTag.get("unlocked_task_types");

        if (unlockedTypesTag != null)
        {
            for (String typeIdString : unlockedTypesTag.getAllKeys())
            {
                taskTypeUnlockedMap.put(getTaskType(UUID.fromString(typeIdString)), unlockedTypesTag.getBoolean(typeIdString));
            }
        }

        CompoundNBT taskListTag = (CompoundNBT) tasksTag.get("tasks");

        if (taskListTag != null)
        {
            prioritisedTasks.clear();

            List<UUID> taskIds = new ArrayList<>();
            List<Integer> taskPriorities = new ArrayList<>();

            for (String taskIdString : taskListTag.getAllKeys())
            {
                CompoundNBT taskTag = (CompoundNBT) taskListTag.get(taskIdString);

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
                        CompoundNBT whitelistTag = (CompoundNBT) whitelistsTag.get(whitelist.id.toString());

                        if (whitelistTag != null)
                        {
                            whitelist.readFromNBT(whitelistTag, tagVersion);
                        }
                    }

                    ListNBT propertiesTag = (ListNBT) taskTag.get("properties");

                    if (propertiesTag != null)
                    {
                        for (INBT tag : propertiesTag)
                        {
                            CompoundNBT propertyTag = (CompoundNBT) tag;

                            task.getGoal().properties.stream()
                                    .filter(property -> property.id.equals(propertyTag.getUUID("id")))
                                    .findFirst()
                                    .ifPresent(property -> property.readFromNBT(propertyTag, tagVersion));
                        }
                    }

                    task.getGoal().setState(BlocklingGoal.State.values()[taskTag.getInt("state")], false);

                    task.getGoal().readFromNBT(taskTag, tagVersion);
                }
            }

            prioritisedTasks = new TaskList(prioritisedTasks.stream().sorted((o1, o2) -> taskPriorities.get(taskIds.indexOf(o1.id)) > taskPriorities.get(taskIds.indexOf(o2.id)) ? 1 : -1).collect(Collectors.toList()));

            reapplyGoals();
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

                for (Property property : task.getGoal().properties)
                {
                    property.encode(buf);
                }

                task.getGoal().encode(buf);

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

                for (Property property : task.getGoal().properties)
                {
                    property.decode(buf);
                }

                task.getGoal().decode(buf);

                task.getGoal().setState(buf.readEnum(BlocklingGoal.State.class), false);
            }
        }
    }

    /**
     * @return Whether the task list is full.
     */
    public boolean isTaskListFull()
    {
        return prioritisedTasks.size() >= MAX_TASKS;
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

            onCreateTask.handle(new CreateTaskEvent(task));

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

        onRemoveTask.handle(new RemoveTaskEvent(task));

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

    /**
     * An event that occurs when a task is created.
     */
    public static class CreateTaskEvent extends HandleableEvent
    {
        /**
         * The task that was created.
         */
        @Nonnull
        public final Task task;

        /**
         * @param task the task that was created.
         */
        public CreateTaskEvent(@Nonnull Task task)
        {
            this.task = task;
        }
    }

    /**
     * An event that occurs when a task is removed.
     */
    public static class RemoveTaskEvent extends HandleableEvent
    {
        /**
         * The task that was removed.
         */
        @Nonnull
        public final Task task;

        /**
         * @param task the task that was removed.
         */
        public RemoveTaskEvent(@Nonnull Task task)
        {
            this.task = task;
        }
    }

    public static class GoalTexture extends Texture
    {
        public static final int ICON_SIZE = 20;
        public static final int ICON_TEXTURE_Y = 186;

        public GoalTexture(int x, int y)
        {
            super(Textures.Tasks.TASKS, x * ICON_SIZE, y * ICON_SIZE + ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE);
        }
    }
}
