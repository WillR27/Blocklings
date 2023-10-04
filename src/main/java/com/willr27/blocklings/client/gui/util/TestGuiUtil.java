package com.willr27.blocklings.client.gui.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.willr27.blocklings.client.gui.texture.Texture;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
    public boolean isKeyDown(@Nonnull KeyMapping key)
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
    public FormattedText trimWithEllipsis(@Nonnull FormattedText text, int width)
    {
        return text;
    }

    @Nonnull
    @Override
    public FormattedText trim(@Nonnull FormattedText text, int width)
    {
        return text;
    }

    @Nonnull
    @Override
    public List<FormattedCharSequence> split(@Nonnull FormattedText text, int width)
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
    public int getTextWidth(@Nonnull FormattedCharSequence text)
    {
        return 0;
    }

    @Override
    public int getLineHeight()
    {
        return 0;
    }

    @Override
    public void renderShadowedText(@Nonnull PoseStack poseStack, @Nonnull FormattedCharSequence text, int x, int y, int color)
    {

    }

    @Override
    public void renderText(@Nonnull PoseStack poseStack, @Nonnull FormattedCharSequence text, int x, int y, int color)
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
    public void renderEntityOnScreen(@Nullable PoseStack poseStack, @Nullable LivingEntity entity, int screenX, int screenY, float screenMouseX, float screenMouseY, float scale, boolean scaleToBoundingBox)
    {

    }

    @Override
    public void renderItemStack(@Nonnull PoseStack poseStack, @Nonnull ItemStack stack, int x, int y, double z, float scale)
    {

    }
}
