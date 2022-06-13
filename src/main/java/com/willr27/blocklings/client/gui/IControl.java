package com.willr27.blocklings.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.util.event.Event;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents some kind of control (including the screen itself).
 */
@OnlyIn(Dist.CLIENT)
public interface IControl
{
    default void setupEventHandlers()
    {
        getOnControlHover().subscribe(this::controlHover);
        getOnControlHoverStart().subscribe(this::controlHoverStart);
        getOnControlHoverStop().subscribe(this::controlHoverStop);

        getOnControlMouseClicked().subscribe(this::controlMouseClicked);
        getOnControlMouseReleased().subscribe(this::controlMouseReleased);
        getOnControlMouseScrolled().subscribe(this::controlMouseScrolled);

        getOnControlKeyPressed().subscribe(this::controlKeyPressed);
        getOnControlKeyReleased().subscribe(this::controlKeyReleased);
        getOnControlKeyHeld().subscribe(this::controlKeyHeld);
        getOnControlCharTyped().subscribe(this::controlCharTyped);
    }

    /**
     * Tries to forward a mouse hover event to the control.
     */
    default void forwardControlHover(@Nonnull MouseEvent e)
    {
        if (!isVisible())
        {
            return;
        }

        if (!isMouseOver(e.mouseX, e.mouseY))
        {
            return;
        }

        if (getReverseChildrenCopy().stream().anyMatch(control -> { control.forwardControlHover(e); return e.isHandled(); }))
        {
            return;
        }

        if (!isInteractive())
        {
            return;
        }

        setIsHovered(true, e.mouseX, e.mouseY);

        getOnControlHover().handle(e);
    }

    /**
     * Called when the mouse is hovered over the control.
     */
    default void controlHover(@Nonnull MouseEvent e)
    {
        e.setIsHandled(true);
    }

    /**
     * Called when the mouse starts hovering over the control.
     */
    default void controlHoverStart(@Nonnull MouseEvent e)
    {
        e.setIsHandled(true);
    }

    /**
     * Called when the mouse stops hovering over the control.
     */
    default void controlHoverStop(@Nonnull MouseEvent e)
    {
        e.setIsHandled(true);
    }

    /**
     * Calls all tick methods each tick.
     * This is useful for things like ticking the cursor in a text field.
     */
    default void tickAll()
    {
        if (!isVisible())
        {
            return;
        }

        if (getParent() != null)
        {
            tick();
        }

        getChildrenCopy().forEach(IControl::tickAll);
    }

    /**
     * Called each tick.
     * This is useful for things like ticking the cursor in a text field.
     */
    default void tick()
    {

    }

    /**
     * Calls all preRender methods before any render methods are called each frame.
     * This is useful for things like updating the position of the control.
     *
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     * @param partialTicks the fraction of ticks that have occurred since the last frame.
     */
    default void preRenderAll(int mouseX, int mouseY, float partialTicks)
    {
        if (isVisible())
        {
            preRender(mouseX, mouseY, partialTicks);

            getChildrenCopy().forEach(control -> control.preRenderAll(mouseX, mouseY, partialTicks));
        }
    }

    /**
     * Called before the control is rendered.
     * This is useful for things like updating the position of the control.
     *
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     * @param partialTicks the fraction of ticks that have occurred since the last frame.
     */
    default void preRender(int mouseX, int mouseY, float partialTicks)
    {

    }

    /**
     * Renders the control and child controls.
     *
     * @param matrixStack the current matrix stack.
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     * @param partialTicks the fraction of ticks that have occurred since the last frame.
     */
    default void renderAll(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        matrixStack.pushPose();

        if (getParent() == null)
        {
            GuiUtil.clearScissorBounds();
        }
        else
        {
            getParent().enableScissor();

            RenderSystem.enableDepthTest();

            if (isVisible())
            {
                // Scale the control, but also make sure to cancel out the translation caused by the scaling.
                matrixStack.scale(getEffectiveScale(), getEffectiveScale(), 1.0f);
                matrixStack.translate((getScreenX() / getEffectiveScale()) - getScreenX(), (getScreenY() / getEffectiveScale()) - getScreenY(), 0.0f);

                render(matrixStack, mouseX, mouseY, partialTicks);

                // Revert the previous transformations.
                matrixStack.translate(getScreenX() - (getScreenX() / getEffectiveScale()), getScreenY() - (getScreenY() / getEffectiveScale()), 0.0f);
                matrixStack.scale(1.0f / getEffectiveScale(), 1.0f / getEffectiveScale(), 1.0f);
            }
        }

        if (isVisible())
        {
            getChildrenCopy().forEach(control -> control.renderAll(matrixStack, mouseX, mouseY, partialTicks));
        }

        matrixStack.popPose();

        if (getParent() != null)
        {
            getParent().disableScissor();
        }
    }

