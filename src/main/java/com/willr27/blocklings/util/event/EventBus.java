package com.willr27.blocklings.util.event;

import net.jodah.typetools.TypeResolver;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * An event bus that will forward events to the appropriate subscribers.
 *
 * @param <S> the type of the sender.
 */
public class EventBus<S>
{
    /**
     * Map of subscribers to event handlers.
     */
    @Nonnull
    private final Map<Class<? extends IEvent>, List<BiConsumer<S, ? extends IEvent>>> subscribers = new HashMap<>();

    /**
     * List of buses that this bus will forward events to after its own subscribers.
     */
    @Nonnull
    private final Set<EventBus<S>> chainedBuses = new HashSet<>();

    /**
     * Subscribe to an event.
     *
     * @param subscriber the subscriber.
     * @param <E> the event type.
     */
    public <E extends IEvent> void subscribe(@Nonnull BiConsumer<S, E> subscriber)
    {
        Class<E> eventClass = getEventClass(subscriber);
        subscribers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(subscriber);
    }

    /**
     * Post an event to the subscribers.
     *
     * @param sender the object that is sending the event.
     * @param event the event.
     * @param <E> the event type.
     */
    public <E extends IEvent> void post(@Nonnull S sender, @Nonnull E event)
    {
        List<BiConsumer<S, ? extends IEvent>> consumers = subscribers.get(event.getClass());

        if (consumers != null)
        {
            for (BiConsumer<S, ? extends IEvent> consumer : consumers)
            {
                ((BiConsumer<S, E>) consumer).accept(sender, event);
            }
        }

        for (EventBus<S> bus : new HashSet<>(chainedBuses))
        {
            bus.post(sender, event);
        }
    }

    /**
     * Add a bus to the chain of buses that this bus will forward events to.
     *
     * @param bus the bus to add.
     */
    public void addChainedBus(@Nonnull EventBus<S> bus)
    {
        chainedBuses.add(bus);
    }

    /**
     * Remove a bus from the chain of buses that this bus will forward events to.
     *
     * @param bus the bus to remove.
     */
    public void removeChainedBus(@Nonnull EventBus<S> bus)
    {
        chainedBuses.remove(bus);
    }

    /**
     * Clears the event bus of all subscribers and chained buses.
     */
    public void clear()
    {
        subscribers.clear();
        chainedBuses.clear();
    }

    /**
     * @return the number of subscribers.
     */
    public int getSubscriberCount()
    {
        return subscribers.size();
    }

    /**
     * @return the number of chained buses.
     */
    public int getChainedBusCount()
    {
        return chainedBuses.size();
    }

    /**
     * @param <E> gets the event class from the subscriber.
     */
    private <E extends IEvent> Class<E> getEventClass(BiConsumer<S, E> consumer)
    {
        return (Class<E>) TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass())[1];
    }
}
