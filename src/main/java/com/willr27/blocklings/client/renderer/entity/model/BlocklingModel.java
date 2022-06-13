package com.willr27.blocklings.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
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

import javax.annotation.Nonnull;

/**
 * The model for the blockling.
 */
@OnlyIn(Dist.CLIENT)
public class BlocklingModel extends EntityModel<BlocklingEntity> implements IHasArm
{
    /**
     * The initial x rotation for the body.
     */
    public static final float BODY_BASE_ROT_X = 0.0872665f;

    /**
     * The initial x rotation for the right leg.
     */
    public static final float RIGHT_LEG_BASE_ROT_X = -BODY_BASE_ROT_X;

    /**
     * The initial x rotation for the left leg.
     */
    public static final float LEFT_LEG_BASE_ROT_X = -BODY_BASE_ROT_X;

    /**
     * The initial x rotation for the right arm.
     */
    public static final float RIGHT_ARM_BASE_ROT_X = 0.785398f - BODY_BASE_ROT_X;

    /**
     * The initial x rotation for the left arm.
     */
    public static final float LEFT_ARM_BASE_ROT_X =  0.785398f - BODY_BASE_ROT_X;

    /**
     * The model rendered for the body.
     */
    private final ModelRenderer body;

    /**
     * The model rendered for the right leg.
     */
    private final ModelRenderer rightLeg;

    /**
     * The model rendered for the left leg.
     */
    private final ModelRenderer leftLeg;

    /**
     * The model rendered for the right arm.
     */
    private final ModelRenderer rightArm;

    /**
     * The model rendered for the left arm.
     */
    private final ModelRenderer leftArm;

    /**
     * The model rendered for the right eye.
     */
    private final ModelRenderer rightEye;

    /**
     * The model rendered for the left eye.
     */
    private final ModelRenderer leftEye;

    /**
     * The blockling's x scale.
     */
    private float scaleX = 1.0f;

    /**
     * The blockling's y scale.
     */
    private float scaleY = 1.0f;

    /**
     * Constructor.
     */
    public BlocklingModel()
    {
        texWidth = 128;
        texHeight = 64;

        body = new ModelRenderer(this, 16, 0);
        body.addBox(-6.0f, -3.0f, -6.0f, 12, 12, 12);
        body.setPos(0.0f, 13.0f, 0.0f);
        body.setTexSize(128, 64);
        body.visible = true;
        setRotation(this.body, BODY_BASE_ROT_X, 0.0f, 0.0f);
        rightLeg = new ModelRenderer(this, 16, 24);
        rightLeg.addBox(-1.5f, 1.0f, -3.5f, 5, 6, 6);
        rightLeg.setPos(-4.0f, 4.0f, 0.5f);
        rightLeg.setTexSize(128, 64);
        rightLeg.visible = true;
        setRotation(this.rightLeg, -RIGHT_LEG_BASE_ROT_X, 0.0f, 0.0f);
        leftLeg = new ModelRenderer(this, 42, 24);
        leftLeg.addBox(-3.5f, 1.0f, -3.5f, 5, 6, 6);
        leftLeg.setPos(4.0f, 4.0f, 0.5f);
        leftLeg.setTexSize(128, 64);
        leftLeg.visible = true;
        setRotation(this.leftLeg, -LEFT_LEG_BASE_ROT_X, 0.0f, 0.0f);
        rightArm = new ModelRenderer(this, 0, 12);
        rightArm.addBox(0.0f, 0.0f, -7.0f, 2, 6, 6);
        rightArm.setPos(-8.0f, 0.0f, 0.0f);
        rightArm.setTexSize(128, 64);
        rightArm.visible = true;
        setRotation(this.rightArm, RIGHT_ARM_BASE_ROT_X, 0.0f, 0.0f);
        leftArm = new ModelRenderer(this, 64, 12);
        leftArm.addBox(-2.0f, 0.0f, -7.0f, 2, 6, 6);
        leftArm.setPos(8.0f, 0.0f, 0.0f);
        leftArm.setTexSize(128, 64);
        leftArm.visible = true;
        setRotation(this.leftArm, LEFT_ARM_BASE_ROT_X, 0.0f, 0.0f);
        rightEye = new ModelRenderer(this, 22, 8);
        rightEye.addBox(-1.0f, -0.2f, 1.5f, 2, 3, 1);
        rightEye.setPos(-2.0f, 3.0f, -8.0f);
        rightEye.setTexSize(128, 64);
        rightEye.visible = true;
        setRotation(this.rightEye, 0.0f, 0.0f, 0.0f);
        leftEye = new ModelRenderer(this, 52, 8);
        leftEye.addBox(-1.0f, -0.2f, 1.5f, 2, 3, 1);
        leftEye.setPos(2.0f, 3.0f, -8.0f);
        leftEye.setTexSize(128, 64);
        leftEye.visible = true;
        setRotation(this.leftEye, 0.0f, 0.0f, 0.0f);

        body.addChild(this.rightLeg);
        body.addChild(this.leftLeg);
        body.addChild(this.rightArm);
        body.addChild(this.leftArm);
        body.addChild(this.rightEye);
        body.addChild(this.leftEye);
    }

    @Override
    public void setupAnim(@Nonnull BlocklingEntity blockling, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        EntitySize size = blockling.getDimensions(Pose.STANDING);
        scaleX = size.width;
        scaleY = size.height;

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
    public void renderToBuffer(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float r, float g, float b, float a)
    {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 1.501, 0.0); // There is a random 1.501 translation in render that messes up scales
        matrixStack.scale(scaleX, scaleY, scaleX);
        matrixStack.translate(0.0, -1.501, 0.0);

        body.render(matrixStack, buffer, packedLight, packedOverlay, r, g, b, a);

        matrixStack.popPose();
    }

    @Override
    public void translateToHand(@Nonnull HandSide hand, @Nonnull MatrixStack matrixStack)
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

    /**
     * Helper to set all rotations in one go.
     *
     * @param model the model to set the rotations for.
     * @param x the x rotation.
     * @param y the y rotation.
     * @param z the z rotation.
     */
    private static void setRotation(@Nonnull ModelRenderer model, float x, float y, float z)
    {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }
}
