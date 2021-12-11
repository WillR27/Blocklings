package com.blocklings.models;

import com.blocklings.entities.EntityBlockling;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;

public class ModelBlockling extends ModelBase
{
    ModelRenderer body;
    ModelRenderer rightLeg;
    ModelRenderer leftLeg;
    ModelRenderer rightArm;
    ModelRenderer leftArm;
    ModelRenderer rightEye;
    ModelRenderer leftEye;
    public static final float bodyBaseX = 0.0872665f;
    private float rightLegBaseX = -0.0872665f;
    private float leftLegBaseX = -0.0872665f;
    public static final float rightArmBaseX = 0.785398f - bodyBaseX;
    public static final float leftArmBaseX =  0.785398f - bodyBaseX;
    private float rightEyeBaseX = 0.0f;
    private float leftEyeBaseX = 0.0f;

    private int[] attackAnimation = { 0, 18, 39, 62, 85, 70, 58, 47, 37, 28, 20, 14, 8, 3, 0 };

    public ModelBlockling()
    {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.body = new ModelRenderer(this, 16, 0);
        this.body.addBox(-6.0F, -3.0F, -6.0F, 12, 12, 12);
        this.body.setRotationPoint(0.0F, 13.0F, 0.0F);
        this.body.setTextureSize(128, 64);
        this.body.showModel = true;
        setRotation(this.body, 0.0872665F, 0.0F, 0.0F);
        this.rightLeg = new ModelRenderer(this, 16, 24);
        this.rightLeg.addBox(-1.5F, 1.0F, -3.5F, 5, 6, 6);
        this.rightLeg.setRotationPoint(-4.0F, 4.0F, 0.5F);
        this.rightLeg.setTextureSize(128, 64);
        this.rightLeg.showModel = true;
        setRotation(this.rightLeg, -0.0872665F, 0.0F, 0.0F);
        this.leftLeg = new ModelRenderer(this, 42, 24);
        this.leftLeg.addBox(-3.5F, 1.0F, -3.5F, 5, 6, 6);
        this.leftLeg.setRotationPoint(4.0F, 4.0F, 0.5F);
        this.leftLeg.setTextureSize(128, 64);
        this.leftLeg.showModel = true;
        setRotation(this.leftLeg, -0.0872665F, 0.0F, 0.0F);
        this.rightArm = new ModelRenderer(this, 0, 12);
        this.rightArm.addBox(0.0F, 2.0F, -7.0F, 2, 6, 6);
        this.rightArm.setRotationPoint(-8.0F, -3.0F, 0.0F);
        this.rightArm.setTextureSize(128, 64);
        this.rightArm.showModel = true;
        setRotation(this.rightArm, rightArmBaseX, 0.0F, 0.0F);
        this.leftArm = new ModelRenderer(this, 64, 12);
        this.leftArm.addBox(-2.0F, 2.0F, -7.0F, 2, 6, 6);
        this.leftArm.setRotationPoint(8.0F, -3.0F, 0.0F);
        this.leftArm.setTextureSize(128, 64);
        this.leftArm.showModel = true;
        setRotation(this.leftArm, leftArmBaseX, 0.0F, 0.0F);
        this.rightEye = new ModelRenderer(this, 22, 8);
        this.rightEye.addBox(-1.0F, -0.2F, 1.5F, 2, 3, 1);
        this.rightEye.setRotationPoint(-2.0F, 3.0F, -8.0F);
        this.rightEye.setTextureSize(128, 64);
        this.rightEye.showModel = true;
        setRotation(this.rightEye, 0.0F, 0.0F, 0.0F);
        this.leftEye = new ModelRenderer(this, 52, 8);
        this.leftEye.addBox(-1.0F, -0.2F, 1.5F, 2, 3, 1);
        this.leftEye.setRotationPoint(2.0F, 3.0F, -8.0F);
        this.leftEye.setTextureSize(128, 64);
        this.leftEye.showModel = true;
        setRotation(this.leftEye, 0.0F, 0.0F, 0.0F);

        this.body.addChild(this.rightArm);
        this.body.addChild(this.leftArm);
        this.body.addChild(this.rightLeg);
        this.body.addChild(this.leftLeg);
        this.body.addChild(this.rightEye);
        this.body.addChild(this.leftEye);
    }

