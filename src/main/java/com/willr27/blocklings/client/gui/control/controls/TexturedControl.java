package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A control for all tabbed blockling controls.
 */
@OnlyIn(Dist.CLIENT)
public class TexturedControl extends Control
{
    @Nonnull
    private final Texture backgroundTexture;

    /**
     */
    public TexturedControl(@Nonnull Texture backgroundTexture)
    {
        super();
        this.backgroundTexture = backgroundTexture;

        setWidth(backgroundTexture.width);
        setHeight(backgroundTexture.height);
    }

    @Override
    public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        renderTextureAsBackground(matrixStack, backgroundTexture);
    }
}
