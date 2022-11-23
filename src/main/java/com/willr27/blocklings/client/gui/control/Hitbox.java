package com.willr27.blocklings.client.gui.control;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Represents an area that the mouse can interact with on a control.
 */
@OnlyIn(Dist.CLIENT)
public abstract class Hitbox
{
    /**
     * Resizes the hitbox to fit size changes.
     *
     * @param oldWidth the old width of the control.
     * @param newWidth the new width of the control.
     * @param oldHeight the old height of the control.
     * @param newHeight the new height of the control.
     */
    public abstract void resize(float oldWidth, float newWidth, float oldHeight, float newHeight);

    /**
     * @param control the control to test against.
     * @param pixelX the x pixel coordinate to test against.
     * @param pixelY the y pixel coordinate to test against.
     * @return whether the position collides with the hitbox.
     */
    public abstract boolean collidesWith(@Nonnull Control control, float pixelX, float pixelY);

    @Override
    public abstract boolean equals(Object object);

    /**
     * A rectangular hitbox. A 100 x 100 control would have a default hitbox of 100 x 100 at (0, 0).
     */
    public static class RectangleHitbox extends Hitbox
    {
        /**
         * The relative x coordinate of the hitbox in relation to the top left corner of the control.
         */
        private int x;

        /**
         * The relative y coordinate of the hitbox in relation to the top left corner of the control.
         */
        private int y;

        /**
         * The width of the hitbox.
         */
        private float width;

        /**
         * The height of the hitbox.
         */
        private float height;

        /**
         * @param x the relative x coordinate of the hitbox in relation to the top left corner of the control.
         * @param y the relative y coordinate of the hitbox in relation to the top left corner of the control.
         * @param width the width of the hitbox.
         * @param height the height of the hitbox.
         */
        public RectangleHitbox(int x, int y, float width, float height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void resize(float oldWidth, float newWidth, float oldHeight, float newHeight)
        {
            float dWidth = newWidth - oldWidth;
            float dHeight = newHeight - oldHeight;

            width += dWidth;
            height += dHeight;

            width = Math.max(0, width);
            height = Math.max(0, height);
        }

        @Override
        public boolean collidesWith(@Nonnull Control control, float pixelX, float pixelY)
        {
            return pixelX >= control.getPixelX() &&
                   pixelX <  control.getPixelX() + control.getPixelWidth() &&
                   pixelY >= control.getPixelY() &&
                   pixelY <  control.getPixelY() + control.getPixelHeight();
        }

        @Override
        public boolean equals(Object object)
        {
            if (object instanceof RectangleHitbox)
            {
                RectangleHitbox hitbox = (RectangleHitbox) object;

                return hitbox.x == x && hitbox.y == y && hitbox.width == width && hitbox.height == height;
            }

            return false;
        }
    }
}
