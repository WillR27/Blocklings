package com.willr27.blocklings.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
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
    @Nonnull
    public IControl parent;

    /**
     * The list of child controls.
     */
    @Nonnull
    private final List<Control> children = new ArrayList<>();

    /**
     * The x position relative to the parent.
     */
    private int x;

    /**
     * The y position relative to the parent.
     */
    private int y;

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
     * Whether control is currently pressed.
     */
    private boolean isPressed = false;

    /**
     * The default constructor.
     */
    @Deprecated
    public Control()
    {
        this(null, 0, 0, 0, 0);
    }

    /**
     *
     */
    @Deprecated
    public Control(int screenX, int screenY, int width, int height)
    {
        this.parent = parent;
        this.screen = Objects.requireNonNull(Minecraft.getInstance().screen);
        this.font = Minecraft.getInstance().font;
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = width;
        this.height = height;

        if (parent != null)
        {
            parent.addChild(this);
        }
    }

    /**
     * @param parent the parent control.
     * @param x the local x position.
     * @param y the local y position.
     * @param width the width.
     * @param height the height.
     */
    public Control(@Nonnull IControl parent, int x, int y, int width, int height)
    {
        this.parent = parent;
        this.screen = Objects.requireNonNull(Minecraft.getInstance().screen);
        this.font = Minecraft.getInstance().font;
        this.width = width;
        this.height = height;

        setX(x);
        setY(y);

        parent.addChild(this);
    }

    @Override
    @Nonnull
    public IControl getParent()
    {
        return parent;
    }

    @Override
    @Nonnull
    public List<Control> getChildren()
    {
        return new ArrayList<>(children);
    }

    @Override
    public void addChild(@Nonnull Control control)
    {
        children.add(control);
    }

    @Override
    public void removeChild(@Nullable Control control)
    {
        children.remove(control);
    }

    /**
     * Renders the control.
     *
     * @param matrixStack the current matrix stack.
     * @param mouseX the scaled mouse x position.
     * @param mouseY the scaled mouse y position.
     */
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        // Leave blank as we might not actually want to do any rendering
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

    @Override
    public void mouseClickedNoHandle(int mouseX, int mouseY, int button)
    {
        getChildren().forEach(control -> control.mouseClickedNoHandle(mouseX, mouseY, button));

        if (isMouseOver(mouseX, mouseY))
        {
            isPressed = true;
        }
    }

    @Override
    public void mouseReleasedNoHandle(int mouseX, int mouseY, int button)
    {
        getChildren().forEach(control -> control.mouseReleasedNoHandle(mouseX, mouseY, button));

        isPressed = false;
    }

    /**
     * Handles the mouse being clicked.
     *
     * @param mouseX the mouse x.
     * @param mouseY the mouse y.
     * @param button the mouse button.
     * @return true if the mouse click has been handled.
     */
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        return false;
    }

    /**
     * Handles the mouse being released.
     *
     * @param mouseX the mouse x.
     * @param mouseY the mouse y.
     * @param button the mouse button.
     * @return true if the mouse release has been handled.
     */
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        return false;
    }

    /**
     * Handles the mouse being scrolled.
     *
     * @param mouseX the mouse x.
     * @param mouseY the mouse y.
     * @param scroll the scroll amount.
     * @return true if the mouse scroll has been handled.
     */
    public boolean mouseScrolled(int mouseX, int mouseY, double scroll)
    {
        return false;
    }

    /**
     * Handles a key being pressed.
     *
     * @param keyCode the key code.
     * @param scanCode the scan code.
     * @param mods the modifiers.
     * @return true if the key press has been handled.
     */
    public boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        return false;
    }

    /**
     * Handles a key being released.
     *
     * @param keyCode the key code.
     * @param scanCode the scan code.
     * @param mods the modifiers.
     * @return true if the key release has been handled.
     */
    public boolean keyReleased(int keyCode, int scanCode, int mods)
    {
        return false;
    }

    /**
     * Handles a character being typed.
     *
     * @param character the character.
     * @param keyCode the underlying key code.
     * @return true if the character type has been handled.
     */
    public boolean charTyped(char character, int keyCode)
    {
        return false;
    }

    /**
     * Enables scissoring using the control's bounds.
     */
    public void enableScissor()
    {
        GuiUtil.scissor(screenX, screenY, width, height);
    }

    /**
     * Disables scissoring.
     */
    public void disableScissor()
    {
        GuiUtil.disableScissor();
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
     * @return true if the mouse is over the control.
     */
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return GuiUtil.isMouseOver(mouseX, mouseY, screenX, screenY, width, height);
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

        screenX = parent.getScreenX() + x;
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

        screenY = parent.getScreenY() + y;
    }

    @Override
    public int getScreenX()
    {
        return screenX;
    }

    @Override
    public int getScreenY()
    {
        return screenY;
    }

    /**
     * @return true if the control is currently pressed.
     */
    public boolean isPressed()
    {
        return isPressed;
    }
}
