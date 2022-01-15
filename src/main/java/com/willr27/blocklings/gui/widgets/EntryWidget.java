package com.willr27.blocklings.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.Map;

public class EntryWidget extends Widget
{
    public static final GuiTexture ENTRY_UNSELECTED = new GuiTexture(GuiTextures.WHITELIST, 0, 166, 30, 30);
    public static final GuiTexture ENTRY_SELECTED = ENTRY_UNSELECTED.shift(30, 0);

    private final GoalWhitelist whitelist;
    private final Map.Entry<ResourceLocation, Boolean> entry;
    private final Screen screen;

    public EntryWidget(GoalWhitelist whitelist, Map.Entry<ResourceLocation, Boolean> entry, FontRenderer font, int x, int y)
    {
        super(font, x, y, ENTRY_UNSELECTED.width, ENTRY_UNSELECTED.height);
        this.whitelist = whitelist;
        this.entry = entry;
        this.screen = Minecraft.getInstance().screen;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        GuiUtil.scissor(screenX, screenY, width, height, true);

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

        GuiUtil.scissor(screenX + 2, screenY + 2, width - 4, height - 4, true);

        if (whitelist.type == Whitelist.Type.BLOCK)
        {
            Block block = Registry.BLOCK.get(entry.getKey());
            ItemStack stack = new ItemStack(block);
            GuiUtil.renderItemStack(matrixStack, stack, screenX, screenY, 10);
        }
        else if (whitelist.type == Whitelist.Type.ITEM)
        {
            Item item = Registry.ITEM.get(entry.getKey());
            ItemStack stack = new ItemStack(item);
            GuiUtil.renderItemStack(matrixStack, stack, screenX, screenY, 10);
        }
        else if (whitelist.type == Whitelist.Type.ENTITY)
        {
            LivingEntity entity = (LivingEntity) EntityUtil.VALID_ATTACK_TARGETS.get(entry.getKey());
            GuiUtil.renderEntityOnScreen(screenX + width / 2, screenY + width / 2 + 11, 20, 25, -10, entity);
        }
    }

    public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
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
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        if (isPressed() && isMouseOver(mouseX, mouseY))
        {
            whitelist.toggleEntry(entry.getKey());
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
