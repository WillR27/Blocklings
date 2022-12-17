package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.ScrollOffsetChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrollEvent;
import com.willr27.blocklings.util.event.EventHandler;

import javax.annotation.Nonnull;

/**
 * Displays a scrollbar to scroll an associated control.
 */
public class ScrollbarControl extends Control
{
    /**
     * The amount scrolled.
     */
    private float scrollAmount = 0.0f;

    /**
     * The value at which the scrollbar is at the end.
     */
    private float maxScrollAmount = 0.0f;

    /**
     * The associated control to scroll.
     */
    @Nonnull
    private Control associatedControl;

    /**
     * The grabber control.
     */
    @Nonnull
    private final TexturedControl grabberControl;

     /**
     */
    public ScrollbarControl()
    {
        super();

        setWidth(GuiTextures.Common.Scrollbar.GRABBER_UNPRESSED.width);
        setPressable(false);

        grabberControl = new TexturedControl(GuiTextures.Common.Scrollbar.GRABBER_UNPRESSED, GuiTextures.Common.Scrollbar.GRABBER_PRESSED)
        {
            @Override
            public void setY(float y)
            {
                y = Math.min(Math.max(0.0f, ScrollbarControl.this.getHeight() - grabberControl.getHeight()), Math.max(0.0f, y));

                super.setY(y);
            }

            @Override
            public void onRenderBackground(@Nonnull RenderArgs renderArgs)
            {
                if (getMaxScrollAmount() > 0.0f)
                {
                    super.onRenderBackground(renderArgs);
                }
                else
                {
                    renderTexture(renderArgs.matrixStack, getPressedTexture());
                }
            }
        };
        grabberControl.setParent(this);
        grabberControl.setDraggableY(true);

        grabberControl.onPositionChanged.subscribe((e) ->
        {
            float percent = grabberControl.getY() / (getHeight() - grabberControl.getHeight());
            setPercentScrolled(percent);
        });
    }

    @Override
    protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (getMaxScrollAmount() > 0.0f)
        {
            grabberControl.setY(mouseButtonEvent.mouseY - grabberControl.getHeight() / 2.0f);
            getScreen().setDraggedControl(grabberControl, mouseButtonEvent);
            getScreen().setPressedControl(grabberControl, mouseButtonEvent);
        }

        super.onMouseClicked(mouseButtonEvent);
    }

    @Override
    protected void onMouseScrolled(@Nonnull MouseScrollEvent mouseScrollEvent)
    {
        float newScrollAmount = (float) (getScrollAmount() - mouseScrollEvent.scrollAmount * getScrollSpeed());
        setScrollAmount(newScrollAmount);

        mouseScrollEvent.setIsHandled(newScrollAmount >= 0.0f || newScrollAmount <= getMaxScrollAmount());
    }

    /**
     * @return the percent scrolled.
     */
    public float getPercentScrolled()
    {
        return getMaxScrollAmount() > 0.0f ? getScrollAmount() / getMaxScrollAmount() : 0.0f;
    }

    /**
     * Sets the percent scrolled.
     */
    public void setPercentScrolled(float percent)
    {
        if (Math.abs(percent - getPercentScrolled()) < 0.001f)
        {
            return;
        }

        percent = Math.min(1.0f, Math.max(0.0f, percent));
        float amount = percent * getMaxScrollAmount();

        if (amount != getScrollAmount())
        {
            setScrollAmount(amount);
        }
    }

    /**
     * @return the scroll amount.
     */
    public float getScrollAmount()
    {
        return scrollAmount;
    }

    /**
     * Sets the scroll amount.
     */
    public void setScrollAmount(float scrollAmount)
    {
        scrollAmount = Math.min(getMaxScrollAmount(), Math.max(0.0f, scrollAmount));

        this.scrollAmount = scrollAmount;

        if (getAssociatedControl() != null)
        {
            getAssociatedControl().setScrollOffsetY(this.scrollAmount);
        }

        grabberControl.setY(getPercentScrolled() * (getHeight() - grabberControl.getHeight()));
    }

    /**
     * @return the max scroll amount.
     */
    public float getMaxScrollAmount()
    {
        return maxScrollAmount;
    }

    /**
     * Sets the max scroll amount.
     */
    public void setMaxScrollAmount(float maxScrollAmount)
    {
        this.maxScrollAmount = maxScrollAmount;

        setScrollAmount(getScrollAmount());

        if (getMaxScrollAmount() == 0.0f)
        {
            grabberControl.setY(0.0f);
            grabberControl.setDraggableY(false);
        }
        else
        {
            grabberControl.setDraggableY(true);
        }
    }

    /**
     * @return the associated control.
     */
    @Nonnull
    public Control getAssociatedControl()
    {
        return associatedControl;
    }

    /**
     * Sets the associated control.
     */
    public void setAssociatedControl(@Nonnull Control associatedControl)
    {
        EventHandler.Handler<ScrollOffsetChangedEvent> onMaxScrollOffsetYChanged = (e) -> setMaxScrollAmount(e.control.getMaxScrollOffsetY());
        EventHandler.Handler<ScrollOffsetChangedEvent> onScrollOffsetYChanged = (e) -> setScrollAmount(e.control.getScrollOffsetY());

        if (this.associatedControl != null)
        {
            this.associatedControl.onMaxScrollOffsetYChanged.unsubscribe(onMaxScrollOffsetYChanged);
            this.associatedControl.onScrollOffsetYChanged.unsubscribe(onScrollOffsetYChanged);
        }

        this.associatedControl = associatedControl;

        if (this.associatedControl != null)
        {
            this.associatedControl.onMaxScrollOffsetYChanged.subscribe(onMaxScrollOffsetYChanged);
            this.associatedControl.onScrollOffsetYChanged.subscribe(onScrollOffsetYChanged);

            setMaxScrollAmount(associatedControl.getMaxScrollOffsetY());
        }
    }
}
