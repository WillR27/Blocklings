package com.willr27.blocklings.gui.controls.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.screens.StatsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A control for displaying the blockling's health as a health bar.
 */
@OnlyIn(Dist.CLIENT)
public class HealthBarControl extends Control
{
    /**
     * The health bar background texture.
     */
    @Nonnull
    private static final GuiTexture BACKGROUND_TEXTURE = new GuiTexture(GuiTextures.STATS, 0, 228, 134, 5);

    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * @param parent the parent control.
     * @param blockling the blockling.
     * @param x the x position.
     * @param y the y position.
     */
    public HealthBarControl(@Nonnull IControl parent, @Nonnull BlocklingEntity blockling, int x, int y)
    {
        super(parent, x, y - 2, BACKGROUND_TEXTURE.width, BACKGROUND_TEXTURE.height + 4);
        this.blockling = blockling;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.color3f(0.5f, 0.5f, 0.5f);
        renderTexture(matrixStack, 0, 2, BACKGROUND_TEXTURE);

        RenderSystem.color3f((float) (1.3f - (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 0.3f + (float) (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth()), 0.1f);
        renderTexture(matrixStack, 0, 2, new GuiTexture(GuiTextures.STATS, 0, 228, (int) (134 * (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 5));

        int r = (int) (215 - blockling.getStats().getHealthPercentage() * 150);
        int g = (int) (50 + blockling.getStats().getHealthPercentage() * 180);
        int b = 50;
        String healthText = blockling.getStats().getHealth() + "/" + blockling.getStats().getMaxHealth();
        renderCenteredText(matrixStack, healthText, -width / 2, 1, false, (r << 16) + (g << 8) + b);
    }

    @Override
    public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        List<ITextComponent> tooltip = StatsScreen.createModifiableFloatAttributeTooltip(blockling.getStats().maxHealth, TextFormatting.DARK_GREEN);
        tooltip.add(0, new StringTextComponent(TextFormatting.GOLD + new Attribute.AttributeTranslationTextComponent("health.name").getString()));

        screen.renderTooltip(matrixStack, tooltip.stream().map(ITextComponent::getVisualOrderText).collect(Collectors.toList()), mouseX, mouseY);
    }
}
