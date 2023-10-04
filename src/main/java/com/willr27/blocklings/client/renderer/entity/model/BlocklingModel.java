package com.willr27.blocklings.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class BlocklingModel extends EntityModel<BlocklingEntity> implements ArmedModel
{
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "blocklingmodel"), "main");
	private ModelPart body;

	public BlocklingModel() {
		body = createBodyLayer().bakeRoot();
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
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(BlocklingEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_)
	{

	}

	@Override
	public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_)
	{

	}
}