    /**
     * Renders the control.
     *
     * @param matrixStack the current matrix stack.
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     * @param partialTicks the fraction of ticks that have occurred since the last frame.
     */
    default void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {

    }

    /**
     * Renders the control's tooltip when the control is hovered.
     *
     * @param matrixStack the current matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     */
    default void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {

    }

    /**
     * Forwards a global mouse clicked event.
     */
    default void forwardGlobalMouseClicked(@Nonnull MouseButtonEvent e)
    {
        getReverseChildrenCopy().forEach(control -> control.forwardGlobalMouseClicked(e));

        globalMouseClicked(e);
    }

    /**
     * Called when the mouse is clicked anywhere on the screen.
     */
    default void globalMouseClicked(@Nonnull MouseButtonEvent e)
    {

    }

    /**
     * Tries to forward a mouse clicked event to the control.
     */
    default void forwardControlMouseClicked(@Nonnull MouseButtonEvent e)
    {
        if (!isVisible())
        {
            return;
        }

        if (!isMouseOver(e.mouseX, e.mouseY))
        {
            return;
        }

        if (getReverseChildrenCopy().stream().anyMatch(control -> { control.forwardControlMouseClicked(e); return e.isHandled(); } ))
        {
            return;
        }

        if (!isInteractive())
        {
            return;
        }

        getScreen().setRecentlyClickedControl(this);

        setIsFocused(true);
        setIsPressed(true, e.mouseX, e.mouseY);

        getOnControlMouseClicked().handle(e);
    }

    /**
     * Called when the mouse is clicked on the control.
     */
    default void controlMouseClicked(@Nonnull MouseButtonEvent e)
    {
        e.setIsHandled(true);
    }

    /**
     * Forwards a global mouse released event.
     */
    default void forwardGlobalMouseReleased(@Nonnull MouseButtonEvent e)
    {
        getReverseChildrenCopy().forEach(control -> control.forwardGlobalMouseReleased(e));

        globalMouseReleased(e);
    }

    /**
     * Called when the mouse is released anywhere on the screen.
     */
    default void globalMouseReleased(@Nonnull MouseButtonEvent e)
    {

    }

    /**
     * Tries to forward a mouse released event to the control.
     */
    default void forwardControlMouseReleased(@Nonnull MouseButtonEvent e)
    {
        if (!isVisible())
        {
            return;
        }

        if (!isMouseOver(e.mouseX, e.mouseY))
        {
            return;
        }

        if (getReverseChildrenCopy().stream().anyMatch(control -> { control.forwardControlMouseReleased(e); return e.isHandled(); } ))
        {
            return;
        }

        if (!isInteractive())
        {
            return;
        }

        if (getScreen().getRecentlyPressedControl() != this)
        {
            return;
        }

        setIsFocused(true);

        getOnControlMouseReleased().handle(e);
    }

    /**
     * Called when the mouse is released on the control.
     */
    default void controlMouseReleased(@Nonnull MouseButtonEvent e)
    {
        e.setIsHandled(true);
    }

    /**
     * Tries to forward a mouse scrolled event to the control.
     */
    default void forwardControlMouseScrolled(@Nonnull MouseScrollEvent e)
    {
        if (!isVisible())
        {
            return;
        }

        if (!isMouseOver(e.mouseX, e.mouseY))
        {
            return;
        }

        if (getReverseChildrenCopy().stream().anyMatch(control -> { control.forwardControlMouseScrolled(e); return e.isHandled(); }))
        {
            return;
        }

        if (!isInteractive())
        {
            return;
        }

        getOnControlMouseScrolled().handle(e);
    }

