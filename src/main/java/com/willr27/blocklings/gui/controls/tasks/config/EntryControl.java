package com.willr27.blocklings.gui.controls.tasks.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.*;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A control used to display an individual entry in a whitelist.
 */
@OnlyIn(Dist.CLIENT)
public class EntryControl extends Control
{
    /**
     * The unselected entry background texture.
     */
    @Nonnull
    public static final GuiTexture ENTRY_UNSELECTED = new GuiTexture(GuiTextures.WHITELIST, 0, 166, 30, 30);

    /**
     * The selected entry background texture.
     */
    @Nonnull
    public static final GuiTexture ENTRY_SELECTED = ENTRY_UNSELECTED.shift(30, 0);

    /**
     * The whitelist the entry belongs to.
     */
    @Nonnull
    private final GoalWhitelist whitelist;

    /**
     * The entry to display.
     */
    @Nonnull
    private final Map.Entry<ResourceLocation, Boolean> entry;

    /**
     * @param parent the parent control.
     * @param whitelist the whitelist the entry belongs to.
     * @param entry the entry to display.
     * @param x the x position.
     * @param y the y position.
     */
    public EntryControl(@Nonnull IControl parent, @Nonnull GoalWhitelist whitelist, @Nonnull Map.Entry<ResourceLocation, Boolean> entry, int x, int y)
    {
        super(parent, x, y, ENTRY_UNSELECTED.width, ENTRY_UNSELECTED.height);
        this.whitelist = whitelist;
        this.entry = entry;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.enableDepthTest();

        GuiUtil.addScissorBounds(screenX, screenY, getScreenWidth(), getScreenHeight());
        GuiUtil.enableStackedScissor();

        if (entry.getValue())
        {
            RenderSystem.color3f(0.4f, 0.8f, 0.4f);
            renderTexture(matrixStack, ENTRY_SELECTED);
        }
        else
        {
            RenderSystem.color3f(0.8f, 0.3f, 0.3f);
            renderTexture(matrixStack, ENTRY_UNSELECTED);
        }

        if (entry.getValue())
        {
            RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        }
        else
        {
            RenderSystem.color3f(0.5f, 0.5f, 0.5f);
        }

        GuiUtil.addScissorBounds((int) (screenX + 2 * getEffectiveScale()), (int) (screenY + 2 * getEffectiveScale()), (int) (getScreenWidth() - 4 * getEffectiveScale()), (int) (getScreenHeight() - 4 * getEffectiveScale()));
        GuiUtil.enableStackedScissor();

        if (whitelist.type == Whitelist.Type.BLOCK)
        {
            Block block = Registry.BLOCK.get(entry.getKey());
            ItemStack stack = new ItemStack(block);

            GuiUtil.renderItemStack(matrixStack, stack, (int) (screenX / getEffectiveScale()), (int) (screenY / getEffectiveScale()), 10);
        }
        else if (whitelist.type == Whitelist.Type.ITEM)
        {
            Item item = Registry.ITEM.get(entry.getKey());
            ItemStack stack = new ItemStack(item);

            GuiUtil.renderItemStack(matrixStack, stack, (int) (screenX / getEffectiveScale()), (int) (screenY / getEffectiveScale()), 10);
        }
        else if (whitelist.type == Whitelist.Type.ENTITY)
        {
            LivingEntity entity = (LivingEntity) EntityUtil.VALID_ATTACK_TARGETS.get(entry.getKey());

            if (entity instanceof BlocklingEntity)
            {
                GuiUtil.renderEntityOnScreen(matrixStack, screenX + width / 2, screenY + width / 2 + 11, 20, 25, -10, whitelist.blockling);
            }
            else
            {
                GuiUtil.renderEntityOnScreen(matrixStack, screenX + width / 2, screenY + width / 2 + 11, 20, 25, -10, entity);
            }
        }

        GuiUtil.removeScissorBounds((int) (screenX + 2 * getEffectiveScale()), (int) (screenY + 2 * getEffectiveScale()), (int) (getScreenWidth() - 4 * getEffectiveScale()), (int) (getScreenHeight() - 4 * getEffectiveScale()));
        GuiUtil.removeScissorBounds(screenX, screenY, getScreenWidth(), getScreenHeight());
    }

    @Override
    public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (whitelist.type == Whitelist.Type.BLOCK)
        {
            Block block = Registry.BLOCK.get(entry.getKey());
            screen.renderTooltip(matrixStack, block.getName(), mouseX, mouseY);
        }
        else if (whitelist.type == Whitelist.Type.ITEM)
        {
            Item item = Registry.ITEM.get(entry.getKey());
            screen.renderTooltip(matrixStack, item.getName(item.getDefaultInstance()), mouseX, mouseY);
        }
        else if (whitelist.type == Whitelist.Type.ENTITY)
        {
            Entity ent = EntityUtil.create(entry.getKey(), whitelist.blockling.level);
            screen.renderTooltip(matrixStack, ent.getName(), mouseX, mouseY);
        }
    }

    @Override
    public void controlMouseReleased(@Nonnull MouseButtonEvent e)
    {
        if (isPressed())
        {
            whitelist.toggleEntry(entry.getKey());
        }

        e.setIsHandled(true);
    }
}
