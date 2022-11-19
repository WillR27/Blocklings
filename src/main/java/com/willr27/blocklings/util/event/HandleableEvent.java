package com.willr27.blocklings.util.event;

/**
 * A class that represents an event.
 */
public class HandleableEvent implements IEvent
{
    /**
     * Whether the event is handled.
     */
    private boolean isHandled = false;

    /**
     * @return whether the event is handled.
     */
    public boolean isHandled()
    {
        return isHandled;
    }

    /**
     * Sets whether the event is handled.
     *
     * @param isHandled whether the event is handled.
     */
    public void setIsHandled(boolean isHandled)
    {
        this.isHandled = isHandled;
    }
}
