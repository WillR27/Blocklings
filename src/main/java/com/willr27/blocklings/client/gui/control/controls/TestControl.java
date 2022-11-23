package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrollEvent;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.client.gui2.GuiTextures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jline.utils.Log;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TestControl extends Control
{
    private GuiTexture texture = GuiTextures.RAISED_BAR;

    public TestControl()
    {
        setWidth(50);
        setHeight(20);
        setDraggableXY(true);
    }

    @Override
    public void onHoverEnter(@Nonnull MousePosEvent mousePosEvent)
    {
//        texture = GuiTextures.RAISED_BAR;
    }

    @Override
    public void onHoverExit(@Nonnull MousePosEvent mousePosEvent)
    {
//        texture = GuiTextures.FLAT_BAR;
    }

    @Override
    protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        Log.info("Released ", getClass().getName());

        mouseButtonEvent.setIsHandled(true);
    }

    @Override
    protected void onGlobalMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        Log.info("Released ", getClass().getName());

//        mouseButtonEvent.setIsHandled(true);
    }

    @Override
    public void onPressed(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        texture = GuiTextures.FLAT_BAR;
    }

    @Override
    public void onReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        texture = GuiTextures.RAISED_BAR;

        if (isFocused())
        {
            texture = GuiTextures.DROPDOWN_DOWN_ARROW;
        }
    }

    @Override
    public void onFocused(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        texture = GuiTextures.DROPDOWN_DOWN_ARROW;
    }

    @Override
    public void onUnfocused(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        texture = GuiTextures.FLAT_BAR;
    }

    @Override
    protected void onMouseScrolled(@Nonnull MouseScrollEvent mouseScrollEvent)
    {
        texture = mouseScrollEvent.scrollAmount > 0 ? GuiTextures.DROPDOWN_UP_ARROW : GuiTextures.DROPDOWN_DOWN_ARROW;

//        mouseScrollEvent.setIsHandled(true);

        super.onMouseScrolled(mouseScrollEvent);
    }

    @Override
    protected void onRender(@Nonnull RenderArgs renderArgs)
    {
//        renderTexture(renderArgs.matrixStack, texture);
    }
}
