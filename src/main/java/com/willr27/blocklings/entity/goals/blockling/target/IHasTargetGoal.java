package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.goal.BlocklingTargetGoal;

import javax.annotation.Nonnull;

public interface IHasTargetGoal<T extends BlocklingTargetGoal<?>>
{
    @Nonnull
    T getTargetGoal();
}
