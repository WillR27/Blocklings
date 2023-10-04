package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;

/**
 * A control used to display an item.
 */
@OnlyIn(Dist.CLIENT)
public class ItemControl extends Control
{
    /**
     * The item to display.
     */
    @Nonnull
    private ItemStack itemStack = ItemStack.EMPTY;

    /**
     * The scale of the item.
     */
    private float itemScale = 1.0f;

    /**
     */
    public ItemControl()
    {
        super();
    }

    @Override
    protected void onRender(@Nonnull PoseStack poseStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(poseStack, scissorStack, mouseX, mouseY, partialTicks);

        float scale = (float) (Math.min(getPixelWidth(), getPixelHeight()) / getGuiScale()) * getItemScale();
        int x = (int) ((getPixelX() + getPixelWidth() / 2.0) / getGuiScale());
        int y = (int) ((getPixelY() + getPixelHeight() / 2.0) / getGuiScale());

        float z = (float) (isDraggingOrAncestor() ? getDraggedControl().getDragZ() : getRenderZ());

        try
        {
            // For some reason we can't just access the values in the matrix.
            // So we have to get the z translation via reflection. Nice.
            z = ObfuscationReflectionHelper.getPrivateValue(Matrix4f.class, poseStack.last().pose(), "m23");
        }
        catch (Exception ex)
        {
//            Blocklings.LOGGER.warn(ex.toString());
        }

        GuiUtil.get().renderItemStack(poseStack, itemStack, x, y, z, scale);

        if (getForegroundColourInt() != 0xffffffff)
        {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 16.0);
            renderRectangleAsBackground(poseStack, getForegroundColourInt());
            poseStack.popPose();
        }
    }

    /**
     * @return the item to display.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * Sets the item to display.
     *
     * @param itemStack the item to display.
     */
    public void setItemStack(@Nonnull ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    /**
     * @return the item to display.
     */
    @Nonnull
    public Item getItem()
    {
        return itemStack.getItem();
    }

    /**
     * Sets the item to display.
     *
     * @param item the item to display.
     */
    public void setItem(@Nonnull Item item)
    {
        setItemStack(new ItemStack(item));
    }

    /**
     * @return the scale of the item.
     */
    public float getItemScale()
    {
        return itemScale;
    }

    /**
     * Sets the scale of the item.
     *
     * @param itemScale the scale of the item.
     */
    public void setItemScale(float itemScale)
    {
        this.itemScale = itemScale;
    }
}
