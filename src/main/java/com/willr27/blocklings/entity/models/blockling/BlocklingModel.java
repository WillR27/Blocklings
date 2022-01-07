package com.willr27.blocklings.entity.models.blockling;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlocklingModel extends EntityModel<BlocklingEntity> implements IHasArm
{
    public static final float BODY_BASE_ROT_X = 0.0872665f;
    public static final float RIGHT_LEG_BASE_ROT_X = -BODY_BASE_ROT_X;
    public static final float LEFT_LEG_BASE_ROT_X = -BODY_BASE_ROT_X;
    public static final float RIGHT_ARM_BASE_ROT_X = 0.785398f - BODY_BASE_ROT_X;
    public static final float LEFT_ARM_BASE_ROT_X =  0.785398f - BODY_BASE_ROT_X;

    private final ModelRenderer body;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightEye;
    private final ModelRenderer leftEye;

    public BlocklingModel()
    {
        this.texWidth = 128;
        this.texHeight = 64;

        this.body = new ModelRenderer(this, 16, 0);
        this.body.addBox(-6.0F, -3.0F, -6.0F, 12, 12, 12);
        this.body.setPos(0.0F, 13.0F, 0.0F);
        this.body.setTexSize(128, 64);
        this.body.visible = true;
        setRotation(this.body, BODY_BASE_ROT_X, 0.0F, 0.0F);
        this.rightLeg = new ModelRenderer(this, 16, 24);
        this.rightLeg.addBox(-1.5F, 1.0F, -3.5F, 5, 6, 6);
        this.rightLeg.setPos(-4.0F, 4.0F, 0.5F);
        this.rightLeg.setTexSize(128, 64);
        this.rightLeg.visible = true;
        setRotation(this.rightLeg, -RIGHT_LEG_BASE_ROT_X, 0.0F, 0.0F);
        this.leftLeg = new ModelRenderer(this, 42, 24);
        this.leftLeg.addBox(-3.5F, 1.0F, -3.5F, 5, 6, 6);
        this.leftLeg.setPos(4.0F, 4.0F, 0.5F);
        this.leftLeg.setTexSize(128, 64);
        this.leftLeg.visible = true;
        setRotation(this.leftLeg, -LEFT_LEG_BASE_ROT_X, 0.0F, 0.0F);
        this.rightArm = new ModelRenderer(this, 0, 12);
        this.rightArm.addBox(0.0F, 0.0F, -7.0F, 2, 6, 6);
        this.rightArm.setPos(-8.0F, 0.0F, 0.0F);
        this.rightArm.setTexSize(128, 64);
        this.rightArm.visible = true;
        setRotation(this.rightArm, RIGHT_ARM_BASE_ROT_X, 0.0F, 0.0F);
        this.leftArm = new ModelRenderer(this, 64, 12);
        this.leftArm.addBox(-2.0F, 0.0F, -7.0F, 2, 6, 6);
        this.leftArm.setPos(8.0F, 0.0F, 0.0F);
        this.leftArm.setTexSize(128, 64);
        this.leftArm.visible = true;
        setRotation(this.leftArm, LEFT_ARM_BASE_ROT_X, 0.0F, 0.0F);
        this.rightEye = new ModelRenderer(this, 22, 8);
        this.rightEye.addBox(-1.0F, -0.2F, 1.5F, 2, 3, 1);
        this.rightEye.setPos(-2.0F, 3.0F, -8.0F);
        this.rightEye.setTexSize(128, 64);
        this.rightEye.visible = true;
        setRotation(this.rightEye, 0.0F, 0.0F, 0.0F);
        this.leftEye = new ModelRenderer(this, 52, 8);
        this.leftEye.addBox(-1.0F, -0.2F, 1.5F, 2, 3, 1);
        this.leftEye.setPos(2.0F, 3.0F, -8.0F);
        this.leftEye.setTexSize(128, 64);
        this.leftEye.visible = true;
        setRotation(this.leftEye, 0.0F, 0.0F, 0.0F);

        this.body.addChild(this.rightLeg);
        this.body.addChild(this.leftLeg);
        this.body.addChild(this.rightArm);
        this.body.addChild(this.leftArm);
        this.body.addChild(this.rightEye);
        this.body.addChild(this.leftEye);
    }

    private float scaleX = 1.0f, scaleY = 1.0f;
    private boolean hasOriginalBlocklingType = false;

    @Override
    public void setupAnim(BlocklingEntity blockling, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        EntitySize size = blockling.getDimensions(Pose.STANDING);
        scaleX = size.width;
        scaleY = size.height;
        hasOriginalBlocklingType = blockling.getOriginalBlocklingType() == blockling.getBlocklingType();

        float partialTicks = ageInTicks % 1.0f;

        limbSwing *= 0.5f;
        limbSwing += ageInTicks / 20.0f;

        float rightLegSwingAmount = limbSwingAmount;
        float leftLegSwingAmount = limbSwingAmount;

        if (limbSwingAmount > 1.0f)
        {
            limbSwingAmount = 1.0f;
        }
        limbSwingAmount += 0.1f;

        float rightArmSwingAmount = limbSwingAmount;
        float leftArmSwingAmount = limbSwingAmount;

        float bodySwing = 0.0f;
        float rightArmSwing = 0.0f;
        float leftArmSwing = 0.0f;
        float rightLegSwing = 0.0f;
        float leftLegSwing = 0.0f;

        float weaponBonusRotX = 0.7f;

        BlocklingHand hand = blockling.getStats().hand.getValue();
        BlocklingHand attackingHand = blockling.getEquipment().findAttackingHand();

        if (blockling.getTarget() != null)
        {
            if (attackingHand == BlocklingHand.MAIN || attackingHand == BlocklingHand.BOTH)
            {
                rightArmSwing -= blockling.getEquipment().getHandStack(Hand.MAIN_HAND).isEmpty() ? 0.0f : weaponBonusRotX;
                rightArmSwingAmount /= 2.0f;
            }

            if (attackingHand == BlocklingHand.OFF || attackingHand == BlocklingHand.BOTH)
            {
                leftArmSwing += blockling.getEquipment().getHandStack(Hand.OFF_HAND).isEmpty() ? 0.0f : weaponBonusRotX;
                leftArmSwingAmount /= 2.0f;
            }
        }

        if (blockling.getActions().attack.isRunning(BlocklingHand.MAIN))
        {
            float percent = blockling.getActions().attack.percentThroughHandAction(-1) + (blockling.getActions().attack.percentThroughHandAction() - blockling.getActions().attack.percentThroughHandAction(-1)) * partialTicks;
            float attackSwing = (MathHelper.cos(percent * (float) Math.PI / 2.0f) * 2.0f);
            rightArmSwing += blockling.getEquipment().getHandStack(Hand.MAIN_HAND).isEmpty() ? -attackSwing : attackSwing;
        }

        if (blockling.getActions().attack.isRunning(BlocklingHand.OFF))
        {
            float percent = blockling.getActions().attack.percentThroughHandAction(-1) + (blockling.getActions().attack.percentThroughHandAction() - blockling.getActions().attack.percentThroughHandAction(-1)) * partialTicks;
            float attackSwing = (MathHelper.cos(percent * (float) Math.PI / 2.0f) * 2.0f);
            leftArmSwing -= blockling.getEquipment().getHandStack(Hand.OFF_HAND).isEmpty() ? -attackSwing : attackSwing;
        }

        if (blockling.getActions().gather.isRunning())
        {
            if (hand == BlocklingHand.MAIN || hand == BlocklingHand.BOTH)
            {
                rightArmSwing = (MathHelper.cos(ageInTicks + (float) Math.PI) * 1.0f);
            }

            if (hand == BlocklingHand.OFF || hand == BlocklingHand.BOTH)
            {
                leftArmSwing = (MathHelper.cos(ageInTicks + (float) Math.PI) * 1.0f);
            }
        }

        bodySwing += (MathHelper.cos(limbSwing + (float) Math.PI) * limbSwingAmount * 0.1f);
        rightArmSwing += (MathHelper.cos(limbSwing + (float) Math.PI) * rightArmSwingAmount * 0.8f);
        leftArmSwing += (MathHelper.cos(limbSwing + (float) Math.PI) * leftArmSwingAmount) * 0.8f;
        rightLegSwing += (MathHelper.cos(limbSwing + (float) Math.PI) * rightLegSwingAmount * 0.5f);
        leftLegSwing += (MathHelper.cos(limbSwing + (float) Math.PI) * leftLegSwingAmount * 0.5f);

        rightArm.xRot = rightArmSwing + RIGHT_ARM_BASE_ROT_X;
        leftArm.xRot = LEFT_ARM_BASE_ROT_X - leftArmSwing;
        rightLeg.xRot = RIGHT_LEG_BASE_ROT_X - rightLegSwing;
        leftLeg.xRot = leftLegSwing + LEFT_LEG_BASE_ROT_X;

        body.zRot = bodySwing;
        rightLeg.zRot = -body.zRot;
        leftLeg.zRot = -body.zRot;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float r, float g, float b, float a)
    {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 1.501, 0.0); // There is a random 1.501 translation in render that messes up scales
        matrixStack.scale(scaleX, scaleY, scaleX);
        matrixStack.translate(0.0, -1.501, 0.0);

        if (!hasOriginalBlocklingType)
        {
            r *= 0.7f;
            g *= 0.7f;
            b *= 0.7f;
        }

        body.render(matrixStack, buffer, packedLight, packedOverlay, r, g, b, a);

        matrixStack.popPose();
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void translateToHand(HandSide hand, MatrixStack matrixStack)
    {
        body.translateAndRotate(matrixStack);

        if (hand == HandSide.LEFT)
        {
            leftArm.translateAndRotate(matrixStack);
        }
        else
        {
            rightArm.translateAndRotate(matrixStack);
        }
    }
}
