package com.willr27.blocklings.util.event;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used to handle events.
 */
public class EventHandler<T extends Event>
{
    /**
     * The list of subscribed event handlers.
     */
    private final List<Handler<T>> handlers = new ArrayList<>();

    /**
     * Forwards the event to all subscribed event handlers until it is handled.
     */
    public T handle(@Nonnull T event)
    {
        for (Handler<T> handler : handlers)
        {
            handler.handle(event);

            if (event.isHandled())
            {
                return event;
            }
        }

        return event;
    }

    /**
     * Subscribes an event handler.
     */
    public void subscribe(@Nonnull Handler<T> handler)
    {
        this.handlers.add(handler);
    }

    /**
     * Unsubscribes an event handler.
     */
    public void unsubscribe(@Nonnull Handler<T> handler)
    {
        this.handlers.remove(handler);
    }

    /**
     * An event handler.
     *
     * @param <T> the type of event.
     */
    @FunctionalInterface
    public interface Handler<T extends Event>
    {
        void handle(@Nonnull T event);
    }
}
