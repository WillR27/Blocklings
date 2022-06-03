package com.willr27.blocklings.gui.controls.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A control to display a level.
 */
@OnlyIn(Dist.CLIENT)
public class LevelControl extends Control
{
    /**
     * The width and height of the icon next to the xp bar.
     */
    public static final int ICON_SIZE = 11;

    /**
     * The level to display.
     */
    @Nonnull
    private final BlocklingAttributes.Level level;

    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * The xp bar control.
     */
    @Nonnull
    private final XpBarControl xpBar;

    /**
     * @param parent the parent control.
     * @param level the level to display.
     * @param blockling the blockling.
     * @param x the x position.
     * @param y the y position.
     */
    public LevelControl(@Nonnull IControl parent, @Nonnull BlocklingAttributes.Level level, @Nonnull BlocklingEntity blockling, int x, int y)
    {
        super(parent, x, y, ICON_SIZE + XpBarControl.WIDTH + ICON_SIZE + ICON_SIZE, ICON_SIZE);
        this.level = level;
        this.blockling = blockling;
        this.xpBar = new XpBarControl(this, level, blockling, ICON_SIZE + 5, (ICON_SIZE - XpBarControl.HEIGHT) / 2);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        int level = blockling.getStats().getLevelAttribute(this.level).getValue();

        renderTexture(matrixStack, getTexture(level));
        renderTexture(matrixStack, width - getTexture(level + 1).width, 0, getTexture(level + 1));
    }

    @Override
    public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        Attribute<Integer> level = blockling.getStats().getLevelAttribute(this.level);

        List<IReorderingProcessor> tooltip = new ArrayList<>();
        tooltip.add(new StringTextComponent(TextFormatting.GOLD + blockling.getStats().getLevelAttribute(this.level).displayStringNameSupplier.get()).getVisualOrderText());
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("gui.current_level", TextFormatting.WHITE, level.getValue()).getString()).getVisualOrderText());
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("gui.xp_required", TextFormatting.WHITE, blockling.getStats().getLevelXpAttribute(this.level).getValue(), BlocklingAttributes.getXpForLevel(level.getValue())).getString()).getVisualOrderText());

        screen.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
    }

    /**
     * @return the texture for the current level.
     */
    @Nonnull
    private GuiTexture getTexture(int level)
    {
        if (level >= BlocklingAttributes.Level.MAX)
        {
            return new GuiTexture(GuiTextures.STATS, ICON_SIZE * this.level.ordinal(), 166, ICON_SIZE, ICON_SIZE);
        }

        return new GuiTexture(GuiTextures.STATS, (level / 20) * (ICON_SIZE * 4) + (this.level.ordinal() * ICON_SIZE), 177, ICON_SIZE, ICON_SIZE);
    }
}
