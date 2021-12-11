package com.willr27.blocklings.entity.renderers.blockling;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.models.blockling.BlocklingModel;
import com.willr27.blocklings.item.ToolUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlocklingHeldItemLayer extends LayerRenderer<BlocklingEntity, BlocklingModel>
{
    public BlocklingHeldItemLayer(BlocklingRenderer blocklingRenderer)
    {
        super(blocklingRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, BlocklingEntity blockling, float goesUpWhenMovesCouldBeSpeed, float changesWhenMovesCouldBeSwing, float p_225628_7_, float consistentCouldBePartialTicks, float p_225628_9_, float p_225628_10_)
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

    private void renderItem(MatrixStack matrixStack, ItemStack stack, boolean isLeftHand, BlocklingEntity blockling, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_)
    {
        matrixStack.pushPose();
        getParentModel().translateToHand(isLeftHand ? HandSide.LEFT : HandSide.RIGHT, matrixStack);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(190.0f));
        matrixStack.translate((isLeftHand ? -1.0f : 1.0f) / 16.0f, 0.0f, getItemHandDisplacement(stack));
        Minecraft.getInstance().getItemInHandRenderer().renderItem(blockling, stack, isLeftHand ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, isLeftHand, matrixStack, renderTypeBuffer, p_225628_3_);
        matrixStack.popPose();
    }

    private float getItemHandDisplacement(ItemStack stack)
    {
        if (ToolUtil.isWeapon(stack))
        {
            return 0.1744f;
        }

        return 0.1335f;
    }
}
