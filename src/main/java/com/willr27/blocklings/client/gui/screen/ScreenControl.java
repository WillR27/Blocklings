package com.willr27.blocklings.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.DragEndEvent;
import com.willr27.blocklings.client.gui.control.event.events.DragStartEvent;
import com.willr27.blocklings.client.gui.control.event.events.FocusChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.*;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * The root control for each screen.
 */
@OnlyIn(Dist.CLIENT)
public class ScreenControl extends Control implements IScreen
{
    /**
     * The instance of {@link Random} to use.
     */
    @Nonnull
    private final Random random = new Random();

    /**
     * Used to prevent a mouse released event from triggering when a mouse clicked event never occurred.
     * Useful for when the UI first opens to prevent a mouse released event from triggering by itself.
     */
    private boolean wasMouseClickedBeforeRelease = false;

    /**
     * The currently hovered control.
     */
    @Nullable
    private Control hoveredControl;

    /**
     * The currently pressed control.
     */
    @Nullable
    private Control pressedControl;

    /**
     * The pixel x coordinate that the pressed control was pressed at.
     */
    private int pressedStartPixelX = 0;

    /**
     * The pixel y coordinate that the pressed control was pressed at.
     */
    private int pressedStartPixelY = 0;

    /**
     * The currently focused control.
     */
    @Nullable
    private Control focusedControl;

    /**
     * The currently dragged control.
     */
    @Nullable
    private Control draggedControl;

    /**
     */
    public ScreenControl()
    {
        setScreen(this);
    }

    /**
     * Mimics {@link Screen#init(Minecraft, int, int)}.
     */
    public void init(int screenWidth, int screenHeight)
    {
        setInnerScale(getInnerScale());
        clearChildren();
        setWidth(screenWidth);
        setHeight(screenHeight);
    }

    /**
     * Mimics {@link Screen#tick()}.
     */
    public void tick()
    {
        forwardTick();
    }

    /**
     * Mimics {@link Screen#render(MatrixStack, int, int, float)}.
     */
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        float guiScale = GuiUtil.getInstance().getGuiScale();
        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        {
            MousePosEvent mousePosEvent = new MousePosEvent(Math.round(toLocalX(pixelMouseX)), Math.round(toLocalY(pixelMouseY)), pixelMouseX, pixelMouseY);

            forwardHover(mousePosEvent);

            if (!mousePosEvent.isHandled())
            {
                // This probably isn't necessary, but we can set it back to unhandled anyway.
                mousePosEvent.setIsHandled(false);

                setHoveredControl(null, mousePosEvent);
            }
        }

        {
            MousePosEvent mousePosEvent = new MousePosEvent(Math.round(toLocalX(pixelMouseX)), Math.round(toLocalY(pixelMouseY)), pixelMouseX, pixelMouseY);

            forwardTryDrag(mousePosEvent);

            if (getDraggedControl() != null)
            {
                mousePosEvent.mouseX = Math.round(getDraggedControl().toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(getDraggedControl().toLocalY(mousePosEvent.mousePixelY));

                getDraggedControl().onDrag(mousePosEvent, partialTicks);
            }
        }

        matrixStack.pushPose();
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        forwardRender(new RenderArgs(matrixStack, pixelMouseX, pixelMouseY, partialTicks));

        matrixStack.popPose();

        if (getHoveredControl() != null && getDraggedControl() == null)
        {
            matrixStack.pushPose();
            matrixStack.scale(getHoveredControl().getCumulativeScale(), getHoveredControl().getCumulativeScale(), 1.0f);

            RenderSystem.enableDepthTest();
            getHoveredControl().onRenderTooltip(new RenderArgs(matrixStack, pixelMouseX, pixelMouseY, partialTicks));

            matrixStack.popPose();
        }
    }

    /**
     * Mimics {@link Screen#mouseClicked(double, double, int)}.
     */
    public boolean mouseClicked(double screenMouseX, double screenMouseY, int mouseButton)
    {
        wasMouseClickedBeforeRelease = true;

        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseButtonEvent mouseButtonEvent = new MouseButtonEvent(Math.round(toLocalX(pixelMouseX)), Math.round(toLocalY(pixelMouseY)), pixelMouseX, pixelMouseY, mouseButton);

        forwardGlobalMouseClicked(mouseButtonEvent);
        forwardMouseClicked(mouseButtonEvent);

        if (mouseButtonEvent.isHandled())
        {
            return true;
        }
        else
        {
            setFocusedControl(null);
        }

        return false;
    }

    /**
     * Mimics {@link Screen#mouseReleased(double, double, int)}.
     */
    public boolean mouseReleased(double screenMouseX, double screenMouseY, int mouseButton)
    {
        if (!wasMouseClickedBeforeRelease)
        {
            return true;
        }

        wasMouseClickedBeforeRelease = false;

        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseButtonEvent mouseButtonEvent = new MouseButtonEvent(Math.round(toLocalX(pixelMouseX)), Math.round(toLocalY(pixelMouseY)), pixelMouseX, pixelMouseY, mouseButton);

        forwardGlobalMouseReleased(mouseButtonEvent);
        forwardMouseReleased(mouseButtonEvent);

        if (mouseButtonEvent.isHandled())
        {
            return true;
        }

        return false;
    }

