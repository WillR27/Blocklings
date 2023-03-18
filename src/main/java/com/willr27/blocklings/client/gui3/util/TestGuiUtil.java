package com.willr27.blocklings.client.gui3.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public void renderEntityOnScreen(@Nullable MatrixStack matrixStack, @Nullable LivingEntity entity, int screenX, int screenY, float screenMouseX, float screenMouseY, float scale, boolean scaleToBoundingBox)
    {

    }
}
