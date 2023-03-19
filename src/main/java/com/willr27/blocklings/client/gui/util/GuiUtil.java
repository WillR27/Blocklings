package com.willr27.blocklings.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.texture.Texture;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public static GuiUtil get()
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
     * @return the maximum gui scale.
     */
    public abstract float getMaxGuiScale();

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
     * @return whether the given key is currently pressed.
     */
    public abstract boolean isKeyDown(@Nonnull KeyBinding key);

    /**
     * @return whether the control key is currently pressed.
     */
    public abstract boolean isControlKeyDown();

    /**
     * @return whether the shift key is currently pressed.
     */
    public abstract boolean isShiftKeyDown();

    /**
     * @param key the key to check.
     * @return whether the given key is the close key.
     */
    public abstract boolean isCloseKey(int key);

    /**
     * @param key the key to check.
     * @return whether the given key is a key to unfocus a text field.
     */
    public abstract boolean isUnfocusTextFieldKey(int key);

    /**
     * Trims the given text to fit within the given width and replaces the last characters with ellipsis.
     *
     * @param text the text to trim.
     * @param width the width to trim the text to.
     * @return the trimmed text.
     */
    @Nonnull
    public abstract ITextProperties trimWithEllipsis(@Nonnull ITextProperties text, int width);

    /**
     * Trims the given text to fit within the given width.
     *
     * @param text the text to trim.
     * @param width the width to trim the text to.
     * @return the trimmed text.
     */
    @Nonnull
    public abstract ITextProperties trim(@Nonnull ITextProperties text, int width);

    /**
     * Trims the given text to fit within the given width.
     *
     * @param text the text to split.
     * @param width the width to split the text to.
     * @return the split text.
     */
    @Nonnull
    public abstract List<IReorderingProcessor> split(@Nonnull ITextProperties text, int width);

    /**
     * Splits the given text to fit within the given width.
     *
     * @param text the text to split.
     * @param width the width to split the text to.
     * @return the split text.
     */
    @Nonnull
    public abstract List<String> split(@Nonnull String text, int width);

    /**
     * Binds the given texture.
     *
     * @param texture the texture to bind.
     */
    public abstract void bindTexture(@Nonnull ResourceLocation texture);

    /**
     * Binds the given texture.
     *
     * @param texture the texture to bind.
     */
    public abstract void bindTexture(@Nonnull Texture texture);

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

    /**
     * Renders an item stack on the screen.
     *
     * @param matrixStack the matrix stack.
     * @param stack the item stack to render.
     * @param x the screen x position to render the item stack at.
     * @param y the screen y position to render the item stack at.
     * @param scale the scale to render the item stack at (scale of 1 means 1/16 block to 1 screen pixel).
     * @param z the z position to render the item stack at.
     */
    public abstract void renderItemStack(@Nonnull MatrixStack matrixStack, @Nonnull ItemStack stack, int x, int y, double z, float scale);

    public static void enableScissor()
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void disableScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void scissor(float x, float y, int width, int height)
    {
        MainWindow window = Minecraft.getInstance().getWindow();
        float scale = 1.0f;

        int scissorX = (int) (x * scale);
        int scissorY = (int) (window.getHeight() - ((y + height) * scale));
        int scissorWidth = (int) (width * scale);
        int scissorHeight = (int) (height * scale);

        enableScissor();
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }
}