    /**
     * Called when the mouse is scrolled over the control.
     */
    default void controlMouseScrolled(@Nonnull MouseScrollEvent e)
    {
        e.setIsHandled(false);
    }

    /**
     * Forwards a mouse scrolled event.
     */
    default void forwardGlobalMouseScrolled(@Nonnull MouseScrollEvent e)
    {
        getReverseChildrenCopy().forEach(control -> control.forwardGlobalMouseScrolled(e));

        globalMouseScrolled(e);
    }


    /**
     * Called when the mouse is scrolled anywhere on the screen.
     */
    default void globalMouseScrolled(@Nonnull MouseScrollEvent e)
    {

    }

    /**
     * Tries to forward a key pressed event to the control.
     */
    default void forwardControlKeyPressed(@Nonnull KeyEvent e)
    {
        if (isVisible() && isInteractive())
        {
            controlKeyPressed(e);
        }

        if (e.isHandled())
        {
            return;
        }

        if (hasParent())
        {
            getParent().forwardControlKeyPressed(e);
        }
    }

    /**
     * Called when a key is pressed and the control is focused.
     */
    default void controlKeyPressed(@Nonnull KeyEvent e)
    {

    }

    /**
     * Forwards a key pressed event.
     */
    default void forwardGlobalKeyPressed(@Nonnull KeyEvent e)
    {
        getReverseChildrenCopy().forEach(control -> control.forwardGlobalKeyPressed(e));

        globalKeyPressed(e);
    }

    /**
     * Called when a key is pressed anywhere on the screen.
     */
    default void globalKeyPressed(@Nonnull KeyEvent e)
    {

    }

    /**
     * Tries to forward a key released event to the control.
     */
    default void forwardControlKeyReleased(@Nonnull KeyEvent e)
    {
        if (isVisible() && isInteractive())
        {
            controlKeyReleased(e);
        }

        if (e.isHandled())
        {
            return;
        }

        if (hasParent())
        {
            getParent().forwardControlKeyReleased(e);
        }
    }

    /**
     * Called when a key is released and the control is focused.
     */
    default void controlKeyReleased(@Nonnull KeyEvent e)
    {

    }

    /**
     * Forwards a key released event.
     */
    default void forwardGlobalKeyReleased(@Nonnull KeyEvent e)
    {
        getReverseChildrenCopy().forEach(control -> control.forwardGlobalKeyReleased(e));

        globalKeyReleased(e);
    }

    /**
     * Called when a key is released anywhere on the screen.
     */
    default void globalKeyReleased(@Nonnull KeyEvent e)
    {

    }

    /**
     * Tries to forward a key held event to the control.
     */
    default void forwardControlKeyHeld(@Nonnull KeyEvent e)
    {
        if (isVisible() && isInteractive())
        {
            controlKeyHeld(e);
        }

        if (e.isHandled())
        {
            return;
        }

        if (hasParent())
        {
            getParent().forwardControlKeyHeld(e);
        }
    }

    /**
     * Called when a key is held and the control is focused.
     */
    default void controlKeyHeld(@Nonnull KeyEvent e)
    {

    }

    /**
     * Forwards a key held event.
     */
    default void forwardGlobalKeyHeld(@Nonnull KeyEvent e)
    {
        getReverseChildrenCopy().forEach(control -> control.forwardGlobalKeyHeld(e));

        globalKeyHeld(e);
    }

    /**
     * Called when a key is held anywhere on the screen.
     */
    default void globalKeyHeld(@Nonnull KeyEvent e)
    {

    }

    /**
     * Tries to forward a char typed event to the control.
     */
    default void forwardControlCharTyped(@Nonnull CharEvent e)
    {
        if (isVisible() && isInteractive())
        {
            controlCharTyped(e);
        }

        if (e.isHandled())
        {
            return;
        }

        if (hasParent())
        {
            getParent().forwardControlCharTyped(e);
        }
    }

    /**
     * Called when a char is typed and the control is focused.
     */
    default void controlCharTyped(@Nonnull CharEvent e)
    {

    }

    /**
     * Forwards a char typed event.
     */
    default void forwardGlobalCharTyped(@Nonnull CharEvent e)
    {
        getReverseChildrenCopy().forEach(control -> control.forwardGlobalCharTyped(e));

        globalCharTyped(e);
    }

