package com.willr27.blocklings.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
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
        return InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    @Override
    public boolean isKeyDown(@Nonnull KeyBinding key)
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
    public ITextProperties trimWithEllipsis(@Nonnull ITextProperties text, int width)
    {
        if (text.getString().equals(trim(text, width).getString()))
        {
            return text;
        }
        else
        {
            return ITextProperties.composite(trim(text, width - mc.font.width("...")), new StringTextComponent("..."));
        }
    }

    @Nonnull
    @Override
    public ITextProperties trim(@Nonnull ITextProperties text, int width)
    {
        return Minecraft.getInstance().font.substrByWidth(text, width);
    }

    @Nonnull
    @Override
    public List<IReorderingProcessor> split(@Nonnull ITextProperties text, int width)
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
    public int getTextWidth(@Nonnull IReorderingProcessor text)
    {
        return mc.font.width(text);
    }

    @Override
    public int getLineHeight()
    {
        return mc.font.lineHeight;
    }

    @Override
    public void renderShadowedText(@Nonnull MatrixStack matrixStack, @Nonnull IReorderingProcessor text, int x, int y, int color)
    {
        mc.font.drawShadow(matrixStack, text, x, y, color);
    }

    @Override
    public void renderText(@Nonnull MatrixStack matrixStack, @Nonnull IReorderingProcessor text, int x, int y, int color)
    {
        mc.font.draw(matrixStack, text, x, y, color);
    }

    @Override
    public void bindTexture(@Nonnull ResourceLocation texture)
    {
        mc.getTextureManager().bind(texture);
    }

    @Override
    public void bindTexture(@Nonnull Texture texture)
    {
        bindTexture(texture.resourceLocation);
    }

    @Override
    public void renderEntityOnScreen(@Nonnull MatrixStack matrixStack, @Nonnull LivingEntity entity, int screenX, int screenY, float screenMouseX, float screenMouseY, float scale, boolean scaleToBoundingBox)
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
        matrixStack.mulPose(quaternion);
        float f2 = entity.yBodyRot;
        float f3 = entity.yRot;
        float f4 = entity.xRot;
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.yRot = 180.0F + f * 40.0F;
        entity.xRot = -f1 * 20.0F;
        entity.yHeadRot = entity.yRot;
        entity.yHeadRotO = entity.yRot;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        RenderSystem.disableDepthTest();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, irendertypebuffer$impl, 15728880);
        });
        irendertypebuffer$impl.endBatch();
        entityrenderermanager.setRenderShadow(true);
        entity.yBodyRot = f2;
        entity.yRot = f3;
        entity.xRot = f4;
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        RenderSystem.popMatrix();
        matrixStack.popPose();
        entity.setCustomName(new StringTextComponent(name));
    }

    @Override
    public void renderItemStack(@Nonnull MatrixStack matrixStack, @Nonnull ItemStack stack, int x, int y, double z, float scale)
    {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderSystem.pushMatrix();
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, (float) z + 7.0f);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(scale, scale, scale);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !itemRenderer.getModel(stack, null, null).usesBlockLight();
        if (flag) {
            RenderHelper.setupForFlatItems();
        }

        itemRenderer.render(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null));
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
