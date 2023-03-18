package com.willr27.blocklings.client.gui3.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.command.TextComponentHelper;

import javax.annotation.Nonnull;
import java.util.List;

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
        return mc.font.split(text, width);
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
}