    /**
     * Called when a char is typed anywhere on the screen.
     */
    default void globalCharTyped(@Nonnull CharEvent e)
    {

    }

    /**
     * Enables scissoring using the control's bounds.
     */
    default void enableScissor()
    {
        GuiUtil.addScissorBounds(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
        GuiUtil.enableStackedScissor();
    }

    /**
     * Disables scissoring.
     */
    default void disableScissor()
    {
        GuiUtil.removeScissorBounds(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
        GuiUtil.disableScissor();
    }

    /**
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @return true if the mouse is over the control.
     */
    default boolean isMouseOver(int mouseX, int mouseY)
    {
        return GuiUtil.isMouseOver(mouseX, mouseY, getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
    }

    /**
     * @return the underlying screen.
     */
    @Nonnull
    IScreen getScreen();

    /**
     * @return whether the control has a parent.
     */
    default boolean hasParent()
    {
        return getParent() != null;
    }

    /**
     * @return the parent control.
     */
    @Nullable
    default IControl getParent()
    {
        return null;
    }

    /**
     * Sets the parent control.
     */
    default void setParent(@Nullable IControl parent)
    {

    }

    /**
     * @return the list of child controls.
     */
    @Nonnull
    ArrayList<Control> getChildren();

    /**
     * @return the list of children in reverse.
     */
    @Nonnull
    default ArrayList<Control> getReverseChildren()
    {
        ArrayList<Control> reverseChildren = new ArrayList<>(getChildren());
        Collections.reverse(reverseChildren);

        return reverseChildren;
    }

    /**
     * @return a copy of the list of child controls.
     */
    @Nonnull
    default ArrayList<Control> getChildrenCopy()
    {
        return new ArrayList<>(getChildren());
    }

    /**
     * @return a copy of the list of children in reverse.
     */
    @Nonnull
    default ArrayList<Control> getReverseChildrenCopy()
    {
        return new ArrayList<>(getReverseChildren());
    }

    /**
     * Adds the given control as a child of this control.
     *
     * @param control the control to add as a child.
     */
    void addChild(@Nonnull Control control);

    /**
     * Removes the given control as a child of this control.
     *
     * @param control the control to remove as a child.
     */
    void removeChild(@Nullable Control control);

    /**
     * Removes all children from this control.
     */
    default void removeChildren()
    {
        for (Control control : getChildrenCopy())
        {
            removeChild(control);
        }
    }

    /**
     * @return the current y-axis scrollbar control.
     */
    @Nullable
    default ScrollbarControl getScrollbarY()
    {
        return getParent() != null ? getParent().getScrollbarY() : null;
    }

    /**
     * Sets the current y-axis scrollbar control.
     */
    default void setScrollbarY(@Nullable ScrollbarControl scrollbarControlY)
    {

    }

    /**
     * @return the scroll offset in the x-axis.
     */
    default int getScrollX()
    {
        return 0;
    }

    /**
     * Sets the scroll offset in the x-axis.
     */
    default void setScrollX(int scroll)
    {

    }

    /**
     * @return the scroll offset in the y-axis.
     */
    default int getScrollY()
    {
        return 0;
    }

    /**
     * Sets the scroll offset in the y-axis.
     */
    default void setScrollY(int scroll)
    {

    }

    /**
     * @return the max scroll offset in the x-axis.
     */
    default int getMaxScrollX()
    {
        return 0;
    }

    /**
     * Sets the max scroll offset in the x-axis.
     */
    default void setMaxScrollX(int maxScroll)
    {

    }

    /**
     * @return the max scroll offset in the y-axis.
     */
    default int getMaxScrollY()
    {
        return 0;
    }

    /**
     * Sets the max scroll offset in the y-axis.
     */
    default void setMaxScrollY(int maxScroll)
    {

    }

    /**
     * @return the x position on the screen.
     */
    int getScreenX();

    /**
     * @return the y position on the screen.
     */
    int getScreenY();

    /**
     * @return the width of the control.
     */
    int getWidth();

    /**
     * @return the width of the control on the screen.
     */
    default int getScreenWidth()
    {
        return (int) (getEffectiveScale() * getWidth());
    }

    /**
     * @return the height of the control.
     */
    int getHeight();

    /**
     * @return the height of the control on the screen.
     */
    default int getScreenHeight()
    {
        return (int) (getEffectiveScale() * getHeight());
    }

    /**
     * @return the cumulative scale of the control including all parent's scales.
     */
    default float getEffectiveScale()
    {
        return getScale() * (getParent() != null ? getParent().getEffectiveScale() : 1.0f);
    }

    /**
     * @return the control's scale.
     */
    default float getScale()
    {
        return 1.0f;
    }

    /**
     * Sets the control's scale.
     */
    default void setScale(float scale)
    {

    }

    /**
     * @return the padding for the given side.
     */
    default int getPadding(@Nonnull Side side)
    {
        return 0;
    }

    /**
     * Sets the padding for the given side.
     */
    default void setPadding(@Nonnull Side side, int padding)
    {

    }

    /**
     * @return the margin for the given side.
     */
    default int getMargin(@Nonnull Side side)
    {
        return 0;
    }

    /**
     * Sets the margin for the given side.
     */
    default void setMargin(@Nonnull Side side, int margin)
    {

    }

    /**
     * Sets all the margins on the control.
     */
    default void setMargins(int left, int top, int right, int bottom)
    {

    }

    /**
     * @return whether the control is visible.
     */
    default boolean isVisible()
    {
        return true;
    }

    /**
     * Sets whether the control is visible.
     *
     * @param isVisible whether the control is visible.
     */
    default void setIsVisible(boolean isVisible)
    {

    }

    /**
     * @return whether the control is focused.
     */
    default boolean isFocused()
    {
        return getScreen().getFocusedControl() == this;
    }

    /**
     * Sets whether the control is focused.
     *
     * @param isFocused whether the control is focused.
     */
    default void setIsFocused(boolean isFocused)
    {
        getScreen().setFocusedControl(isFocused ? this : null);
    }

    /**
     * @return whether the control is hovered.
     */
    default boolean isHovered()
    {
        return getScreen().getHoveredControl() == this;
    }

    /**
     * Sets whether the control is hovered.
     *
     * @param isHovered whether the control is hovered.
     * @param mouseX the x position of the mouse.
     * @param mouseY the y position of the mouse.
     */
    default void setIsHovered(boolean isHovered, int mouseX, int mouseY)
    {
        getScreen().setHoveredControl(isHovered ? this : null, mouseX, mouseY);
    }

    /**
     * @return whether the control is being clicked on.
     */
    default boolean isPressed()
    {
        return getScreen().getPressedControl() == this;
    }

    /**
     * @return the x position of the mouse when the control was pressed.
     */
    default int getPressedMouseX()
    {
        return getScreen().getPressedMouseX();
    }

    /**
     * @return the y position of the mouse when the control was pressed.
     */
    default int getPressedMouseY()
    {
        return getScreen().getPressedMouseY();
    }

    /**
     * @return whether the control is being dragged.
     */
    default boolean isDragging()
    {
        return getScreen().getDraggedControl() == this;
    }

    /**
     * Sets whether the control is being dragged.
     */
    default void setIsDragging(boolean isDragging)
    {
        getScreen().setDraggedControl(isDragging ? this : null);
    }

    /**
     * Sets whether the control is being clicked on.
     *
     * @param isClicked whether the control is being clicked on.
     * @param mouseX the x position of the mouse when the control was pressed.
     * @param mouseY the y position of the mouse when the control was pressed.
     */
    default void setIsPressed(boolean isClicked, int mouseX, int mouseY)
    {
        getScreen().setPressedControl(isClicked ? this : null, mouseX, mouseY);
    }

    /**
     * @return true if the control is interactive.
     */
    default boolean isInteractive()
    {
        return true;
    }

    /**
     * Sets whether the control is interactive.
     *
     * @param isInteractive whether the control is interactive.
     */
    default void setIsInteractive(boolean isInteractive)
    {

    }

    /**
     * @return whether the control is scrollable in the x-axis.
     */
    default boolean isScrollableX()
    {
        return false;
    }

    /**
     * Sets whether the control is scrollable in the x-axis.
     */
    default void setIsScrollableX(boolean isScrollable)
    {

    }

    /**
     * @return whether the control is scrollable in the y-axis.
     */
    default boolean isScrollableY()
    {
        return false;
    }

    /**
     * Sets whether the control is scrollable in the y-axis.
     */
    default void setIsScrollableY(boolean isScrollable)
    {

    }

    /**
     * @return the event handler for hover events.
     */
    @Nonnull
    EventHandler<MouseEvent> getOnControlHover();

    /**
     * @return the event handler for hover start events.
     */
    @Nonnull
    EventHandler<MouseEvent> getOnControlHoverStart();

    /**
     * @return the event handler for hover stop events.
     */
    @Nonnull
    EventHandler<MouseEvent> getOnControlHoverStop();

    /**
     * @return the event handler for mouse click events.
     */
    @Nonnull
    EventHandler<MouseButtonEvent> getOnControlMouseClicked();

    /**
     * @return the event handler for mouse release events.
     */
    @Nonnull
    EventHandler<MouseButtonEvent> getOnControlMouseReleased();

    /**
     * @return the event handler for mouse scrolled events.
     */
    @Nonnull
    EventHandler<MouseScrollEvent> getOnControlMouseScrolled();

    /**
     * @return the event handler for key pressed events.
     */
    @Nonnull
    EventHandler<KeyEvent> getOnControlKeyPressed();

    /**
     * @return the event handler for key released events.
     */
    @Nonnull
    EventHandler<KeyEvent> getOnControlKeyReleased();

    /**
     * @return the event handler for key held events.
     */
    @Nonnull
    EventHandler<KeyEvent> getOnControlKeyHeld();

    /**
     * @return the event handler for char typed events.
     */
    @Nonnull
    EventHandler<CharEvent> getOnControlCharTyped();

    /**
     * A class containing the information about a mouse event.
     */
    class MouseEvent extends Event
    {
        /**
         * The x position of the mouse.
         */
        public final int mouseX;

        /**
         * The y position of the mouse.
         */
        public final int mouseY;

        /**
         * @param mouseX the x position of the mouse.
         * @param mouseY the y position of the mouse.
         */
        public MouseEvent(int mouseX, int mouseY)
        {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    /**
     * A class containing the information about a mouse button event.
     */
    class MouseButtonEvent extends MouseEvent
    {
        /**
         * The mouse button.
         */
        public final int button;

        /**
         * @param mouseX the x position of the mouse.
         * @param mouseY the y position of the mouse.
         * @param button the mouse button.
         */
        public MouseButtonEvent(int mouseX, int mouseY, int button)
        {
            super(mouseX, mouseY);
            this.button = button;
        }
    }

    /**
     * A class containing the information about a mouse button event.
     */
    class MouseScrollEvent extends MouseEvent
    {
        /**
         * The scroll amount.
         */
        public final double scroll;

        /**
         * @param mouseX the x position of the mouse.
         * @param mouseY the y position of the mouse.
         * @param scroll the scroll amount.
         */
        public MouseScrollEvent(int mouseX, int mouseY, double scroll)
        {
            super(mouseX, mouseY);
            this.scroll = scroll;
        }
    }

    /**
     * A class containing the information about a key event.
     */
    class KeyEvent extends Event
    {
        /**
         * The key code.
         */
        public final int keyCode;

        /**
         * The scan code.
         */
        public final int scanCode;

        /**
         * The modifiers.
         */
        public final int modifiers;

        /**
         * @param keyCode the key code.
         * @param scanCode the scan code.
         * @param modifiers the modifiers.
         */
        public KeyEvent(int keyCode, int scanCode, int modifiers)
        {
            this.keyCode = keyCode;
            this.scanCode = scanCode;
            this.modifiers = modifiers;
        }
    }

    /**
     * A class containing the information about a character event.
     */
    class CharEvent extends Event
    {
        /**
         * The character.
         */
        public final char character;

        /**
         * The key code.
         */
        public final int keyCode;

        /**
         * @param character the character.
         * @param keyCode the key code.
         */
        public CharEvent(char character, int keyCode)
        {
            this.character = character;
            this.keyCode = keyCode;
        }
    }

    /**
     * An enum of the sides of a control.
     */
    enum Side
    {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }
}
