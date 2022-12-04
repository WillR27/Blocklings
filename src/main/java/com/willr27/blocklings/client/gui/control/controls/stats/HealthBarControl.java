package com.willr27.blocklings.client.gui.control.controls.stats;

import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Displays a health bar.
 */
@OnlyIn(Dist.CLIENT)
public class HealthBarControl extends Control
{
    /**
     * @param blockling the blockling.
     */
    public HealthBarControl(@Nonnull BlocklingEntity blockling)
    {
        super();

        setHeight(11);
        setWidth(GuiTextures.Stats.HEALTH_BAR.width);

        TexturedControl backgroundHealthBarControl = new TexturedControl(GuiTextures.Stats.HEALTH_BAR)
        {
            @Override
            protected void onRenderBackground(@Nonnull RenderArgs renderArgs)
            {
                RenderSystem.color3f(0.5f, 0.5f, 0.5f);
                super.onRenderBackground(renderArgs);
                RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            }
        };
        backgroundHealthBarControl.setParent(this);
        backgroundHealthBarControl.setAlignmentY(new Alignment(0.5f));

        TexturedControl colouredHealthBarControl = new TexturedControl(GuiTextures.Stats.HEALTH_BAR)
        {
            @Override
            protected void onTick()
            {
                setWidth(texture.width * ((float) blockling.getStats().getHealth() / blockling.getStats().getMaxHealth()));
            }

            @Override
            protected void onRenderBackground(@Nonnull RenderArgs renderArgs)
            {
                RenderSystem.color3f((float) (1.3f - (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 0.3f + (float) (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth()), 0.1f);
                super.onRenderBackground(renderArgs);
                RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            }
        };
        colouredHealthBarControl.setParent(this);
        colouredHealthBarControl.setAlignmentY(new Alignment(0.5f));


        TextBlockControl healthTextControl = new TextBlockControl()
        {
            @Override
            protected void onTick()
            {
                int r = (int) (215 - blockling.getStats().getHealthPercentage() * 150);
                int g = (int) (50 + blockling.getStats().getHealthPercentage() * 180);
                int b = 50;
                setTextColour(new Colour(r, g, b));

                String healthText = blockling.getStats().getHealth() + "/" + blockling.getStats().getMaxHealth();
                setText(healthText);
            }
        };
        healthTextControl.setParent(this);
        healthTextControl.setFitToContentsXY(true);
        healthTextControl.setHorizontalAlignment(HorizontalAlignment.MIDDLE);
        healthTextControl.setAlignmentX(new Alignment(0.5f));
        healthTextControl.setAlignmentY(new Alignment(0.7f));
    }
}