    public void render(Entity entity, float time, float speed, float age, float yaw, float pitch, float scale)
    {
        super.render(entity, time, speed, age, yaw, pitch, scale);

        this.body.render(scale);
    }

    public void setRotationAngles(float time, float speed, float age, float yaw, float pitch, float scale, Entity entity)
    {
        super.setRotationAngles(time, speed, age, yaw, pitch, scale, entity);

        EntityBlockling blockling = (EntityBlockling) entity;
        EnumHand hand = blockling.calculateAttackingHand();

        float logSpeed = (float) Math.log(speed + 1);
        float swingHeight = (0.05f + logSpeed / 7.0f) / 12.0f;
        float swingSpeed = 0.6f;
        float flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);

        if (blockling.getAnimationState() == EntityBlockling.AnimationState.IDLE)
        {
            body.rotateAngleX = bodyBaseX + (flipFlopper);

            swingHeight = 0.05f + logSpeed / 4.0f;
            swingSpeed = 1.2f;
            flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
            if (blockling.isSetToAttack())
            {
                if (blockling.hasWeapon())
                {
                    if (blockling.hasWeapon(EnumHand.OFF_HAND)) leftArm.rotateAngleX = leftArmBaseX + (flipFlopper) - (float) Math.toRadians(30);
                    else leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                    if (blockling.hasWeapon(EnumHand.MAIN_HAND)) rightArm.rotateAngleX = rightArmBaseX - (flipFlopper) - (float) Math.toRadians(30);
                    else rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                }
                else if (blockling.hasTool())
                {
                    if (blockling.hasTool(EnumHand.OFF_HAND)) leftArm.rotateAngleX = leftArmBaseX + (flipFlopper) - (float) Math.toRadians(30);
                    else leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                    if (blockling.hasTool(EnumHand.MAIN_HAND)) rightArm.rotateAngleX = rightArmBaseX - (flipFlopper) - (float) Math.toRadians(30);
                    else rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                }
                else
                {
                    leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                    rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                }
            }
            else
            {
                leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
            }

            swingHeight = logSpeed / 4.0f;
            flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
            leftLeg.rotateAngleX = leftLegBaseX - (flipFlopper);
            rightLeg.rotateAngleX = rightLegBaseX + (flipFlopper);
        }
        else if (blockling.getAnimationState() == EntityBlockling.AnimationState.ATTACKING)
        {
            body.rotateAngleX = bodyBaseX + (flipFlopper);

            swingHeight = 0.05f + logSpeed / 4.0f;
            swingSpeed = 1.2f;
            flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);

            int ticksLeft = blockling.getAttackInterval() - blockling.getAttackTimer();
            if (ticksLeft < 2 && blockling.isInAttackRange)
            {
                if (blockling.hasTool())
                {
                    if (hand == EnumHand.OFF_HAND)
                        leftArm.rotateAngleX = leftArmBaseX + (flipFlopper) - (float) Math.toRadians((35 * ticksLeft) - 40);
                    if (hand == EnumHand.MAIN_HAND)
                        rightArm.rotateAngleX = rightArmBaseX - (flipFlopper) - (float) Math.toRadians((35 * ticksLeft) - 40);
                }
                else
                {
                    swingHeight = 0.05f + logSpeed / 4.0f;
                    swingSpeed = 1.2f;
                    flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
                    leftArm.rotateAngleX = leftArmBaseX + (flipFlopper) - (float) Math.toRadians((60 * ticksLeft));
                    rightArm.rotateAngleX = rightArmBaseX - (flipFlopper) - (float) Math.toRadians((60 * ticksLeft));
                }
            }
            else
            {
                swingHeight = 0.05f + logSpeed / 4.0f;
                swingSpeed = 1.2f;
                flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
                if (blockling.isSetToAttack())
                {
                    if (blockling.hasWeapon())
                    {
                        if (blockling.hasWeapon(EnumHand.OFF_HAND)) leftArm.rotateAngleX = leftArmBaseX + (flipFlopper) - (float) Math.toRadians(30);
                        else leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                        if (blockling.hasWeapon(EnumHand.MAIN_HAND)) rightArm.rotateAngleX = rightArmBaseX - (flipFlopper) - (float) Math.toRadians(30);
                        else rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                    }
                    else if (blockling.hasTool())
                    {
                        if (blockling.hasTool(EnumHand.OFF_HAND)) leftArm.rotateAngleX = leftArmBaseX + (flipFlopper) - (float) Math.toRadians(30);
                        else leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                        if (blockling.hasTool(EnumHand.MAIN_HAND)) rightArm.rotateAngleX = rightArmBaseX - (flipFlopper) - (float) Math.toRadians(30);
                        else rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                    }
                    else
                    {
                        leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                        rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                    }
                }
                else
                {
                    leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                    rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                }
            }

