package com.willr27.blocklings.util.event;

import net.jodah.typetools.TypeResolver;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An event bus that will forward events to the appropriate subscribers.
 */
public class EventBus<S>
{
    @Nonnull
    private final Map<Class<? extends IEvent>, List<BiConsumer<S, ? extends IEvent>>> subscribers = new HashMap<>();

    public <E extends IEvent> void subscribe(@Nonnull BiConsumer<S, E> subscriber)
    {
        Class<E> eventClass = getEventClass(subscriber);
        subscribers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(subscriber);
    }

    public <E extends IEvent> void post(@Nonnull S control, @Nonnull E event)
    {
        List<BiConsumer<S, ? extends IEvent>> consumers = subscribers.get(event.getClass());

        if (consumers == null)
        {
            return;
        }

        for (BiConsumer<S, ? extends IEvent> consumer : consumers)
        {
            ((BiConsumer<S, E>) consumer).accept(control, event);
        }
    }

    private <E extends IEvent> Class<E> getEventClass(BiConsumer<S, E> consumer)
    {
        return (Class<E>) TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass())[1];
    }
}
