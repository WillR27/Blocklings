package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.Gui;
import com.willr27.blocklings.client.gui.screen.IScreen;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.ScissorBounds;
import com.willr27.blocklings.client.gui.control.event.events.*;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrollEvent;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jline.utils.Log;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * A control is an atomic component of a GUI.
 */
@OnlyIn(Dist.CLIENT)
public class Control extends Gui
{
    /**
     * The list of operations to apply to the control before/after rendering.
     */
    @Nonnull
    private final List<RenderOperation> renderOperations = new ArrayList<>();

    /**
     * The screen the control is part of.
     */
    @Nullable
    private IScreen screen;

    /**
     * The optional parent control.
     */
    @Nullable
    private Control parent = null;

    /**
     * Occurs when the control's parent is changed.
     */
    @Nonnull
    public final EventHandler<ParentChangedEvent> onParentChanged = new EventHandler<>();

    /**
     * The list of child controls.
     */
    @Nonnull
    private List<Control> children = new ArrayList<>();

    /**
     * Occurs when the control's children are reordered.
     */
    @Nonnull
    public final EventHandler<ChildrenReorderedEvent> onChildrenReordered = new EventHandler<>();

    /**
     * Occurs when a control is added as a child.
     */
    @Nonnull
    public final EventHandler<ChildAddedEvent> onChildAdded = new EventHandler<>();

    /**
     * Occurs when a control is removed as a child.
     */
    @Nonnull
    public final EventHandler<ChildRemovedEvent> onChildRemoved = new EventHandler<>();

    /**
     * The x pixel position of the top left of the control on the screen.
     */
    private int pixelX = 0;

    /**
     * The y pixel position of the top left of the control on the screen.
     */
    private int pixelY = 0;

    /**
     * The scaled local x position of the control relative to the parent, i.e. (0, 0) would be the top left corner
     * of the parent control regardless of where the parent is on the screen.
     */
    private float x = 0;

    /**
     * The scaled local y position of the control relative to the parent, i.e. (0, 0) would be the top left corner
     * of the parent control regardless of where the parent is on the screen.
     */
    private float y = 0;

    /**
     * Called when the control's position changes.
     */
    @Nonnull
    public final EventHandler<PositionChangedEvent> onPositionChanged = new EventHandler<>();

    /**
     * The pixel width of the control on the screen.
     */
    private int pixelWidth = 100;

    /**
     * The pixel height of the control on the screen.
     */
    private int pixelHeight = 100;

    /**
     * The scaled width of the control relative to the parent, i.e. 100 x 100 would be 200 x 200 pixels if the
     * cumulative product of the parent control's inner scale was 2.0f.
     */
    private float width = 100;

    /**
     * The scaled height of the control relative to the parent, i.e. 100 x 100 would be 200 x 200 pixels if the
     * cumulative product of the parent control's inner scale was 2.0f.
     */
    private float height = 100;

    /**
     * The max width of the control. Set to -1 to ignore.
     */
    private int maxWidth = Integer.MAX_VALUE;

    /**
     * The max height of the control. Set to -1 to ignore.
     */
    private int maxHeight = Integer.MAX_VALUE;

    /**
     * Called when the control's size changes.
     */
    @Nonnull
    public final EventHandler<SizeChangedEvent> onSizeChanged = new EventHandler<>();

    /**
     * The margin values for each side of the control.
     */
    @Nonnull
    private Map<Side, Integer> margins = new HashMap<>();

    /**
     * Occurs when the control's margins changes.
     */
    @Nonnull
    public final EventHandler<MarginsChangedEvent> onMarginsChanged = new EventHandler<>();

    /**
     * The padding values for each side of the control.
     */
    @Nonnull
    private Map<Side, Integer> padding = new HashMap<>();

    /**
     * Occurs when the control's padding changes.
     */
    @Nonnull
    public final EventHandler<PaddingChangedEvent> onPaddingChanged = new EventHandler<>();

    /**
     * How the control is position/sized relative to its parent.
     */
    @Nullable
    private EnumSet<Side> anchor = EnumSet.of(Side.TOP, Side.LEFT);

    /**
     * Occurs when the control's anchor changes.
     */
    @Nonnull
    public final EventHandler<AnchorChangedEvent> onAnchorChanged = new EventHandler<>();

    /**
     * The cumulative inner scale of the child controls. So this includes all ancestor {@link #innerScale} values.
     * So if the control has an inner scale of 2.0f but the parent has an inner scale of 0.5f, the cumulative inner
     * scale would be 1.0f.
     */
    private float cumulativeInnerScale = 1.0f;

    /**
     * The scale of the child controls. So a control with an {@link #innerScale} of 2.0f would mean the child
     * controls are scaled up by 2. So 100x100 control would actually display at 200 x 200 in pixels.
     */
    private float innerScale = 1.0f;

    /**
     * Called when the control's inner scale changes.
     */
    @Nonnull
    public final EventHandler<InnerScaleChangedEvent> onInnerScaleChanged = new EventHandler<>();

    /**
     * Whether to stretch the control's size to fit its contents in the x-axis.
     */
    private boolean fitToContentsX = false;

    /**
     * Whether to stretch the control's size to fit its contents in the y-axis.
     */
    private boolean fitToContentsY = false;

    /**
     * Resizes the hitbox automatically when the size of the control changes.
     */
    private boolean autoSizeHitbox = true;

    /**
     * The hitbox used to handle collisions.
     */
    @Nonnull
    private Hitbox hitbox = new Hitbox.RectangleHitbox(0, 0, width, height);

    /**
     * Called when the control's hitbox changes.
     */
    @Nonnull
    public final EventHandler<HitboxChangedEvent> onHitboxChanged = new EventHandler<>();

    /**
     * Whether the control can is rendered.
     */
    private boolean isVisible = true;

    /**
     * Whether the control can interact with any input events.
     */
    private boolean isInteractive = true;

    /**
     * Whether the control blocks dragging at its boundaries. So child controls that attempt
     * to be dragged outside this control will be blocked.
     */
    private boolean blocksDrag = true;

    /**
     * Whether the control is draggable in the x-axis.
     */
    private boolean isDraggableX = false;

    /**
     * Whether the control is draggable in the y-axis.
     */
    private boolean isDraggableY = false;

    /**
     * Occurs when the control is first dragged.
     */
    @Nonnull
    public final EventHandler<DragStartEvent> onDragStart = new EventHandler<>();

    /**
     * Occurs when the control stops being dragged.
     */
    @Nonnull
    public final EventHandler<DragEndEvent> onDragEnd = new EventHandler<>();

    /**
     * The distance the mouse needs to move while pressed in the to start dragging.
     */
    private int dragThreshold = 3;

    /**
     * Whether the control is scrollable in the x-axis.
     */
    private boolean isScrollableX = false;

    /**
     * Whether the control is scrollable in the y-axis.
     */
    private boolean isScrollableY = false;

    /**
     * The multiplier used to adjust the amount scrolled.
     */
    private float scrollSpeed = 15.0f;

    /**
     * The scroll offset in the x-axis.
     */
    private float scrollOffsetX = 0;

    /**
     * The scroll offset in the y-axis.
     */
    private float scrollOffsetY = 0;

    /**
     * The max scroll offset in the x-axis.
     */
    private int maxScrollOffsetX = 0;

    /**
     * The max scroll offset in the y-axis.
     */
    private int maxScrollOffsetY = 0;

    /**
     * The background colour of the control.
     */
    @Nonnull
    private Colour backgroundColour = new Colour(0, 0, 0, 0);