            swingHeight = logSpeed / 4.0f;
            flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
            leftLeg.rotateAngleX = leftLegBaseX - (flipFlopper);
            rightLeg.rotateAngleX = rightLegBaseX + (flipFlopper);
        }
        else if (blockling.getAnimationState() == EntityBlockling.AnimationState.MINING)
        {
            body.rotateAngleX = bodyBaseX + (flipFlopper);

            // Mining animation
            swingHeight = 0.25f;
            swingSpeed = 40f;
            flipFlopper = flipFlopper(age, swingSpeed) * (swingHeight);
            if (blockling.hasCorrectToolForJob(EnumHand.OFF_HAND))
            {
                leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
            }
            else
            {
                // Idle animation
                swingHeight = 0.05f + logSpeed / 4.0f;
                swingSpeed = 1.2f;
                flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
                if (blockling.isSetToAttack())
                {
                    if (blockling.hasTool(EnumHand.OFF_HAND))
                    {
                        if (hand == EnumHand.OFF_HAND) leftArm.rotateAngleX = leftArmBaseX + (flipFlopper) - (float) Math.toRadians(30);
                        else leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                    }
                    else
                    {
                        leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                    }
                }
                else
                {
                    leftArm.rotateAngleX = leftArmBaseX + (flipFlopper);
                }
            }

            swingHeight = 0.25f;
            swingSpeed = 40f;
            flipFlopper = flipFlopper(age, swingSpeed) * (swingHeight);
            if (blockling.hasCorrectToolForJob(EnumHand.MAIN_HAND))
            {
                rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
            }
            else
            {
                // Idle animation
                swingHeight = 0.05f + logSpeed / 4.0f;
                swingSpeed = 1.2f;
                flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
                if (blockling.isSetToAttack())
                {
                    if (blockling.hasTool(EnumHand.MAIN_HAND))
                    {
                        if (hand == EnumHand.MAIN_HAND) rightArm.rotateAngleX = rightArmBaseX - (flipFlopper) - (float) Math.toRadians(30);
                        else rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                    }
                    else
                    {
                        rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                    }
                }
                else
                {
                    rightArm.rotateAngleX = rightArmBaseX - (flipFlopper);
                }
            }

            swingHeight = logSpeed / 4.0f;
            swingSpeed = 1.2f;
            flipFlopper = flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight);
            leftLeg.rotateAngleX = leftLegBaseX - (flipFlopper);
            rightLeg.rotateAngleX = rightLegBaseX + (flipFlopper);
        }

        if (blockling.isSitting())
        {
            body.rotateAngleX -= bodyBaseX;
            body.offsetY = 0.08f;
        }
        else
        {
            body.offsetY = 0.0f;
        }
    }
    // Goes from -PI to PI and back again
    private float flipFlopper(float age, float coef)
    {
        return ((float) Math.sin(Math.toRadians((coef * age) % 360.0f))) * (float) Math.PI;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}