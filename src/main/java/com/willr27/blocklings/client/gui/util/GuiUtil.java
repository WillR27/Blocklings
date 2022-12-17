package com.willr27.blocklings.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Contains useful methods related to guis.
 */
@OnlyIn(Dist.CLIENT)
public abstract class GuiUtil
{
    /**
     * The instance of {@link GuiUtil}.
     */
    @Nullable
    private static GuiUtil instance;

    /**
     * @return the instance of {@link GuiUtil}.
     */
    @Nonnull
    public static GuiUtil getInstance()
    {
        if (instance == null)
        {
            if (Minecraft.getInstance() != null)
            {
                instance = new FullGuiUtil();
            }
            else
            {
                instance = new TestGuiUtil();
            }
        }

        return instance;
    }

    /**
     * @return the current gui scale.
     */
    public abstract float getGuiScale();

    /**
     * @return the current mouse pixel x coordinate.
     */
    public abstract int getPixelMouseX();

    /**
     * @return the current mouse pixel y coordinate.
     */
    public abstract int getPixelMouseY();

    /**
     * @return whether the given key is currently pressed.
     */
    public abstract boolean isKeyDown(int key);

    /**
     * Renders an entity on the screen.
     *
     * @param matrixStack the matrix stack.
     * @param entity the entity to render.
     * @param screenX the screen x position to render the entity at (0 is center of entity).
     * @param screenY the screen y position to render the entity at (0 is feet level).
     * @param screenMouseX the screen mouse x position.
     * @param screenMouseY the screen mouse y position
     * @param scale the scale to render the entity at (scale of 1 means 1/16 block to 1 screen pixel).
     * @param scaleToBoundingBox whether to scale the entity based on its bounding box (essentially scale up/down relatively to a single block hitbox).
     */
    public abstract void renderEntityOnScreen(@Nonnull MatrixStack matrixStack, @Nonnull LivingEntity entity, int screenX, int screenY, float screenMouseX, float screenMouseY, float scale, boolean scaleToBoundingBox);
}