    /**
     */
    public Control()
    {
        for (Side side : Side.values())
        {
            margins.put(side, 0);
            padding.put(side, 0);
        }

        EventHandler.Handler<SizeChangedEvent> onSizeChanged = (e) -> tryFitToContents();
        EventHandler.Handler<MarginsChangedEvent> onMarginsChanged = (e) -> tryFitToContents();

        onChildAdded.subscribe((e) ->
        {
            e.childAdded.onSizeChanged.subscribe(onSizeChanged);
            e.childAdded.onMarginsChanged.subscribe(onMarginsChanged);
        });
        onChildRemoved.subscribe((e) ->
        {
            e.childRemoved.onSizeChanged.unsubscribe(onSizeChanged);
            e.childRemoved.onMarginsChanged.unsubscribe(onMarginsChanged);
        });
    }

    /**
     * Transforms the control based on its anchor property and the given changes in parent size.
     */
    private void tryTransformFromAnchorWhenParentResized(float newParentWidth, float newParentHeight)
    {
        if (getAnchor() == null)
        {
            return;
        }

        if (getAnchor().isEmpty())
        {
            float dWidthScaled = (newParentWidth - getParent().getWidth()) / getParent().getInnerScale();
            float dHeightScaled = (newParentHeight - getParent().getHeight()) / getParent().getInnerScale();

            float parentLeft = getParent().getPadding(Side.LEFT);
            float controlLeft = getX() - getMargin(Side.LEFT);
            float parentRight = (getParent().getWidth() / getParent().getInnerScale()) - getPadding(Side.RIGHT);
            float totalWidth = (parentRight - parentLeft - getEffectiveWidth());
            float leftPercent = (controlLeft - parentLeft) / totalWidth;

            float parentTop = getParent().getPadding(Side.TOP);
            float controlTop = getY() - getMargin(Side.TOP);
            float parentBottom = (getParent().getHeight() / getParent().getInnerScale()) - getPadding(Side.BOTTOM);
            float totalHeight = (parentBottom - parentTop - getEffectiveHeight());
            float topPercent = (controlTop - parentTop) / totalHeight;

            setX(getX() + dWidthScaled * leftPercent);
            setY(getY() + dHeightScaled * topPercent);
        }

        if (getAnchor().contains(Side.LEFT) && getAnchor().contains(Side.RIGHT))
        {
            float oldRightSideOfControl = getX() + getEffectiveWidth();
            float oldRightSideOfParent = (getParent().getWidth() / getParent().getInnerScale()) - getParent().getPadding(Side.RIGHT);
            float oldMarginWidth = oldRightSideOfParent - oldRightSideOfControl;
            float newRightSideOfParent = (newParentWidth / getParent().getInnerScale()) - getParent().getPadding(Side.RIGHT);
            float newMarginWidth = newRightSideOfParent - oldRightSideOfControl;

            setWidth(getWidth() + (newMarginWidth - oldMarginWidth));
        }
        else if (getAnchor().contains(Side.RIGHT))
        {
            setX(getX() + (newParentWidth - getParent().getWidth()) / getParent().getInnerScale());
        }

        if (getAnchor().contains(Side.TOP) && getAnchor().contains(Side.BOTTOM))
        {
            float oldBottomSideOfControl = getY() + getEffectiveHeight();
            float oldBottomSideOfParent = (getParent().getHeight() / getParent().getInnerScale()) - getParent().getPadding(Side.BOTTOM);
            float oldMarginHeight = oldBottomSideOfParent - oldBottomSideOfControl;
            float newBottomSideOfParent = (newParentHeight / getParent().getInnerScale()) - getParent().getPadding(Side.BOTTOM);
            float newMarginHeight = newBottomSideOfParent - oldBottomSideOfControl;

            setHeight(getHeight() + (newMarginHeight - oldMarginHeight));
        }
        else if (getAnchor().contains(Side.BOTTOM))
        {
            setY(getY() + (newParentHeight - getParent().getHeight()) / getParent().getInnerScale());
        }
    }

    /**
     * Tries to resize the control to fit its contents. Will reposition all controls if any have negative
     * coordinates, and it will remove any unnecessary max scroll offsets.
     */
    public void tryFitToContents()
    {
        tryFitToContents(false);
    }

    /**
     * Tries to resize the control to fit its contents. Will reposition all controls if any have negative
     * coordinates, and it will remove any unnecessary max scroll offsets.
     *
     * @param ignoreTopLeftPadding used when the children already include the top left padding.
     */
    public void tryFitToContents(boolean ignoreTopLeftPadding)
    {
        if (shouldFitToContentsX() || shouldFitToContentsY())
        {
            float minX = 0;
            float minY = 0;
            float maxX = 0;
            float maxY = 0;

            for (Control control : getChildren())
            {
                float controlMinX = control.getX() - control.getMargin(Side.LEFT);
                float controlMinY = control.getY() - control.getMargin(Side.TOP);
                float controlMaxX = control.getX() - control.getMargin(Side.LEFT) + control.getEffectiveWidth();
                float controlMaxY = control.getY() - control.getMargin(Side.TOP) + control.getEffectiveHeight();

                minX = Math.min(minX, controlMinX);
                minY = Math.min(minY, controlMinY);
                maxX = Math.max(maxX, controlMaxX);
                maxY = Math.max(maxY, controlMaxY);
            }

            if (shouldFitToContentsX())
            {
                float oldWidth = getWidth();
                float desiredWidth = maxX - minX + (ignoreTopLeftPadding ? 0 : getPadding(Side.LEFT)) + getPadding(Side.RIGHT);
                float finalMinX = minX;
                setWidth(desiredWidth * getInnerScale());
                getChildren().forEach(control -> control.setX(control.getX() - finalMinX));
                setMaxScrollOffsetX(Math.round(getMaxScrollOffsetX() - Math.max(0, desiredWidth - oldWidth)));
            }

            if (shouldFitToContentsY())
            {
                float oldHeight = getHeight();
                float desiredHeight = maxY - minY + (ignoreTopLeftPadding ? 0 : getPadding(Side.TOP)) + getPadding(Side.BOTTOM);
                float finalMinY = minY;
                setHeight(desiredHeight * getInnerScale());
                getChildren().forEach(control -> control.setY(control.getY() - finalMinY));
                setMaxScrollOffsetY(Math.round(getMaxScrollOffsetY() - Math.max(0, desiredHeight - oldHeight)));
            }
        }
    }

    /**
     * Applies the pre render transformations to the control.
     */
    protected void applyPreRenderTransformations(@Nonnull RenderArgs renderArgs)
    {
        float scale = getCumulativeScale() * GuiUtil.getInstance().getGuiScale();

        // Scale the control, but also make sure to cancel out the translation caused by the scaling.
        renderArgs.matrixStack.pushPose();
        renderArgs.matrixStack.scale(scale, scale, 1.0f);
        renderArgs.matrixStack.translate((getPixelX() / scale) - getPixelX(), (getPixelY() / scale) - getPixelY(), isDraggingOrAncestorIsDragging() ? 100.0f : 0.0f);
    }

    /**
     * Applies the post render transformations to the control.
     */
    protected void applyPostRenderTransformations(@Nonnull RenderArgs renderArgs)
    {
        // Revert the previous transformations.
        renderArgs.matrixStack.popPose();
    }

    /**
     * Applies any scissoring to the control before rendering.
     */
    protected void applyScissor(@Nonnull RenderArgs renderArgs)
    {
        renderArgs.scissorStack.push(new ScissorBounds(getPixelX(), getPixelY(), getPixelWidth(), getPixelHeight()));
        renderArgs.scissorStack.enable();
    }

    /**
     * Undoes any scissoring to the control after rendering.
     */
    protected void undoScissor(@Nonnull RenderArgs renderArgs)
    {
        renderArgs.scissorStack.pop();
        renderArgs.scissorStack.disable();
    }

