package com.willr27.blocklings.entity.goals.blockling.target;

import com.willr27.blocklings.goal.BlocklingTargetGoal;

public interface IHasTargetGoal<T extends BlocklingTargetGoal<?>>
{
    T getTargetGoal();
}
