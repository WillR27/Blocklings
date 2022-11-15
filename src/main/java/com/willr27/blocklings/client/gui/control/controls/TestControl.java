package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
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
        setWidth(20);
        setHeight(10);
    }

    @Override
    public void onHoverEnter(@Nonnull MousePosEvent mousePosEvent)
    {
        texture = GuiTextures.RAISED_BAR;
    }

    @Override
    public void onHoverExit(@Nonnull MousePosEvent mousePosEvent)
    {
        texture = GuiTextures.FLAT_BAR;
    }

    @Override
    protected void render(@Nonnull RenderArgs renderArgs)
    {
        renderTexture(renderArgs.matrixStack, texture);
    }
}
