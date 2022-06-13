package com.willr27.blocklings.client.renderer.entity.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.renderer.entity.BlocklingRenderer;
import com.willr27.blocklings.client.renderer.entity.model.BlocklingModel;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.ToolUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The renderer for the items held by a blockling.
 */
@OnlyIn(Dist.CLIENT)
public class BlocklingHeldItemLayer extends LayerRenderer<BlocklingEntity, BlocklingModel>
{
    /**
     * @param blocklingRenderer the parent renderer.
     */
    public BlocklingHeldItemLayer(@Nonnull BlocklingRenderer blocklingRenderer)
    {
        super(blocklingRenderer);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, @Nonnull BlocklingEntity blockling, float goesUpWhenMovesCouldBeSpeed, float changesWhenMovesCouldBeSwing, float p_225628_7_, float consistentCouldBePartialTicks, float p_225628_9_, float p_225628_10_)
    {
        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        if (!mainStack.isEmpty())
        {
            renderItem(matrixStack, mainStack, false, blockling, renderTypeBuffer, p_225628_3_);
        }

        if (!offStack.isEmpty())
        {
            renderItem(matrixStack, offStack, true, blockling, renderTypeBuffer, p_225628_3_);
        }
    }

    /**
     * Renders a held item.
     *
     * @param matrixStack the matrix stack.
     * @param stack the stack to render.
     * @param isLeftHand whether the item is in the left.
     * @param blockling the blockling holding the item.
     * @param renderTypeBuffer the render buffer.
     * @param p_225628_3_ ???
     */
    private void renderItem(@Nonnull MatrixStack matrixStack, @Nonnull ItemStack stack, boolean isLeftHand, @Nonnull BlocklingEntity blockling, @Nonnull IRenderTypeBuffer renderTypeBuffer, int p_225628_3_)
    {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 1.501, 0.0); // There is a random 1.501 translation in render that messes up scales
        matrixStack.scale(blockling.getScale(), blockling.getScale(), blockling.getScale());
        matrixStack.translate(0.0, -1.501, 0.0);
        getParentModel().translateToHand(isLeftHand ? HandSide.LEFT : HandSide.RIGHT, matrixStack);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(190.0f));
        matrixStack.translate((isLeftHand ? 1.0f : -1.0f) / 16.0f, -0.1f, getItemHandDisplacement(stack));

        Minecraft.getInstance().getItemInHandRenderer().renderItem(blockling, stack, isLeftHand ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, isLeftHand, matrixStack, renderTypeBuffer, p_225628_3_);

        matrixStack.popPose();
    }

    /**
     * @param stack the item stack to render.
     * @return the additional adjustment to line up the item with blockling's hand.
     */
    private float getItemHandDisplacement(@Nonnull ItemStack stack)
    {
        if (ToolUtil.isWeapon(stack))
        {
            return -0.3044f;
        }

        return -0.3552f;
    }
}
