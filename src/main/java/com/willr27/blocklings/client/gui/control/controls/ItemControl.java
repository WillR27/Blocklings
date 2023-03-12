package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.client.gui2.GuiUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

        float scale = (float) (Math.min(getPixelWidth(), getPixelHeight()) / getGuiScale()) * getItemScale();
        int x = (int) ((getPixelX() + getPixelWidth() / 2.0) / getGuiScale());
        int y = (int) ((getPixelY() + getPixelHeight() / 2.0) / getGuiScale());

        GuiUtil.renderItemStack(matrixStack, itemStack, x, y, scale);
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
