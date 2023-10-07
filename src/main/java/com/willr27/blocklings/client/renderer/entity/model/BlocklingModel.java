package com.willr27.blocklings.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;

import javax.annotation.Nonnull;

public class BlocklingModel extends EntityModel<BlocklingEntity> implements ArmedModel
{
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("blocklings", "blocklingmodel"), "main");
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
	private ModelPart body;
	private ModelPart rightLeg;
	private ModelPart leftLeg;
	private ModelPart rightArm;
	private ModelPart leftArm;

	/**
	 * The blockling's x scale.
	 */
	private float scaleX = 1.0f;

	/**
	 * The blockling's y scale.
	 */
	private float scaleY = 1.0f;
	public BlocklingModel(ModelPart root) {
		body = root.getChild("body");
		rightLeg = body.getChild("rightLeg");
		leftLeg = body.getChild("leftLeg");
		rightArm = body.getChild("rightArm");
		leftArm = body.getChild("leftArm");

		setRotation(this.body, BODY_BASE_ROT_X, 0.0f, 0.0f);
		setRotation(this.rightLeg, -RIGHT_LEG_BASE_ROT_X, 0.0f, 0.0f);
		setRotation(this.leftLeg, -LEFT_LEG_BASE_ROT_X, 0.0f, 0.0f);
		setRotation(this.rightArm, RIGHT_ARM_BASE_ROT_X, 0.0f, 0.0f);
		setRotation(this.leftArm, LEFT_ARM_BASE_ROT_X, 0.0f, 0.0f);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 0).addBox(-6.0F, -3.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition rightLeg = body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(16, 24).addBox(-1.5F, 1.0F, -3.5F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 4.0F, 0.5F));

		PartDefinition leftLeg = body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(42, 24).addBox(-3.5F, 1.0F, -3.5F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 4.0F, 0.5F));

		PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(0, 12).addBox(0.0F, 0.0F, -7.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 0.0F, 0.0F));

		PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(64, 12).addBox(-2.0F, 0.0F, -7.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 0.0F, 0.0F));

		PartDefinition rightEye = body.addOrReplaceChild("rightEye", CubeListBuilder.create().texOffs(22, 8).addBox(-1.0F, -0.2F, 1.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 3.0F, -8.0F));

		PartDefinition leftEye = body.addOrReplaceChild("leftEye", CubeListBuilder.create().texOffs(52, 8).addBox(-1.0F, -0.2F, 1.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 3.0F, -8.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

		poseStack.pushPose();
		poseStack.translate(0.0, 1.501, 0.0); // There is a random 1.501 translation in render that messes up scales
		poseStack.scale(scaleX, scaleY, scaleX);
		poseStack.translate(0.0, -1.501, 0.0);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}

	@Override
	public void setupAnim(@Nonnull BlocklingEntity blockling, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
	{
		EntityDimensions size = blockling.getDimensions(Pose.STANDING);
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
				rightArmSwing -= blockling.getEquipment().getHandStack(InteractionHand.MAIN_HAND).isEmpty() ? 0.0f : weaponBonusRotX;
				rightArmSwingAmount /= 2.0f;
			}

			if (attackingHand == BlocklingHand.OFF || attackingHand == BlocklingHand.BOTH)
			{
				leftArmSwing += blockling.getEquipment().getHandStack(InteractionHand.OFF_HAND).isEmpty() ? 0.0f : weaponBonusRotX;
				leftArmSwingAmount /= 2.0f;
			}
		}

		if (blockling.getActions().attack.isRunning(BlocklingHand.MAIN))
		{
			float percent = blockling.getActions().attack.percentThroughHandAction(-1) + (blockling.getActions().attack.percentThroughHandAction() - blockling.getActions().attack.percentThroughHandAction(-1)) * partialTicks;
			float attackSwing = (Mth.cos(percent * (float) Math.PI / 2.0f) * 2.0f);
			rightArmSwing += blockling.getEquipment().getHandStack(InteractionHand.MAIN_HAND).isEmpty() ? -attackSwing : attackSwing;
		}

		if (blockling.getActions().attack.isRunning(BlocklingHand.OFF))
		{
			float percent = blockling.getActions().attack.percentThroughHandAction(-1) + (blockling.getActions().attack.percentThroughHandAction() - blockling.getActions().attack.percentThroughHandAction(-1)) * partialTicks;
			float attackSwing = (Mth.cos(percent * (float) Math.PI / 2.0f) * 2.0f);
			leftArmSwing -= blockling.getEquipment().getHandStack(InteractionHand.OFF_HAND).isEmpty() ? -attackSwing : attackSwing;
		}

		if (blockling.getActions().gather.isRunning())
		{
			if (hand == BlocklingHand.MAIN || hand == BlocklingHand.BOTH)
			{
				rightArmSwing = (Mth.cos(ageInTicks + (float) Math.PI) * 1.0f);
			}

			if (hand == BlocklingHand.OFF || hand == BlocklingHand.BOTH)
			{
				leftArmSwing = (Mth.cos(ageInTicks + (float) Math.PI) * 1.0f);
			}
		}

		bodySwing += (Mth.cos(limbSwing + (float) Math.PI) * limbSwingAmount * 0.1f);
		rightArmSwing += (Mth.cos(limbSwing + (float) Math.PI) * rightArmSwingAmount * 0.8f);
		leftArmSwing += (Mth.cos(limbSwing + (float) Math.PI) * leftArmSwingAmount) * 0.8f;
		rightLegSwing += (Mth.cos(limbSwing + (float) Math.PI) * rightLegSwingAmount * 0.5f);
		leftLegSwing += (Mth.cos(limbSwing + (float) Math.PI) * leftLegSwingAmount * 0.5f);

		rightArm.xRot = rightArmSwing + RIGHT_ARM_BASE_ROT_X;
		leftArm.xRot = LEFT_ARM_BASE_ROT_X - leftArmSwing;
		rightLeg.xRot = RIGHT_LEG_BASE_ROT_X - rightLegSwing;
		leftLeg.xRot = leftLegSwing + LEFT_LEG_BASE_ROT_X;

		body.zRot = bodySwing;
		rightLeg.zRot = -body.zRot;
		leftLeg.zRot = -body.zRot;
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack)
	{
		body.translateAndRotate(poseStack);

		if (arm == HumanoidArm.LEFT)
		{
			leftArm.translateAndRotate(poseStack);
		}
		else
		{
			rightArm.translateAndRotate(poseStack);
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
	private static void setRotation(@Nonnull ModelPart model, float x, float y, float z)
	{
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}