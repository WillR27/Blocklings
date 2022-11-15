package com.willr27.blocklings.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.RootControl;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
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
    protected final RootControl rootControl = new RootControl(this);

    /**
     * The currently hovered control.
     */
    @Nullable
    private Control hoveredControl;

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

        rootControl.getChildrenCopy().forEach(control -> rootControl.removeChild(control));
        rootControl.setWidth(width);
        rootControl.setHeight(height);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        float guiScale = GuiUtil.getInstance().getGuiScale();
        int pixelMouseX = GuiUtil.getInstance().getPixelMouseX();
        int pixelMouseY = GuiUtil.getInstance().getPixelMouseY();

        {
            MousePosEvent mousePosEvent = new MousePosEvent(rootControl.toLocalX(pixelMouseX), rootControl.toLocalY(pixelMouseY), pixelMouseX, pixelMouseY);

            rootControl.forwardHover(mousePosEvent);

            if (!mousePosEvent.isHandled())
            {
                // This probably isn't necessary, but we can set it back to unhandled anyway.
                mousePosEvent.setIsHandled(false);

                setHoveredControl(null, mousePosEvent);
            }
        }

        matrixStack.pushPose();
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        rootControl.forwardRender(new RenderArgs(matrixStack, pixelMouseX, pixelMouseY, partialTicks));

        matrixStack.popPose();
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
                hoveredControl.onHoverExit(mousePosEvent);
            }

            if (control != null)
            {
                control.onHoverEnter(mousePosEvent);
            }
        }

        hoveredControl = control;
    }
}
