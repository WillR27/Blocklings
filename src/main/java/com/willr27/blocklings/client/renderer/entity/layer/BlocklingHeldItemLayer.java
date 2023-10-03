package com.willr27.blocklings.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.willr27.blocklings.client.renderer.entity.BlocklingRenderer;
import com.willr27.blocklings.client.renderer.entity.model.BlocklingModel;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.ToolUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The renderer for the items held by a blockling.
 */
@OnlyIn(Dist.CLIENT)
public class BlocklingHeldItemLayer extends RenderLayer<BlocklingEntity, BlocklingModel>
{
    /**
     * @param blocklingRenderer the parent renderer.
     */
    public BlocklingHeldItemLayer(@Nonnull BlocklingRenderer blocklingRenderer)
    {
        super(blocklingRenderer);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int p_225628_3_, @Nonnull BlocklingEntity blockling, float goesUpWhenMovesCouldBeSpeed, float changesWhenMovesCouldBeSwing, float p_225628_7_, float consistentCouldBePartialTicks, float p_225628_9_, float p_225628_10_)
    {
        ItemStack mainStack = blockling.getMainHandItem();
        ItemStack offStack = blockling.getOffhandItem();

        if (!mainStack.isEmpty())
        {
            renderItem(poseStack, mainStack, false, blockling, multiBufferSource, p_225628_3_);
        }

        if (!offStack.isEmpty())
        {
            renderItem(poseStack, offStack, true, blockling, multiBufferSource, p_225628_3_);
        }
    }

    /**
     * Renders a held item.
     *
     * @param poseStack the pose stack.
     * @param stack the stack to render.
     * @param isLeftHand whether the item is in the left.
     * @param blockling the blockling holding the item.
     * @param multiBufferSource the render buffer.
     * @param p_225628_3_ ???
     */
    private void renderItem(@Nonnull PoseStack poseStack, @Nonnull ItemStack stack, boolean isLeftHand, @Nonnull BlocklingEntity blockling, @Nonnull MultiBufferSource multiBufferSource, int p_225628_3_)
    {
        poseStack.pushPose();
        poseStack.translate(0.0, 1.501, 0.0); // There is a random 1.501 translation in render that messes up scales
        poseStack.scale(blockling.getScale(), blockling.getScale(), blockling.getScale());
        poseStack.translate(0.0, -1.501, 0.0);
        getParentModel().translateToHand(isLeftHand ? HumanoidArm.LEFT : HumanoidArm.RIGHT, poseStack);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(190.0f));
        poseStack.translate((isLeftHand ? 1.0f : -1.0f) / 16.0f, -0.1f, getItemHandDisplacement(stack));

        Minecraft.getInstance().getItemInHandRenderer().renderItem(blockling, stack, isLeftHand ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, isLeftHand, poseStack, multiBufferSource, p_225628_3_);

        poseStack.popPose();
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
