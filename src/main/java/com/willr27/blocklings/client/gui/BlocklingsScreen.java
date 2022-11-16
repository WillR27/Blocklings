package com.willr27.blocklings.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.ScreenControl;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrollEvent;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A base screen used to support using {@link Control} objects.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BlocklingsScreen extends Screen implements IScreen
{
    /**
     * The root control that contains all the sub controls on the screen.
     */
    @Nonnull
    protected final ScreenControl screenControl = new ScreenControl();

    /**
     * Default constructor.
     */
    protected BlocklingsScreen()
    {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init()
    {
        super.init();

        screenControl.getChildrenCopy().forEach(control -> screenControl.removeChild(control));
        screenControl.setWidth(width);
        screenControl.setHeight(height);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        float guiScale = GuiUtil.getInstance().getGuiScale();
        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        {
            MousePosEvent mousePosEvent = new MousePosEvent(screenControl.toLocalX(pixelMouseX), screenControl.toLocalY(pixelMouseY), pixelMouseX, pixelMouseY);

            screenControl.forwardHover(mousePosEvent);

            if (!mousePosEvent.isHandled())
            {
                // This probably isn't necessary, but we can set it back to unhandled anyway.
                mousePosEvent.setIsHandled(false);

                setHoveredControl(null, mousePosEvent);
            }
        }

        {
            MousePosEvent mousePosEvent = new MousePosEvent(screenControl.toLocalX(pixelMouseX), screenControl.toLocalY(pixelMouseY), pixelMouseX, pixelMouseY);

            screenControl.forwardTryDrag(mousePosEvent);

            if (getDraggedControl() != null)
            {
                mousePosEvent.mouseX = getDraggedControl().toLocalX(mousePosEvent.mousePixelX);
                mousePosEvent.mouseY = getDraggedControl().toLocalY(mousePosEvent.mousePixelY);

                getDraggedControl().onDrag(mousePosEvent);
            }
        }

        matrixStack.pushPose();
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        screenControl.forwardRender(new RenderArgs(matrixStack, pixelMouseX, pixelMouseY, partialTicks));

        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(double screenMouseX, double screenMouseY, int mouseButton)
    {
        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseButtonEvent mouseButtonEvent = new MouseButtonEvent(screenControl.toLocalX(pixelMouseX), screenControl.toLocalY(pixelMouseY), pixelMouseX, pixelMouseY, mouseButton);

        screenControl.forwardGlobalMouseClicked(mouseButtonEvent);
        screenControl.forwardMouseClicked(mouseButtonEvent);

        if (mouseButtonEvent.isHandled())
        {
            return true;
        }

        return super.mouseClicked(screenMouseX, screenMouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double screenMouseX, double screenMouseY, int mouseButton)
    {
        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseButtonEvent mouseButtonEvent = new MouseButtonEvent(screenControl.toLocalX(pixelMouseX), screenControl.toLocalY(pixelMouseY), pixelMouseX, pixelMouseY, mouseButton);

        screenControl.forwardGlobalMouseReleased(mouseButtonEvent);
        screenControl.forwardMouseReleased(mouseButtonEvent);

        if (mouseButtonEvent.isHandled())
        {
            return true;
        }

        return super.mouseReleased(screenMouseX, screenMouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double screenMouseX, double screenMouseY, double scrollAmount)
    {
        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseScrollEvent mouseScrollEvent = new MouseScrollEvent(screenControl.toLocalX(pixelMouseX), screenControl.toLocalY(pixelMouseY), pixelMouseX, pixelMouseY, scrollAmount);

        screenControl.forwardGlobalMouseScrolled(mouseScrollEvent);
        screenControl.forwardMouseScrolled(mouseScrollEvent);

        if (mouseScrollEvent.isHandled())
        {
            return true;
        }

        return super.mouseScrolled(screenMouseX, screenMouseY, scrollAmount);
    }

    @Nullable
    @Override
    public Control getHoveredControl()
    {
        return screenControl.getHoveredControl();
    }

    @Override
    public void setHoveredControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent)
    {
        screenControl.setHoveredControl(control, mousePosEvent);
    }

    @Nullable
    @Override
    public Control getPressedControl()
    {
        return screenControl.getPressedControl();
    }

    @Override
    public void setPressedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent)
    {
        screenControl.setPressedControl(control, mouseButtonEvent);
    }

    @Override
    public int getPressedStartPixelX()
    {
        return screenControl.getPressedStartPixelX();
    }

    @Override
    public int getPressedStartPixelY()
    {
        return screenControl.getPressedStartPixelY();
    }

    @Nullable
    @Override
    public Control getFocusedControl()
    {
        return screenControl.getFocusedControl();
    }

    @Override
    public void setFocusedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent)
    {
        screenControl.setFocusedControl(control, mouseButtonEvent);
    }

    @Nullable
    @Override
    public Control getDraggedControl()
    {
        return screenControl.getDraggedControl();
    }

    @Override
    public void setDraggedControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent)
    {
        screenControl.setDraggedControl(control, mousePosEvent);
    }
}
