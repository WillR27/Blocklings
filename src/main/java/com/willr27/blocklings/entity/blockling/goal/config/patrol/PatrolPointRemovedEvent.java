package com.willr27.blocklings.entity.blockling.goal.config.patrol;

import com.willr27.blocklings.util.event.IEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An event used when {@link PatrolPoint} is removed from an {@link OrderedPatrolPointList}.
 */
public class PatrolPointRemovedEvent implements IEvent
{
    /**
     * The {@link PatrolPoint} that was removed.
     */
    @Nonnull
    public final PatrolPoint removedPatrolPoint;

    /**
     * The next {@link PatrolPoint} in the list. Null if there are no more points.
     */
    @Nullable
    public final PatrolPoint nextPatrolPoint;

    /**
     * @param removedPatrolPoint the {@link PatrolPoint} that was removed.
     * @param nextPatrolPoint the next {@link PatrolPoint} in the list. Null if there are no more points.
     */
    public PatrolPointRemovedEvent(@Nonnull PatrolPoint removedPatrolPoint, @Nullable PatrolPoint nextPatrolPoint)
    {
        this.removedPatrolPoint = removedPatrolPoint;
        this.nextPatrolPoint = nextPatrolPoint;
    }
}
