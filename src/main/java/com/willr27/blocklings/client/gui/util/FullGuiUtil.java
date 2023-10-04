package com.willr27.blocklings.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.willr27.blocklings.client.gui.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains fully implemented methods for {@link GuiUtil}.
 */
@OnlyIn(Dist.CLIENT)
public class FullGuiUtil extends GuiUtil
{
    /**
     * The instance of {@link Minecraft}.
     */
    @Nonnull
    private static final Minecraft mc = Minecraft.getInstance();

    @Override
    public float getGuiScale()
    {
        return (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    @Override
    public float getMaxGuiScale()
    {
        return (float) Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode());
    }

    @Override
    public int getPixelMouseX()
    {
        return (int) Minecraft.getInstance().mouseHandler.xpos();
    }

    @Override
    public int getPixelMouseY()
    {
        return (int) Minecraft.getInstance().mouseHandler.ypos();
    }

    @Override
    public boolean isKeyDown(int key)
    {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    @Override
    public boolean isKeyDown(@Nonnull KeyMapping key)
    {
        return isKeyDown(key.getKey().getValue());
    }

    @Override
    public boolean isControlKeyDown()
    {
        return isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL);
    }

    @Override
    public boolean isCrouchKeyDown()
    {
        return isKeyDown(mc.options.keyShift);
    }

    @Override
    public boolean isCloseKey(int key)
    {
        return key == GLFW.GLFW_KEY_ESCAPE || key == mc.options.keyInventory.getKey().getValue();
    }

    @Override
    public boolean isUnfocusTextFieldKey(int key)
    {
        return key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER;
    }

    @Nonnull
    @Override
    public FormattedText trimWithEllipsis(@Nonnull FormattedText text, int width)
    {
        if (text.getString().equals(trim(text, width).getString()))
        {
            return text;
        }
        else
        {
            return FormattedText.composite(trim(text, width - mc.font.width("...")), new TextComponent("..."));
        }
    }

    @Nonnull
    @Override
    public FormattedText trim(@Nonnull FormattedText text, int width)
    {
        return Minecraft.getInstance().font.substrByWidth(text, width);
    }

    @Nonnull
    @Override
    public List<FormattedCharSequence> split(@Nonnull FormattedText text, int width)
    {
        return new ArrayList<>(mc.font.split(text, width));
    }

    @Nonnull
    @Override
    public List<String> split(@Nonnull String text, int width)
    {
        return mc.font.getSplitter().splitLines(text, width, Style.EMPTY).stream().map(t -> t.getString()).collect(Collectors.toList());
    }

    @Override
    public int getTextWidth(@Nonnull String text)
    {
        return mc.font.width(text);
    }

    @Override
    public int getTextWidth(@Nonnull FormattedCharSequence text)
    {
        return mc.font.width(text);
    }

    @Override
    public int getLineHeight()
    {
        return mc.font.lineHeight;
    }

    @Override
    public void renderShadowedText(@Nonnull PoseStack poseStack, @Nonnull FormattedCharSequence text, int x, int y, int color)
    {
        mc.font.drawShadow(poseStack, text, x, y, color);
    }

    @Override
    public void renderText(@Nonnull PoseStack poseStack, @Nonnull FormattedCharSequence text, int x, int y, int color)
    {
        mc.font.draw(poseStack, text, x, y, color);
    }

    @Override
    public void bindTexture(@Nonnull ResourceLocation texture)
    {
        mc.getTextureManager().bindForSetup(texture);
    }

    @Override
    public void bindTexture(@Nonnull Texture texture)
    {
        bindTexture(texture.resourceLocation);
    }

    @Override
    public void renderEntityOnScreen(@Nonnull PoseStack poseStack, @Nonnull LivingEntity entity, int screenX, int screenY, float screenMouseX, float screenMouseY, float scale, boolean scaleToBoundingBox)
    {
        String name = entity.getCustomName() != null ? entity.getCustomName().getString() : null;
        entity.setCustomName(null);
        float f = (float)Math.atan((double)((screenX - screenMouseX) / 40.0F));
        float f1 = (float)Math.atan((double)((screenY - screenMouseY) / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)screenX, (float)screenY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        matrixStack.pushPose();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        float scale2 = scaleToBoundingBox ? 16.0f / Math.max(entity.getBbWidth(), entity.getBbHeight()) : 16.0f;
        matrixStack.scale((scale * scale2), (scale * scale2), (scale * scale2));
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        poseStack.mulPose(quaternion);
        float f2 = entity.yBodyRot;
        float f3 = entity.getYRot();
        float f4 = entity.getXRot();
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.setYBodyRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        RenderSystem.disableDepthTest();
        RenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, irendertypebuffer$impl, 15728880);
        });
        irendertypebuffer$impl.endBatch();
        entityrenderermanager.setRenderShadow(true);
        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        RenderSystem.popMatrix();
        poseStack.popPose();
        entity.setCustomName(new TextComponent(name));
    }

    @Override
    public void renderItemStack(@Nonnull PoseStack poseStack, @Nonnull ItemStack stack, int x, int y, double z, float scale)
    {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderSystem.pushMatrix();
        Minecraft.getInstance().textureManager.bindForSetup(TextureAtlas.LOCATION_BLOCKS);
        Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, (float) z + 7.0f);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(scale, scale, scale);
        PoseStack poseStack = new PoseStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !itemRenderer.getModel(stack, null, null).usesBlockLight();
        if (flag) {
            RenderHelper.setupForFlatItems();
        }

        itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, poseStack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null));
        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupFor3DItems();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }
}
