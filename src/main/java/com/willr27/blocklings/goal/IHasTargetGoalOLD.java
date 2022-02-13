package com.willr27.blocklings.goal;

import javax.annotation.Nonnull;

/**
 * Represents a goal that has a corresponding target goal.
 *
 * @param <T> the type of the target goal.
 */
public interface IHasTargetGoalOLD<T extends BlocklingTargetGoalOLD<?>>
{
    @Nonnull
    T getTargetGoal();
}
