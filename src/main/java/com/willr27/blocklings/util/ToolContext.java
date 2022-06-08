package com.willr27.blocklings.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the context in which a tool is being used
 */
public class ToolContext
{
    /**
     * The type of tool.
     */
    public final com.willr27.blocklings.util.ToolType toolType;

    /**
     * The target entity (should be set if tool type is a weapon).
     */
    public final LivingEntity entity;

    /**
     * The target block state (should be set if tool type is not a weapon).
     */
    public final BlockState blockState;

    /**
     * @param toolType the type of tool.
     * @param entity the target entity (should be set if tool type is a weapon).
     */
    public ToolContext(@Nonnull com.willr27.blocklings.util.ToolType toolType, @Nonnull LivingEntity entity)
    {
        this(toolType, entity, null);
    }

    /**
     * @param toolType the type of tool.
     * @param blockState the target block state (should be set if tool type is not a weapon).
     */
    public ToolContext(@Nonnull com.willr27.blocklings.util.ToolType toolType, @Nonnull BlockState blockState)
    {
        this(toolType, null, blockState);
    }

    /**
     * @param toolType the type of tool.
     * @param entity the target entity (should be set if tool type is a weapon).
     * @param blockState the target block state (should be set if tool type is not a weapon).
     */
    private ToolContext(@Nonnull com.willr27.blocklings.util.ToolType toolType, @Nullable LivingEntity entity, @Nullable BlockState blockState)
    {
        this.toolType = toolType;
        this.entity = entity;
        this.blockState = blockState;
    }
}