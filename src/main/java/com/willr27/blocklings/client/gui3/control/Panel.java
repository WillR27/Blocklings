package com.willr27.blocklings.client.gui3.control;

import com.willr27.blocklings.client.gui3.RenderArgs;
import com.willr27.blocklings.client.gui3.control.event.events.DragEndEvent;
import com.willr27.blocklings.client.gui3.control.event.events.MarginsChangedEvent;
import com.willr27.blocklings.client.gui3.control.event.events.SizeChangedEvent;
import com.willr27.blocklings.client.gui3.control.event.events.VisibilityChangedEvent;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A panel is a control that automatically lays out its contents.
 */
@OnlyIn(Dist.CLIENT)
public abstract class Panel extends Control
{
    /**
     * The way in which contents are reordered via dragging.
     */
    private DragReorderType dragReorderType = DragReorderType.NONE;

    /**
     */
    public Panel()
    {
        onChildAdded.subscribe((e) -> layoutContents());
        onChildRemoved.subscribe((e) -> layoutContents());
        onChildrenReordered.subscribe((e) -> layoutContents());
        onSizeChanged.subscribe((e) -> layoutContents());

        EventHandler.Handler<DragEndEvent> onDragEnd = (e) ->
        {
            updateDraggedControlOnRelease(e.control);
            layoutContents(true);
        };

        EventHandler.Handler<SizeChangedEvent> onSizeChanged = (e) -> layoutContents(false);
        EventHandler.Handler<MarginsChangedEvent> onMarginsChanged = (e) -> layoutContents(false);
        EventHandler.Handler<VisibilityChangedEvent> onVisibilityChanged = (e) -> layoutContents(false);

        onChildAdded.subscribe((e) ->
        {
            e.childAdded.onDragEnd.subscribe(onDragEnd);
            e.childAdded.onSizeChanged.subscribe(onSizeChanged);
            e.childAdded.onMarginsChanged.subscribe(onMarginsChanged);
            e.childAdded.onVisibilityChanged.subscribe(onVisibilityChanged);
        });
        onChildRemoved.subscribe((e) ->
        {
            e.childRemoved.onDragEnd.unsubscribe(onDragEnd);
            e.childRemoved.onSizeChanged.unsubscribe(onSizeChanged);
            e.childRemoved.onMarginsChanged.unsubscribe(onMarginsChanged);
            e.childRemoved.onVisibilityChanged.unsubscribe(onVisibilityChanged);
        });
    }

    /**
     * Re-lays out the child controls of the panel.
     */
    public void layoutContents()
    {
        layoutContents(false);
    }

    /**
     * Re-lays out the child controls of the panel.
     *
     * @param setDraggedPosition whether to also set the current dragged control's position (if there is one).
     */
    public abstract void layoutContents(boolean setDraggedPosition);

    /**
     * Updates the contents of the panel based on the currently dragged child when it is released.
     *
     * @param draggedChild the child control currently being dragged.
     */
    protected abstract void updateDraggedControlOnRelease(@Nonnull Control draggedChild);

    /**
     * Updates the contents of the panel based on the currently dragged child.
     *
     * @param draggedChild the child control currently being dragged.
     */
    protected abstract void updateDraggedControl(@Nonnull Control draggedChild);

    @Override
    public void tryFitToContents()
    {
        tryFitToContents(true);
    }

    @Override
    public void onRenderUpdate(@Nonnull RenderArgs renderArgs)
    {
        if (getScreen().getDraggedControl() != null && getScreen().getDraggedControl().getParent() == this)
        {
            updateDraggedControl(getScreen().getDraggedControl());
        }
    }

    /**
     * @return the way in which the contents are reordered via dragging.
     */
    @Nonnull
    public DragReorderType getDragReorderType()
    {
        return dragReorderType;
    }

    /**
     * Sets the way in which the contents are reordered via dragging.
     */
    public void setDragReorderType(@Nonnull DragReorderType dragReorderType)
    {
        this.dragReorderType = dragReorderType;
    }
}
