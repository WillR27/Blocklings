package com.willr27.blocklings.action.actions;

import com.willr27.blocklings.action.Action;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import javax.annotation.Nonnull;

/**
 * An action where the target count is not known or easy to provide from a supplier.
 */
public class UnknownTargetAction extends Action
{
    /**
     * @param blockling the blockling.
     * @param key the key used to identify the action and for the underlying attribute.
     */
    public UnknownTargetAction(@Nonnull BlocklingEntity blockling, @Nonnull String key)
    {
        super(blockling, key);
    }
}
