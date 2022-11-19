package com.willr27.blocklings.util.event;

import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

/**
 * An event relating to an entity.
 */
public class EntityEvent<T extends Entity> extends HandleableEvent
{
    /**
     * The entity.
     */
    @Nonnull
    public final T entity;

    /**
     * @param entity the entity.
     */
    public EntityEvent(@Nonnull T entity)
    {
        this.entity = entity;
    }
}
