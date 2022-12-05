package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A control used to display an entity.
 */
@OnlyIn(Dist.CLIENT)
public class EntityControl extends Control
{
    /**
     * The entity to display.
     */
    @Nonnull
    private LivingEntity entity;

    /**
     * The offset to display the entity at in the x-axis. Effected by {@link #entityScale}.
     * An offset of 1.0f at a scale of 1.0f would shift the entity by 1/16 of a block.
     */
    private float offsetX = 0.0f;

    /**
     * The offset to display the entity at in the y-axis. Effected by {@link #entityScale}.
     * An offset of 1.0f at a scale of 1.0f would shift the entity by 1/16 of a block.
     */
    private float offsetY = 0.0f;

    /**
     * The scale of the entity (1.0f means a block will fit the control).
     */
    private float entityScale = 1.0f;

    /**
     * Whether to scale the entity up/down based on its bounding box. Useful for rendering all entities at a
     * similar relative scale.
     */
    private boolean scaleToBoundingBox = true;

    @Override
    public void onRender(@Nonnull RenderArgs renderArgs)
    {
        float minDimension = Math.min(getWidth(), getHeight());
        float scale = getEntityScale() * getCumulativeScale() * minDimension / 16.0f;
        GuiUtil.getInstance().renderEntityOnScreen(new MatrixStack(), entity, (int) (getScreenX() + getScreenWidth() / 2 + getOffsetX() * scale), (int) (getScreenY() + getScreenHeight() + getOffsetY() * scale), toScreenX(renderArgs.pixelMouseX), toScreenY(renderArgs.pixelMouseY), scale, shouldScaleToBoundingBox());
    }

    /**
     * @return the entity to display.
     */
    @Nonnull
    public LivingEntity getEntity()
    {
        return entity;
    }

    /**
     * Sets the entity to display.
     */
    public void setEntity(@Nonnull LivingEntity entity)
    {
        this.entity = entity;
    }

    /**
     * Gets entity scale.
     */
    public float getEntityScale()
    {
        return entityScale;
    }

    /**
     * Sets entity scale.
     */
    public void setEntityScale(float entityScale)
    {
        this.entityScale = entityScale;
    }

    /**
     * Gets the entity offset in the x-axis.
     */
    public float getOffsetX()
    {
        return offsetX;
    }

    /**
     * Sets the entity offset in the x-axis.
     */
    public void setOffsetX(float offset)
    {
        this.offsetX = offset;
    }

    /**
     * Gets the entity offset in the y-axis.
     */
    public float getOffsetY()
    {
        return offsetY;
    }

    /**
     * Sets the entity offset in the y-axis.
     */
    public void setOffsetY(float offset)
    {
        this.offsetY = offset;
    }

    /**
     * @return whether to scale the entity relative to its bounding box.
     */
    public boolean shouldScaleToBoundingBox()
    {
        return scaleToBoundingBox;
    }

    /**
     * Sets whether to scale the entity relative to its bounding box.
     */
    public void setScaleToBoundingBox(boolean scaleToBoundingBox)
    {
        this.scaleToBoundingBox = scaleToBoundingBox;
    }
}
