package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A control for all tabbed blockling controls.
 */
@OnlyIn(Dist.CLIENT)
public class TexturedControl extends Control
{
    /**
     * The background texture.
     */
    @Nonnull
    private Texture backgroundTexture;

    /**
     * The pressed background texture.
     */
    @Nullable
    private Texture pressedBackgroundTexture;

    /**
     * @param backgroundTexture the background texture.
     */
    public TexturedControl(@Nonnull Texture backgroundTexture)
    {
        this(backgroundTexture, null);
    }

    /**
     * @param backgroundTexture the background texture.
     */
    public TexturedControl(@Nonnull Texture backgroundTexture, @Nullable Texture pressedBackgroundTexture)
    {
        super();

        setBackgroundTexture(backgroundTexture, true);
        setPressedBackgroundTexture(pressedBackgroundTexture, false);
    }

    @Override
    public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        if (isPressed() && getPressedBackgroundTexture() != null)
        {
            renderTextureAsBackground(matrixStack, pressedBackgroundTexture);
        }
        else
        {
            renderTextureAsBackground(matrixStack, backgroundTexture);
        }
    }

    /**
     * @return the background texture.
     */
    @Nonnull
    public Texture getBackgroundTexture()
    {
        return backgroundTexture;
    }

    /**
     * Sets the background texture.
     */
    public void setBackgroundTexture(@Nonnull Texture backgroundTexture, boolean updateSize)
    {
        this.backgroundTexture = backgroundTexture;

        if (updateSize)
        {
            setWidth(backgroundTexture.width);
            setHeight(backgroundTexture.height);
        }
    }

    /**
     * @return the pressed background texture.
     */
    @Nullable
    public Texture getPressedBackgroundTexture()
    {
        return pressedBackgroundTexture;
    }

    /**
     * Sets the pressed background texture.
     */
    public void setPressedBackgroundTexture(@Nullable Texture pressedBackgroundTexture, boolean updateSize)
    {
        this.pressedBackgroundTexture = pressedBackgroundTexture;

        if (updateSize)
        {
            setWidth(pressedBackgroundTexture.width);
            setHeight(pressedBackgroundTexture.height);
        }
    }
}
