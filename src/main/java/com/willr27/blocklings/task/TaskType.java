package com.willr27.blocklings.task;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.goal.BlocklingGoal;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.TriFunction;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class TaskType
{
    public final UUID id;
    public final TranslationTextComponent name;
    public final TranslationTextComponent desc;
    public final boolean isUnlockedByDefault;
    public final boolean isActiveByDefault;
    public final GuiTexture texture;
    public final TriFunction<UUID, BlocklingEntity, BlocklingTasks, BlocklingGoal> createGoal;

    public TaskType(String id, String key, boolean unlockedByDefault, boolean activeByDefault, GuiTexture texture, TriFunction<UUID, BlocklingEntity, BlocklingTasks, BlocklingGoal> createGoal)
    {
        this.id = UUID.fromString(id);
        this.name = new GoalTranslationTextComponent(key + ".name");
        this.desc = new GoalTranslationTextComponent(key + ".desc");
        this.isUnlockedByDefault = unlockedByDefault;
        this.isActiveByDefault = activeByDefault;
        this.texture = texture;
        this.createGoal = createGoal;
    }

    public class GoalTranslationTextComponent extends BlocklingsTranslationTextComponent
    {
        public GoalTranslationTextComponent(String key)
        {
            super("task." + key);
        }
    }
}
