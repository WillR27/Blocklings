package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.willr27.blocklings.client.gui.control.controls.config.BlockSideSelectionControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;

import static net.minecraftforge.common.model.TransformationHelper.quatFromXYZ;

public class TestScreen extends BlocklingsScreen
{
    /**
     * @param blockling the blockling associated with the screen.
     */
    public TestScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling);

        BlockSideSelectionControl blockSideSelectionControl = new BlockSideSelectionControl();
        blockSideSelectionControl.setParent(screenControl);
        blockSideSelectionControl.setBlock(Blocks.CHEST);
//        blockSideSelectionControl.setBackgroundColour(0xffffffff);
        blockSideSelectionControl.setWidth(100);
        blockSideSelectionControl.setHeight(100);
        blockSideSelectionControl.setBlockScale(0.5f);
        blockSideSelectionControl.setRenderZ(50.0);
        blockSideSelectionControl.setHorizontalAlignment(0.5);
        blockSideSelectionControl.setVerticalAlignment(0.5);
        blockSideSelectionControl.setCanMouseRotate(true);
    }
float ticks = 0.0f;
    @Override
    public void render(@Nonnull PoseStack poseStac, int mouseX, int mouseY, float partialTicks)
    {
//        super.render(poseStack, mouseX, mouseY, partialTicks);

        ticks += 0.5f;

        int cwidth = 100;
        int cheight = 100;
        float scaleX = 1.0f;
        float blockScale = 1.0f;
        float pixelScale = GuiUtil.get().getGuiScale() * scaleX;
        float pixelX = 50;
        float pixelY = 100;
        float pixelWidth = cwidth * pixelScale;
        float pixelHeight = cheight * pixelScale;
        Block block = Blocks.FURNACE;
        BlockState blockState = block.defaultBlockState();

        Quaternion rotationQuat = Quaternion.ONE.copy();
        rotationQuat.mul(Vector3f.XP.rotationDegrees(ticks));
//        rotationQuat.mul(Vector3f.XP.rotationDegrees(10.0f));
        rotationQuat.mul(Vector3f.YP.rotationDegrees(ticks));

        float scale = (float) ((Math.min(cwidth, cheight) * scaleX) * blockScale * pixelScale);
        float width = scale / 2.0f;
        float widthSquared = width * width;
        double cubeDiagFromCenterToCorner = Math.sqrt(widthSquared + widthSquared + widthSquared);
        float x = (float) ((pixelX) * scaleX);
        float y = (float) ((pixelY) * scaleX);
        float z = (float) cubeDiagFromCenterToCorner;
        double extraX = (((pixelWidth / 2.0) / scale)) * scaleX;
        double extraY = (((pixelHeight / 2.0) / scale)) * scaleX;

        Minecraft.getInstance().textureManager.bindForSetup(TextureAtlas.LOCATION_BLOCKS);
        Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        //RenderSystem.enableRescaleNormal();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.defaultAlphaFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

//        PoseStack poseStack = new PoseStack();
//        poseStack.pushPose();
////        poseStack.translate(x, y, z);
//        poseStack.translate(100, 100, 0);
//        poseStack.scale(1.0F, -1.0F, 1.0F);
//        poseStack.scale(scale, scale, scale);
//
//        poseStack.pushPose();
//        poseStack.translate(extraX, -extraY, 0.0);
//        poseStack.translate(-0.5f, 0.5f, -0.5f);
//        poseStack.mulPose(rotationQuat);
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
//        poseStack.mulPose(Vector3f.YP.rotationDegrees(blockState.getRenderShape() == RenderShape.MODEL ? 180.0f : 0.0f));
//        poseStack.translate(-0.5f, -0.5f, -0.5f);
//        poseStack.scale(1.0f, 1.0f, 1.0f);

        GuiUtil.disableScissor();
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
//        poseStack.scale(1.0f, -1.0f, 1.0f);
        poseStack.translate(200, 00, 100);
        poseStack.scale(100, 100, 100);
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(10.0f));
        poseStack.mulPose(rotationQuat);
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
//        itemRenderer.renderGuiItem(new ItemStack(block.asItem()), 10, 10);

        PoseStack asd = RenderSystem.getModelViewStack();
        asd.pushPose();
//        asd.translate((double)10, (double)10, (double)(100.0F));
        asd.translate(x, y, 0.0D);
        asd.scale(1.0F, -1.0F, 1.0F);
//        asd.translate(-0.5f, -0.5f, -0.5f);
//        asd.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.setupGuiFlatDiffuseLighting(
                Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize),
                Util.make(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::normalize));
        blockRenderer.renderSingleBlock(blockState, poseStack, irendertypebuffer$impl, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        irendertypebuffer$impl.endBatch();
        asd.popPose();

        ItemStack stack = new ItemStack(blockState.getBlock());

        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((double)10, (double)10, (double)(100.0F));
        posestack.translate(x, y, 0.0D);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();

        PoseStack pogPoseStack = new PoseStack();
//        Transformation transformation = new Transformation(new Vector3f(0, 0, 0), quatFromXYZ(new Vector3f(30.0f, ticks, 0.0f), true), new Vector3f(0.625f, 0.625f, 0.625f), null);
//        transformation.push(pogPoseStack);
//        pogPoseStack.translate(0, 0, 0);
        pogPoseStack.mulPose(rotationQuat);
//        pogPoseStack.scale(0.625f, 0.625f, 0.625f);
        pogPoseStack.translate(-0.5, -0.5, -0.5);
        net.minecraftforge.client.RenderProperties.get(stack).getItemStackRenderer().renderByItem(stack, ItemTransforms.TransformType.GUI, pogPoseStack, irendertypebuffer$impl, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        irendertypebuffer$impl.endBatch();

        poseStack.popPose();
        posestack.popPose();
    }
}