    /**
     * Mimics {@link Screen#mouseScrolled(double, double, double)}.
     */
    public boolean mouseScrolled(double screenMouseX, double screenMouseY, double scrollAmount)
    {
        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseScrollEvent mouseScrollEvent = new MouseScrollEvent(Math.round(toLocalX(pixelMouseX)), Math.round(toLocalY(pixelMouseY)), pixelMouseX, pixelMouseY, scrollAmount);

        forwardGlobalMouseScrolled(mouseScrollEvent);
        forwardMouseScrolled(mouseScrollEvent);

        if (mouseScrollEvent.isHandled())
        {
            return true;
        }

        return false;
    }

    @Override
    public void forwardMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        super.forwardMouseReleased(mouseButtonEvent);

        setPressedControl(null, mouseButtonEvent);
        setDraggedControl(null, mouseButtonEvent);
    }

    @Override
    protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    @Override
    protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * Mimics {@link Screen#keyPressed(int, int, int)}.
     */
    public final boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        KeyEvent keyEvent = new KeyEvent(keyCode, scanCode, mods);

        forwardGlobalKeyPressed(keyEvent);

        if (!keyEvent.isHandled() && getFocusedControl() != null)
        {
            getFocusedControl().onKeyPressed(keyEvent);
        }

        return keyEvent.isHandled();
    }

    /**
     * Mimics {@link Screen#keyReleased(int, int, int)}.
     */
    public final boolean keyReleased(int keyCode, int scanCode, int mods)
    {
        KeyEvent keyEvent = new KeyEvent(keyCode, scanCode, mods);

        forwardGlobalKeyReleased(keyEvent);

        if (!keyEvent.isHandled() && getFocusedControl() != null)
        {
            getFocusedControl().onKeyReleased(keyEvent);
        }

        return keyEvent.isHandled();
    }

    /**
     * Mimics {@link Screen#charTyped(char, int)}.
     */
    public final boolean charTyped(char character, int keyCode)
    {
        CharEvent charEvent = new CharEvent(character, keyCode);

        forwardGlobalCharTyped(charEvent);

        if (!charEvent.isHandled() && getFocusedControl() != null)
        {
            getFocusedControl().onCharTyped(charEvent);
        }

        return charEvent.isHandled();
    }

    @Override
    @Nullable
    public Control getHoveredControl()
    {
        return hoveredControl;
    }

    @Override
    public void setHoveredControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent)
    {
        if (hoveredControl != control)
        {
            if (hoveredControl != null)
            {
                mousePosEvent.mouseX = Math.round(hoveredControl.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(hoveredControl.toLocalY(mousePosEvent.mousePixelY));

                hoveredControl.onHoverExit(mousePosEvent);
            }

            if (control != null)
            {
                mousePosEvent.mouseX = Math.round(control.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(control.toLocalY(mousePosEvent.mousePixelY));

                control.onHoverEnter(mousePosEvent);
            }
        }

        hoveredControl = control;
    }

    @Nullable
    @Override
    public Control getPressedControl()
    {
        return pressedControl;
    }

    @Override
    public void setPressedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (pressedControl != control)
        {
            if (pressedControl != null)
            {
                mouseButtonEvent.mouseX = Math.round(pressedControl.toLocalX(mouseButtonEvent.mousePixelX));
                mouseButtonEvent.mouseY = Math.round(pressedControl.toLocalY(mouseButtonEvent.mousePixelY));

                pressedControl.onReleased(mouseButtonEvent);
            }

            if (control != null)
            {
                pressedStartPixelX = mouseButtonEvent.mousePixelX;
                pressedStartPixelY = mouseButtonEvent.mousePixelY;

                mouseButtonEvent.mouseX = Math.round(control.toLocalX(mouseButtonEvent.mousePixelX));
                mouseButtonEvent.mouseY = Math.round(control.toLocalY(mouseButtonEvent.mousePixelY));

                control.onPressed(mouseButtonEvent);
            }
        }

        pressedControl = control;
    }

    @Override
    public int getPressedStartPixelX()
    {
        return pressedStartPixelX;
    }

    @Override
    public int getPressedStartPixelY()
    {
        return pressedStartPixelY;
    }

    @Nullable
    @Override
    public Control getFocusedControl()
    {
        return focusedControl;
    }

    @Override
    public void setFocusedControl(@Nullable Control control)
    {
        if (focusedControl != control)
        {
            if (focusedControl != null)
            {
                focusedControl.onUnfocused(control);
                focusedControl.focusChanged.handle(new FocusChangedEvent(focusedControl, true));
            }

            if (control != null)
            {
                control.onFocused();
            }
        }

        focusedControl = control;

        if (focusedControl != null)
        {
            focusedControl.focusChanged.handle(new FocusChangedEvent(focusedControl, false));
        }
    }

    @Nullable
    @Override
    public Control getDraggedControl()
    {
        return draggedControl;
    }

    @Override
    public void setDraggedControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent)
    {
        if (draggedControl != control)
        {
            if (draggedControl != null)
            {
                mousePosEvent.mouseX = Math.round(draggedControl.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(draggedControl.toLocalY(mousePosEvent.mousePixelY));

                draggedControl.onDragEnd.handle(new DragEndEvent(draggedControl));
                draggedControl.onDragEnd(mousePosEvent);
            }

            if (control != null)
            {
                mousePosEvent.mouseX = Math.round(control.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(control.toLocalY(mousePosEvent.mousePixelY));

                control.onDragStart.handle(new DragStartEvent(control));
                control.onDragStart(mousePosEvent);
            }
        }

        draggedControl = control;
    }

    @Nonnull
    @Override
    public Random getRandom()
    {
        return random;
    }
}
