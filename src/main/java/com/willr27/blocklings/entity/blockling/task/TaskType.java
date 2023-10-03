package com.willr27.blocklings.entity.blockling.task;

import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.util.BlocklingsTranslatableComponent;
import com.willr27.blocklings.util.TriFunction;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.UUID;

public class TaskType
{
    public final UUID id;
    public final TranslatableComponent name;
    public final TranslatableComponent desc;
    public final boolean isUnlockedByDefault;
    public final boolean isActiveByDefault;
    public final Texture texture;
    public final TriFunction<UUID, BlocklingEntity, BlocklingTasks, BlocklingGoal> createGoal;

    public TaskType(String id, String key, boolean unlockedByDefault, boolean activeByDefault, Texture texture, TriFunction<UUID, BlocklingEntity, BlocklingTasks, BlocklingGoal> createGoal)
    {
        this.id = UUID.fromString(id);
        this.name = new GoalTranslatableComponent(key + ".name");
        this.desc = new GoalTranslatableComponent(key + ".desc");
        this.isUnlockedByDefault = unlockedByDefault;
        this.isActiveByDefault = activeByDefault;
        this.texture = texture;
        this.createGoal = createGoal;
    }

    public class GoalTranslatableComponent extends BlocklingsTranslatableComponent
    {
        public GoalTranslatableComponent(String key)
        {
            super("task." + key);
        }
    }

    @Override
    public String toString()
    {
        return name.getString();
    }
}
