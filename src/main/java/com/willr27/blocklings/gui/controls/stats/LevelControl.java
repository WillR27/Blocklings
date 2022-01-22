package com.willr27.blocklings.gui.controls.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.IControl;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

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
        super(parent, x, y, ICON_SIZE + XpBarControl.WIDTH + ICON_SIZE, ICON_SIZE);
        this.level = level;
        this.blockling = blockling;
        this.xpBar = new XpBarControl(this, level, blockling, ICON_SIZE + 5, (ICON_SIZE - XpBarControl.HEIGHT) / 2);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        renderTexture(matrixStack, getTexture());
        xpBar.render(matrixStack, mouseX, mouseY);

        if (isMouseOver(mouseX, mouseY))
        {
            screen.renderTooltip(matrixStack, new StringTextComponent(blockling.getStats().getLevelXpAttribute(level).getValue() + "/" + BlocklingAttributes.getXpUntilNextLevel(blockling.getStats().getLevelAttribute(level).getValue())), mouseX, mouseY);
        }
    }

    /**
     * @return the texture for the current level.
     */
    @Nonnull
    private GuiTexture getTexture()
    {
        int level = blockling.getStats().getLevelAttribute(this.level).getValue();

        if (level >= 99)
        {
            return new GuiTexture(GuiTextures.STATS, ICON_SIZE * this.level.ordinal(), 166, ICON_SIZE, ICON_SIZE);
        }

        return new GuiTexture(GuiTextures.STATS, (level / 20) * (ICON_SIZE * 4) + (this.level.ordinal() * ICON_SIZE), 177, ICON_SIZE, ICON_SIZE);
    }
}
