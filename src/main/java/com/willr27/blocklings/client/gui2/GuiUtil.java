package com.willr27.blocklings.client.gui2;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class GuiUtil
{
    /**
     * @return the mouse x position on the window.
     */
    public static int getPixelMouseX()
    {
        return (int) Minecraft.getInstance().mouseHandler.xpos();
    }

    /**
     * @return the mouse y position on the window.
     */
    public static int getPixelMouseY()
    {
        return (int) Minecraft.getInstance().mouseHandler.ypos();
    }

    /**
     * @return the current gui scale.
     */
    public static float getGuiScale()
    {
        return (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    public static void bindTexture(ResourceLocation texture)
    {
        Minecraft.getInstance().getTextureManager().bind(texture);
    }

    public static boolean isCloseInventoryKey(int keyCode)
    {
        return keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue();
    }

    public static List<String> splitText(FontRenderer font, String text, int maxWidth)
    {
        List<String> outText = new ArrayList<>();

        String tempString = text;
        while (font.width(tempString) > maxWidth)
        {
            String trim = font.plainSubstrByWidth(tempString, maxWidth);
            if (tempString.substring(trim.length(), trim.length() + 1) != " ")
            {
                int i = trim.lastIndexOf(" ");
                if (i != -1) trim = trim.substring(0, i);
            }
            outText.add(trim);
            tempString = tempString.substring(trim.length() + 1);
        }
        outText.add(tempString);

        return outText;
    }

    public static List<StringTextComponent> splitText(FontRenderer font, ITextComponent text, int maxWidth)
    {
        return splitText(font, text.getString(), maxWidth).stream().map(s -> new StringTextComponent(s)).collect(Collectors.toList());
    }

    public static List<IReorderingProcessor> toReorderingProcessorList(List<? extends ITextComponent> list)
    {
        return list.stream().map(iTextComponent -> iTextComponent.getVisualOrderText()).collect(Collectors.toList());
    }

    public static String trimWithEllipses(FontRenderer font, String text, int maxWidth)
    {
        String trimmed = trim(font, text, maxWidth);
        if (!trimmed.equals(text))
        {
            return appendOrReplace(font, trimmed, "...", maxWidth);
        }
        return trimmed;
    }

    public static String trim(FontRenderer font, String text, int maxWidth)
    {
        return font.substrByWidth(new StringTextComponent(text), maxWidth).getString();
    }

    public static String appendOrReplace(FontRenderer font, String text, String replace, int maxWidth)
    {
        int replaceWidth = font.width(replace);
        int cumulativeWidth = 0;

        while (cumulativeWidth < replaceWidth && text.length() != 0 && font.width(text + replace) > maxWidth)
        {
            cumulativeWidth += font.width(text.substring(text.length() - 1));
            text = text.substring(0, text.length() - 1);
        }

        return text + replace;
    }

    public static boolean isKeyDown(int key)
    {
        return InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    public static boolean isMouseOver(int mouseX, int mouseY, int left, int top, int width, int height)
    {
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY <= top + height;
    }

    public static void renderItemStack(MatrixStack matrixStack, ItemStack stack, int x, int y, float scale)
    {
//        matrixStack.pushPose();
//        RenderSystem.disableDepthTest();
//        RenderSystem.enableLighting();
//        if (i % 4 == 1) matrixStack.translate(0.2F, 0.0F, 0.0F);
//        else if (i % 4 == 2) matrixStack.translate(-0.23F, 0.0F, 0.0F);
//        float scale = 1.2f;
//        matrixStack.scale(scale, scale, scale);
//        itemRenderer.renderAndDecorateItem(stack, (int)(x / scale), (int)(y / scale));
//        itemRenderer.renderGuiItemDecorations(font, stack, (int)(x / scale), (int)(y / scale));
//        if (i % 4 == 1) matrixStack.translate(-0.2F, 0.0F, 0.0F);
//        else if (i % 4 == 2) matrixStack.translate(0.23F, 0.0F, 0.0F);
//        matrixStack.scale(1 / scale, 1 / scale, 1 / scale);
//        matrixStack.popPose();
//        RenderSystem.enableDepthTest();

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderSystem.pushMatrix();
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 100.0F);
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

    public static void renderEntityOnScreen(MatrixStack matrixStack, int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity entity)
    {
        String name = entity.getCustomName() != null ? entity.getCustomName().getString() : null;
        entity.setCustomName(null);
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float f1 = (float)Math.atan((double)(mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        matrixStack.pushPose();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        float scale2 = 1.0f / Math.max(entity.getBbWidth(), entity.getBbHeight());
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

//    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity ent)
//    {
//        String name = ent.getCustomName() != null ?ent.getCustomName().getString() : null;
//        ent.setCustomName(null);
//        GlStateManager.enableColorMaterial();
//        GlStateManager.pushMatrix();
//        GlStateManager.translatef((float)posX, (float)posY, 50.0F);
//        float scale2 = 1.0f / Math.max(ent.getWidth(), ent.getHeight());
//        GlStateManager.scalef((float)(-scale * scale2), (float)scale * scale2, (float)scale * scale2);
//        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
//        float f = ent.renderYawOffset;
//        float f1 = ent.rotationYaw;
//        float f2 = ent.rotationPitch;
//        float f3 = ent.prevRotationYawHead;
//        float f4 = ent.rotationYawHead;
//        GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
//        GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
//        GlStateManager.rotatef(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
//        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
//        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
//        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
//        ent.rotationYawHead = ent.rotationYaw;
//        ent.prevRotationYawHead = ent.rotationYaw;
//        GlStateManager.translatef(0.0F, 0.0F, 0.0F);
//        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
//        entityrenderermanager.setPlayerViewY(180.0F);
//        entityrenderermanager.setRenderShadow(false);
//        RenderHelper.enableStandardItemLighting();
//        entityrenderermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
//        entityrenderermanager.setRenderShadow(true);
//        ent.renderYawOffset = f;
//        ent.rotationYaw = f1;
//        ent.rotationPitch = f2;
//        ent.prevRotationYawHead = f3;
//        ent.rotationYawHead = f4;
//        GlStateManager.popMatrix();
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
//        GlStateManager.disableTexture();
//        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
//        ent.setCustomName(new StringTextComponent(name));
//    }

    public static void enableScissor()
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void disableScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static int minScissorX, minScissorY, maxScissorX, maxScissorY;

    public static void setScissorBounds(int minX, int minY, int maxX, int maxY)
    {
        MainWindow window = Minecraft.getInstance().getWindow();
        int scale = (int) window.getGuiScale();

        minScissorX = minX * scale;
        minScissorY = window.getHeight() - (maxY * scale);
        maxScissorX = maxX * scale;
        maxScissorY = window.getHeight() - (minY * scale);
    }

    private static class ScissorBounds
    {
        public int x, y, width, height;

        public ScissorBounds(int x, int y, int width, int height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof ScissorBounds)
            {
                ScissorBounds scissorBounds = (ScissorBounds) obj;

                return x == scissorBounds.x && y == scissorBounds.y && width == scissorBounds.width && height == scissorBounds.height;
            }

            return super.equals(obj);
        }
    }

    private static List<ScissorBounds> stackedScissors = new ArrayList<>();

    public static void clearScissorBounds()
    {
        stackedScissors.clear();
        disableScissor();
    }

    public static void addScissorBounds(int x, int y, int width, int height)
    {
        stackedScissors.add(new ScissorBounds(x, y, width, height));
    }

    public static void removeScissorBounds(int x, int y, int width, int height)
    {
        ScissorBounds scissorBounds1 = stackedScissors.stream().filter(scissorBounds -> scissorBounds.equals(new ScissorBounds(x, y, width, height))).findFirst().orElse(null);
        stackedScissors.remove(scissorBounds1);
    }

    public static void enableStackedScissor()
    {
        int minX = 0;
        int minY = 0;
        int maxX = 10000;
        int maxY = 10000;

        for (ScissorBounds bounds : stackedScissors)
        {
            minX = Math.max(minX, bounds.x);
            minY = Math.max(minY, bounds.y);
            maxX = Math.min(maxX, bounds.x + bounds.width);
            maxY = Math.min(maxY, bounds.y + bounds.height);
        }

        if (minX < maxX && minY < maxY)
        {
            scissor(minX, minY, maxX - minX, maxY - minY);
        }
        else
        {
            scissor(0, 0, 0, 0);
        }
    }

    public static void scissor(float x, float y, int width, int height)
    {
        scissor(x, y, width, height, false);
    }

    public static boolean useGuiScaleForScissor = false;

    public static void scissor(float x, float y, int width, int height, boolean useMaxBounds)
    {
        MainWindow window = Minecraft.getInstance().getWindow();
        float scale = useGuiScaleForScissor ? (int) window.getGuiScale() : 1.0f;

        int scissorX = (int) (x * scale);
        int scissorY = (int) (window.getHeight() - ((y + height) * scale));
        int scissorWidth = (int) (width * scale);
        int scissorHeight = (int) (height * scale);

        if (useMaxBounds)
        {
            scissorX = Math.max(scissorX, minScissorX);
            scissorY = Math.max(scissorY, minScissorY);

            if (scissorX > maxScissorX)
            {
                scissorWidth = 0;
            }
            else
            {
                scissorWidth = Math.min(scissorWidth, maxScissorX - scissorX);
            }

            if (scissorY > maxScissorY)
            {
                scissorHeight = 0;
            }
            else
            {
                scissorHeight = Math.min(scissorHeight, maxScissorY - scissorY);
            }
        }

        enableScissor();
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }
}