    /**
     * Applies any render system related operations like depth test or enabling blend.
     */
    protected void applyRenderSystemOperations(@Nonnull RenderArgs renderArgs)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableDepthTest();
    }

    /**
     * Undoes any render system related operations like depth test or enabling blend.
     */
    protected void undoRenderSystemOperations(@Nonnull RenderArgs renderArgs)
    {
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    /**
     * Forwards the call to {@link #onRender(RenderArgs)} to the control's children after rendering
     * itself.
     *
     * @param renderArgs the render args.
     */
    public final void forwardRender(@Nonnull RenderArgs renderArgs)
    {
        if (!isVisible())
        {
            return;
        }

        applyPreRenderTransformations(renderArgs);
        applyPreRenderOperations(renderArgs);
        applyScissor(renderArgs);
        applyRenderSystemOperations(renderArgs);
        onRenderUpdate(renderArgs);
        onRenderBackground(renderArgs);
        onRender(renderArgs);
        undoRenderSystemOperations(renderArgs);
        applyPostRenderOperations(renderArgs);
        applyPostRenderTransformations(renderArgs);

        getChildrenCopy().forEach(control -> control.forwardRender(renderArgs));
        undoScissor(renderArgs);
    }

    /**
     * Applies any pre render operations added via {@link #addRenderOperation(RenderOperation)}.
     *
     * @param renderArgs the render args.
     */
    private void applyPreRenderOperations(@Nonnull RenderArgs renderArgs)
    {
        renderOperations.forEach(renderOperation -> renderOperation.preRenderOperation.accept(this, renderArgs));
    }

    /**
     * Called before anything is rendered on the control.
     *
     * @param renderArgs the render args.
     */
    protected void onRenderUpdate(@Nonnull RenderArgs renderArgs)
    {

    }

    /**
     * Renders the control's background.
     *
     * @param renderArgs the render args.
     */
    protected void onRenderBackground(@Nonnull RenderArgs renderArgs)
    {
        renderRectangle(renderArgs.matrixStack, backgroundColour.rgba());
    }

    /**
     * Renders the control.
     *
     * @param renderArgs the render args.
     */
    protected void onRender(@Nonnull RenderArgs renderArgs)
    {

    }

    /**
     * Applies any post render operations added via {@link #addRenderOperation(RenderOperation)} in reverse order.
     *
     * @param renderArgs the render args.
     */
    private void applyPostRenderOperations(@Nonnull RenderArgs renderArgs)
    {
        List<RenderOperation> renderOperationsReversed = new ArrayList<>(renderOperations);
        Collections.reverse(renderOperations);

        renderOperationsReversed.forEach(renderOperation -> renderOperation.postRenderOperation.accept(this, renderArgs));
    }

    /**
     * Adds a render operation to be applied before and after rendering.
     */
    public void addRenderOperation(@Nonnull RenderOperation renderOperation)
    {
        renderOperations.add(renderOperation);
    }

    /**
     * Removes a render operation that is applied before and after rendering.
     */
    public void removeRenderOperation(@Nonnull RenderOperation renderOperation)
    {
        renderOperations.remove(renderOperation);
    }

    /**
     * Renders the given texture at the control's position.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     */
    protected void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture)
    {
        renderTexture(matrixStack, 0, 0, texture);
    }

    /**
     * Renders the given texture at the control's position, with the given offset.
     *
     * @param matrixStack the matrix stack.
     * @param dx the x offset.
     * @param dy the y offset.
     * @param texture the texture to render.
     */
    protected void renderTexture(@Nonnull MatrixStack matrixStack, int dx, int dy, @Nonnull GuiTexture texture)
    {
        renderTexture(matrixStack, texture, getPixelX() + dx, getPixelY() + dy);
    }

    /**
     * Renders a rectangle at the control's position.
     *
     * @param matrixStack the matrix stack.
     * @param colour the colour of the rectangle.
     */
    public void renderRectangle(@Nonnull MatrixStack matrixStack, int colour)
    {
        fill(matrixStack, getPixelX(), getPixelY(), Math.round(getPixelX() + getWidth()), Math.round(getPixelY() + getHeight()), colour);
    }

    /**
     * Renders a rectangle at the given localised position and scaled width/height.
     *
     * @param matrixStack the matrix stack.
     * @param x the localised x position.
     * @param y the localised y position.
     * @param width the scaled width.
     * @param height the scaled height.
     * @param colour the colour of the rectangle.
     */
    public void renderRectangle(@Nonnull MatrixStack matrixStack, int x, int y, int width, int height, int colour)
    {
        fill(matrixStack, getPixelX() + x, getPixelY() + y, getPixelX() + x + width, getPixelY() + y + height, colour);
    }

     /**
     * Forwards the call to {@link #onHover(MousePosEvent)} to the control's children.
     *
     * @param mousePosEvent the pixel position event.
     */
    public final void forwardHover(@Nonnull MousePosEvent mousePosEvent)
    {
        if (!isInteractive())
        {
            return;
        }

        if (!collidesWith(mousePosEvent.mousePixelX, mousePosEvent.mousePixelY))
        {
            return;
        }

        for (Control control : getChildrenCopy())
        {
            if (!mousePosEvent.isHandled())
            {
                control.forwardHover(mousePosEvent);
            }
        }

        if (!mousePosEvent.isHandled())
        {
            mousePosEvent.mouseX = Math.round(toLocalX(mousePosEvent.mousePixelX));
            mousePosEvent.mouseY = Math.round(toLocalY(mousePosEvent.mousePixelY));

            onHover(mousePosEvent);

            if (mousePosEvent.isHandled())
            {
                if (getScreen() != null)
                {
                    // This probably isn't necessary, but we can set it back to unhandled anyway.
                    mousePosEvent.setIsHandled(false);

                    getScreen().setHoveredControl(this, mousePosEvent);

                    // Set back to handled so that the screen can detect if a control was hovered.
                    mousePosEvent.setIsHandled(true);
                }
            }
        }
    }

    /**
     * Called when the control is initially hovered.
     */
    public void onHoverEnter(@Nonnull MousePosEvent mousePosEvent)
    {

    }

    /**
     * Occurs when the mouse is hovering over the control's hitbox.
     */
    public void onHover(@Nonnull MousePosEvent mousePosEvent)
    {
        mousePosEvent.setIsHandled(true);
    }

    /**
     * Called when the control is no longer hovered.
     */
    public void onHoverExit(@Nonnull MousePosEvent mousePosEvent)
    {

    }

    /**
     * Forwards the attempt to drag the control to its children before attempting to drag itself.
     */
    public void forwardTryDrag(@Nonnull MousePosEvent mousePosEvent)
    {
        if (!isInteractive())
        {
            return;
        }

        for (Control control : getChildrenCopy())
        {
            if (!mousePosEvent.isHandled())
            {
                control.forwardTryDrag(mousePosEvent);
            }
        }

        if (!mousePosEvent.isHandled())
        {
            mousePosEvent.mouseX = Math.round(toLocalX(mousePosEvent.mousePixelX));
            mousePosEvent.mouseY = Math.round(toLocalY(mousePosEvent.mousePixelY));

            Control pressedControl = getScreen().getPressedControl();

            if ((isPressed() || hasDescendant(pressedControl)) && (isDraggableX() || isDraggableY()))
            {
                int pixelDragDifX = mousePosEvent.mousePixelX - getScreen().getPressedStartPixelX();
                int pixelDragDifY = mousePosEvent.mousePixelY - getScreen().getPressedStartPixelY();
                int localDragDifX = Math.round((pixelDragDifX / getCumulativeScale()) / GuiUtil.getInstance().getGuiScale());
                int localDragDifY = Math.round((pixelDragDifY / getCumulativeScale()) / GuiUtil.getInstance().getGuiScale());
                int absLocalDragDifX = Math.abs(localDragDifX);
                int absLocalDragDifY = Math.abs(localDragDifY);

                boolean isDraggedX = absLocalDragDifX >= getDragThreshold();
                boolean isDraggedY = absLocalDragDifY >= getDragThreshold();

                // If we can drag but a pressed child can't then start dragging the child to prevent the parent dragging slightly then the child.
                if (pressedControl != this && ((pressedControl.isDraggableX() && isDraggedX) || (pressedControl.isDraggableY() && isDraggedY) || ((isDraggedX || isDraggedY) && pressedControl.isDraggableXY())))
                {
                    getScreen().setDraggedControl(pressedControl, mousePosEvent);

                    mousePosEvent.setIsHandled(true);
                }
                // Otherwise just drag this control as normal.
                else if ((isDraggableX() && isDraggedX) || (isDraggableY() && isDraggedY) || ((isDraggedX || isDraggedY) && isDraggableXY()))
                {
                    getScreen().setDraggedControl(this, mousePosEvent);

                    mousePosEvent.setIsHandled(true);
                }
            }
        }
    }

    /**
     * Occurs when the control is initially dragged.
     */
    public void onDragStart(@Nonnull MousePosEvent mousePosEvent)
    {

    }

    /**
     * Occurs when a control is being dragged.
     */
    public void onDrag(@Nonnull MousePosEvent mousePosEvent, float partialTicks)
    {
        if (isDraggableX())
        {
            setX((getParent().toLocalX(mousePosEvent.mousePixelX) / getParent().getInnerScale()) - getWidth() / 2);
        }

        if (isDraggableY())
        {
            setY((getParent().toLocalY(mousePosEvent.mousePixelY) / getParent().getInnerScale()) - getHeight() / 2);
        }

        List<Side> atParentBounds = getParentBoundsAt();
        float scrollAmount = getParent().getScrollSpeed() * partialTicks;

        if (isDraggableX())
        {
            if (atParentBounds.contains(Side.LEFT))
            {
                if (getParent().isScrollableX())
                {
                    getParent().scrollX(scrollAmount * -1);
                }

                if (getParent().blocksDrag())
                {
                    setX(0);
                }
            }
            else if (atParentBounds.contains(Side.RIGHT))
            {
                if (getParent().isScrollableX())
                {
                    getParent().scrollX(scrollAmount);
                }

                if (getParent().blocksDrag())
                {
                    setX((getParent().toLocalX(getParent().getPixelX() + getParent().getPixelWidth()) / getParent().getInnerScale()) - getWidth());
                }
            }
        }

        if (isDraggableY())
        {
            if (atParentBounds.contains(Side.TOP))
            {
                if (getParent().isScrollableY())
                {
                    getParent().scrollY(scrollAmount * -1);
                }

                if (getParent().blocksDrag())
                {
                    setY(0);
                }
            }
            else if (atParentBounds.contains(Side.BOTTOM))
            {
                if (getParent().isScrollableY())
                {
                    getParent().scrollY(scrollAmount);
                }

                if (getParent().blocksDrag())
                {
                    setY((getParent().toLocalY(getParent().getPixelY() + getParent().getPixelHeight()) / getParent().getInnerScale()) - getHeight());
                }
            }
        }
    }

    /**
     * @return the sides of the parent the control is currently at or exceeding, null if not.
     */
    @Nonnull
    public List<Side> getParentBoundsAt()
    {
        List<Side> sides = new ArrayList<>();

        if (getPixelX() <= getParent().getPixelX())
        {
            sides.add(Side.LEFT);
        }

        if (getPixelX() + getPixelWidth() >= getParent().getPixelX() + getParent().getPixelWidth())
        {
            sides.add(Side.RIGHT);
        }

        if (getPixelY() <= getParent().getPixelY())
        {
            sides.add(Side.TOP);
        }

        if (getPixelY() + getPixelHeight() >= getParent().getPixelY() + getParent().getPixelHeight())
        {
            sides.add(Side.BOTTOM);
        }

        return sides;
    }

    /**
     * Occurs when the control stops being dragged.
     */
    public void onDragEnd(@Nonnull MousePosEvent mousePosEvent)
    {

    }

    /**
     * Forwards the call to {@link #onGlobalMouseClicked(MouseButtonEvent)} to the child controls before itself.
     */
    public void forwardGlobalMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        for (Control control : getChildrenCopy())
        {
            if (!mouseButtonEvent.isHandled())
            {
                control.forwardGlobalMouseClicked(mouseButtonEvent);
            }
        }

        if (!mouseButtonEvent.isHandled())
        {
            mouseButtonEvent.mouseX = Math.round(toLocalX(mouseButtonEvent.mousePixelX));
            mouseButtonEvent.mouseY = Math.round(toLocalY(mouseButtonEvent.mousePixelY));

            onGlobalMouseClicked(mouseButtonEvent);
        }
    }

    /**
     * Occurs when the mouse is clicked anywhere.
     */
    protected void onGlobalMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * Forwards the call to {@link #onMouseClicked(MouseButtonEvent)} to the child controls before itself.
     */
    public void forwardMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (!isInteractive())
        {
            return;
        }

        if (!collidesWith(mouseButtonEvent.mousePixelX, mouseButtonEvent.mousePixelY))
        {
            return;
        }

        for (Control control : getChildrenCopy())
        {
            if (!mouseButtonEvent.isHandled())
            {
                control.forwardMouseClicked(mouseButtonEvent);
            }
        }

        if (!mouseButtonEvent.isHandled())
        {
            mouseButtonEvent.mouseX = Math.round(toLocalX(mouseButtonEvent.mousePixelX));
            mouseButtonEvent.mouseY = Math.round(toLocalY(mouseButtonEvent.mousePixelY));

            onMouseClicked(mouseButtonEvent);

            if (mouseButtonEvent.isHandled())
            {
                if (getScreen() != null)
                {
                    // This probably isn't necessary, but we can set it back to unhandled anyway.
                    mouseButtonEvent.setIsHandled(false);

                    getScreen().setFocusedControl(this, mouseButtonEvent);
                    getScreen().setPressedControl(this, mouseButtonEvent);

                    // Set back to handled so that the screen can detect if a control handled the mouse clicked event.
                    mouseButtonEvent.setIsHandled(true);
                }
            }
        }
    }

    /**
     * Occurs when the mouse is clicked on the control.
     */
    protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        mouseButtonEvent.setIsHandled(true);
    }

    /**
     * Forwards the call to {@link #onGlobalMouseReleased(MouseButtonEvent)} to the child controls before itself.
     */
    public void forwardGlobalMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        for (Control control : getChildrenCopy())
        {
            if (!mouseButtonEvent.isHandled())
            {
                control.forwardGlobalMouseReleased(mouseButtonEvent);
            }
        }

        if (!mouseButtonEvent.isHandled())
        {
            mouseButtonEvent.mouseX = Math.round(toLocalX(mouseButtonEvent.mousePixelX));
            mouseButtonEvent.mouseY = Math.round(toLocalY(mouseButtonEvent.mousePixelY));

            onGlobalMouseReleased(mouseButtonEvent);
        }
    }

    /**
     * Occurs when the mouse is released anywhere.
     */
    protected void onGlobalMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * Forwards the call to {@link #onMouseReleased(MouseButtonEvent)} to the child controls before itself.
     */
    public void forwardMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (!isInteractive())
        {
            return;
        }

        if (!collidesWith(mouseButtonEvent.mousePixelX, mouseButtonEvent.mousePixelY))
        {
            return;
        }

        for (Control control : getChildrenCopy())
        {
            if (!mouseButtonEvent.isHandled())
            {
                control.forwardMouseReleased(mouseButtonEvent);
            }
        }

        if (!mouseButtonEvent.isHandled())
        {
            mouseButtonEvent.mouseX = Math.round(toLocalX(mouseButtonEvent.mousePixelX));
            mouseButtonEvent.mouseY = Math.round(toLocalY(mouseButtonEvent.mousePixelY));

            onMouseReleased(mouseButtonEvent);

            if (mouseButtonEvent.isHandled())
            {
                if (getScreen() != null && getScreen().getPressedControl() == null)
                {
                    // This probably isn't necessary, but we can set it back to unhandled anyway.
                    mouseButtonEvent.setIsHandled(false);

                    getScreen().setFocusedControl(this, mouseButtonEvent);

                    // Set back to handled so that the screen can detect if a control handled the mouse released event.
                    mouseButtonEvent.setIsHandled(true);
                }
            }
        }
    }

    /**
     * Occurs when the mouse is released on the control.
     */
    protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        mouseButtonEvent.setIsHandled(true);
    }

    /**
     * Occurs when the control is pressed from a mouse click.
     */
    public void onPressed(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * Occurs when the control is released from a mouse release.
     */
    public void onReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * Occurs when the control is focused from a mouse click.
     */
    public void onFocused(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * Occurs when the control is unfocused from a mouse click.
     */
    public void onUnfocused(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * Forwards the call to {@link #onGlobalMouseScrolled(MouseScrollEvent)} to the child controls before itself.
     */
    public void forwardGlobalMouseScrolled(@Nonnull MouseScrollEvent mouseScrollEvent)
    {
        for (Control control : getChildrenCopy())
        {
            if (!mouseScrollEvent.isHandled())
            {
                control.forwardGlobalMouseScrolled(mouseScrollEvent);
            }
        }

        if (!mouseScrollEvent.isHandled())
        {
            mouseScrollEvent.mouseX = Math.round(toLocalX(mouseScrollEvent.mousePixelX));
            mouseScrollEvent.mouseY = Math.round(toLocalY(mouseScrollEvent.mousePixelY));

            onGlobalMouseScrolled(mouseScrollEvent);
        }
    }

    /**
     * Occurs when the mouse is released anywhere.
     */
    protected void onGlobalMouseScrolled(@Nonnull MouseScrollEvent mouseScrollEvent)
    {

    }

    /**
     * Forwards the call to {@link #onMouseScrolled(MouseScrollEvent)} to the child controls before itself.
     */
    public void forwardMouseScrolled(@Nonnull MouseScrollEvent mouseScrollEvent)
    {
        if (!isInteractive())
        {
            return;
        }

        if (!collidesWith(mouseScrollEvent.mousePixelX, mouseScrollEvent.mousePixelY))
        {
            return;
        }

        for (Control control : getChildrenCopy())
        {
            if (!mouseScrollEvent.isHandled())
            {
                control.forwardMouseScrolled(mouseScrollEvent);
            }
        }

        if (!mouseScrollEvent.isHandled())
        {
            mouseScrollEvent.mouseX = Math.round(toLocalX(mouseScrollEvent.mousePixelX));
            mouseScrollEvent.mouseY = Math.round(toLocalY(mouseScrollEvent.mousePixelY));

            onMouseScrolled(mouseScrollEvent);
        }
    }

    /**
     * Occurs when the mouse is released on the control.
     */
    protected void onMouseScrolled(@Nonnull MouseScrollEvent mouseScrollEvent)
    {
        scrollControl(mouseScrollEvent);
    }

    /**
     * Uses the given scroll event and attempts to scroll the control.
     *
     * @param mouseScrollEvent the scroll event to use.
     */
    protected void scrollControl(@Nonnull MouseScrollEvent mouseScrollEvent)
    {
        float scrollAmount = (float) ((getScrollSpeed() / (getCumulativeScale() * getCumulativeScale())) * -1.0f * mouseScrollEvent.scrollAmount);

        if (GuiUtil.getInstance().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || GuiUtil.getInstance().isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))
        {
            if (isScrollableX())
            {
                if (scrollX(scrollAmount) != 0)
                {
                    mouseScrollEvent.setIsHandled(true);
                }
            }
        }
        else
        {
            if (isScrollableY())
            {
                if (scrollY(scrollAmount) != 0)
                {
                    mouseScrollEvent.setIsHandled(true);
                }
            }
        }
    }

    /**
     * @return the screen the control is a part of.
     */
    @Nullable
    public final IScreen getScreen()
    {
        return screen;
    }

    /**
     * Sets the screen the control is on and all its children,
     */
    protected final void setScreen(@Nullable IScreen screen)
    {
        this.screen = screen;

        for (Control control : getChildren())
        {
            control.setScreen(screen);
        }
    }

    /**
     * @return the current parent control.
     */
    @Nullable
    public final Control getParent()
    {
        return parent;
    }

    /**
     * Sets the current parent control to the given control.
     */
    public final void setParent(@Nullable Control parent)
    {
        if (this.parent == parent)
        {
            return;
        }

        Control oldParent = this.parent;

        this.parent = parent;

        if (oldParent != null)
        {
            oldParent.children.remove(this);

            setScreen(null);

            oldParent.onChildRemoved.handle(new ChildRemovedEvent(oldParent, this));
        }

        if (this.parent != null)
        {
            this.parent.children.add(this);

            setScreen(this.parent.getScreen());

            this.parent.onChildAdded.handle(new ChildAddedEvent(this.parent, this));
        }

        recalcPixelX();
        recalcPixelY();
        recalcPixelWidth();
        recalcPixelHeight();

        onParentChanged.handle(new ParentChangedEvent(this, parent));
    }

    /**
     * @return the list of children (be careful of concurrent modification).
     */
    @Nonnull
    public final List<Control> getChildren()
    {
        return children;
    }

    /**
     * @return a copy of the list of children.
     */
    @Nonnull
    public final List<Control> getChildrenCopy()
    {
        return new ArrayList<>(children);
    }

    /**
     * Inserts the given control before the given child control in the list of children.
     *
     * @param controlToInsert the control to insert (can be an existing child).
     * @param childToInsertBefore the child to insert the control before.
     */
    public final void insertChildBefore(@Nonnull Control controlToInsert, @Nonnull Control childToInsertBefore)
    {
        if (childToInsertBefore.getParent() != this)
        {
            throw new IllegalArgumentException("The child to insert before was not a child.");
        }

        if (controlToInsert.getParent() != this)
        {
            addChild(controlToInsert);
        }

        if (children.indexOf(childToInsertBefore) == children.indexOf(controlToInsert))
        {
            return;
        }

        children.remove(controlToInsert);
        children.add(children.indexOf(childToInsertBefore), controlToInsert);

        onChildrenReordered.handle(new ChildrenReorderedEvent(this));
    }

    /**
     * Inserts the given control after the given child control in the list of children.
     *
     * @param controlToInsert the control to insert (can be an existing child).
     * @param childToInsertAfter the child to insert the control after.
     */
    public final void insertChildAfter(@Nonnull Control controlToInsert, @Nonnull Control childToInsertAfter)
    {
        if (childToInsertAfter.getParent() != this)
        {
            throw new IllegalArgumentException("The child to insert after was not a child.");
        }

        if (controlToInsert.getParent() != this)
        {
            addChild(controlToInsert);
        }

        if (children.indexOf(childToInsertAfter) + 1 == children.indexOf(controlToInsert))
        {
            return;
        }

        children.remove(controlToInsert);
        children.add(children.indexOf(childToInsertAfter) + 1, controlToInsert);

        onChildrenReordered.handle(new ChildrenReorderedEvent(this));
    }

    /**
     * Adds the given control as a child. This will change the parent of the given control too.
     */
    public final void addChild(@Nonnull Control control)
    {
        control.setParent(this);
    }

    /**
     * Removes the given control as a child. This will remove the parent from the given control too.
     */
    public final void removeChild(@Nonnull Control control)
    {
        control.setParent(null);
    }

    /**
     * @return whether the given control is a descendant.
     */
    public final boolean hasDescendant(@Nullable Control control)
    {
        if (control == null)
        {
            return false;
        }

        return getChildren().contains(control) || getChildren().stream().anyMatch(child -> child.hasDescendant(control));
    }

    /**
     * @return the x pixel position of the top left of the control on the screen.
     */
    public final int getPixelX()
    {
        return pixelX;
    }

    /**
     * Recalculates the value of {@link #pixelX}.
     */
    public void recalcPixelX()
    {
        if (getParent() != null)
        {
            pixelX = Math.round(getParent().getPixelX() + (getX() * getParent().getCumulativeInnerScale() * GuiUtil.getInstance().getGuiScale()));
        }
        else
        {
            pixelX = Math.round(getX() * GuiUtil.getInstance().getGuiScale());
        }

        children.forEach(Control::recalcPixelX);
    }

    /**
     * @return the y position of the top left of the control on the screen.
     */
    public final int getPixelY()
    {
        return pixelY;
    }

    /**
     * Recalculates the value of {@link #pixelY}.
     */
    public void recalcPixelY()
    {
        if (getParent() != null)
        {
            pixelY = Math.round(getParent().getPixelY() + (getY() * getParent().getCumulativeInnerScale() * GuiUtil.getInstance().getGuiScale()));
        }
        else
        {
            pixelY = Math.round(getY() * GuiUtil.getInstance().getGuiScale());
        }

        children.forEach(Control::recalcPixelY);
    }

    /**
     * @return the x position of the middle of the control, including margins.
     */
    public final float getMidX()
    {
        return getX() + getEffectiveWidth() / 2;
    }

    /**
     * @return the scaled local x position of the control.
     */
    public float getX()
    {
        return x;
    }

    /**
     * Sets the scaled local x position of the control.
     */
    public final void setX(float x)
    {
        PositionChangedEvent event = onPositionChanged.handle(new PositionChangedEvent(this, x, getY()));

        if (!event.isHandled())
        {
            this.x = x;

            recalcPixelX();
        }
    }

    /**
     * Moves the control by the given amount in the x-axis.
     */
    public final void moveX(float dx)
    {
        setX(getX() + dx);
    }

    /**
     * Aligns the control horizontally within the parent the given percentage respecting padding and
     * margins. So 0.5f would be the center of the control, 0.1f would be 10% from the left.
     */
    public final void setPercentX(float percent)
    {
        percent = Math.min(1.0f, Math.max(0.0f, percent));

        if (getParent() != null)
        {
            setX(getParent().getPadding(Side.LEFT) + (((getParent().getWidth() / getParent().getInnerScale()) - getParent().getPadding(Side.LEFT) - getParent().getPadding(Side.RIGHT) - getEffectiveWidth()) * percent));
        }
    }

    /**
     * @return the y position of the middle of the control, including margins.
     */
    public final float getMidY()
    {
        return getY() + getEffectiveHeight() / 2;
    }

    /**
     * @return the scaled local y position of the control.
     */
    public final float getY()
    {
        return y;
    }

    /**
     * Sets the scaled local y position of the control.
     */
    public final void setY(float y)
    {
        PositionChangedEvent event = onPositionChanged.handle(new PositionChangedEvent(this, getX(), y));

        if (!event.isHandled())
        {
            this.y = y;

            recalcPixelY();
        }
    }

    /**
     * Moves the control by the given amount in the y-axis.
     */
    public final void moveY(float dy)
    {
        setY(getY() + dy);
    }

    /**
     * Aligns the control vertically within the parent the given percentage respecting padding and
     * margins. So 0.5f would be the center of the control, 0.1f would be 10% from the top.
     */
    public final void setPercentY(float percent)
    {
        percent = Math.min(1.0f, Math.max(0.0f, percent));

        if (getParent() != null)
        {
            setY(getParent().getPadding(Side.TOP) + (((getParent().getHeight() / getParent().getInnerScale()) - getParent().getPadding(Side.TOP) - getParent().getPadding(Side.BOTTOM) - getEffectiveHeight()) * percent));
        }
    }

    /**
     * @return the pixel width of the control including margins.
     */
    public int getEffectivePixelWidth()
    {
        return Math.round((getWidth() + getMargin(Side.LEFT) + getMargin(Side.RIGHT)) * getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
    }

    /**
     * @return the pixel width of the control.
     */
    public int getPixelWidth()
    {
        return pixelWidth;
    }

    /**
     * Recalculates the pixel width of the control.
     */
    public final void recalcPixelWidth()
    {
        pixelWidth = Math.round(getWidth() * getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
    }

    /**
     * @return the pixel height of the control including margins.
     */
    public int getEffectivePixelHeight()
    {
        return Math.round((getHeight() + getMargin(Side.TOP) + getMargin(Side.BOTTOM)) * getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
    }

    /**
     * @return the pixel height of the control.
     */
    public int getPixelHeight()
    {
        return pixelHeight;
    }

    /**
     * Recalculates the pixel height of the control.
     */
    public final void recalcPixelHeight()
    {
        pixelHeight = Math.round(getHeight() * getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
    }

    /**
     * @return the width of the control including margins.
     */
    public float getEffectiveWidth()
    {
        return getWidth() + getMargin(Side.LEFT) + getMargin(Side.RIGHT);
    }

    /**
     * @return the scaled width of the control.
     */
    public float getWidth()
    {
        return width;
    }

    /**
     * Sets the scaled width of the control.
     */
    public void setWidth(float width)
    {
        if (width == this.width)
        {
            return;
        }

        float oldWidth = this.width;

        width = Math.max(0, width);
        width = getMaxWidth() >= 0 ? Math.min(width, getMaxWidth()) : width;

        if (autoSizeHitbox)
        {
            hitbox.resize(this.width, width, height, height);
        }

        float finalWidth = width;
        getChildren().forEach(child -> child.tryTransformFromAnchorWhenParentResized(finalWidth, getHeight()));

        this.width = width;

        recalcPixelWidth();

        onSizeChanged.handle(new SizeChangedEvent(this, oldWidth, getHeight()));
    }

    /**
     * @return the height of the control including margins.
     */
    public float getEffectiveHeight()
    {
        return getHeight() + getMargin(Side.TOP) + getMargin(Side.BOTTOM);
    }

    /**
     * @return the scaled height of the control.
     */
    public float getHeight()
    {
        return height;
    }

    /**
     * Sets the scaled with of the control.
     */
    public void setHeight(float height)
    {
        if (height == this.height)
        {
            return;
        }

        float oldHeight = this.height;

        height = Math.max(0, height);
        height = getMaxHeight() >= 0 ? Math.min(height, getMaxHeight()) : height;

        if (autoSizeHitbox)
        {
            hitbox.resize(width, width, this.height, height);
        }

        float finalHeight = height;
        getChildren().forEach(child -> child.tryTransformFromAnchorWhenParentResized(getWidth(), finalHeight));

        this.height = height;

        recalcPixelHeight();

        onSizeChanged.handle(new SizeChangedEvent(this, oldHeight, getHeight()));
    }

    /**
     * @return the max width of the control.
     */
    public int getMaxWidth()
    {
        return maxWidth;
    }

    /**
     * Sets the max width of the control. Set to -1 to ignore.
     */
    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth < 0 ? Integer.MAX_VALUE : maxWidth;

        tryFitToContents();
    }

    /**
     * @return the max height of the control.
     */
    public int getMaxHeight()
    {
        return maxHeight;
    }

    /**
     * Sets the max height of the control. Set to -1 to ignore.
     */
    public void setMaxHeight(int maxHeight)
    {
        this.maxHeight = maxHeight < 0 ? Integer.MAX_VALUE : maxHeight;

        tryFitToContents();
    }

    /**
     * @return the margin of the control for the given side.
     */
    public int getMargin(@Nonnull Side side)
    {
        return margins.get(side);
    }

    /**
     * @return the margins of the control.
     */
    @Nonnull
    public Map<Side, Integer> getMargins()
    {
        return new HashMap<>(margins);
    }

    /**
     * Sets the margin of the control for the given side.
     */
    public void setMargin(@Nonnull Side side, int margin)
    {
        switch (side)
        {
            case LEFT: setMargins(margin, getMargin(Side.TOP), getMargin(Side.RIGHT), getMargin(Side.BOTTOM)); break;
            case TOP: setMargins(getMargin(Side.LEFT), margin, getMargin(Side.RIGHT), getMargin(Side.BOTTOM)); break;
            case RIGHT: setMargins(getMargin(Side.LEFT), getMargin(Side.TOP), margin, getMargin(Side.BOTTOM)); break;
            case BOTTOM: setMargins(getMargin(Side.LEFT), getMargin(Side.TOP), getMargin(Side.RIGHT), margin); break;
        }
    }

    /**
     * Sets the margins of the control.
     */
    public void setMargins(int left, int top, int right, int bottom)
    {
        Map<Side, Integer> prevMargins = new HashMap<>(margins);

        margins.put(Side.LEFT, left);
        margins.put(Side.TOP, top);
        margins.put(Side.RIGHT, right);
        margins.put(Side.BOTTOM, bottom);

        onMarginsChanged.handle(new MarginsChangedEvent(this, prevMargins.get(Side.LEFT), prevMargins.get(Side.TOP), prevMargins.get(Side.RIGHT), prevMargins.get(Side.BOTTOM)));
    }

    /**
     * @return the padding of the control for the given side.
     */
    public int getPadding(@Nonnull Side side)
    {
        return padding.get(side);
    }

    /**
     * @return the padding of the control.
     */
    @Nonnull
    public Map<Side, Integer> getPadding()
    {
        return new HashMap<>(padding);
    }

    /**
     * Sets the padding of the control for the given side.
     */
    public void setPadding(@Nonnull Side side, int padding)
    {
        switch (side)
        {
            case LEFT: setPadding(padding, getPadding(Side.TOP), getPadding(Side.RIGHT), getPadding(Side.BOTTOM)); break;
            case TOP: setPadding(getPadding(Side.LEFT), padding, getPadding(Side.RIGHT), getPadding(Side.BOTTOM)); break;
            case RIGHT: setPadding(getPadding(Side.LEFT), getPadding(Side.TOP), padding, getPadding(Side.BOTTOM)); break;
            case BOTTOM: setPadding(getPadding(Side.LEFT), getPadding(Side.TOP), getPadding(Side.RIGHT), padding); break;
        }
    }

    /**
     * Sets the padding for the control.
     */
    public void setPadding(int left, int top, int right, int bottom)
    {
        PaddingChangedEvent event = onPaddingChanged.handle(new PaddingChangedEvent(this, left, top, right, bottom));

        if (!event.isHandled())
        {
            padding = event.getNewPadding();
        }
    }

    /**
     * @return the anchor for the control.
     */
    @Nullable
    public EnumSet<Side> getAnchor()
    {
        return anchor;
    }

    /**
     * Sets the anchor for the control.
     */
    public void setAnchor(@Nullable EnumSet<Side> anchor)
    {
        AnchorChangedEvent event = onAnchorChanged.handle(new AnchorChangedEvent(this, anchor));

        if (!event.isHandled())
        {
            this.anchor = anchor;
        }
    }

    /**
     * @return the cumulative scale of the control, i.e. effectively the cumulative inner scale of the parent.
     */
    public final float getCumulativeScale()
    {
        if (getParent() != null)
        {
            return getParent().getCumulativeInnerScale();
        }
        else
        {
            return 1.0f;
        }
    }

    /**
     * @return the cumulative inner scale of the control.
     */
    public final float getCumulativeInnerScale()
    {
        return cumulativeInnerScale;
    }

    /**
     * Recalculates the cumulative inner scale of the control.
     */
    public void recalcCumulativeInnerScale()
    {
        if (getParent() != null)
        {
            cumulativeInnerScale = getInnerScale() * getParent().getCumulativeInnerScale();
        }
        else
        {
            cumulativeInnerScale = getInnerScale();
        }

        children.forEach((control) ->
        {
            control.recalcCumulativeInnerScale();
            control.recalcPixelX();
            control.recalcPixelY();
            control.recalcPixelWidth();
            control.recalcPixelHeight();
        });
    }

    /**
     * @return the inner scale of the control.
     */
    public float getInnerScale()
    {
        return innerScale;
    }

    /**
     * Sets the inner scale of the control.
     */
    public void setInnerScale(float innerScale)
    {
        InnerScaleChangedEvent event = onInnerScaleChanged.handle(new InnerScaleChangedEvent(this, innerScale));

        if (!event.isHandled())
        {
            this.innerScale = innerScale;

            recalcCumulativeInnerScale();
        }
    }

    /**
     * @return converts a pixel x coordinate into a local x coordinate.
     */
    public float toLocalX(float pixelX)
    {
        return (pixelX - getPixelX()) / (getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
    }

    /**
     * @return converts a pixel y coordinate into a local y coordinate.
     */
    public float toLocalY(float pixelY)
    {
        return (pixelY - getPixelY()) / (getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
    }

    /**
     * @return whether the control is automatically sized to fit its contents in both axes.
     */
    public boolean shouldFitToContentsXY()
    {
        return shouldFitToContentsX() && shouldFitToContentsY();
    }

    /**
     * Sets whether the control is automatically sized to fit its contents in both axes.
     */
    public void setFitToContentsXY(boolean fitToContentsXY)
    {
        setFitToContentsX(fitToContentsXY);
        setFitToContentsY(fitToContentsXY);
    }

    /**
     * @return whether the control is automatically sized to fit its contents in the x-axis.
     */
    public boolean shouldFitToContentsX()
    {
        return fitToContentsX;
    }

    /**
     * Sets whether the control is automatically sized to fit its contents in the x-axis.
     */
    public void setFitToContentsX(boolean fitToContentsX)
    {
        this.fitToContentsX = fitToContentsX;

        tryFitToContents();
    }

    /**
     * @return whether the control is automatically sized to fit its contents in the y-axis.
     */
    public boolean shouldFitToContentsY()
    {
        return fitToContentsY;
    }

    /**
     * Sets whether the control is automatically sized to fit its contents in the y-axis.
     */
    public void setFitToContentsY(boolean fitToContentsY)
    {
        this.fitToContentsY = fitToContentsY;

        tryFitToContents();
    }

    /**
     * @return whether to auto resize the hitbox when the control's size changes.
     */
    public boolean isAutoSizeHitbox()
    {
        return autoSizeHitbox;
    }

    /**
     * Sets whether to auto resize the hitbox when the control's size changes.
     */
    public void setAutoSizeHitbox(boolean autoSizeHitbox)
    {
        this.autoSizeHitbox = autoSizeHitbox;
    }

    /**
     * @return the hitbox of the control.
     */
    @Nonnull
    public Hitbox getHitbox()
    {
        return hitbox;
    }

    /**
     * Sets the hitbox of the control.
     */
    public void setHitbox(@Nonnull Hitbox hitbox)
    {
        HitboxChangedEvent event = onHitboxChanged.handle(new HitboxChangedEvent(this, hitbox));

        if (!event.isHandled())
        {
            this.hitbox = hitbox;
        }
    }

    /**
     * @return whether the given coordinates collide with the control's hitbox.
     */
    public boolean collidesWith(int pixelX, int pixelY)
    {
        return hitbox.collidesWith(this, pixelX, pixelY);
    }

    /**
     * @return whether the control is rendered.
     */
    public boolean isVisible()
    {
        return isVisible;
    }

    /**
     * Sets whether the control is rendered.
     */
    public void setVisible(boolean visible)
    {
        isVisible = visible;
    }

    /**
     * @return whether the control can interact with input events.
     */
    public boolean isInteractive()
    {
        return isInteractive;
    }

    /**
     * Sets whether the control can interact with input events.
     */
    public void setInteractive(boolean interactive)
    {
        isInteractive = interactive;
    }

    /**
     * @return whether this control blocks dragging of child controls.
     */
    public boolean blocksDrag()
    {
        return blocksDrag;
    }

    /**
     * Sets whether this control blocks dragging of child controls.
     */
    public void setBlocksDrag(boolean blocksDrag)
    {
        this.blocksDrag = blocksDrag;
    }

    /**
     * @return whether the control is draggable in the x and y axes.
     */
    public boolean isDraggableXY()
    {
        return isDraggableX() && isDraggableY();
    }

    /**
     * Sets whether the control is draggable in the x and y axes.
     */
    public void setDraggableXY(boolean draggableXY)
    {
        setDraggableX(draggableXY);
        setDraggableY(draggableXY);
    }

    /**
     * @return whether the control is draggable in the x-axis.
     */
    public boolean isDraggableX()
    {
        return isDraggableX;
    }

    /**
     * Sets whether the control is draggable in the x-axis.
     */
    public void setDraggableX(boolean draggableX)
    {
        isDraggableX = draggableX;
    }

    /**
     * @return whether the control is draggable in the y-axis.
     */
    public boolean isDraggableY()
    {
        return isDraggableY;
    }

    /**
     * Sets whether the control is draggable in the y-axis.
     */
    public void setDraggableY(boolean draggableY)
    {
        isDraggableY = draggableY;
    }

    /**
     * @return the distance the mouse needs to move while pressed to start dragging.
     */
    public int getDragThreshold()
    {
        return dragThreshold;
    }

    /**
     * Sets the distance the mouse needs to move while pressed to start dragging.
     */
    public void setDragThreshold(int dragThreshold)
    {
        this.dragThreshold = dragThreshold;
    }

    /**
     * @return whether the control is scrollable in both axes.
     */
    public boolean isScrollableXY()
    {
        return isScrollableX() && isScrollableY();
    }

    /**
     * Sets whether the control is scrollable in both axes.
     */
    public void setScrollableXY(boolean scrollableXY)
    {
        setScrollableX(scrollableXY);
        setScrollableY(scrollableXY);
    }

    /**
     * @return whether the control is scrollable in the x-axis.
     */
    public boolean isScrollableX()
    {
        return isScrollableX;
    }

    /**
     * Sets whether the control is scrollable in the x-axis.
     */
    public void setScrollableX(boolean scrollableX)
    {
        isScrollableX = scrollableX;
    }

    /**
     * @return whether the control is scrollable in the y-axis.
     */
    public boolean isScrollableY()
    {
        return isScrollableY;
    }

    /**
     * Sets whether the control is scrollable in the y-axis.
     */
    public void setScrollableY(boolean scrollableY)
    {
        isScrollableY = scrollableY;
    }

    /**
     * @return the multiplier used to adjust the amount scrolled.
     */
    public float getScrollSpeed()
    {
        return scrollSpeed;
    }

    /**
     * Sets the multiplier used to adjust the amount scrolled.
     */
    public void setScrollSpeed(float scrollSpeed)
    {
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * @return the scroll offset in the x-axis.
     */
    public float getScrollOffsetX()
    {
        return scrollOffsetX;
    }

    /**
     * Increments the scroll offset in the x-axis by the given amount.
     *
     * @return the amount actually scrolled.
     */
    public float scrollX(float scrollAmount)
    {
        return setScrollOffsetX(getScrollOffsetX() + scrollAmount);
    }

    /**
     * Sets the scroll offset in the x-axis.
     *
     * @return the amount scrolled.
     */
    public float setScrollOffsetX(float scrollOffsetX)
    {
        float prevOffset = this.scrollOffsetX;

        this.scrollOffsetX = Math.max(0, Math.min(getMaxScrollOffsetX(), scrollOffsetX));

        float amountScrolled = this.scrollOffsetX - prevOffset;

        getChildrenCopy().forEach(control -> control.setX(control.getX() - amountScrolled));

        return amountScrolled;
    }

    /**
     * @return the scroll offset in the y-axis.
     */
    public float getScrollOffsetY()
    {
        return scrollOffsetY;
    }

    /**
     * Increments the scroll offset in the y-axis by the given amount.
     *
     * @return the amount actually scrolled.
     */
    public float scrollY(float scrollAmount)
    {
        return setScrollOffsetY(getScrollOffsetY() + scrollAmount);
    }

    /**
     * Sets the scroll offset in the y-axis.
     *
     * @return the amount scrolled.
     */
    public float setScrollOffsetY(float scrollOffsetY)
    {
        float prevOffset = this.scrollOffsetY;

        this.scrollOffsetY = Math.max(0, Math.min(getMaxScrollOffsetY(), scrollOffsetY));

        float amountScrolled = this.scrollOffsetY - prevOffset;

        getChildrenCopy().forEach(control -> control.setY(control.getY() - amountScrolled));

        return amountScrolled;
    }

    /**
     * @return the max scroll offset in the x-axis.
     */
    public int getMaxScrollOffsetX()
    {
        return maxScrollOffsetX;
    }

    /**
     * Sets the max scroll offset in the x-axis.
     */
    public void setMaxScrollOffsetX(int maxScrollOffsetX)
    {
        this.maxScrollOffsetX = Math.max(0, maxScrollOffsetX);

        if (getScrollOffsetX() > getMaxScrollOffsetX())
        {
            setScrollOffsetX(getMaxScrollOffsetX());
        }
    }

    /**
     * @return the max scroll offset in the y-axis.
     */
    public int getMaxScrollOffsetY()
    {
        return maxScrollOffsetY;
    }

    /**
     * Sets the max scroll offset in the y-axis.
     */
    public void setMaxScrollOffsetY(int maxScrollOffsetY)
    {
        this.maxScrollOffsetY = Math.max(0, maxScrollOffsetY);

        if (getScrollOffsetY() > getMaxScrollOffsetY())
        {
            setScrollOffsetY(getMaxScrollOffsetY());
        }
    }

    /**
     * @return the background colour.
     */
    @Nonnull
    public Colour getBackgroundColour()
    {
        return backgroundColour;
    }

    /**
     * Sets the background colour.
     */
    public void setBackgroundColour(@Nonnull Colour backgroundColour)
    {
        this.backgroundColour = backgroundColour;
    }

    /**
     * @return whether the control is currently hovered over.
     */
    public boolean isHovered()
    {
        return getScreen().getHoveredControl() == this;
    }

    /**
     * @return whether the control is currently pressed.
     */
    public boolean isPressed()
    {
        return getScreen().getPressedControl() == this;
    }

    /**
     * @return whether the control is currently focused.
     */
    public boolean isFocused()
    {
        return getScreen().getFocusedControl() == this;
    }

    /**
     * @return whether the control is currently being dragged.
     */
    public boolean isDragging()
    {
        return getScreen().getDraggedControl() == this;
    }

    /**
     * @return whether the control or its ancestor is currently being dragged.
     */
    public boolean isDraggingOrAncestorIsDragging()
    {
        if (isDragging())
        {
            return true;
        }

        if (getParent() == null)
        {
            return false;
        }

        return getParent().isDraggingOrAncestorIsDragging();
    }

    /**
     * Represents an operation and an undo operation to be performed before and after rendering.
     */
    public static class RenderOperation
    {
        /**
         * The operation to perform before rendering.
         */
        @Nonnull
        public final BiConsumer<Control, RenderArgs> preRenderOperation;

        /**
         * The operation to perform after rendering.
         */
        @Nonnull
        public final BiConsumer<Control, RenderArgs> postRenderOperation;

        /**
         * @param preRenderOperation the operation to perform before rendering.
         * @param postRenderOperation the operation to perform after rendering.
         */
        public RenderOperation(@Nullable BiConsumer<Control, RenderArgs> preRenderOperation, @Nullable BiConsumer<Control, RenderArgs> postRenderOperation)
        {
            this.preRenderOperation = preRenderOperation != null ? preRenderOperation : (control, renderArgs) -> {};
            this.postRenderOperation = postRenderOperation != null ? postRenderOperation : (control, renderArgs) -> {};
        }
    }
}
