package com.willr27.blocklings.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Something on the screen that might render something or be interactable.
 */
@OnlyIn(Dist.CLIENT)
public class Control extends AbstractGui implements IControl
{
    /**
     * The screen currently displayed.
     */
    @Nonnull
    public final Screen screen;

    /**
     * The font renderer currently used.
     */
    @Nonnull
    public final FontRenderer font;

    /**
     * The parent control (might be a screen).
     */
    @Nullable
    public IControl parent;

    /**
     * The list of child controls.
     */
    @Nonnull
    protected final ArrayList<Control> children = new ArrayList<>();

    /**
     * The associated y-axis scrollbar control.
     */
    @Nullable
    protected ScrollbarControl scrollbarControlY = null;

    /**
     * The background colour of the control.
     */
    @Nonnull
    private Colour backgroundColour = new Colour(Color.white);

    /**
     * The foreground colour of the control.
     */
    @Nonnull
    private Colour foregroundColour = new Colour(Color.white);

    /**
     * The x position relative to the parent.
     */
    private int x;

    /**
     * The y position relative to the parent.
     */
    private int y;

    /**
     * The scroll offset in the x-axis.
     */
    private int scrollX;

    /**
     * The scroll offset in the y-axis.
     */
    private int scrollY;

    /**
     * The max scroll offset in the x-axis.
     */
    private int maxScrollX;

    /**
     * The max scroll offset in the y-axis.
     */
    private int maxScrollY;

    /**
     * The x position on the screen.
     */
    public int screenX;

    /**
     * The y position on the screen.
     */
    public int screenY;

    /**
     * The width of the control.
     */
    public int width;

    /**
     * The height of the control.
     */
    public int height;

    /**
     * The control's scale.
     */
    private float scale;

    /**
     * The padding of the control.
     */
    private final Map<Side, Integer> padding = new HashMap<>(4);

    /**
     * The margins of the control.
     */
    private final Map<Side, Integer> margins = new HashMap<>(4);

    /**
     * Whether control is visible.
     */
    private boolean isVisible = true;

    /**
     * Whether control is interactive.
     */
    private boolean isInteractive = true;

    /**
     * Whether control can be scrolled in the x-axis.
     */
    private boolean isScrollableX = false;

    /**
     * Whether control can be scrolled in the y-axis.
     */
    private boolean isScrollableY = false;

    /**
     * The event handler for hover events.
     */
    private final EventHandler<MouseEvent> onControlHover = new EventHandler<>();

    /**
     * The event handler for hover start events.
     */
    private final EventHandler<MouseEvent> onControlHoverStart = new EventHandler<>();

    /**
     * The event handler for hover stop events.
     */
    private final EventHandler<MouseEvent> onControlHoverStop = new EventHandler<>();

    /**
     * The event handler for mouse click events.
     */
    private final EventHandler<MouseButtonEvent> onControlMouseClicked = new EventHandler<>();

    /**
     * The event handler for mouse release events.
     */
    private final EventHandler<MouseButtonEvent> onControlMouseReleased = new EventHandler<>();

    /**
     * The event handler for mouse scroll events.
     */
    private final EventHandler<MouseScrollEvent> onControlMouseScrolled = new EventHandler<>();

    /**
     * The event handler for key pressed events.
     */
    private final EventHandler<KeyEvent> onControlKeyPressed = new EventHandler<>();

    /**
     * The event handler for key released events.
     */
    private final EventHandler<KeyEvent> onControlKeyReleased = new EventHandler<>();

    /**
     * The event handler for key held events.
     */
    private final EventHandler<KeyEvent> onControlKeyHeld = new EventHandler<>();

    /**
     * The event handler for char typed events.
     */
    private final EventHandler<CharEvent> onControlCharTyped = new EventHandler<>();

    /**
     * Default constructor.
     */
    public Control()
    {
        this(null, 0, 0, 0, 0);
    }

