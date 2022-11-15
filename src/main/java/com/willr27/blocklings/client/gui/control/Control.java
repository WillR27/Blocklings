package com.willr27.blocklings.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.Gui;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.ScissorBounds;
import com.willr27.blocklings.client.gui.control.event.events.*;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    private int x = 0;

    /**
     * The scaled local y position of the control relative to the parent, i.e. (0, 0) would be the top left corner
     * of the parent control regardless of where the parent is on the screen.
     */
    private int y = 0;

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
    private int width = 100;

    /**
     * The scaled height of the control relative to the parent, i.e. 100 x 100 would be 200 x 200 pixels if the
     * cumulative product of the parent control's inner scale was 2.0f.
     */
    private int height = 100;

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
    @Nonnull
    private Optional<EnumSet<Side>> anchor = Optional.empty();

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
    public boolean isVisible = true;

    /**
     * Whether the control can interact with any input events.
     */
    public boolean isInteractive = true;

    /**
     */
    public Control()
    {
        for (Side side : Side.values())
        {
            margins.put(side, 0);
            padding.put(side, 0);
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
        renderArgs.matrixStack.translate((getPixelX() / scale) - getPixelX(), (getPixelY() / scale) - getPixelY(), 0.0f);
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
     * Forwards the call to {@link #onHover(MousePosEvent)} to the control's children.
     *
     * @param mousePosEvent the mouse position event.
     */
    public final void forwardHover(@Nonnull MousePosEvent mousePosEvent)
    {
        if (!isInteractive())
        {
            return;
        }

        for (Control control : getChildrenCopy())
        {

        }

        onHover(mousePosEvent);
    }

    /**
     *
     */
    public void onHover(@Nonnull MousePosEvent mousePosEvent)
    {

    }

    /**
     * Forwards the call to {@link #render(RenderArgs)} to the control's children after rendering
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
        render(renderArgs);
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
     * Renders the control.
     *
     * @param renderArgs the render args.
     */
    protected void render(@Nonnull RenderArgs renderArgs)
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
     * @return the current parent control.
     */
    @Nullable
    public Control getParent()
    {
        return parent;
    }

    /**
     * Sets the current parent control to the given control.
     */
    public void setParent(@Nullable Control parent)
    {
        if (this.parent == parent)
        {
            return;
        }

        ParentChangedEvent event = onParentChanged.handle(new ParentChangedEvent(this, parent));

        if (!event.isHandled())
        {
            Control oldParent = this.parent;

            this.parent = parent;

            if (oldParent != null)
            {
                oldParent.children.remove(this);
            }

            if (this.parent != null)
            {
                this.parent.addChild(this);
            }

            recalcPixelX();
            recalcPixelY();
            recalcPixelWidth();
            recalcPixelHeight();
        }
    }

    /**
     * @return a copy of the list of children.
     */
    @Nonnull
    public List<Control> getChildrenCopy()
    {
        return new ArrayList<>(children);
    }

    /**
     * Adds the given control as a child. This will change the parent of the given control too.
     */
    public void addChild(@Nonnull Control control)
    {
        if (children.contains(control))
        {
            return;
        }

        ChildAddedEvent event = onChildAdded.handle(new ChildAddedEvent(this, control));

        if (!event.isHandled())
        {
            control.parent = this;

            children.add(control);

            control.recalcPixelX();
            control.recalcPixelY();
            control.recalcPixelWidth();
            control.recalcPixelHeight();
        }
    }

    /**
     * Removes the given control as a child. This will remove the parent from the given control too.
     */
    public void removeChild(@Nonnull Control control)
    {
        if (!children.contains(control))
        {
            return;
        }

        ChildRemovedEvent event = onChildRemoved.handle(new ChildRemovedEvent(this, control));

        if (!event.isHandled())
        {
            control.parent = null;

            children.remove(control);

            control.recalcPixelX();
            control.recalcPixelY();
            control.recalcPixelWidth();
            control.recalcPixelHeight();
        }
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
            pixelX = (int) (getParent().getPixelX() + (getX() * getParent().getCumulativeInnerScale() * GuiUtil.getInstance().getGuiScale()));
        }
        else
        {
            pixelX = getX();
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
            pixelY = (int) (getParent().getPixelY() + (getY() * getParent().getCumulativeInnerScale() * GuiUtil.getInstance().getGuiScale()));
        }
        else
        {
            pixelY = getY();
        }

        children.forEach(Control::recalcPixelY);
    }

    /**
     * @return the scaled local x position of the control.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Sets the scaled local x position of the control.
     */
    public void setX(int x)
    {
        PositionChangedEvent event = onPositionChanged.handle(new PositionChangedEvent(this, x, getY()));

        if (!event.isHandled())
        {
            this.x = x;

            recalcPixelX();
        }
    }

    /**
     * @return the scaled local y position of the control.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Sets the scaled local y position of the control.
     */
    public void setY(int y)
    {
        PositionChangedEvent event = onPositionChanged.handle(new PositionChangedEvent(this, getX(), y));

        if (!event.isHandled())
        {
            this.y = y;

            recalcPixelY();
        }
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
        pixelWidth = (int) (getWidth() * getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
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
        pixelHeight = (int) (getHeight() * getCumulativeScale() * GuiUtil.getInstance().getGuiScale());
    }

    /**
     * @return the scaled width of the control.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Sets the scaled width of the control.
     */
    public void setWidth(int width)
    {
        width = Math.max(0, width);

        SizeChangedEvent event = onSizeChanged.handle(new SizeChangedEvent(this, width, getHeight()));

        if (!event.isHandled())
        {
            if (autoSizeHitbox)
            {
                hitbox.resize(this.width, width, height, height);
            }

            this.width = width;

            recalcPixelWidth();
        }
    }

    /**
     * @return the scaled height of the control.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Sets the scaled with of the control.
     */
    public void setHeight(int height)
    {
        height = Math.max(0, height);

        SizeChangedEvent event = onSizeChanged.handle(new SizeChangedEvent(this, getWidth(), height));

        if (!event.isHandled())
        {
            if (autoSizeHitbox)
            {
                hitbox.resize(width, width, this.height, height);
            }

            this.height = height;

            recalcPixelHeight();
        }
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
        MarginsChangedEvent event = onMarginsChanged.handle(new MarginsChangedEvent(this, left, top, right, bottom));

        if (!event.isHandled())
        {
            margins = event.getNewMargins();
        }
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
    @Nonnull
    public Optional<EnumSet<Side>> getAnchor()
    {
        return anchor;
    }

    /**
     * Sets the anchor for the control.
     */
    public void setAnchor(@Nonnull Optional<EnumSet<Side>> anchor)
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
    public boolean collidesWith(int screenX, int screenY)
    {
        return hitbox.collidesWith(this, screenX, screenY);
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
