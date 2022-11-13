package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui2.GuiTextures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TestControl extends Control
{
    public TestControl()
    {
        setWidth(20);
        setHeight(10);
    }

    @Override
    protected void render(@Nonnull RenderArgs renderArgs)
    {
        renderTexture(renderArgs.matrixStack, GuiTextures.FLAT_BAR);
    }
}
