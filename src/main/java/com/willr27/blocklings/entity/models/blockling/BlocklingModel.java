package com.willr27.blocklings.entity.models.blockling;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.item.ToolType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jline.utils.Log;

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
        this.rightArm.addBox(0.0F, 2.0F, -7.0F, 2, 6, 6);
        this.rightArm.setPos(-8.0F, -3.0F, 0.0F);
        this.rightArm.setTexSize(128, 64);
        this.rightArm.visible = true;
        setRotation(this.rightArm, RIGHT_ARM_BASE_ROT_X, 0.0F, 0.0F);
        this.leftArm = new ModelRenderer(this, 64, 12);
        this.leftArm.addBox(-2.0F, 2.0F, -7.0F, 2, 6, 6);
        this.leftArm.setPos(8.0F, -3.0F, 0.0F);
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

    @Override
    public void setupAnim(BlocklingEntity blockling, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        EntitySize size = blockling.getDimensions(Pose.STANDING);
        scaleX = size.width;
        scaleY = size.height;

        float partialTicks = ageInTicks % 1.0f;

        limbSwing *= 0.5f;
        limbSwing += ageInTicks / 40.0f;

        if (limbSwingAmount > 1.0f)
        {
            limbSwingAmount = 0.0f;
        }
        limbSwingAmount += 0.1f;

        float weaponBonusRotX = 0.4f;

        float bodySwing = (MathHelper.cos(limbSwing + (float) Math.PI) * limbSwingAmount * 0.1f);
        float rightArmSwing = (MathHelper.cos(limbSwing + (float) Math.PI) * limbSwingAmount);
        float leftArmSwing = (MathHelper.cos(limbSwing + (float) Math.PI) * limbSwingAmount);
        float rightLegSwing = (MathHelper.cos(limbSwing + (float) Math.PI) * limbSwingAmount * 0.5f);
        float leftLegSwing = (MathHelper.cos(limbSwing + (float) Math.PI) * limbSwingAmount * 0.5f);

        BlocklingHand attackingHand = blockling.getEquipment().findAttackingHand();

        if (attackingHand == BlocklingHand.RIGHT || attackingHand == BlocklingHand.BOTH)
        {
            rightArmSwing -= weaponBonusRotX;
        }

        if (attackingHand == BlocklingHand.LEFT || attackingHand == BlocklingHand.BOTH)
        {
            leftArmSwing += weaponBonusRotX;
        }

        if (blockling.getActions().attacking.isRunning(BlocklingHand.RIGHT))
        {
            float percent = blockling.getActions().attacking.percentThroughHandAction(-1) + (blockling.getActions().attacking.percentThroughHandAction() - blockling.getActions().attacking.percentThroughHandAction(-1)) * partialTicks;
            float attackSwing = (MathHelper.cos(percent * (float) Math.PI / 2.0f) * 1.5f);
            rightArmSwing += blockling.getEquipment().getHandStack(Hand.MAIN_HAND).isEmpty() ? -attackSwing : attackSwing;
        }
        else if (blockling.getActions().attacking.isRunning(BlocklingHand.LEFT))
        {
            float percent = blockling.getActions().attacking.percentThroughHandAction(-1) + (blockling.getActions().attacking.percentThroughHandAction() - blockling.getActions().attacking.percentThroughHandAction(-1)) * partialTicks;
            float attackSwing = (MathHelper.cos(percent * (float) Math.PI / 2.0f) * 1.5f);
            leftArmSwing -= blockling.getEquipment().getHandStack(Hand.OFF_HAND).isEmpty() ? -attackSwing : attackSwing;
        }
        else if (blockling.getActions().mining.isRunning())
        {
            if (blockling.getEquipment().hasToolEquipped(Hand.MAIN_HAND, ToolType.PICKAXE))
            {
                rightArmSwing = (MathHelper.cos(ageInTicks + (float) Math.PI) * 1.0f);
            }

            if (blockling.getEquipment().hasToolEquipped(Hand.OFF_HAND, ToolType.PICKAXE))
            {
                leftArmSwing = (MathHelper.cos(ageInTicks + (float) Math.PI) * 1.0f);
            }
        }

        body.xRot = bodySwing + BODY_BASE_ROT_X;
        rightArm.xRot = rightArmSwing + RIGHT_ARM_BASE_ROT_X;
        leftArm.xRot = LEFT_ARM_BASE_ROT_X - leftArmSwing;
        rightLeg.xRot = RIGHT_LEG_BASE_ROT_X - rightLegSwing - bodySwing;
        leftLeg.xRot = leftLegSwing + LEFT_LEG_BASE_ROT_X - bodySwing;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
    {
        matrixStack.scale(scaleX, scaleY, scaleX);
        body.render(matrixStack, p_225598_2_, p_225598_3_, p_225598_4_);
    }

//    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
//    {
//        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
//    }
//
//    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
//    {
//        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
//    }

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
