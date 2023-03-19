package com.willr27.blocklings.client.gui.control.controls.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.screen.screens.StatsScreen;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.Colour;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.Attribute;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Displays a blockling's health bar.
 */
@OnlyIn(Dist.CLIENT)
public class HealthBarControl extends Control
{
    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * @param blockling the blockling.
     */
    public HealthBarControl(@Nonnull BlocklingEntity blockling)
    {
        super();
        this.blockling = blockling;

        setFitWidthToContent(true);
        setFitHeightToContent(true);

        TexturedControl backgroundHealthBarControl = new TexturedControl(Textures.Stats.HEALTH_BAR)
        {
            @Override
            public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                RenderSystem.color3f(0.5f, 0.5f, 0.5f);
                super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            }
        };
        backgroundHealthBarControl.setParent(this);
        backgroundHealthBarControl.setVerticalAlignment(0.5);
        backgroundHealthBarControl.setInteractive(false);

        TexturedControl colouredHealthBarControl = new TexturedControl(Textures.Stats.HEALTH_BAR)
        {
            @Override
            public void onTick()
            {
                setWidth(getBackgroundTexture().width * ((float) blockling.getStats().getHealth() / blockling.getStats().getMaxHealth()));
            }

            @Override
            public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                RenderSystem.color3f((float) (1.3f - (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 0.3f + (float) (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth()), 0.1f);
                super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            }
        };
        colouredHealthBarControl.setParent(this);
        colouredHealthBarControl.setVerticalAlignment(0.5);
        colouredHealthBarControl.onTick();
        colouredHealthBarControl.setInteractive(false);
        colouredHealthBarControl.setFitWidthToContent(false);

        TextBlockControl levelTextControl = new TextBlockControl()
        {
            @Override
            public void onTick()
            {
                int r = (int) (215 - blockling.getStats().getHealthPercentage() * 150);
                int g = (int) (50 + blockling.getStats().getHealthPercentage() * 180);
                int b = 50;
                setTextColour(new Colour(r, g, b).argb());

                String healthText = blockling.getStats().getHealth() + "/" + blockling.getStats().getMaxHealth();
                setText(healthText);
            }
        };
        levelTextControl.setParent(this);
        levelTextControl.setFitWidthToContent(true);
        levelTextControl.setFitHeightToContent(true);
        levelTextControl.useDescenderlessLineHeight();
        levelTextControl.setHorizontalAlignment(0.5);
        levelTextControl.setVerticalAlignment(0.5);
        levelTextControl.setInteractive(false);
        levelTextControl.onTick();
    }

    @Override
    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {
        List<ITextComponent> tooltip = StatsScreen.createModifiableFloatAttributeTooltip(blockling.getStats().maxHealth, TextFormatting.DARK_GREEN);
        tooltip.add(0, new StringTextComponent(TextFormatting.GOLD + new Attribute.AttributeTranslationTextComponent("health.name").getString()));

        renderTooltip(matrixStack, mouseX, mouseY, tooltip.stream().map(ITextComponent::getVisualOrderText).collect(Collectors.toList()));
    }

}
