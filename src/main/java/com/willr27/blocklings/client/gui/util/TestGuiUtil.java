package com.willr27.blocklings.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains test implementations for methods in {@link GuiUtil}.
 */
@OnlyIn(Dist.CLIENT)
public class TestGuiUtil extends GuiUtil
{
    @Override
    public float getGuiScale()
    {
        return 1.0f;
    }

    @Override
    public float getMaxGuiScale()
    {
        return 4.0f;
    }

    @Override
    public int getPixelMouseX()
    {
        return 0;
    }

    @Override
    public int getPixelMouseY()
    {
        return 0;
    }

    @Override
    public boolean isKeyDown(int key)
    {
        return false;
    }

    @Override
    public boolean isKeyDown(@Nonnull KeyBinding key)
    {
        return false;
    }

    @Override
    public boolean isControlKeyDown()
    {
        return false;
    }

    @Override
    public boolean isCrouchKeyDown()
    {
        return false;
    }

    @Override
    public boolean isCloseKey(int key)
    {
        return false;
    }

    @Override
    public boolean isUnfocusTextFieldKey(int key)
    {
        return false;
    }

    @Nonnull
    @Override
    public ITextProperties trimWithEllipsis(@Nonnull ITextProperties text, int width)
    {
        return text;
    }

    @Nonnull
    @Override
    public ITextProperties trim(@Nonnull ITextProperties text, int width)
    {
        return text;
    }

    @Nonnull
    @Override
    public List<IReorderingProcessor> split(@Nonnull ITextProperties text, int width)
    {
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public List<String> split(@Nonnull String text, int width)
    {
        return null;
    }

    @Override
    public int getTextWidth(@Nonnull String text)
    {
        return 0;
    }

    @Override
    public int getTextWidth(@Nonnull IReorderingProcessor text)
    {
        return 0;
    }

    @Override
    public int getLineHeight()
    {
        return 0;
    }

    @Override
    public void renderShadowedText(@Nonnull MatrixStack matrixStack, @Nonnull IReorderingProcessor text, int x, int y, int color)
    {

    }

    @Override
    public void renderText(@Nonnull MatrixStack matrixStack, @Nonnull IReorderingProcessor text, int x, int y, int color)
    {

    }

    @Override
    public void bindTexture(@Nonnull ResourceLocation texture)
    {

    }

    @Override
    public void bindTexture(@Nonnull Texture texture)
    {

    }

    @Override
    public void renderEntityOnScreen(@Nullable MatrixStack matrixStack, @Nullable LivingEntity entity, int screenX, int screenY, float screenMouseX, float screenMouseY, float scale, boolean scaleToBoundingBox)
    {

    }

    @Override
    public void renderItemStack(@Nonnull MatrixStack matrixStack, @Nonnull ItemStack stack, int x, int y, double z, float scale)
    {

    }
}