    /**
     * @param parent the parent control.
     * @param x the local x position.
     * @param y the local y position.
     * @param width the width of the control.
     * @param height the height of the control.
     */
    public Control(@Nullable IControl parent, int x, int y, int width, int height)
    {
        this.screen = Objects.requireNonNull(Minecraft.getInstance().screen);
        this.font = Minecraft.getInstance().font;
        this.scale = 1.0f;

        padding.put(Side.LEFT, 0);
        padding.put(Side.TOP, 0);
        padding.put(Side.RIGHT, 0);
        padding.put(Side.BOTTOM, 0);

        margins.put(Side.LEFT, 0);
        margins.put(Side.TOP, 0);
        margins.put(Side.RIGHT, 0);
        margins.put(Side.BOTTOM, 0);

        setParent(parent);

        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        setupEventHandlers();
    }

    /**
     * Removes the control from the parent.
     */
    public void remove()
    {
        parent.removeChild(this);
    }

    /**
     * Renders the control.
     * @param matrixStack the current matrix stack.
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     * @param partialTicks the partial ticks.
     */
    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {

    }

    /**
     * Renders a texture at the control's location.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     */
    public void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture)
    {
        renderTexture(matrixStack, 0, 0, texture);
    }

    /**
     * Renders a texture at the control's location plus the given offset.
     *
     * @param matrixStack the matrix stack.
     * @param dx the x offset to render at.
     * @param dy the y offset to render at.
     * @param texture the texture to render.
     */
    public void renderTexture(@Nonnull MatrixStack matrixStack, int dx, int dy, @Nonnull GuiTexture texture)
    {
        GuiUtil.bindTexture(texture.texture);
        renderTexture(matrixStack, texture, screenX + dx, screenY + dy);
    }

    /**
     * Renders a texture where the x and y positions represent the top left corner.
     *
     * @param matrixStack the matrix stack.
     * @param texture the texture to render.
     * @param x the x position to render at.
     * @param y the y position to render at.
     */
    public void renderTexture(@Nonnull MatrixStack matrixStack, @Nonnull GuiTexture texture, int x, int y)
    {
        GuiUtil.bindTexture(texture.texture);
        blit(matrixStack, x, y, texture.x, texture.y, texture.width, texture.height);
    }

    /**
     * Renders shadowed text.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param dx the x offset from the control's top left.
     * @param dy the y offset from the control's top left.
     * @param right whether to render from the right-hand side of the control.
     * @param colour the colour of the text.
     */
    public void renderText(MatrixStack matrixStack, String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -font.width(text) - dx + width : dx;
        font.draw(matrixStack, text, screenX + bonusX, screenY + dy, colour);
        RenderSystem.enableDepthTest(); // Apparently depth test gets turned off so turn it back on
    }

    /**
     * Renders shadowed text.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param dx the x offset from the control's top left.
     * @param dy the y offset from the control's top left.
     * @param right whether to render from the right-hand side of the control.
     * @param colour the colour of the text.
     */
    public void renderShadowedText(MatrixStack matrixStack, String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -font.width(text) - dx + width : dx;

//        String shadowText = text;
//        List<Integer> toRemove = new ArrayList<>();
//
//        for (int i = 0; i < shadowText.length(); i++)
//        {
//            byte[] bytes = shadowText.substring(i, i + 1).getBytes(StandardCharsets.UTF_8);
//
//            if (bytes[0] == -62 && bytes[1] == -89)
//            {
//                toRemove.add(i);
//                toRemove.add(i + 1);
//            };
//        }
//
//        Collections.reverse(toRemove);
//
//        for (int i : toRemove)
//        {
//            shadowText = shadowText.substring(0, i) + shadowText.substring(i + 1);
//        }
//
//        font.draw(matrixStack, shadowText, screenX + bonusX + 1, screenY + dy + 1, 0x222222);
        font.drawShadow(matrixStack, text, screenX + bonusX, screenY + dy, colour);
        RenderSystem.enableDepthTest(); // Apparently depth test gets turned off so turn it back on
    }

    /**
     * Renders centered shadowed text.
     *
     * @param matrixStack the matrix stack.
     * @param text the text to render.
     * @param dx the x offset from the control's top left.
     * @param dy the y offset from the control's top left.
     * @param right whether to render from the right-hand side of the control.
     * @param colour the colour of the text.
     */
    public void renderCenteredText(MatrixStack matrixStack, String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -dx : width + dx;
        drawCenteredString(matrixStack, font, text, screenX + bonusX, screenY + dy, colour);
        RenderSystem.enableDepthTest(); // Apparently depth test gets turned off so turn it back on
    }

    /**
     * Resets the render colour back to white.
     */
    public void resetRenderColour()
    {
        setRenderColour(Color.white);
    }

    /**
     * Sets the current colour of the render system.
     */
    public void setRenderColour(@Nonnull Color colour)
    {
        new Colour(colour).apply();
    }

    /**
     * Sets the current colour of the render system.
     */
    public void setRenderColour(float r, float g, float b)
    {
        new Colour(r, g, b).apply();
    }

    /**
     * Sets the current colour of the render system.
     */
    public void setRenderColour(float r, float g, float b, float a)
    {
        new Colour(r, g, b, a).apply();
    }

    @Override
    public ScrollbarControl getScrollbarY()
    {
        return scrollbarControlY != null ? scrollbarControlY : parent != null ? parent.getScrollbarY() : null;
    }

    @Override
    public void setScrollbarY(@Nullable ScrollbarControl scrollbarControlY)
    {
        this.scrollbarControlY = scrollbarControlY;
    }

    /**
     * @return the background colour of the control.
     */
    @Nonnull
    public Colour getBackgroundColour()
    {
        return backgroundColour;
    }

    /**
     * Sets the background colour to the given colour.
     */
    public void setBackgroundColour(float r, float g, float b)
    {
        backgroundColour = new Colour(r, g, b);
    }

    /**
     * @return the foreground colour of the control.
     */
    @Nonnull
    public Colour getForegroundColour()
    {
        return foregroundColour;
    }

    /**
     * Sets the foreground colour to the given colour.
     */
    public void setForegroundColour(float r, float g, float b)
    {
        foregroundColour = new Colour(r, g, b);
    }

    /**
     * @param x the x position to localise.
     * @return the x position converted to a local coordinate.
     */
    public int toLocalX(int x)
    {
        return x - this.screenX;
    }

    /**
     * @param y the y position to localise.
     * @return the y position converted to a local coordinate.
     */
    public int toLocalY(int y)
    {
        return y - this.screenY;
    }

    /**
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param scale the gui scale.
     * @return true if the mouse is over the control after scaling.
     */
    public boolean isMouseOver(int mouseX, int mouseY, float scale)
    {
        return GuiUtil.isMouseOver((int) (mouseX / scale), (int) (mouseY / scale), screenX, screenY, width, height);
    }

    /**
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @return true if the mouse is over the control, and it is the top-most control.
     */
    public boolean isTopControlWithMouseOver(int mouseX, int mouseY)
    {
        boolean hasChildWithMouseOver = children.stream().anyMatch(control -> control.isTopControlWithMouseOver(mouseX, mouseY));

        return !hasChildWithMouseOver && GuiUtil.isMouseOver(mouseX, mouseY, screenX, screenY, width, height);
    }

    @Nonnull
    @Override
    public IScreen getScreen()
    {
        return (IScreen) screen;
    }

    @Override
    @Nullable
    public IControl getParent()
    {
        return parent;
    }

    @Override
    public void setParent(@Nullable IControl parent)
    {
        if (this.parent != null)
        {
            this.parent.removeChild(this);
        }

        this.parent = parent;

        if (this.parent != null)
        {
            this.parent.addChild(this);
        }
    }

    @Override
    @Nonnull
    public ArrayList<Control> getChildren()
    {
        return children;
    }

    @Override
    public void addChild(@Nonnull Control control)
    {
        if (!children.contains(control))
        {
            children.add(control);
            control.parent = this;
        }
    }

    @Override
    public void removeChild(@Nullable Control control)
    {
        if (control != null)
        {
            control.parent = null;
            children.remove(control);
        }
    }

    /**
     * @return the x position relative to the parent control.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Sets the x position relative to the parent control.
     *
     * @param x the local x position.
     */
    public void setX(int x)
    {
        this.x = x;

        recalcScreenX();
    }

    /**
     * @return the y position relative to the parent control.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Sets the y position relative to the parent control.
     *
     * @param y the local y position.
     */
    public void setY(int y)
    {
        this.y = y;

        recalcScreenY();
    }

    @Override
    public int getScrollX()
    {
        return scrollX;
    }

    @Override
    public void setScrollX(int scroll)
    {
        scrollX = isScrollableX ? scroll : 0;
        scrollX = Math.min(scrollX, maxScrollX);
        scrollX = Math.max(scrollX, 0);

        recalcScreenX();
    }

    @Override
    public int getScrollY()
    {
        return scrollY;
    }

    @Override
    public void setScrollY(int scroll)
    {
        scrollY = isScrollableY ? scroll : 0;
        scrollY = Math.min(scrollY, maxScrollY);
        scrollY = Math.max(scrollY, 0);

        recalcScreenY();
    }

    @Override
    public int getMaxScrollX()
    {
        return maxScrollX;
    }

    @Override
    public void setMaxScrollX(int maxScroll)
    {
        maxScrollX = maxScroll;

        setScrollX(getScrollX());
    }

    @Override
    public int getMaxScrollY()
    {
        return maxScrollY;
    }

    @Override
    public void setMaxScrollY(int maYScroll)
    {
        maxScrollY = maYScroll;

        setScrollY(getScrollY());
    }

    /**
     * Recalculates the screen x position.
     */
    public void recalcScreenX()
    {
        int parentOffset = 0;

        if (parent != null)
        {
            parentOffset = (int) (parent.getScreenX() - (parent.getScrollX() - parent.getPadding(Side.LEFT)) * parent.getEffectiveScale());
        }

        screenX = parentOffset + (int) ((getMargin(Side.LEFT) + x) * getEffectiveScale());

        getChildren().forEach(Control::recalcScreenX);
    }

    @Override
    public int getScreenX()
    {
        return screenX;
    }

    /**
     * Recalculates the screen y position.
     */
    public void recalcScreenY()
    {
        int parentOffset = 0;

        if (parent != null)
        {
            parentOffset = (int) (parent.getScreenY() - (parent.getScrollY() - parent.getPadding(Side.TOP)) * parent.getEffectiveScale());
        }

        screenY = parentOffset + (int) ((getMargin(Side.TOP) + y) * getEffectiveScale());

        getChildren().forEach(Control::recalcScreenY);
    }

    @Override
    public int getScreenY()
    {
        return screenY;
    }

    /**
     * @return the width of the control, including margins.
     */
    public int getEffectiveWidth()
    {
        return width + getMargin(Side.LEFT) + getMargin(Side.RIGHT);
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    /**
     * Sets the width of the control.
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * @return the height of the control, including margins.
     */
    public int getEffectiveHeight()
    {
        return height + getMargin(Side.TOP) + getMargin(Side.BOTTOM);
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    /**
     * Sets the height of the control.
     */
    public void setHeight(int height)
    {
        this.height = height;
    }

    @Override
    public float getScale()
    {
        return scale;
    }

    @Override
    public void setScale(float scale)
    {
        this.scale = scale;

        recalcScreenX();
        recalcScreenY();
    }

    @Override
    public int getPadding(@Nonnull Side side)
    {
        return padding.get(side);
    }

    @Override
    public void setPadding(@Nonnull Side side, int padding)
    {
        this.padding.put(side, padding);

        if (side == Side.LEFT || side == Side.RIGHT)
        {
            recalcScreenX();
        }
        else
        {
            recalcScreenY();
        }
    }

    /**
     * Sets all the padding on the control.
     */
    public void setPadding(int left, int top, int right, int bottom)
    {
        padding.put(Side.LEFT, left);
        padding.put(Side.TOP, top);
        padding.put(Side.RIGHT, right);
        padding.put(Side.BOTTOM, bottom);

        recalcScreenX();
        recalcScreenY();
    }

    @Override
    public int getMargin(@Nonnull Side side)
    {
        return margins.get(side);
    }

    @Override
    public void setMargin(@Nonnull Side side, int margin)
    {
        margins.put(side, margin);

        if (side == Side.LEFT || side == Side.RIGHT)
        {
            recalcScreenX();
        }
        else
        {
            recalcScreenY();
        }
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom)
    {
        margins.put(Side.LEFT, left);
        margins.put(Side.TOP, top);
        margins.put(Side.RIGHT, right);
        margins.put(Side.BOTTOM, bottom);

        recalcScreenX();
        recalcScreenY();
    }

    @Override
    public boolean isInteractive()
    {
        return isInteractive;
    }

    @Override
    public void setIsInteractive(boolean isInteractive)
    {
        this.isInteractive = isInteractive;
    }

    @Override
    public boolean isVisible()
    {
        return isVisible;
    }

    @Override
    public void setIsVisible(boolean isVisible)
    {
        this.isVisible = isVisible;
    }

    @Override
    public boolean isScrollableX()
    {
        return isScrollableX;
    }

    @Override
    public void setIsScrollableX(boolean isScrollable)
    {
        isScrollableX = isScrollable;

        if (!isScrollableX)
        {
            setScrollX(0);
        }
    }

    @Override
    public boolean isScrollableY()
    {
        return isScrollableY;
    }

    @Override
    public void setIsScrollableY(boolean isScrollable)
    {
        isScrollableY = isScrollable;

        if (!isScrollableY)
        {
            setScrollY(0);
        }
    }

    /**
     * Sets the index of the control in the list of children to the given index.
     *
     * @param index the index to move the control to.
     */
    public void setZIndex(int index)
    {
        if (parent == null)
        {
            return;
        }

        ArrayList<Control> children = parent.getChildren();

        if (index > children.size())
        {
            index = children.size();
        }
        else if (index < 0)
        {
            index = 0;
        }

        children.ensureCapacity(index + 1);
        children.set(children.indexOf(this), null);
        children.add(index, this);
        children.remove(null);
    }

    @Nonnull
    @Override
    public EventHandler<MouseEvent> getOnControlHover()
    {
        return onControlHover;
    }

    @Nonnull
    @Override
    public EventHandler<MouseEvent> getOnControlHoverStart()
    {
        return onControlHoverStart;
    }

    @Nonnull
    @Override
    public EventHandler<MouseEvent> getOnControlHoverStop()
    {
        return onControlHoverStop;
    }

    @Nonnull
    @Override
    public EventHandler<MouseButtonEvent> getOnControlMouseClicked()
    {
        return onControlMouseClicked;
    }

    @Nonnull
    @Override
    public EventHandler<MouseButtonEvent> getOnControlMouseReleased()
    {
        return onControlMouseReleased;
    }

    @Nonnull
    @Override
    public EventHandler<MouseScrollEvent> getOnControlMouseScrolled()
    {
        return onControlMouseScrolled;
    }

    @Nonnull
    @Override
    public EventHandler<KeyEvent> getOnControlKeyPressed()
    {
        return onControlKeyPressed;
    }

    @Nonnull
    @Override
    public EventHandler<KeyEvent> getOnControlKeyReleased()
    {
        return onControlKeyReleased;
    }

    @Nonnull
    @Override
    public EventHandler<KeyEvent> getOnControlKeyHeld()
    {
        return onControlKeyHeld;
    }

    @Nonnull
    @Override
    public EventHandler<CharEvent> getOnControlCharTyped()
    {
        return onControlCharTyped;
    }
}
