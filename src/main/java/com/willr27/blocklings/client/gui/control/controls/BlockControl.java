package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;

/**
 * A control used to display a block.
 */
@OnlyIn(Dist.CLIENT)
public class BlockControl extends Control
{
    /**
     * The previous mouse x position.
     */
    private double previousMouseX = 0.0;

    /**
     * The previous mouse y position.
     */
    private double previousMouseY = 0.0;

    /**
     * The amount dragged in the last drag.
     */
    protected double dragAmount = 0.0;

    /**
     * The amount the block is rotated.
     */
    @Nonnull
    protected Quaternion rotationQuat = Quaternion.ONE.copy();

    /**
     * The block.
     */
    @Nonnull
    private Block block = Blocks.AIR;

    /**
     * The scale to render the block at relative to size of the control.
     */
    private float blockScale = 0.6f;

    /**
     * Whether the block can be rotated with the mouse.
     */
    private boolean canMouseRotate = false;

    /**
     * The screen x position of the block.
     */
    protected float x;

    /**
     * The screen y position of the block.
     */
    protected float y;

    /**
     * The screen z position of the block.
     */
    protected float z;

    /**
     * The scale of the block.
     */
    protected float scale;

    /**
     */
    public BlockControl()
    {
        super();
        rotationQuat.mul(Vector3f.XP.rotationDegrees(30.0f));
        rotationQuat.mul(Vector3f.YP.rotationDegrees(45.0f));
    }

    @Override
    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        double pixelMouseDeltaX = mouseX - previousMouseX;
        double pixelMouseDeltaY = mouseY - previousMouseY;
        float mouseDeltaX = (float) (pixelMouseDeltaX / getPixelScaleX());
        float mouseDeltaY = (float) (pixelMouseDeltaY / getPixelScaleY());

        if (isPressed() && canMouseRotate())
        {
            Quaternion quat = Vector3f.YP.rotationDegrees((float) (mouseDeltaX * getPixelScaleX()) * 0.4f);
            quat.mul(Vector3f.XP.rotationDegrees((float) (mouseDeltaY * getPixelScaleY()) * 0.4f));
            quat.mul(rotationQuat);
            rotationQuat = quat;

            dragAmount += Math.abs(mouseDeltaX) + Math.abs(mouseDeltaY);
        }
        else
        {
            dragAmount = 0.0;
        }

        z = isDraggingOrAncestor() ? (float) getDraggedControl().getDragZ() : (float) getRenderZ();

        try
        {
            // For some reason we can't just access the values in the matrix.
            // So we have to get the z translation via reflection. Nice.
            z = ObfuscationReflectionHelper.getPrivateValue(Matrix4f.class, matrixStack.last().pose(), "m23");
        }
        catch (Exception ex)
        {
//            Blocklings.LOGGER.warn(ex.toString());
        }

        scale = (float) (Math.min(getWidth(), getHeight()) * getScaleX()) * getBlockScale();
        float width = scale / 2.0f;
        float widthSquared = width * width;
        double cubeDiagFromCenterToCorner = Math.sqrt(widthSquared + widthSquared + widthSquared);
        x = (float) ((getPixelX() / getPixelScaleX()) * getScaleX());
        y = (float) ((getPixelY() / getPixelScaleY()) * getScaleY());
        z += (float) cubeDiagFromCenterToCorner;
        double extraX = (((getPixelWidth() / 2.0) / scale) / getPixelScaleX()) * getScaleX();
        double extraY = (((getPixelHeight() / 2.0) / scale) / getPixelScaleY()) * getScaleY();

        RenderSystem.pushMatrix();
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.translatef(x, y, z);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(scale, scale, scale);

        MatrixStack blockMatrixStack = new MatrixStack();
        blockMatrixStack.translate(extraX, -extraY, 0.0);
        blockMatrixStack.mulPose(rotationQuat);
        blockMatrixStack.mulPose(Vector3f.YP.rotationDegrees(getBlockState().getRenderShape() == BlockRenderType.MODEL ? 180.0f : 0.0f));
        blockMatrixStack.translate(-0.5f, -0.5f, -0.5f);

        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        blockRenderer.renderSingleBlock(getBlockState(), blockMatrixStack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY);
        irendertypebuffer$impl.endBatch();

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();

        previousMouseX = mouseX;
        previousMouseY = mouseY;
    }

    /**
     * @return the block state.
     */
    @Nonnull
    public BlockState getBlockState()
    {
        return block.defaultBlockState();
    }

    /**
     * @return the block.
     */
    @Nonnull
    public Block getBlock()
    {
        return block;
    }

    /**
     * Sets the block.
     *
     * @param block the block.
     */
    public void setBlock(@Nonnull Block block)
    {
        this.block = block;
    }

    /**
     * @return the scale to render the block.
     */
    public float getBlockScale()
    {
        return blockScale;
    }

    /**
     * Sets the scale to render the block.
     *
     * @param blockScale the scale.
     */
    public void setBlockScale(float blockScale)
    {
        this.blockScale = blockScale;
    }

    /**
     * @return whether the block can be rotated with the mouse.
     */
    public boolean canMouseRotate()
    {
        return canMouseRotate;
    }

    /**
     * Sets whether the block can be rotated with the mouse.
     *
     * @param canMouseRotate whether the block can be rotated with the mouse.
     */
    public void setCanMouseRotate(boolean canMouseRotate)
    {
        this.canMouseRotate = canMouseRotate;
    }

    /**
     * @return the rotation quaternion.
     */
    @Nonnull
    public Quaternion getRotationQuat()
    {
        return rotationQuat;
    }

    /**
     * Sets the rotation quaternion.
     *
     * @param rotationQuat the rotation quaternion.
     */
    public void setRotationQuat(@Nonnull Quaternion rotationQuat)
    {
        this.rotationQuat = rotationQuat;
    }
}
