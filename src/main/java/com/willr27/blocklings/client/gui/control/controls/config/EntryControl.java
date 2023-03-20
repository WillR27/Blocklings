package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.EntityControl;
import com.willr27.blocklings.client.gui.control.controls.ItemControl;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.GoalWhitelist;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.Whitelist;
import com.willr27.blocklings.util.EntityUtil;
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

import static com.willr27.blocklings.client.gui.texture.Textures.Tasks.ENTRY_SELECTED;
import static com.willr27.blocklings.client.gui.texture.Textures.Tasks.ENTRY_UNSELECTED;

/**
 * A control used to display an individual entry in a whitelist.
 */
@OnlyIn(Dist.CLIENT)
public class EntryControl extends Control
{
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
     * @param whitelist the whitelist the entry belongs to.
     * @param entry the entry to display.
     */
    public EntryControl(@Nonnull GoalWhitelist whitelist, @Nonnull Map.Entry<ResourceLocation, Boolean> entry)
    {
        super();
        this.whitelist = whitelist;
        this.entry = entry;

        setWidth(ENTRY_SELECTED.width);
        setHeight(ENTRY_SELECTED.height);

        if (whitelist.type == Whitelist.Type.ENTITY)
        {
            LivingEntity entity = (LivingEntity) EntityUtil.VALID_ATTACK_TARGETS.get().get(entry.getKey());
            EntityControl entityControl = new EntityControl()
            {
                @Override
                protected void onRenderUpdate(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    super.onRenderUpdate(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

                    setLookX((float) (-25 + (getPixelX() + getPixelWidth() / 2.0) / getGuiScale()));
                    setLookY((float) (25 + (getPixelY() + getPixelHeight() / 2.0) / getGuiScale()));
                }
            };
            entityControl.setParent(this);
            entityControl.setEntity(entity);
            entityControl.setWidth(getWidth() - 4);
            entityControl.setHeight(getHeight() - 4);
            entityControl.setHorizontalAlignment(0.5);
            entityControl.setVerticalAlignment(0.5);
            entityControl.setInteractive(false);
            entityControl.setScaleToBoundingBox(true);
            entityControl.setEntityScale(0.8f);
            entityControl.setOffsetY(-0.1f);
        }
        else if (whitelist.type == Whitelist.Type.BLOCK || whitelist.type == Whitelist.Type.ITEM)
        {
            ItemStack stack = ItemStack.EMPTY;

            if (whitelist.type == Whitelist.Type.BLOCK)
            {
                Block block = Registry.BLOCK.get(entry.getKey());
                stack = block.asItem().getDefaultInstance();
            }
            else if (whitelist.type == Whitelist.Type.ITEM)
            {
                Item item = Registry.ITEM.get(entry.getKey());
                stack = item.getDefaultInstance();
            }

            ItemControl itemControl = new ItemControl();
            itemControl.setParent(this);
            itemControl.setItemStack(stack);
            itemControl.setWidth(getWidth() - 4);
            itemControl.setHeight(getHeight() - 4);
            itemControl.setHorizontalAlignment(0.5);
            itemControl.setVerticalAlignment(0.5);
            itemControl.setInteractive(false);
            itemControl.setItemScale(0.95f);
        }
    }

    @Override
    public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        if (entry.getValue())
        {
            RenderSystem.color3f(0.4f, 0.8f, 0.4f);
            renderTextureAsBackground(matrixStack, ENTRY_SELECTED);
        }
        else
        {
            RenderSystem.color3f(0.8f, 0.3f, 0.3f);
            renderTextureAsBackground(matrixStack, ENTRY_UNSELECTED);
        }

        if (entry.getValue())
        {
            RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        }
        else
        {
            RenderSystem.color3f(0.5f, 0.5f, 0.5f);
        }
    }

    @Override
    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {
        if (whitelist.type == Whitelist.Type.BLOCK)
        {
            Block block = Registry.BLOCK.get(entry.getKey());
            renderTooltip(matrixStack, mouseX, mouseY, block.getName());
        }
        else if (whitelist.type == Whitelist.Type.ITEM)
        {
            Item item = Registry.ITEM.get(entry.getKey());
            renderTooltip(matrixStack, mouseX, mouseY, item.getName(item.getDefaultInstance()));
        }
        else if (whitelist.type == Whitelist.Type.ENTITY)
        {
            Entity ent = EntityUtil.create(entry.getKey(), whitelist.blockling.level);
            renderTooltip(matrixStack, mouseX, mouseY, ent.getName());
        }
    }

    @Override
    public void onMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        if (isPressed())
        {
            whitelist.toggleEntry(entry.getKey());
        }

        e.setIsHandled(true);
    }
}
