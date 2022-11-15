package com.willr27.blocklings.client.gui.control.event.events.input;

/**
 * A mouse position event.
 */
public class MousePosEvent extends MouseScreenPosEvent
{
    /**
     * The mouse x position.
     */
    public final int mouseX;

    /**
     * The mouse y position.
     */
    public final int mouseY;

    /**
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param mouseScreenX the mouse screen x position.
     * @param mouseScreenY the mouse screen y position.
     */
    public MousePosEvent(int mouseX, int mouseY, int mouseScreenX, int mouseScreenY)
    {
        super(mouseScreenX, mouseScreenY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
