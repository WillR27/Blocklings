package com.willr27.blocklings.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.Control;
import com.willr27.blocklings.client.gui.GuiUtil;
import com.willr27.blocklings.client.gui.IControl;
import com.willr27.blocklings.client.gui.IScreen;
import com.willr27.blocklings.client.gui.controls.TabbedControl;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A screen that includes the blockling gui tabs.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedScreen extends Screen implements IControl, IScreen
{
    /**
     * The blockling.
     */
    @Nonnull
    protected final BlocklingEntity blockling;

    /**
     * The player opening the gui.
     */
    @Nonnull
    protected final PlayerEntity player;

    /**
     * The scale of the screen.
     */
    protected float scale;

    /**
     * The x position in the center of the screen.
     */
    protected int centerX;

    /**
     * The y position in the center of the screen.
     */
    protected int centerY;

    /**
     * The x position at the left of the gui's tabs.
     */
    protected int left;

    /**
     * The y position at the top of the gui.
     */
    protected int top;

    /**
     * The x position at the left of the gui excluding the tabs.
     */
    public int contentLeft;

    /**
     * The y position at the top of the gui excluding.
     */
    public int contentTop;

    /**
     * The x position at the right of the gui excluding the tabs.
     */
    protected int contentRight;

    /**
     * The y position at the bottom of the gui excluding.
     */
    protected int contentBottom;

    /**
     * The control used to the draw and handle the tabs.
     */
    public TabbedControl tabbedControl;

    /**
     * The list of child controls.
     */
    @Nonnull
    private final ArrayList<Control> children = new ArrayList<>();

    /**
     * The currently held keys.
     */
    @Nonnull
    private final Map<Integer, Integer> heldKeys = new HashMap<>();

    /**
     *  The most recent control that was pressed without being released.
     */
    @Nullable
    private IControl recentlyPressedControl = null;

    /**
     *  The currently focused control.
     */
    @Nonnull
    private IControl focusedControl = this;

    /**
     *  The currently hovered control.
     */
    @Nonnull
    private IControl hoveredControl = this;

    /**
     *  The currently pressed control.
     */
    @Nullable
    private IControl pressedControl = null;

    /**
     * The mouse x position the pressed control was pressed at.
     */
    private int pressedMouseX = 0;

    /**
     * The mouse y position the pressed control was pressed at.
     */
    private int pressedMouseY = 0;

    /**
     *  The currently dragged control.
     */
    @Nullable
    private IControl draggedControl = null;

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
     * @param blockling the blockling.
     */
    public TabbedScreen(@Nonnull BlocklingEntity blockling)
    {
        super(new StringTextComponent(""));
        this.blockling = blockling;
        this.player = Minecraft.getInstance().player;
        this.scale = (float) Minecraft.getInstance().getWindow().getGuiScale();

        setupEventHandlers();
    }

    /**
     * Called on first creation and whenever the screen is resized.
     */
    @Override
    protected void init()
    {
        super.init();

        removeChildren();

        scale = (float) Minecraft.getInstance().getWindow().getGuiScale();

        children.clear();

        width = (int) (minecraft.getWindow().getWidth() / scale);
        height = (int) (minecraft.getWindow().getHeight() / scale);

        centerX = width / 2;
        centerY = height / 2 + TabbedControl.OFFSET_Y;

        left = centerX - TabbedControl.GUI_WIDTH / 2;
        top = centerY - TabbedControl.GUI_HEIGHT / 2;

        contentLeft = centerX - TabbedControl.CONTENT_WIDTH / 2;
        contentTop = top;
        contentRight = contentLeft + TabbedControl.CONTENT_WIDTH;
        contentBottom = contentTop + TabbedControl.CONTENT_HEIGHT;

        removeChild(tabbedControl);
        tabbedControl = new TabbedControl(this, blockling, left, top);

        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
    }

    @Override
    public void onClose()
    {
        super.onClose();
    }

    @Override
    public void tick()
    {
        tickAll();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        mouseX = GuiUtil.getMouseX();
        mouseY = GuiUtil.getMouseY();

        GuiUtil.useGuiScaleForScissor = false;

        float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();

        matrixStack.pushPose();
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        matrixStack.pushPose();
        matrixStack.scale(scale, scale, 1.0f);

        RenderSystem.enableDepthTest();
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        renderScreen(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.enableDepthTest();

        if (getPressedControl() != null)
        {
            int difX = Math.abs(mouseX - getPressedMouseX());
            int difY = Math.abs(mouseY - getPressedMouseY());

            if (difX >= 4 || difY >= 4)
            {
                setDraggedControl(getPressedControl());
            }
        }

        matrixStack.popPose();

        forwardControlHover(new MouseEvent(mouseX, mouseY));
        preRenderAll(mouseX, mouseY, partialTicks);
        renderAll(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.pushPose();
        matrixStack.scale(getHoveredControl().getEffectiveScale(), getHoveredControl().getEffectiveScale(), 1.0f);

        Minecraft.getInstance().getWindow().setGuiScale(getHoveredControl().getEffectiveScale());

        RenderSystem.enableDepthTest();
        getHoveredControl().renderTooltip(matrixStack, (int) (mouseX / getHoveredControl().getEffectiveScale()), (int) (mouseY / getHoveredControl().getEffectiveScale()));

        Minecraft.getInstance().getWindow().setGuiScale(guiScale);

        matrixStack.popPose();
        matrixStack.popPose();

        GuiUtil.useGuiScaleForScissor = true;
    }

    @Override
    public void renderScreen(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        fillGradient(matrixStack, 0, 0, width, height, -1072689136, -804253680);

        renderTitle(matrixStack, mouseX, mouseY, partialTicks);
    }

    /**
     * Renders the screen's title.
     */
    protected void renderTitle(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        drawCenteredString(matrixStack, font, getTitle(), contentLeft + TabbedControl.CONTENT_WIDTH / 2, contentTop - 15, 0xffffff);
    }

    @Override
    public ITextComponent getTitle()
    {
        return tabbedControl.getChildren().stream().map(control -> ((TabbedControl.TabControl) control)).filter(tabControl -> tabControl.isSelected()).findFirst().get().tab.name;
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        mouseX = GuiUtil.getMouseX();
        mouseY = GuiUtil.getMouseY();

        MouseButtonEvent e = new MouseButtonEvent((int) mouseX, (int) mouseY, button);

        forwardControlMouseClicked(e);
        forwardGlobalMouseClicked(e);

        return e.isHandled() || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        mouseX = GuiUtil.getMouseX();
        mouseY = GuiUtil.getMouseY();

        MouseButtonEvent e = new MouseButtonEvent((int) mouseX, (int) mouseY, button);

        forwardControlMouseReleased(e);
        forwardGlobalMouseReleased(e);

        setRecentlyClickedControl(null);
        setPressedControl(null, (int) mouseX, (int) mouseY);
        setDraggedControl(null);

        return e.isHandled() || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        mouseX = GuiUtil.getMouseX();
        mouseY = GuiUtil.getMouseY();

        MouseScrollEvent e = new MouseScrollEvent((int) mouseX, (int) mouseY, scroll);

        forwardControlMouseScrolled(e);
        forwardGlobalMouseScrolled(e);

        return e.isHandled() || super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        KeyEvent e = new KeyEvent(keyCode, scanCode, mods);

        if (isKeyHeld(keyCode) && heldKeys.get(keyCode) > 10)
        {
            getFocusedControl().forwardControlKeyHeld(e);
            forwardGlobalKeyHeld(e);
        }
        else
        {
            getFocusedControl().forwardControlKeyPressed(e);
            forwardGlobalKeyPressed(e);
        }

        Integer oldCount = heldKeys.put(keyCode, 0);

        if (oldCount != null)
        {
            heldKeys.put(keyCode, oldCount + 1);
        }

        return e.isHandled() || super.keyPressed(keyCode, scanCode, mods);
    }

    @Override
    public final boolean keyReleased(int keyCode, int scanCode, int mods)
    {
        KeyEvent e = new KeyEvent(keyCode, scanCode, mods);

        getFocusedControl().controlKeyReleased(e);
        forwardGlobalKeyReleased(e);

        heldKeys.remove(keyCode);

        return e.isHandled() || super.keyReleased(keyCode, scanCode, mods);
    }

    @Override
    public final boolean charTyped(char character, int keyCode)
    {
        CharEvent e = new CharEvent(character, keyCode);

        getFocusedControl().controlCharTyped(e);
        forwardGlobalCharTyped(e);

        return super.charTyped(character, keyCode);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Nonnull
    @Override
    public IScreen getScreen()
    {
        return this;
    }

    @Override
    @Nullable
    public IControl getParent()
    {
        return null;
    }

    @Nonnull
    @Override
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

    @Override
    public int getScreenX()
    {
        return 0;
    }

    @Override
    public int getScreenY()
    {
        return 0;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
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

        init();
    }

    @Override
    public boolean isKeyHeld(int keyCode)
    {
        Integer count = heldKeys.get(keyCode);

        return count != null && count > 0;
    }

    @Nonnull
    @Override
    public IControl getFocusedControl()
    {
        return focusedControl;
    }

    @Override
    public void setFocusedControl(@Nullable IControl control)
    {
        this.focusedControl = control == null ? this : control;
    }

    @Nonnull
    @Override
    public IControl getHoveredControl()
    {
        return hoveredControl;
    }

    @Override
    public void setHoveredControl(@Nullable IControl control, int mouseX, int mouseY)
    {
        control = control == null ? this : control;

        if (control != hoveredControl)
        {
            hoveredControl.controlHoverStop(new MouseEvent(mouseX, mouseY));
            control.controlHoverStart(new MouseEvent(mouseX, mouseY));
        }

        hoveredControl = control;
    }

    @Nullable
    @Override
    public IControl getPressedControl()
    {
        return pressedControl;
    }

    @Override
    public int getPressedMouseX()
    {
        return pressedMouseX;
    }

    @Override
    public int getPressedMouseY()
    {
        return pressedMouseY;
    }

    @Override
    public void setPressedControl(@Nullable IControl control, int mouseX, int mouseY)
    {
        this.pressedControl = control;
        this.pressedMouseX = mouseX;
        this.pressedMouseY = mouseY;
    }

    @Nullable
    @Override
    public IControl getDraggedControl()
    {
        return draggedControl;
    }

    @Override
    public void setDraggedControl(@Nullable IControl control)
    {
        if (control != null && !control.isDraggable())
        {
            control = null;
        }

        draggedControl = control;
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

    @Nullable
    @Override
    public IControl getRecentlyPressedControl()
    {
        return recentlyPressedControl;
    }

    @Override
    public void setRecentlyClickedControl(@Nullable IControl control)
    {
        this.recentlyPressedControl = control;
    }
}
