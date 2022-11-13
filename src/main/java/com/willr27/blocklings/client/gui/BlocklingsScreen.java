package com.willr27.blocklings.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A base screen used to support using {@link Control} objects.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BlocklingsScreen extends Screen
{
    /**
     * The root control that contains all the sub controls on the screen.
     */
    @Nonnull
    protected final Control rootControl = new Control();

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
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        rootControl.forwardRender(new RenderArgs(matrixStack, screenMouseX, screenMouseY, partialTicks));
    }
}
