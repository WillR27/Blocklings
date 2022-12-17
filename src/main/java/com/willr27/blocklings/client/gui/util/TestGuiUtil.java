package com.willr27.blocklings.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

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
    public void renderEntityOnScreen(@Nullable MatrixStack matrixStack, @Nullable LivingEntity entity, int screenX, int screenY, float screenMouseX, float screenMouseY, float scale, boolean scaleToBoundingBox)
    {

    }
}
